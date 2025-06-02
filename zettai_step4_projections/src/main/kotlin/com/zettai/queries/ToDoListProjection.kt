package com.zettai.queries

import com.zettai.domain.ListName
import com.zettai.domain.ToDoItem
import com.zettai.domain.ToDoList
import com.zettai.domain.User
import com.zettai.events.ItemAdded
import com.zettai.events.ItemModified
import com.zettai.events.ItemRemoved
import com.zettai.events.ListClosed
import com.zettai.events.ListCreated
import com.zettai.events.ListPutOnHold
import com.zettai.events.ListReleased
import com.zettai.events.ToDoListEvent
import com.zettai.fp.ConcurrentMapProjection
import com.zettai.fp.CreateRow
import com.zettai.fp.DeleteRow
import com.zettai.fp.DeltaRow
import com.zettai.fp.FetchStoredEvents
import com.zettai.fp.InMemoryProjection
import com.zettai.fp.RowId
import com.zettai.fp.UpdateRow
import com.zettai.fp.toSingle

data class ToDoListProjectionRow(
    val user: User,
    val active: Boolean,
    val list: ToDoList
) {
    fun addItem(item: ToDoItem): ToDoListProjectionRow =
        copy(list = list.copy(items = list.items + item))

    fun removeItem(item: ToDoItem): ToDoListProjectionRow =
        copy(list = list.copy(items = list.items - item))

    fun replateItem(prevItem: ToDoItem, item: ToDoItem): ToDoListProjectionRow =
        copy(list = list.copy(items = list.items - prevItem + item))

    fun putOnHold(): ToDoListProjectionRow =
        copy(active = false)

    fun release(): ToDoListProjectionRow =
        copy(active = true)
}

class ToDoListProjection(eventFetcher: FetchStoredEvents<ToDoListEvent>) :
    InMemoryProjection<ToDoListProjectionRow, ToDoListEvent> by ConcurrentMapProjection(
        eventFetcher,
        ::eventProjector
    ) {

    fun findAll(user: User): List<ListName>? =
        allRows().values
            .filter { it.user == user }
            .map { it.list.listName }

    fun findList(user: User, name: ListName): ToDoList? =
        allRows().values
            .firstOrNull { it.user == user && it.list.listName == name }
            ?.list

    companion object {
        fun eventProjector(e: ToDoListEvent): List<DeltaRow<ToDoListProjectionRow>> =
            when (e) {
                is ListCreated -> CreateRow(
                    e.rowId(),
                    ToDoListProjectionRow(e.owner, true, ToDoList(e.name, emptyList()))
                )

                is ItemAdded -> UpdateRow(e.rowId()) { addItem(e.item) }
                is ItemRemoved -> UpdateRow(e.rowId()) { removeItem(e.item) }
                is ItemModified -> UpdateRow(e.rowId()) { replateItem(e.prevItem, e.item) }
                is ListPutOnHold -> UpdateRow(e.rowId()) { putOnHold() }
                is ListReleased -> UpdateRow(e.rowId()) { release() }
                is ListClosed -> DeleteRow(e.rowId())
            }.toSingle()
    }
}

private fun ToDoListEvent.rowId(): RowId = RowId(id.toString())