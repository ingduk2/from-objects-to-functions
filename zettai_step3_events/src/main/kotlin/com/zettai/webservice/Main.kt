package com.zettai.webservice

import com.zettai.domain.*
import org.http4k.server.Jetty
import org.http4k.server.asServer
import java.time.LocalDate

fun main() {
    val fetcher = ToDoListFetcherFromMap(storeWithExampleData())
    val hub = ToDoListHub(fetcher)

    Zettai(hub).asServer(Jetty(8080)).start()

    println("Server started at http://localhost:8080/todo/ingduk2/book")
}

fun storeWithExampleData(): ToDoListStore = mutableMapOf(
    User("ingduk2") to mutableMapOf(exampleToDoList().listName to exampleToDoList())
)

private fun exampleToDoList(): ToDoList {
    return ToDoList(
        listName = ListName.fromTrusted("book"),
        items = listOf(
            ToDoItem("prepare the diagram", LocalDate.now().plusDays(1), ToDoStatus.Done),
            ToDoItem("rewrite explanations", LocalDate.now().plusDays(2), ToDoStatus.InProgress),
            ToDoItem("finish the chapter"),
            ToDoItem("draft next chapter"),
        )
    )
}