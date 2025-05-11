package com.zettai.commands

import com.zettai.domain.ListName
import com.zettai.domain.ToDoItem
import com.zettai.domain.User
import com.zettai.events.ToDoListId

sealed class ToDoListCommand

data class CreateToDoList(val user: User, val name: ListName) : ToDoListCommand() {
    val id: ToDoListId = ToDoListId.mint()
}

data class AddToDoItem(val user: User, val name: ListName, val item: ToDoItem) : ToDoListCommand()