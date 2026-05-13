package com.swiftly.swiftlyserver.shorts

import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.Base64

@Component
class ShortFeedCursorCodec {
    fun encode(cursor: ShortFeedCursor): String {
        val rawCursor = "${cursor.id}|${cursor.createdAt}"
        return Base64
            .getUrlEncoder()
            .withoutPadding()
            .encodeToString(rawCursor.toByteArray(StandardCharsets.UTF_8))
    }

    fun decode(cursor: String): ShortFeedCursor =
        runCatching {
            val decodedCursor =
                String(
                    Base64.getUrlDecoder().decode(cursor),
                    StandardCharsets.UTF_8,
                )
            val parts = decodedCursor.split("|", limit = 2)

            require(parts.size == 2)

            ShortFeedCursor(
                id = parts[0].toLong(),
                createdAt = Instant.parse(parts[1]),
            )
        }.getOrElse {
            throw IllegalArgumentException("Invalid feed cursor")
        }
}

data class ShortFeedCursor(
    val id: Long,
    val createdAt: Instant,
)
