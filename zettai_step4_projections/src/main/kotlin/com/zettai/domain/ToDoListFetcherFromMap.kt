package com.zettai.domain

import com.zettai.fp.asFailure
import com.zettai.fp.asSuccess

typealias ToDoListStore = MutableMap<User, MutableMap<ListName, ToDoList>>

interface ToDoListUpdatableFetcher : ToDoListFetcher {
    fun assignListToUser(user: User, list: ToDoList): ToDoList?

    fun addItemToList(user: User, listName: ListName, item: ToDoItem) {
        get(user, listName)
            .transform { list ->
                val newList = list.copy(items = list.items.filterNot { it.description == item.description } + item)
                newList
            }.transform { updated ->
                assignListToUser(user, updated)
            }
        }
}

data class ToDoListFetcherFromMap(
    private val store: ToDoListStore
) : ToDoListUpdatableFetcher {

    override fun get(user: User, listName: ListName): ZettaiOutcome<ToDoList> =
        store[user]?.get(listName)
            .orFailure { NotFoundError("$listName for $user not found") }

    override fun assignListToUser(user: User, list: ToDoList): ToDoList? =
        store.compute(user) { _, value ->
            val listMap = value ?: mutableMapOf()
            listMap.apply { put(list.listName, list) }
        }?.let { list }

    override fun getAll(user: User): ZettaiOutcome<List<ListName>> =
        (store[user]?.keys?.toList() ?: emptyList()).asSuccess()
}

inline fun <T> T?.orFailure(err: () -> ZettaiError): ZettaiOutcome<T> =
    this?.asSuccess() ?: err().asFailure()