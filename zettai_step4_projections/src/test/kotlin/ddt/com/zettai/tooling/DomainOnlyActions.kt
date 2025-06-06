package ddt.com.zettai.tooling

import com.ubertob.pesticide.core.DdtProtocol
import com.ubertob.pesticide.core.DomainOnly
import com.ubertob.pesticide.core.Ready
import com.zettai.commands.AddToDoItem
import com.zettai.commands.CreateToDoList
import com.zettai.domain.*
import com.zettai.domain.tooling.expectSuccess
import strikt.api.expectThat
import strikt.assertions.hasSize

class DomainOnlyActions : ZettaiActions {
    override val protocol: DdtProtocol = DomainOnly
    override fun prepare() = Ready

    private val hub = prepareToDoListHubForTests()

    override fun ToDoListOwner.`starts with a list`(listName: String, items: List<String>) {
        val list = ListName.fromTrusted(listName)
        hub.handle(
            CreateToDoList(
                user,
                list
            )
        ).expectSuccess()

        val events = items.map {
            hub.handle(
                AddToDoItem(user, list, ToDoItem(it))
            ).expectSuccess()
        }

        expectThat(events).hasSize(items.size)
    }

    override fun getToDoList(user: User, listName: ListName): ZettaiOutcome<ToDoList> =
        hub.getList(user, listName)

    override fun addListItem(user: User, listName: ListName, toDoItem: ToDoItem) {
        hub.handle(AddToDoItem(user, listName, toDoItem))
    }

    override fun allUserLists(user: User): ZettaiOutcome<List<ListName>> =
        hub.getLists(user)

    override fun createList(user: User, listName: ListName) {
        hub.handle(CreateToDoList(user, listName))
    }
}