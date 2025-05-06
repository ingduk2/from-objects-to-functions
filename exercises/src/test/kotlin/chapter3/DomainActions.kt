package chapter3

import com.ubertob.pesticide.core.DdtProtocol
import com.ubertob.pesticide.core.DomainOnly
import com.ubertob.pesticide.core.DomainSetUp
import com.ubertob.pesticide.core.Ready

val allActions = setOf(DomainActions)

object DomainActions : CashierActions {

    private val cashier = Cashier()

    override fun setupPrices(prices: Map<Item, Double>) {
        cashier.setupPrices(prices)
    }

    override fun totalFor(actorName: String): Double =
        cashier.totalFor(actorName)

    override fun addItem(actorName: String, qty: Int, item: Item) {
        cashier.addItem(actorName, qty, item)
    }

    override fun setup3x2(item: Item) {
        cashier.setup3x2(item)
    }

    override val protocol: DdtProtocol = DomainOnly

    override fun prepare(): DomainSetUp = Ready
}