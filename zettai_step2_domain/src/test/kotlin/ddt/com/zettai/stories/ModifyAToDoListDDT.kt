package ddt.com.zettai.stories

import com.ubertob.pesticide.core.DDT
import ddt.com.zettai.tooling.ZettaiDDT
import ddt.com.zettai.tooling.allActions
import ddt.com.zettai.tooling.ToDoListOwner

class ModifyAToDoListDDT : ZettaiDDT(allActions()) {

    private val ann by NamedActor(::ToDoListOwner)

    @DDT
    fun `the list owner can add new items`() = ddtScenario {
        setUp {
            ann.`starts with a list`("diy", emptyList())
        }.thenPlay(
            ann.`can add #item to #listname`("paint the shelf", "diy"),
            ann.`can add #item to #listname`("fix the gate", "diy"),
            ann.`can add #item to #listname`("change the lock", "diy"),
            ann.`can see #listname with #itemnames`(
                "diy", listOf("paint the shelf", "fix the gate", "change the lock")
            )
        )
    }
}