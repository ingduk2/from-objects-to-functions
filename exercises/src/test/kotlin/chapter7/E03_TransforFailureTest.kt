package chapter7

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class E03_TransforFailureTest {

    @Test
    fun `transform failure happy path`() {
        val res = sendEmail("myfile.txt")

        expectThat(res).isEqualTo(Unit.asSuccess())
    }
}