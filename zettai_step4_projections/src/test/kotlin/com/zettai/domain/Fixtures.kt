package com.zettai.domain

import com.zettai.commands.ToDoListCommandHandler
import com.zettai.events.ToDoListEventStore
import com.zettai.events.ToDoListEventStreamerInMemory
import com.zettai.queries.ToDoListQueryRunner
import com.zettai.webservice.Zettai

fun prepareToDoListHubForTests(): ToDoListHub {
    val streamer = ToDoListEventStreamerInMemory()
    val eventStore = ToDoListEventStore(streamer)
    val cmdHandler = ToDoListCommandHandler(eventStore)
    val queryRunner = ToDoListQueryRunner(streamer::fetchAfter)
    return ToDoListHub(queryRunner, cmdHandler, eventStore)
}

fun prepareZettaiForTests(): Zettai {
    return Zettai(
        prepareToDoListHubForTests()
    )
}