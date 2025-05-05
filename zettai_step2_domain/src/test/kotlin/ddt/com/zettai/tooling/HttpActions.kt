package ddt.com.zettai.tooling

import com.ubertob.pesticide.core.*
import com.zettai.domain.*
import com.zettai.ui.HtmlPage
import com.zettai.webservice.Zettai
import org.http4k.client.JettyClient
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class HttpActions(env: String = "local") : ZettaiActions {
    private val lists: MutableMap<User, MutableList<ToDoList>> = mutableMapOf()
    private val hub = ToDoListHub(lists)

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
        val user = User(name)
        val newTodoList = ToDoList(
            ListName(listName),
            items.map(::ToDoItem)
        )

        val userTodoLists = lists.getOrPut(user) { mutableListOf() }
        userTodoLists.add(newTodoList)
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

    private fun callZettai(method: Method, path: String): Response =
        client(log(Request(method, "http://localhost:$zettaiPort/$path")))

    private fun todoListUrl(user: User, listName: ListName) =
        "todo/${user.name}/${listName.name}"

    private fun HtmlPage.parse(): Document = Jsoup.parse(raw)

    private fun extractItemsFromPage(html: HtmlPage): List<ToDoItem> =
        html.parse()
            .select("tr")
            .toList()
            .filter { it.select("td").size == 1 }
            .map {
                it.select("td")[0].text().orEmpty()
            }
            .map {
                ToDoItem(it)
            }

    private fun <T> log(something: T): T {
        println("--- $something")
        return something
    }
}