package com.zettai.commands

import com.zettai.domain.ToDoListRetriever
import com.zettai.events.InitialState
import com.zettai.events.ListCreated
import com.zettai.events.ToDoListEvent

typealias CommandHandler<CMD, EVENT> = (CMD) -> List<EVENT>?

class ToDoListCommandHandler(
    val entityRetriever: ToDoListRetriever
) : CommandHandler<ToDoListCommand, ToDoListEvent> {
    override fun invoke(command: ToDoListCommand): List<ToDoListEvent>? =
        when (command) {
            is CreateToDoList -> command.execute()
            else -> null
        }

    private fun CreateToDoList.execute(): List<ToDoListEvent>? =
        entityRetriever.retrieveByName(user, name)
            ?.let { listState ->
                when (listState) {
                    InitialState -> {
                        ListCreated(id, user, name).toList()
                    }
                    else -> null
                }
            }

    private fun ListCreated.toList(): List<ToDoListEvent> = listOf(this)
}
