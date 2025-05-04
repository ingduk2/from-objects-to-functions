package com.zettai.webservice

import com.zettai.domain.ListName
import com.zettai.domain.ToDoList
import com.zettai.domain.User
import com.zettai.ui.HtmlPage
import com.zettai.ui.renderHtml
import org.http4k.core.*
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes

class Zettai(val lists: Map<User, List<ToDoList>>) : HttpHandler {

    val routes = routes(
        "/todo/{user}/{list}" bind Method.GET to ::getToDoList,
    )

    override fun invoke(req: Request): Response {
        return routes(req)
    }

    private fun getToDoList(request: Request): Response =
        request.let(::extractListData)
            .let(::fetchListContent)
            .let(::renderHtml)
            .let(::createResponse)

    private fun extractListData(request: Request): Pair<User, ListName> {
        val user = request.path("user") ?: error("User missing")
        val list = request.path("list") ?: error("List missing")
        return User(user) to ListName(list)
    }

    private fun fetchListContent(listId: Pair<User, ListName>): ToDoList {
        return lists[listId.first]
            ?.firstOrNull { it.listName == listId.second }
            ?: error("List unknown")
    }

    private fun createResponse(html: HtmlPage): Response {
        return Response(Status.OK).body(html.raw)
    }
}