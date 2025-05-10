package ddt.com.zettai.tooling

import com.ubertob.pesticide.core.DdtProtocol
import com.ubertob.pesticide.core.DomainOnly
import com.ubertob.pesticide.core.Ready
import com.zettai.commands.AddToDoItem
import com.zettai.commands.CreateToDoList
import com.zettai.domain.*
import strikt.api.expectThat
import strikt.assertions.hasSize

class DomainOnlyActions : ZettaiActions {
    override val protocol: DdtProtocol = DomainOnly
    override fun prepare() = Ready

    private val store: ToDoListStore = mutableMapOf()
    private val fetcher = ToDoListFetcherFromMap(store)
    private val hub = prepareToDoListHubForTests(fetcher)

    override fun ToDoListOwner.`starts with a list`(listName: String, items: List<String>) {
        val list = ListName.fromTrusted(listName)
        val events = hub.handle(
            CreateToDoList(
                user,
                list
            )
        )
        events ?: throw RuntimeException("Failed to create list $listName")
        val created = items.mapNotNull {
            hub.handle(
                AddToDoItem(
                    user,
                    list,
                    ToDoItem(it)
                )
            )
        }
        expectThat(created).hasSize(items.size)
    }

    override fun getToDoList(user: User, listName: ListName): ToDoList? =
        hub.getList(user, listName)

    override fun addListItem(user: User, listName: ListName, toDoItem: ToDoItem) {
        hub.handle(AddToDoItem(user, listName, toDoItem))
    }

    override fun allUserLists(user: User): List<ListName> =
        hub.getLists(user) ?: emptyList()

    override fun createList(user: User, listName: ListName) {
        hub.handle(CreateToDoList(user, listName))
    }
}