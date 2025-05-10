package com.zettai.commands

import com.zettai.domain.ToDoList
import com.zettai.domain.ToDoListRetriever
import com.zettai.domain.ToDoListUpdatableFetcher
import com.zettai.events.*

typealias CommandHandler<CMD, EVENT> = (CMD) -> List<EVENT>?

class ToDoListCommandHandler(
    private val entityRetriever: ToDoListRetriever,
    private val readModel: ToDoListUpdatableFetcher
) : CommandHandler<ToDoListCommand, ToDoListEvent> {
    override fun invoke(command: ToDoListCommand): List<ToDoListEvent>? =
        when (command) {
            is CreateToDoList -> command.execute()
            is AddToDoItem-> command.execute()
        }

    private fun CreateToDoList.execute(): List<ToDoListEvent>? =
        entityRetriever.retrieveByName(user, name)
            ?.let { listState ->
                when (listState) {
                    InitialState -> {
                        readModel.assignListToUser(user, ToDoList(name, emptyList()))
                        ListCreated(id, user, name).toList()
                    }
                    else -> null
                }
            }

    private fun AddToDoItem.execute(): List<ToDoListEvent>? =
        entityRetriever.retrieveByName(user, name)
            ?.let { listState ->
                when (listState) {
                    is ActiveToDoList -> {
                        if (listState.items.any { it.description == item.description })
                            null
                        else {
                            readModel.addItemToList(user, listState.name, item)
                            ItemAdded(listState.id, item).toList()
                        }
                    }

                    InitialState,
                    is OnHoldToDoList,
                    is ClosedToDoList -> null
                }
            }

    private fun ToDoListEvent.toList(): List<ToDoListEvent> = listOf(this)
}
