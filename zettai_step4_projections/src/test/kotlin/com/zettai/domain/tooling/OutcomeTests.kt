package com.zettai.domain.tooling

import com.zettai.fp.Outcome
import com.zettai.fp.OutcomeError
import com.zettai.fp.onFailure
import org.junit.jupiter.api.fail

fun <E : OutcomeError, T> Outcome<E, T>.expectSuccess(): T =
    onFailure { error -> fail { "$this expected success but was $error" } }


fun <E : OutcomeError, T> Outcome<E, T>.expectFailure(): E =
    onFailure { error -> return error }
        .let { fail { "Expected failure but was $it" } }