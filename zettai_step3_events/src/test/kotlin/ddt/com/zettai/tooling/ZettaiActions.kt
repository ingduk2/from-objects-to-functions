package ddt.com.zettai.tooling

import com.ubertob.pesticide.core.DdtActions
import com.ubertob.pesticide.core.DdtProtocol
import com.ubertob.pesticide.core.DomainDrivenTest
import com.zettai.domain.ListName
import com.zettai.domain.ToDoItem
import com.zettai.domain.ToDoList
import com.zettai.domain.User

interface ZettaiActions : DdtActions<DdtProtocol> {
    fun ToDoListOwner.`starts with a list`(listName: String, items: List<String>)

    fun getToDoList(user: User, listName: ListName): ToDoList?

    fun addListItem(user: User, listName: ListName, toDoItem: ToDoItem)
}

typealias ZettaiDDT = DomainDrivenTest<ZettaiActions>

fun allActions() = setOf(
    DomainOnlyActions(),
    HttpActions(),
)