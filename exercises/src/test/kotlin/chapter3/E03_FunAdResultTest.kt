package chapter3

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class E03_FunAdResultTest {

    @Test
    fun `char at pos function builder`() {
        val myCharAtPosKotlin: (Int) -> Char = buildCharAtPos("Kotlin")
        expectThat(myCharAtPosKotlin(0)).isEqualTo('K')

        val myCharAtPosPragProg: (Int) -> Char = buildCharAtPos("PragProg")
        expectThat(myCharAtPosPragProg(5)).isEqualTo('r')
    }

    private fun buildCharAtPos(text: String): (Int) -> Char =
        { idx -> text[idx] }
}