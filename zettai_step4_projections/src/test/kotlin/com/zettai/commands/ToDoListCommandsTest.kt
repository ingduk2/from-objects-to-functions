package com.zettai.commands

import com.zettai.domain.*
import com.zettai.domain.tooling.expectFailure
import com.zettai.domain.tooling.expectSuccess
import com.zettai.events.*
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import strikt.assertions.single

class ToDoListCommandsTest {

    private val streamer = ToDoListEventStreamerInMemory()
    private val eventStore = ToDoListEventStore(streamer)
    private val handler = ToDoListCommandHandler(eventStore)

    @Test
    fun `CreateToDoList generate the correct event`() {
        val cmd = CreateToDoList(randomUser(), randomListName())

        val entityRetriever: ToDoListRetriever = object : ToDoListRetriever {
            override fun retrieveByName(user: User, listName: ListName) = InitialState
        }
        val handler = ToDoListCommandHandler(entityRetriever)
        val res = handler(cmd).expectSuccess().single()

        expectThat(res).isEqualTo(ListCreated(cmd.id, cmd.user, cmd.name))
    }

    @Test
    fun `Add list fails if the user has already a list with same name`() {
        val cmd = CreateToDoList(randomUser(), randomListName())
        val res = handler(cmd).expectSuccess()

        expectThat(res).single().isA<ListCreated>()
        eventStore(res)

        val duplicatedRes = handler(cmd).expectFailure()
        expectThat(duplicatedRes).isA<InconsistentStateError>()
    }
}