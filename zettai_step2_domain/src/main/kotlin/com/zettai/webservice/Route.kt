package com.zettai.webservice

import com.zettai.domain.ListName
import com.zettai.domain.ToDoList
import com.zettai.domain.ToDoItem
import com.zettai.domain.User
import com.zettai.domain.ZettaiHub
import com.zettai.ui.HtmlPage
import com.zettai.ui.renderHtml
import org.http4k.core.*
import org.http4k.core.body.form
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes

class Zettai(val hub: ZettaiHub) : HttpHandler {

    val routes = routes(
        "/todo/{user}/{listname}" bind Method.GET to ::getToDoList,
        "/todo/{user}/{listname}" bind Method.POST to ::addNewItem,
    )

    override fun invoke(req: Request): Response {
        return routes(req)
    }

    private fun getToDoList(request: Request): Response =
        request.let(::extractListData)
            .let(::fetchListContent)
            ?.let(::renderHtml)
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
        val user = request.path("user")
            ?.let(::User)
            ?: return Response(Status.BAD_REQUEST)
        val listName = request.path("listname")
            ?.let(::ListName)
            ?: return Response(Status.BAD_REQUEST)
        val item = request.form("itemname")
            ?.let { ToDoItem(it) }
            ?: return Response(Status.BAD_REQUEST)
        return hub.addItemToList(user, listName, item)
            ?.let {
                Response(Status.SEE_OTHER)
                    .header("Location", "/todo/${user.name}/${listName.name}")
            }
            ?: Response(Status.NOT_FOUND)
    }
}