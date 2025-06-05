package com.zettai.queries

import com.zettai.events.ToDoListEvent
import com.zettai.fp.FetchStoredEvents
import com.zettai.fp.ProjectionQuery
import com.zettai.fp.QueryRunner

class ToDoListQueryRunner(
    eventFetcher: FetchStoredEvents<ToDoListEvent>
) : QueryRunner<ToDoListQueryRunner> {

    internal val listProjection = ToDoListProjection(eventFetcher)

    override fun <R> invoke(f: ToDoListQueryRunner.() -> R): ProjectionQuery<R> =
        ProjectionQuery(setOf(listProjection)) { f(this) }
}