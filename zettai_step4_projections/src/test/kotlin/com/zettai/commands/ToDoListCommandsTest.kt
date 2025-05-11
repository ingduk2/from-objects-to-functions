package com.zettai.commands

import com.zettai.domain.*
import com.zettai.events.*
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import strikt.assertions.isNull

class ToDoListCommandsTest {

    val noopFetcher = object : ToDoListUpdatableFetcher {
        override fun assignListToUser(user: User, list: ToDoList): ToDoList? = null //do nothing
        override fun get(user: User, listName: ListName): ToDoList? = TODO("not implemented")
        override fun getAll(user: User): List<ListName>? = TODO("not implemented")
    }

    private val streamer = ToDoListEventStreamerInMemory()
    private val eventStore = ToDoListEventStore(streamer)
    private val handler = ToDoListCommandHandler(eventStore, noopFetcher)
    private fun handle(cmd: ToDoListCommand): List<ToDoListEvent>? =
        handler(cmd)?.let(eventStore)

    @Test
    fun `CreateToDoList generate the correct event`() {
        val cmd = CreateToDoList(randomUser(), randomListName())

        val entityRetriever: ToDoListRetriever = object : ToDoListRetriever {
            override fun retrieveByName(user: User, listName: ListName) = InitialState
        }
        val handler = ToDoListCommandHandler(entityRetriever, noopFetcher)
        val res = handler(cmd)?.single()

        expectThat(res).isEqualTo(ListCreated(cmd.id, cmd.user, cmd.name))
    }

    @Test
    fun `Add list fails if the user has already a list with same name`() {
        val cmd = CreateToDoList(randomUser(), randomListName())
        val res = handle(cmd)?.single()

        expectThat(res).isA<ListCreated>()

        val duplicatedRes = handle(cmd)
        expectThat(duplicatedRes).isNull()
    }
}