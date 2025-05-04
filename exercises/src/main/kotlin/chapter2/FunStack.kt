package chapter2

data class FunStack<T>(
    private val elements: List<T> = emptyList(),
) {
    fun push(element: T): FunStack<T> {
        return FunStack(listOf(element) + elements)
    }

    fun pop(): Pair<T, FunStack<T>> = elements.first() to FunStack(elements.drop(1))

    fun size(): Int = elements.size
}