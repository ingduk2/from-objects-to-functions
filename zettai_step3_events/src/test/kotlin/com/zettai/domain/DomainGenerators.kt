package com.zettai.domain

import com.zettai.domain.tooling.digits
import com.zettai.domain.tooling.lowercase
import com.zettai.domain.tooling.randomString
import kotlin.random.Random.Default.nextInt

fun userGenerator(): Sequence<User> = generateSequence {
    randomUser()
}

fun randomUser() = User.fromTrusted(randomString(lowercase, 3, 6).capitalize())

fun itemGenerator(): Sequence<ToDoItem> = generateSequence {
    randomItem()
}

fun randomItem() = ToDoItem(randomString(lowercase + digits, 5, 20), null, ToDoStatus.Todo)

fun randomToDoList(): ToDoList = ToDoList(
    randomListName(),
    itemGenerator().take(nextInt(5) + 1).toList()
)

fun randomListName(): ListName = ListName.fromTrusted(randomString(lowercase, 3, 6))