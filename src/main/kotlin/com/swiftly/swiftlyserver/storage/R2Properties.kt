package com.swiftly.swiftlyserver.storage

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "storage.r2")
data class R2Properties(
    val endpoint: String = "",
    val bucket: String = "",
    val accessKeyId: String = "",
    val secretAccessKey: String = "",
    val presignedUrlTtl: Duration = Duration.ofMinutes(30),
)
