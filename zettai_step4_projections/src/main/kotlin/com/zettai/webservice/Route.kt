package com.zettai.webservice

import com.zettai.commands.AddToDoItem
import com.zettai.commands.CreateToDoList
import com.zettai.domain.ListName
import com.zettai.domain.ToDoList
import com.zettai.domain.ToDoItem
import com.zettai.domain.User
import com.zettai.domain.ZettaiHub
import com.zettai.fp.tryOrNull
import com.zettai.ui.HtmlPage
import com.zettai.ui.renderListsPage
import com.zettai.ui.renderPage
import org.http4k.core.*
import org.http4k.core.body.form
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import java.time.LocalDate

class Zettai(val hub: ZettaiHub) : HttpHandler {

    val routes = routes(
        "/todo/{user}/{listname}" bind Method.GET to ::getToDoList,
        "/todo/{user}/{listname}" bind Method.POST to ::addNewItem,
        "/todo/{user}" bind Method.GET to ::getAllLists,
        "/todo/{user}" bind Method.POST to ::createNewList,
    )

    override fun invoke(req: Request): Response {
        return routes(req)
    }

    private fun getToDoList(request: Request): Response =
        request.let(::extractListData)
            .let(::fetchListContent)
            ?.let(::renderPage)
            ?.let(::createResponse)
            ?: Response(Status.NOT_FOUND)

    private fun extractListData(request: Request): Pair<User, ListName> {
        val user = request.path("user") ?: error("User missing")
        val list = request.path("listname") ?: error("ListName missing")
        return User(user) to ListName(list)
    }

    private fun fetchListContent(listId: Pair<User, ListName>): ToDoList? {
        return hub.getList(listId.first, listId.second)
    }

    private fun createResponse(html: HtmlPage): Response {
        return Response(Status.OK).body(html.raw)
    }

    private fun addNewItem(request: Request): Response {
        val user = request.extractUser()
        val listName = request.extractListName()
        return request.extractItem()
            ?.let { AddToDoItem(user, listName, it) }
            ?.let(hub::handle)
            ?.let { Response(Status.SEE_OTHER).header("Location", "/todo/${user.name}/${listName.name}") }
            ?: Response(Status.NOT_FOUND)
    }

    private fun getAllLists(request: Request): Response {
        val user = request.extractUser()

        return hub.getLists(user)
            ?.let { renderListsPage(user, it) }
            ?.let(::toResponse)
            ?: Response(Status.BAD_REQUEST)
    }

    private fun createNewList(request: Request): Response {
        val user = request.extractUser()
        val listName = request.extractListNameFromForm("listname")
        return listName
            ?.let { CreateToDoList(user, it) }
            ?.let(hub::handle)
            ?.let { Response(Status.SEE_OTHER)
                .header("Location", "/todo/${user.name}") }
            ?: Response(Status.BAD_REQUEST)
    }

    private fun toResponse(htmlPage: HtmlPage): Response =
        Response(Status.OK).body(htmlPage.raw)

    private fun Request.extractUser(): User = path("user").orEmpty().let(::User)

    private fun Request.extractListNameFromForm(formName: String) =
        form(formName)?.let(ListName.Companion::fromUntrusted)

    private fun Request.extractListName(): ListName =
        path("listname").orEmpty().let(ListName.Companion::fromUntrustedOrThrow)

    private fun Request.extractItem(): ToDoItem? {
        val name = form("itemname") ?: return null
        val duedate = tryOrNull { LocalDate.parse(form("itemdue")) }
        return ToDoItem(name, duedate)
    }
}
