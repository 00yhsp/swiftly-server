package com.swiftly.swiftlyserver.common.exception

class UnauthorizedException(
    override val message: String,
) : RuntimeException(message)
