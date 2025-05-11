package com.zettai.events

import com.zettai.domain.ListName
import com.zettai.domain.ToDoItem
import com.zettai.domain.User
import com.zettai.fp.EntityEvent
import com.zettai.fp.EntityId
import com.zettai.fp.EntityState
import java.time.Instant

typealias ToDoListId = EntityId

sealed class ToDoListEvent : EntityEvent
data class ListCreated(override val id: ToDoListId, val owner: User, val name: ListName) : ToDoListEvent()
data class ItemAdded(override val id: ToDoListId, val item: ToDoItem) : ToDoListEvent()
data class ItemRemoved(override val id: ToDoListId, val item: ToDoItem) : ToDoListEvent()
data class ItemModified(override val id: ToDoListId, val prevItem: ToDoItem, val item: ToDoItem) : ToDoListEvent()
data class ListPutOnHold(override val id: ToDoListId, val reason: String) : ToDoListEvent()
data class ListReleased(override val id: ToDoListId) : ToDoListEvent()
data class ListClosed(override val id: ToDoListId, val closedOn: Instant) : ToDoListEvent()

sealed class ToDoListState : EntityState<ToDoListEvent> {
    abstract override fun combine(event: ToDoListEvent): ToDoListState
}

fun Iterable<ToDoListEvent>.fold(): ToDoListState =
    fold(InitialState as ToDoListState) { acc, e -> acc.combine(e) }

data object InitialState : ToDoListState() {
    override fun combine(event: ToDoListEvent) =
        when (event) {
            is ListCreated -> create(event.id, event.owner, event.name, emptyList())
            else -> this // 다른 이벤트는 무시
        }
}

data class ActiveToDoList internal constructor(
    val id: ToDoListId,
    val owner: User,
    val name: ListName,
    val items: List<ToDoItem>
) : ToDoListState() {
    override fun combine(event: ToDoListEvent): ToDoListState =
        when (event) {
            is ItemAdded -> copy(items = items + event.item)
            is ItemRemoved -> copy(items = items - event.item)
            is ItemModified -> copy(items = items - event.prevItem + event.item)
            is ListPutOnHold -> onHold(event.reason)
            is ListClosed -> close(event.closedOn)
            else -> this
        }
}

data class OnHoldToDoList internal constructor(
    val id: ToDoListId,
    val owner: User,
    val name: ListName,
    val items: List<ToDoItem>,
    val reason: String
) : ToDoListState() {
    override fun combine(event: ToDoListEvent): ToDoListState =
        when (event) {
            is ListReleased -> release()
            else -> this
        }
}

data class ClosedToDoList internal constructor(
    val id: ToDoListId,
    val closedOn: Instant
) : ToDoListState() {
    override fun combine(event: ToDoListEvent): ToDoListState =
        this
}

fun InitialState.create(id: ToDoListId, owner: User, name: ListName, items: List<ToDoItem>) = ActiveToDoList(id, owner, name, items)
fun ActiveToDoList.onHold(reason: String) = OnHoldToDoList(id, owner, name, items, reason)
fun OnHoldToDoList.release() = ActiveToDoList(id, owner, name, items)
fun ActiveToDoList.close(closedOn: Instant) = ClosedToDoList(id, closedOn)