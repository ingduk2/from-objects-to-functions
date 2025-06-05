package com.zettai.domain

import com.zettai.commands.ToDoListCommand
import com.zettai.commands.ToDoListCommandHandler
import com.zettai.events.ToDoListEvent
import com.zettai.events.ToDoListState
import com.zettai.fp.EventPersister
import com.zettai.fp.Outcome
import com.zettai.fp.OutcomeError
import com.zettai.fp.failIfNull
import com.zettai.queries.ToDoListQueryRunner

sealed class ZettaiError : OutcomeError
data class NotFoundError(override val msg: String) : ZettaiError()
data class InvalidRequestError(override val msg: String) : ZettaiError()
data class ToDoListCommandError(override val msg: String) : ZettaiError()
data class InconsistentStateError(val command: ToDoListCommand, val state: ToDoListState) : ZettaiError() {
    override val msg = "Command $command cannot be applied to state $state"
}

typealias ZettaiOutcome<T> = Outcome<ZettaiError, T>

interface ZettaiHub {
    fun getList(user: User, listName: ListName): ZettaiOutcome<ToDoList>
    fun getLists(user: User): ZettaiOutcome<List<ListName>>
    fun handle(command: ToDoListCommand): ZettaiOutcome<ToDoListCommand>
}

interface ToDoListFetcher {
    fun get(user: User, listName: ListName): ZettaiOutcome<ToDoList>
    fun getAll(user: User): ZettaiOutcome<List<ListName>>
}

class ToDoListHub(
    val queryRunner: ToDoListQueryRunner,
    val commandHandler: ToDoListCommandHandler,
    val persistEvents: EventPersister<ToDoListEvent>
) : ZettaiHub {
    override fun getList(user: User, listName: ListName): ZettaiOutcome<ToDoList> =
        queryRunner {
            listProjection
                .findList(user, listName)
                .failIfNull(InvalidRequestError("List $listName of user $user not found!"))
        }.runIt()

    override fun getLists(user: User): ZettaiOutcome<List<ListName>> =
        queryRunner {
            listProjection
                .findAll(user)
                .failIfNull(InvalidRequestError("User $user not found!"))
        }.runIt()

    override fun handle(command: ToDoListCommand): ZettaiOutcome<ToDoListCommand> =
        commandHandler(command)
            .transform(persistEvents)
            .transform { command }
}