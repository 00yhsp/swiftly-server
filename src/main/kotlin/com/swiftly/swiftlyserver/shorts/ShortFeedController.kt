package com.swiftly.swiftlyserver.shorts

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/shorts")
@Tag(name = "Shorts")
class ShortFeedController(
    private val shortFeedService: ShortFeedService,
) {
    @GetMapping("/feed")
    @Operation(
        summary = "Get the short-form video feed.",
        description = "Returns feed items with temporary signed R2 URLs for playback and thumbnails.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Short-form video feed page.",
                content = [
                    Content(
                        schema = Schema(implementation = ShortFeedResponse::class),
                        examples = [
                            ExampleObject(
                                value = """
                                {
                                  "items": [
                                    {
                                      "id": 7,
                                      "title": "CarPlay 앱을 더 강력하게 만들기",
                                      "has_code_blocks": true,
                                      "thumbnail_url": "https://example.r2.cloudflarestorage.com/thumbnails/sample.jpg?X-Amz-Signature=...",
                                      "video_url": "https://example.r2.cloudflarestorage.com/videos/sample.mp4?X-Amz-Signature=...",
                                      "duration_seconds": 78,
                                      "created_at": "2026-05-13T03:21:00Z"
                                    }
                                  ],
                                  "next_cursor": "N3wyMDI2LTA1LTEzVDAzOjIxOjAwWg",
                                  "has_next": true
                                }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
            ApiResponse(responseCode = "400", description = "Invalid pagination parameter or cursor."),
        ],
    )
    fun getFeed(
        @Parameter(description = "Page size. Defaults to 20 and is capped at 50.")
        @RequestParam(defaultValue = "20")
        @Min(1)
        @Max(50)
        size: Int,
        @Parameter(description = "Cursor returned from the previous response.")
        @RequestParam(required = false)
        cursor: String?,
    ): ShortFeedResponse =
        shortFeedService.getFeed(
            size = size,
            cursor = cursor,
        )
}
