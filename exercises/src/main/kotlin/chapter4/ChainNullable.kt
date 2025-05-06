package chapter4

typealias FUN<A, B> = (A) -> B

infix fun <A : Any, B : Any, C : Any> FUN<A, B?>.andUnlessNull(other: FUN<B, C?>): FUN<A, C?> =
    { a: A -> this(a)?.let(other) }