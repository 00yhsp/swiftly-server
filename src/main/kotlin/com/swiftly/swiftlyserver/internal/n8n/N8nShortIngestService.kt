package com.swiftly.swiftlyserver.internal.n8n

import com.swiftly.swiftlyserver.shorts.ShortVideo
import com.swiftly.swiftlyserver.shorts.ShortVideoCodeBlock
import com.swiftly.swiftlyserver.shorts.ShortVideoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class N8nShortIngestService(
    private val shortVideoRepository: ShortVideoRepository,
) {
    @Transactional
    fun ingest(requests: List<N8nShortIngestRequest>): N8nShortIngestResponse {
        val results = requests.map { request -> upsert(request) }

        return N8nShortIngestResponse(
            createdCount = results.count { it.created },
            updatedCount = results.count { !it.created },
            shortIds = results.map { it.shortId },
        )
    }

    private fun upsert(request: N8nShortIngestRequest): UpsertResult {
        val existingShort = shortVideoRepository.findByVideoKey(request.videoKey)
        val shortVideo =
            existingShort ?: ShortVideo(
                title = request.title,
                summary = request.summary,
                videoKey = request.videoKey,
                thumbnailKey = request.thumbnailKey,
            )

        shortVideo.replaceContent(
            title = request.title,
            summary = request.summary,
            thumbnailKey = request.thumbnailKey,
            codeBlocks = request.codeBlocks.mapIndexed { index, codeBlock -> codeBlock.toEntity(shortVideo, index) },
        )

        val savedShort = shortVideoRepository.save(shortVideo)
        return UpsertResult(
            shortId = requireNotNull(savedShort.id),
            created = existingShort == null,
        )
    }

    private fun N8nCodeBlockRequest.toEntity(
        shortVideo: ShortVideo,
        sortOrder: Int,
    ): ShortVideoCodeBlock =
        ShortVideoCodeBlock(
            shortVideo = shortVideo,
            title = title,
            code = code,
            sortOrder = sortOrder,
        )

    private data class UpsertResult(
        val shortId: Long,
        val created: Boolean,
    )
}
