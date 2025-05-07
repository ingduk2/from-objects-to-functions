package study

tailrec fun <T, R> Iterable<T>.composeOver(
    initial: R,
    operation: (acc: R, element: T) -> R
): R {
    val head = firstOrNull()

    return if (head == null)
        initial
    else {
        drop(1).composeOver(operation(initial, head), operation)
    }
}

fun <T, R> Iterable<T>.composeOverRight(
    initial: R,
    operation: (element: T, acc: R) -> R
): R {
    val head = firstOrNull()

    return if (head == null) {
        initial
    } else {
        operation(head, drop(1).composeOverRight(initial, operation))
    }
}

fun triple(c: Char): String {
    print(c)
    return "$c$c$c "
}

fun main() {
    val chars = listOf('a', 'b', 'c')

    val l = chars.composeOver("") { acc, u -> acc + triple(u) }
    println(" composeOver is $l")

    val r = chars.composeOverRight("") { u, acc -> triple(u) + acc }
    println(" composeOverRight is $r")
}