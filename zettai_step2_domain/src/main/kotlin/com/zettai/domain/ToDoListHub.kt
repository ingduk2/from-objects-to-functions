package com.zettai.domain

interface ZettaiHub {
    fun getList(user: User, listName: ListName): ToDoList?
}

class ToDoListHub(
    val lists: Map<User, List<ToDoList>>
) : ZettaiHub {
    override fun getList(user: User, listName: ListName): ToDoList? {
        return lists[user]
            ?.firstOrNull { it.listName == listName }
    }
}