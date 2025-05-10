package chapter5

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class E04_MonoidTest {

    @Test
    fun `verify monoid of Int`() {
        val list = listOf(1, 2, 3, 4, 10)

        // kotlin
        with(Monoid(0, Int::plus)) {
            expectThat(list.fold())
                .isEqualTo(20)
        }

        // java
        val monoidJava = MonoidJava(0) { a: Int, b: Int -> a + b }
        val result = monoidJava.fold(list)
        expectThat(result).isEqualTo(20)
    }


    @Test
    fun `verify monoid of String`() {
        val list = listOf("My", "Fair", "Lady")

        // kotlin
        with(Monoid("", String::plus)) {
            expectThat(list.fold())
                .isEqualTo("MyFairLady")
        }

        // java
        val monoidJava = MonoidJava("") { a: String, b: String -> a + b }
        val result = monoidJava.fold(list)
        expectThat(result).isEqualTo("MyFairLady")
    }

    @Test
    fun `verify monoid of Money`() {
        val list = listOf(
            Money(2.1),
            Money(3.9),
            Money(4.0)
        )

        // kotlin
        with(Monoid(zeroMoney, Money::sum)) {
            expectThat(
                list.fold()
            ).isEqualTo(Money(10.0))
        }

        // java
        val monoidJava = MonoidJava(zeroMoney, Money::sum)
        val result = monoidJava.fold(list)
        expectThat(result).isEqualTo(Money(10.0))
    }
}