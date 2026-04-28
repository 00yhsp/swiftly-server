/*
 * Global Exception Handler for Swiftly Server
 */
package com.swiftly.swiftlyserver.common.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.net.URI

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message)
        problemDetail.title = "Invalid Argument"
        problemDetail.type = URI.create("https://api.swiftly.com/errors/invalid-argument")
        return problemDetail
    }

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalStateException(e: IllegalStateException): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.message)
        problemDetail.title = "Invalid State"
        problemDetail.type = URI.create("https://api.swiftly.com/errors/invalid-state")
        return problemDetail
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(e: Exception): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred")
        problemDetail.title = "Internal Server Error"
        return problemDetail
    }
}
