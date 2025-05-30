package ddt.com.zettai.tooling

import com.ubertob.pesticide.core.*
import com.zettai.commands.AddToDoItem
import com.zettai.commands.CreateToDoList
import com.zettai.domain.*
import com.zettai.ui.HtmlPage
import com.zettai.ui.toIsoLocalDate
import com.zettai.ui.toStatus
import com.zettai.webservice.Zettai
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
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo
import kotlin.test.fail

class HttpActions(env: String = "local") : ZettaiActions {
    private val store: ToDoListStore = mutableMapOf()
    private val fetcher = ToDoListFetcherFromMap(store)
    private val hub = prepareToDoListHubForTests(fetcher)

    private val zettaiPort = 8000
    private val server = Zettai(hub).asServer(Jetty(zettaiPort))
    val client = JettyClient()

    override val protocol: DdtProtocol = Http(env)

    override fun prepare(): DomainSetUp {
        server.start()
        return Ready
    }

    override fun tearDown(): HttpActions =
        also { server.stop() }

    override fun ToDoListOwner.`starts with a list`(listName: String, items: List<String>) {
        val list = ListName.fromTrusted(listName)
        val events = hub.handle(
            CreateToDoList(user, list)
        )
        events ?: fail("Failed to create list $listName for $name")
        val created = items.mapNotNull {
            hub.handle(AddToDoItem(user, list, ToDoItem(it)))
        }
        expectThat(created).hasSize(items.size)
    }

    override fun getToDoList(user: User, listName: ListName): ToDoList? {
        val response = callZettai(Method.GET, todoListUrl(user, listName))

        if (response.status == Status.NOT_FOUND)
            return null

        expectThat(response.status).isEqualTo(Status.OK)

        val html = HtmlPage(response.bodyString())

        val items = extractItemsFromPage(html)

        return ToDoList(listName, items)
    }

    override fun addListItem(user: User, listName: ListName, toDoItem: ToDoItem) {
        val response = submitToZettai(
            todoListUrl(user, listName),
            listOf("itemname" to toDoItem.description,
                "itemdue" to toDoItem.dueDate?.toString())
        )

        expectThat(response.status).isEqualTo(Status.SEE_OTHER)
    }

    override fun allUserLists(user: User): List<ListName> {
        val response = callZettai(Method.GET, allUserListsUrl(user))
        expectThat(response.status).isEqualTo(Status.OK)
        val html = HtmlPage(response.bodyString())
        val names = extractListNamesFromPage(html)
        return names.map { name -> ListName.fromTrusted(name) }
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