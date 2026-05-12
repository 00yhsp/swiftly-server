package com.swiftly.swiftlyserver.internal.n8n

import com.swiftly.swiftlyserver.common.exception.UnauthorizedException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

@Component
class N8nIngestTokenInterceptor(
    @param:Value("\${internal.n8n.ingest-token:}")
    private val ingestToken: String,
) : HandlerInterceptor {
    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        val providedToken = request.getHeader(INGEST_TOKEN_HEADER)

        if (ingestToken.isBlank() || providedToken.isNullOrBlank()) {
            throw UnauthorizedException("Invalid ingest token")
        }

        if (!constantTimeEquals(ingestToken, providedToken)) {
            throw UnauthorizedException("Invalid ingest token")
        }

        return true
    }

    private fun constantTimeEquals(
        expected: String,
        actual: String,
    ): Boolean =
        MessageDigest.isEqual(
            expected.toByteArray(StandardCharsets.UTF_8),
            actual.toByteArray(StandardCharsets.UTF_8),
        )

    companion object {
        const val INGEST_TOKEN_HEADER = "X-Ingest-Token"
    }
}
