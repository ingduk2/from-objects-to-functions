package com.zettai.domain

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class ToDoListHubTest {

    @Test
    fun `get list by user and name`() {
        val frank = User("Frank")
        val list = createList("shopping", listOf("carrots", "apples", "milk"))
        val listMap = mapOf(frank to listOf(list))

        val hub = ToDoListHub(listMap)
        val myList = hub.getList(frank, list.listName)

        expectThat(myList).isEqualTo(list)
    }

    private fun createList(listName: String, items: List<String>) =
        ToDoList(ListName(listName), items.map(::ToDoItem))
}