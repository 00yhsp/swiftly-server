package com.swiftly.swiftlyserver.internal.n8n

import com.swiftly.swiftlyserver.common.exception.UnauthorizedException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.mockk.every
import io.mockk.mockk
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

class N8nIngestTokenInterceptorTest :
    DescribeSpec({
        describe("N8nIngestTokenInterceptor") {
            it("server ingest token is blank이면 fail-closed로 거절한다") {
                val request = mockk<HttpServletRequest>()
                val response = mockk<HttpServletResponse>()
                val interceptor = N8nIngestTokenInterceptor("")

                every { request.getHeader(N8nIngestTokenInterceptor.INGEST_TOKEN_HEADER) } returns "test-ingest-token"

                shouldThrow<UnauthorizedException> {
                    interceptor.preHandle(request, response, Any())
                }
            }
        }
    })
