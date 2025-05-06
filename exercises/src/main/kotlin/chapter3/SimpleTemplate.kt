package chapter3

data class StringTag(val text: String)

infix fun String.tag(value: String): Pair<String, StringTag> {
    return this to StringTag(value)
}

fun renderTemplate(template: String, data: Map<String, StringTag>): String {
    return data.entries.fold(template) { acc, (key, stringTag) ->
        acc.replace("{$key}", stringTag.text)
    }
}