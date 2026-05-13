package com.swiftly.swiftlyserver.shorts

import java.time.Instant

data class ShortVideoFeedProjection(
    val id: Long,
    val title: String,
    val videoKey: String,
    val thumbnailKey: String,
    val durationSeconds: Int,
    val createdAt: Instant,
    val hasCodeBlocks: Boolean,
)
