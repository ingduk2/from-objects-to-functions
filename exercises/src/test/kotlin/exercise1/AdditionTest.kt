package exercise1

import org.junit.jupiter.api.Test
import strikt.api.expect
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import kotlin.random.Random

class AdditionTest {
    @Test
    fun `add two numbers`() {
        expectThat(5 + 6).isEqualTo(11)
        expectThat(7 + 42).isEqualTo(49)
        expectThat(9999 + 1).isEqualTo(10000)
    }

    private fun randomNatural() = Random.nextInt(from = 1, until = 100_000_000)

    @Test
    fun `zero identity`() {
        repeat(100) {
            val x = randomNatural()
            expectThat(x + 0).isEqualTo(x)
        }
    }

    @Test
    fun `commutative property`() {
        repeat(100) {
            val x = randomNatural()
            val y = randomNatural()
            expectThat(x + y).isEqualTo(y + x)
        }
    }

    @Test
    fun `associative property`() {
        repeat(100) {
            val x = randomNatural()
            val y = randomNatural()
            val z = randomNatural()
            expect {
                that((x + y) + z).isEqualTo(x + (y + z))
                that((y + z) + x).isEqualTo(y + (z + x))
                that((z + x) + y).isEqualTo(z + (x + y))
            }
        }
    }

    @Test
    fun `작은 수 부터 1씩 더해 큰 수와 같아지는지 테스트`() {
        val num1 = randomNatural()
        val num2 = randomNatural()

        val (start, end) = if (num1 < num2) num1 to num2 else num2 to num1

        var current = start
        var steps = 0

        while (current < end) {
            current++
            steps++
        }

        expectThat(current).isEqualTo(end)
    }

    @Test
    fun `adding Y is equal to adding one Y times`() {
        repeat(100) {
            val x = randomNatural()
            val y = Random.nextInt(1, 100)

            val ones = List(y) { 1 }
            val z = ones.fold(x) { acc, one -> acc + one }

            expectThat(z).isEqualTo(x + y)
        }
    }
}