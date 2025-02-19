package com.chrysalide.transmemo.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class FixedClock(
    private val fixedInstant: Instant
) : Clock {
    override fun now(): Instant = fixedInstant
}

private fun Clock.Companion.fixed(fixedInstant: Instant): Clock = FixedClock(fixedInstant)
