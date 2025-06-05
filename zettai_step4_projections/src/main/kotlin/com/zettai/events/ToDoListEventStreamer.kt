package com.zettai.events

import com.zettai.domain.ListName
import com.zettai.domain.User
import com.zettai.fp.EntityEvent
import com.zettai.fp.EventStreamer
import java.util.concurrent.atomic.AtomicReference

interface ToDoListEventStreamer : EventStreamer<ToDoListEvent> {
    fun retrieveIdFromName(user: User, listName: ListName): ToDoListId?
    fun store(newEvents: Iterable<ToDoListEvent>): List<ToDoListEvent>
    fun fetchAfter(startEvent: EventSeq): Sequence<StoredEvent<ToDoListEvent>>
}

data class EventSeq(val progressive: Int) {
    operator fun compareTo(other: EventSeq): Int = progressive.compareTo(other.progressive)
}

data class StoredEvent<E: EntityEvent>(val eventSeq: EventSeq, val event: E)

typealias ToDoListStoredEvent = StoredEvent<ToDoListEvent>

class ToDoListEventStreamerInMemory : ToDoListEventStreamer {
    val events = AtomicReference<List<ToDoListStoredEvent>>(listOf())

    override fun retrieveIdFromName(user: User, listName: ListName): ToDoListId? =
        events.get()
            .map(ToDoListStoredEvent::event)
            .firstOrNull { it == ListCreated(it.id, user, listName)}
            ?.id

    override fun store(newEvents: Iterable<ToDoListEvent>): List<ToDoListEvent> =
        newEvents.toList().also { ne ->
            events.updateAndGet {
                it + ne.toSavedEvents(it.size)
            }
        }
    override fun fetchAfter(startEvent: EventSeq): Sequence<ToDoListStoredEvent> =
        events.get()
            .asSequence()
            .dropWhile { it.eventSeq <= startEvent }

    override fun invoke(id: ToDoListId): List<ToDoListEvent> =
        events.get()
            .map(ToDoListStoredEvent::event)
            .filter { it.id == id }

    private fun Iterable<ToDoListEvent>.toSavedEvents(last: Int): List<ToDoListStoredEvent> =
        mapIndexed { index, event ->
            ToDoListStoredEvent(EventSeq(last + index), event)
        }
}