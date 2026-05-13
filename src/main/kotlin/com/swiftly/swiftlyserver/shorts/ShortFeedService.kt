package com.swiftly.swiftlyserver.shorts

import com.swiftly.swiftlyserver.storage.R2PresignedUrlService
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ShortFeedService(
    private val shortVideoRepository: ShortVideoRepository,
    private val shortFeedCursorCodec: ShortFeedCursorCodec,
    private val r2PresignedUrlService: R2PresignedUrlService,
) {
    @Transactional(readOnly = true)
    fun getFeed(
        size: Int,
        cursor: String?,
    ): ShortFeedResponse {
        val limit = size + 1
        val pageRequest = PageRequest.of(0, limit)
        val decodedCursor = cursor?.let(shortFeedCursorCodec::decode)
        val rows =
            if (decodedCursor == null) {
                shortVideoRepository.findFeedItems(pageRequest)
            } else {
                shortVideoRepository.findFeedItemsAfter(
                    createdAt = decodedCursor.createdAt,
                    id = decodedCursor.id,
                    pageable = pageRequest,
                )
            }
        val hasNext = rows.size > size
        val items = rows.take(size)

        return ShortFeedResponse(
            items = items.map { row -> row.toResponse() },
            nextCursor = items.lastOrNull()?.takeIf { hasNext }?.toCursor(),
            hasNext = hasNext,
        )
    }

    private fun ShortVideoFeedProjection.toResponse(): ShortFeedItemResponse =
        ShortFeedItemResponse(
            id = id,
            title = title,
            hasCodeBlocks = hasCodeBlocks,
            thumbnailUrl = r2PresignedUrlService.createGetUrl(thumbnailKey),
            videoUrl = r2PresignedUrlService.createGetUrl(videoKey),
            durationSeconds = durationSeconds,
            createdAt = createdAt,
        )

    private fun ShortVideoFeedProjection.toCursor(): String =
        shortFeedCursorCodec.encode(
            ShortFeedCursor(
                id = id,
                createdAt = createdAt,
            ),
        )
}
