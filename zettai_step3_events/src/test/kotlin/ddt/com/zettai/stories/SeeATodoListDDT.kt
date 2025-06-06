package ddt.com.zettai.stories

import com.ubertob.pesticide.core.DDT
import ddt.com.zettai.tooling.ZettaiDDT
import ddt.com.zettai.tooling.allActions
import ddt.com.zettai.tooling.ToDoListOwner

class SeeATodoListDDT : ZettaiDDT(allActions()) {

    private val frank by NamedActor(::ToDoListOwner)
    private val bob by NamedActor(::ToDoListOwner)

    private val shoppingListName = "shopping"
    private val shoppingItems = listOf("carrots", "apples", "milk")

    private val gardenListName = "gardening"
    private val gardenItems = listOf("fix the fence", "mowing the lawn")

    @DDT
    fun `list owners can see their lists`() = ddtScenario {
        setUp {
            frank.`starts with a list`(shoppingListName, shoppingItems)
            bob.`starts with a list`(gardenListName, gardenItems)
        }.thenPlay(
            frank.`can see #listname with #itemnames`(
                shoppingListName, shoppingItems
            ),
            bob.`can see #listname with #itemnames`(
                gardenListName, gardenItems
            )
        )
    }

    @DDT
    fun `Only owners can see their lists`() = ddtScenario {
        setUp {
            frank.`starts with a list`(shoppingListName, shoppingItems)
            bob.`starts with a list`(gardenListName, gardenItems)
        }.thenPlay(
            frank.`cannot see #listname`(gardenListName),
            bob.`cannot see #listname`(shoppingListName)
        )
    }
}