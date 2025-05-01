package exercise1

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import kotlin.random.Random

class CashRegisterTest {

    @Test
    fun `checkout Promotion Test`() {
        // Given
        val prices = mapOf(
            "milk" to 1.5,
            "bread" to 0.9
        )
        val promotions = mapOf(
            "milk" to "3x2",
            "eggs" to "8x6"
        )
        val cashRegister = CashRegister(prices, promotions)

        val products = List(3) { "milk" }

        // When
        val result = cashRegister.checkout(products)

        // Then
        expectThat(result).isEqualTo(3.0);
    }

    @Test
    fun `checkout No Promotion Test`() {
        // Given
        val prices = mapOf(
            "milk" to 1.5,
            "bread" to 0.9
        )
        val promotions = mapOf(
            "milk" to "3x2",
            "eggs" to "8x6"
        )
        val cashRegister = CashRegister(prices, promotions)

        val products = List(4) { "bread" }

        // When
        val result = cashRegister.checkout(products)

        // Then
        expectThat(result).isEqualTo(3.6)
    }

    @Test
    fun `checkout Multiple Product Test`() {
        // Given
        val prices = mapOf(
            "milk" to 1.5,
            "bread" to 0.9
        )
        val promotions = mapOf(
            "milk" to "3x2",
            "eggs" to "8x6"
        )
        val cashRegister = CashRegister(prices, promotions)

        val products = List(3) { "milk" } + List(4) { "bread" }

        // When
        val result = cashRegister.checkout(products)

        // Then
        expectThat(result).isEqualTo(6.6)
    }

    @Test
    fun `checkout test with random product count using Double`() {
        // Given
        val price = 1.5
        val prices = mapOf("milk" to price)
        val promotions = mapOf("milk" to "3x2")
        val cashRegister = CashRegister(prices, promotions)

        val count = Random.nextInt(1, 21)
        val products = List(count) { "milk" }

        // When
        val result = cashRegister.checkout(products)

        // Expected
        val promoBuy = 3
        val promoPay = 2
        val promoCount = count / promoBuy
        val remainder = count % promoBuy
        val expected = (promoCount * promoPay + remainder) * price

        // Then
        expectThat(result).isEqualTo(expected)
    }
}