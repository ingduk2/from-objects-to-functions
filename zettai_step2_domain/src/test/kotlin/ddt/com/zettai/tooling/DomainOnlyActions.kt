package ddt.com.zettai.tooling

import com.ubertob.pesticide.core.DdtProtocol
import com.ubertob.pesticide.core.DomainOnly
import com.ubertob.pesticide.core.Ready
import com.zettai.domain.ListName
import com.zettai.domain.ToDoList
import com.zettai.domain.ToDoItem
import com.zettai.domain.ToDoListHub
import com.zettai.domain.User

class DomainOnlyActions : ZettaiActions {
    override val protocol: DdtProtocol = DomainOnly
    override fun prepare() = Ready

    private val lists: MutableMap<User, MutableList<ToDoList>> = mutableMapOf()
    private val hub = ToDoListHub(lists)

    override fun ToDoListOwner.`starts with a list`(listName: String, items: List<String>) {
        val user = User(name)
        val newTodoList = ToDoList(
            ListName(listName),
            items.map(::ToDoItem)
        )

//        val userTodoLists = lists[user]
//        if (userTodoLists == null) {
//            lists[user] = mutableListOf(newTodoList)
//        } else {
//            userTodoLists.add(newTodoList)
//        }

        val userTodoLists = lists.getOrPut(user) { mutableListOf() }
        userTodoLists.add(newTodoList)
    }

    override fun getToDoList(user: User, listName: ListName): ToDoList? =
        hub.getList(user, listName)
}