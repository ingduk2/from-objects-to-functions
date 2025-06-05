package ddt.com.zettai.tooling

import com.ubertob.pesticide.core.DdtActor
import com.zettai.domain.ListName
import com.zettai.domain.ToDoItem
import com.zettai.domain.ToDoList
import com.zettai.domain.User
import com.zettai.domain.tooling.expectSuccess
import strikt.api.Assertion
import strikt.api.expectThat
import strikt.assertions.*

data class ToDoListOwner(
    override val name: String
) : DdtActor<ZettaiActions>() {

    val user = User(name)

    fun `can see #listname with #itemnames`(
        listName: String,
        expectedItems: List<String>,
    ) =
        step(listName, expectedItems) {
            val list = getToDoList(user, ListName.fromUntrustedOrThrow(listName)).expectSuccess()
            expectThat(list)
                .itemNames
                .containsExactlyInAnyOrder(expectedItems)
        }

    fun `cannot see #listname`(listName: String) =
        step(listName) {
            val lists = allUserLists(user).expectSuccess()
            expectThat(lists.map { it.name }).doesNotContain(listName)
        }

    fun `can add #item to #listname`(itemName: String, listName: String) =
        step(itemName, listName) {
            val toDoItem = ToDoItem(itemName)
            addListItem(user, ListName(listName), toDoItem)
        }

    fun `cannot see any list`() =
        step {
            val lists = allUserLists(user).expectSuccess()
            expectThat(lists).isEmpty()
        }

    fun `can see the lists #listNames`(expectedLists: Set<String>) =
        step {
            val lists = allUserLists(user).expectSuccess()
            expectThat(lists)
                .map(ListName::name)
                .containsExactly(expectedLists)
        }

    fun `can create a new list called #listname`(listName: String) =
        step(listName) {
            createList(user, ListName.fromUntrustedOrThrow(listName))
        }
}

private val Assertion.Builder<ToDoList>.itemNames
    get() = get { items.map { it.description } }