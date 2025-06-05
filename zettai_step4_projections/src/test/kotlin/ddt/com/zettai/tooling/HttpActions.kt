package ddt.com.zettai.tooling

import com.ubertob.pesticide.core.*
import com.zettai.domain.*
import com.zettai.domain.tooling.expectSuccess
import com.zettai.fp.asSuccess
import com.zettai.ui.HtmlPage
import com.zettai.ui.toIsoLocalDate
import com.zettai.ui.toStatus
import org.http4k.client.JettyClient
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.body.Form
import org.http4k.core.body.toBody
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.time.Duration

data class HttpActions(val env: String = "local") : ZettaiActions {
    private val zettaiPort = 8000

    val client = JettyClient()

    override val protocol: DdtProtocol = Http(env)
    override fun prepare(): DomainSetUp {
        if (verifyStarted(Duration.ZERO) == Ready)
            return Ready

        val server = prepareZettaiForTests().asServer(Jetty(zettaiPort))
        server.start()
        registerShutdownHook {
            server.stop()
        }
        return verifyStarted(Duration.ofSeconds(2))
    }

    private fun verifyStarted(timeout: Duration): DomainSetUp {
        val begin = System.currentTimeMillis()
        while (true) {
            val r = callZettai(Method.GET, "ping").status
            if (r == Status.OK)
                return Ready
            if (elapsed(begin) >= timeout)
                return NotReady("timeout $timeout exceeded")
            Thread.sleep(10)
        }
    }

    private fun elapsed(since: Long): Duration =
        Duration.ofMillis(System.currentTimeMillis() - since)

    private fun registerShutdownHook(hookToExecute: () -> Unit) {
        Runtime.getRuntime().addShutdownHook(Thread {
            val out = System.out
            try {
                hookToExecute()
            } finally {
                System.setOut(out)
            }
        })
    }

    override fun ToDoListOwner.`starts with a list`(listName: String, items: List<String>) {
        val listName1 = ListName.fromTrusted(listName)
        val lists = allUserLists(user).expectSuccess()
        if (listName1 !in lists) {
            val response = submitToZettai(allUserListsUrl(user), newListForm(listName1))
            expectThat(response.status).isEqualTo(Status.SEE_OTHER)
            items.forEach {
                addListItem(user, listName1, ToDoItem(it))
            }
        }
    }

    override fun getToDoList(user: User, listName: ListName): ZettaiOutcome<ToDoList> {
        val response = callZettai(Method.GET, todoListUrl(user, listName))

        expectThat(response.status).isEqualTo(Status.OK)

        val html = HtmlPage(response.bodyString())

        val items = extractItemsFromPage(html)

        return ToDoList(listName, items).asSuccess()
    }

    override fun addListItem(user: User, listName: ListName, toDoItem: ToDoItem) {
        val response = submitToZettai(
            todoListUrl(user, listName),
            listOf("itemname" to toDoItem.description,
                "itemdue" to toDoItem.dueDate?.toString())
        )

        expectThat(response.status).isEqualTo(Status.SEE_OTHER)
    }

    override fun allUserLists(user: User): ZettaiOutcome<List<ListName>> {
        val response = callZettai(Method.GET, allUserListsUrl(user))
        expectThat(response.status).isEqualTo(Status.OK)
        val html = HtmlPage(response.bodyString())
        val names = extractListNamesFromPage(html)
        return names.map { name -> ListName.fromTrusted(name) }.asSuccess()
    }

    override fun createList(user: User, listName: ListName) {
        val response = submitToZettai(allUserListsUrl(user), newListForm(listName))
        expectThat(response.status).isEqualTo(Status.SEE_OTHER)
    }

    private fun newListForm(listName: ListName): Form = listOf("listname" to listName.name)

    private fun submitToZettai(path: String, webForm: Form): Response =
        client(
            log(
                Request(Method.POST, "http://localhost:$zettaiPort/$path")
                    .body(webForm.toBody())
            )
        )

    private fun callZettai(method: Method, path: String): Response =
        client(log(Request(method, "http://localhost:$zettaiPort/$path")))

    private fun todoListUrl(user: User, listName: ListName) =
        "todo/${user.name}/${listName.name}"

    private fun allUserListsUrl(user: User) =
        "todo/${user.name}"

    private fun HtmlPage.parse(): Document = Jsoup.parse(raw)

    private fun extractItemsFromPage(html: HtmlPage): List<ToDoItem> =
        html.parse()
            .select("tr")
            .toList()
            .filter { it.select("td").size == 3 }
            .map {
                Triple(
                    it.select("td")[0].text().orEmpty(),
                    it.select("td")[1].text().toIsoLocalDate(),
                    it.select("td")[2].text().orEmpty().toStatus()
                )
            }
            .map { (name, date, status) ->
                ToDoItem(name, date, status)
            }

    private fun extractListNamesFromPage(html: HtmlPage): List<String> {
        return html.parse()
            .select("tr")
            .mapNotNull {
                it.select("td").firstOrNull()?.text()
            }
    }

    private fun <T> log(something: T): T {
        println("--- $something")
        return something
    }
}