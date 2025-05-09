package com.zettai.events

import com.zettai.domain.ListName
import com.zettai.domain.ToDoListRetriever
import com.zettai.domain.User
import com.zettai.fp.EventPersister

class ToDoListEventStore(
    val eventStreamer: ToDoListEventStreamer
) : ToDoListRetriever, EventPersister<ToDoListEvent> {

    private fun retrieveById(id: ToDoListId): ToDoListState? =
        eventStreamer(id)
            ?.fold()

    override fun retrieveByName(user: User, listName: ListName): ToDoListState? =
        eventStreamer.retrieveIdFromName(user, listName)
            ?.let(::retrieveById)
            ?: InitialState

    override fun invoke(events: Iterable<ToDoListEvent>): List<ToDoListEvent> =
        eventStreamer.store(events)
}