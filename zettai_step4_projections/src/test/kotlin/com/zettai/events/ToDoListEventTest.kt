package com.zettai.events

import com.zettai.domain.randomItem
import com.zettai.domain.randomListName
import com.zettai.domain.randomUser
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

internal class ToDoListEventTest {
    private val id = ToDoListId.mint()
    private val name = randomListName()
    private val user = randomUser()
    private val item1 = randomItem()
    private val item2 = randomItem()
    private val item3 = randomItem()

    @Test
    fun `the first event create a list`() {
        val events = listOf(ListCreated(id, user, name))
        val list = events.fold()

        expectThat(list).isEqualTo(ActiveToDoList(id, user, name, emptyList()))
    }

    @Test
    fun `adding and removing items to active list`() {
        val events: List<ToDoListEvent> = listOf(
            ListCreated(id, user, name),
            ItemAdded(id, item1),
            ItemAdded(id, item2),
            ItemAdded(id, item3),
            ItemRemoved(id, item2)
        )

        val list = events.fold()

        expectThat(list).isEqualTo(ActiveToDoList(id, user, name, listOf(item1, item3)))
    }

    @Test
    fun `putting the list on hold`() {
        val reason = "not urgent anymore"
        val events: List<ToDoListEvent> = listOf(
            ListCreated(id, user, name),
            ItemAdded(id, item1),
            ItemAdded(id, item2),
            ItemAdded(id, item3),
            ListPutOnHold(id, reason)
        )

        val list = events.fold()
        expectThat(list).isEqualTo(OnHoldToDoList(id, user, name, listOf(item1, item2, item3), reason))
    }
}