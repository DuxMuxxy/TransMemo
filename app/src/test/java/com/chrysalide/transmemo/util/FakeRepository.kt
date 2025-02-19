package com.chrysalide.transmemo.util

import kotlinx.coroutines.flow.MutableSharedFlow

class FakeRepository<T> {
    val flow = MutableSharedFlow<T>()

    suspend fun emit(value: T) = flow.emit(value)
}
