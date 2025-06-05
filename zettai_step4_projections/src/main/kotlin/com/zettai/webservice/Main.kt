package com.zettai.webservice

import com.zettai.commands.ToDoListCommandHandler
import com.zettai.domain.*
import com.zettai.events.ToDoListEventStore
import com.zettai.events.ToDoListEventStreamerInMemory
import com.zettai.queries.ToDoListQueryRunner
import org.http4k.server.Jetty
import org.http4k.server.asServer

fun main() {
    val streamer = ToDoListEventStreamerInMemory()
    val eventStore = ToDoListEventStore(streamer)

    val commandHandler = ToDoListCommandHandler(eventStore)
    val queryHandler = ToDoListQueryRunner(streamer::fetchAfter)

    val hub = ToDoListHub(queryHandler, commandHandler, eventStore)

    Zettai(hub).asServer(Jetty(8080)).start()

    println("Server started at http://localhost:8080/todo/ingduk2")
}