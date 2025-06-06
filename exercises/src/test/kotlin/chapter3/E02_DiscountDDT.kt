package chapter3

import chapter3.Item.*
import com.ubertob.pesticide.core.DDT
import com.ubertob.pesticide.core.DomainDrivenTest

class E02_DiscountDDT : DomainDrivenTest<CashierActions>(allActions) {

    private val alice by NamedActor(::CustomerActor)

    @DDT
    fun `customer can benefit from 3x2 offer`() = ddtScenario {
        val prices = mapOf(carrot to 2.0, milk to 5.0)

        setUp {
            setupPrices(prices)
            setup3x2(milk)
        }.thenPlay(
            alice.`can add #qty #item`(3, carrot),
            alice.`can add #qty #item`(3, milk),
            alice.`check total is #total`(16.0)
        )
    }
}