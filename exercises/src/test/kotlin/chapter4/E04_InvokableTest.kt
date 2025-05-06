package chapter4

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class E04_InvokableTest {

    private val john = Person("John", "Smith")
    private val jane = Person("Jane", "Austen")

    @Test
    fun `correctly generate the email text`() {
        val templateText = "Dear {firstName}_{familyName}, ..."
        val emailTemplate = EmailTemplate(templateText)

        expectThat(emailTemplate(john)).isEqualTo("Dear John_Smith, ...")
        expectThat(emailTemplate(jane)).isEqualTo("Dear Jane_Austen, ...")
    }
}