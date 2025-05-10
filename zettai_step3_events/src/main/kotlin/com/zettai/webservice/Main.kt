package com.zettai.webservice

import com.zettai.commands.AddToDoItem
import com.zettai.commands.CreateToDoList
import com.zettai.commands.ToDoListCommandHandler
import com.zettai.domain.*
import com.zettai.events.ToDoListEventStore
import com.zettai.events.ToDoListEventStreamerInMemory
import org.http4k.server.Jetty
import org.http4k.server.asServer
import java.time.LocalDate

fun main() {
    val fetcher = ToDoListFetcherFromMap(mutableMapOf())
    val streamer = ToDoListEventStreamerInMemory()
    val eventStore = ToDoListEventStore(streamer)

    val commandHandler = ToDoListCommandHandler(eventStore, fetcher)

    val hub = ToDoListHub(fetcher, commandHandler, eventStore)


    Zettai(hub).asServer(Jetty(8080)).start()

    println("Server started at http://localhost:8080/todo/ingduk2/book")
}

private fun ToDoListHub.withExampleToDoList(): ToDoListHub =
    also { handle(CreateToDoList(User("uberto"), ListName("book"))) }

private fun ToDoListHub.withExampleItems(): ToDoListHub =
    also { exampleItems.forEach { handle(it) } }

private val exampleItems = sequence {
    val user = User("uberto")
    val listName = ListName("book")
    yieldAll(
        listOf(
            AddToDoItem(user, listName, ToDoItem("prepare the diagram", tomorrow(), ToDoStatus.Done)),
            AddToDoItem(user, listName, ToDoItem("rewrite explanations", dayAfterTomorrow(), ToDoStatus.InProgress)),
            AddToDoItem(user, listName, ToDoItem("finish the chapter")),
            AddToDoItem(user, listName, ToDoItem("draft next chapter")),
        ),
    )
}

private fun tomorrow(): LocalDate = LocalDate.now().plusDays(1)
private fun dayAfterTomorrow(): LocalDate = LocalDate.now().plusDays(2)