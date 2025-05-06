package chapter4

import chapter3.StringTag
import chapter3.renderTemplate

data class Person(
    val firstName: String,
    val familyName: String,
)

data class EmailTemplate(private val template: String) : (Person) -> String {
    override fun invoke(person: Person): String {
        return renderTemplate(template, person.toTags())
    }
}

private fun Person.toTags(): Map<String, StringTag> =
    mapOf(
        "firstName" to StringTag(firstName),
        "familyName" to StringTag(familyName)
    )