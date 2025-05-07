package com.zettai.domain

interface ZettaiHub {
    fun getList(user: User, listName: ListName): ToDoList?
    fun addItemToList(user: User, listName: ListName, item: ToDoItem): ToDoList?
    fun getLists(user: User): List<ListName>?
}

class ToDoListHub(
    val fetcher: ToDoListUpdatableFetcher
) : ZettaiHub {
    override fun getList(user: User, listName: ListName): ToDoList? {
        return fetcher(user, listName)
    }

    override fun addItemToList(user: User, listName: ListName, item: ToDoItem): ToDoList? =
        fetcher(user, listName)?.run {
            // 기존 item 제거 후 갱신
            val newList = copy(items = items.filterNot { it.description == item.description } + item)
            fetcher.assignListToUser(user, newList)
        }

    override fun getLists(user: User): List<ListName>? =
        fetcher.getAll(user)
}