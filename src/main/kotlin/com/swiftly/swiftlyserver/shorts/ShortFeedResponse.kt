package com.swiftly.swiftlyserver.shorts

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

@Schema(description = "Paginated short-form video feed response.")
data class ShortFeedResponse(
    val items: List<ShortFeedItemResponse>,
    @get:JsonProperty("next_cursor")
    @get:Schema(description = "Cursor for the next page. Null when there is no next page.")
    val nextCursor: String?,
    @get:JsonProperty("has_next")
    @get:Schema(description = "Whether more feed items are available.")
    val hasNext: Boolean,
)

@Schema(description = "Short-form video feed item.")
data class ShortFeedItemResponse(
    @get:Schema(example = "7")
    val id: Long,
    @get:Schema(example = "CarPlay 앱을 더 강력하게 만들기")
    val title: String,
    @get:Schema(
        example = "iOS 26의 CarPlay에서는 위젯과 Live Activities를 CarPlay와 CarPlay Ultra에 보여줄 수 있습니다.",
    )
    val summary: String,
    @get:JsonProperty("has_code_blocks")
    @get:Schema(description = "Whether this video has at least one code block.")
    val hasCodeBlocks: Boolean,
    @get:JsonProperty("thumbnail_url")
    @get:Schema(description = "Temporary signed URL for the private R2 thumbnail object.")
    val thumbnailUrl: String,
    @get:JsonProperty("video_url")
    @get:Schema(description = "Temporary signed URL for the private R2 video object.")
    val videoUrl: String,
    @get:JsonProperty("duration_seconds")
    @get:Schema(description = "Video duration in whole seconds.", example = "78")
    val durationSeconds: Int,
    @get:JsonProperty("created_at")
    val createdAt: Instant,
)
