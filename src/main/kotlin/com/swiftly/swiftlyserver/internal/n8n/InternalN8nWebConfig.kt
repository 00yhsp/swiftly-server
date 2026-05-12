package com.swiftly.swiftlyserver.internal.n8n

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class InternalN8nWebConfig(
    private val n8nIngestTokenInterceptor: N8nIngestTokenInterceptor,
) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry
            .addInterceptor(n8nIngestTokenInterceptor)
            .addPathPatterns("/internal/n8n/**")
    }
}
