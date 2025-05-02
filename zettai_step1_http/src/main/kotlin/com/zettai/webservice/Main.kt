package com.zettai.webservice

import com.zettai.domain.ListName
import com.zettai.domain.ToDoItem
import com.zettai.domain.ToDoList
import com.zettai.domain.User
import org.http4k.core.*
import org.http4k.server.Jetty
import org.http4k.server.asServer

fun main() {
    val items = listOf("write chapter", "insert code", "draw diagrams")
    val toDoList = ToDoList(
        ListName("book"),
        items.map(::ToDoItem)
    )
    val lists = mapOf(User("ingduk2") to listOf(toDoList))

    val app: HttpHandler = Zettai(lists)
    app.asServer(Jetty(8080)).start()

    println("Server started at http://localhost:8080/todo/ingduk2/book")
}