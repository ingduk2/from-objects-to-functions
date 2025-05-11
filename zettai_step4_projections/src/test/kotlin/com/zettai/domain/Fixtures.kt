package com.zettai.domain

import com.zettai.commands.ToDoListCommandHandler
import com.zettai.events.ToDoListEventStore
import com.zettai.events.ToDoListEventStreamerInMemory
import com.zettai.webservice.Zettai

fun prepareToDoListHubForTests(fetcher: ToDoListFetcherFromMap): ToDoListHub {
    val streamer = ToDoListEventStreamerInMemory()
    val eventStore = ToDoListEventStore(streamer)
    val cmdHandler = ToDoListCommandHandler(eventStore, fetcher)
    return ToDoListHub(fetcher, cmdHandler, eventStore)
}

fun prepareZettaiForTests(): Zettai {
    return Zettai(
        prepareToDoListHubForTests(ToDoListFetcherFromMap(mutableMapOf()))
    )
}