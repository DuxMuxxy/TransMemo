package com.chrysalide.transmemo.domain.model

enum class IntakeSide {
    UNDEFINED,
    LEFT,
    RIGHT;

    fun getNextSide(): IntakeSide = when (this) {
        LEFT -> RIGHT
        RIGHT -> LEFT
        UNDEFINED -> UNDEFINED
    }
}
