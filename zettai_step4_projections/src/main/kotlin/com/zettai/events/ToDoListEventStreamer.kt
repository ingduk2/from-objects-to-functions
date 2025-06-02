package com.zettai.events

import com.zettai.domain.ListName
import com.zettai.domain.User
import com.zettai.fp.EntityEvent
import com.zettai.fp.EventStreamer
import java.util.concurrent.atomic.AtomicReference

interface ToDoListEventStreamer : EventStreamer<ToDoListEvent> {
    fun retrieveIdFromName(user: User, listName: ListName): ToDoListId?
    fun store(newEvents: Iterable<ToDoListEvent>): List<ToDoListEvent>
}

data class EventSeq(val progressive: Int) {
    operator fun compareTo(other: EventSeq): Int = progressive.compareTo(other.progressive)
}

data class StoredEvent<E: EntityEvent>(val eventSeq: EventSeq, val event: E)

class ToDoListEventStreamerInMemory : ToDoListEventStreamer {
    val events = AtomicReference<List<ToDoListEvent>>(emptyList())

    override fun retrieveIdFromName(user: User, listName: ListName): ToDoListId? =
        events.get()
            .firstOrNull { it == ListCreated(it.id, user, listName)}
            ?.id

    override fun store(newEvents: Iterable<ToDoListEvent>): List<ToDoListEvent> =
        newEvents.toList().also { new -> events.updateAndGet {it + new} }

    override fun invoke(id: ToDoListId): List<ToDoListEvent> =
        events.get()
            .filter { it.id == id }
}