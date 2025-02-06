package com.chrysalide.transmemo.domain.boundary

interface BiometricRepository {
    fun canDeviceAskAuthentication(): Boolean
}
