package com.zettai.domain

import com.zettai.events.ToDoListState

interface ToDoListRetriever {
    fun retrieveByName(user: User, listName: ListName): ToDoListState?
}