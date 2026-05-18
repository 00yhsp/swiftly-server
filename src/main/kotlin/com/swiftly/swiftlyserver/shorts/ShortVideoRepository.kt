package com.swiftly.swiftlyserver.shorts

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant

interface ShortVideoRepository : JpaRepository<ShortVideo, Long> {
    fun findByVideoKey(videoKey: String): ShortVideo?

    @Query(
        """
        select new com.swiftly.swiftlyserver.shorts.ShortVideoFeedProjection(
            shortVideo.id,
            shortVideo.title,
            shortVideo.summary,
            shortVideo.videoKey,
            shortVideo.thumbnailKey,
            shortVideo.durationSeconds,
            shortVideo.createdAt,
            case when count(codeBlock) > 0 then true else false end
        )
        from ShortVideo shortVideo
        left join shortVideo.codeBlocks codeBlock
        group by
            shortVideo.id,
            shortVideo.title,
            shortVideo.summary,
            shortVideo.videoKey,
            shortVideo.thumbnailKey,
            shortVideo.durationSeconds,
            shortVideo.createdAt
        order by shortVideo.createdAt desc, shortVideo.id desc
        """,
    )
    fun findFeedItems(pageable: Pageable): List<ShortVideoFeedProjection>

    @Query(
        """
        select new com.swiftly.swiftlyserver.shorts.ShortVideoFeedProjection(
            shortVideo.id,
            shortVideo.title,
            shortVideo.summary,
            shortVideo.videoKey,
            shortVideo.thumbnailKey,
            shortVideo.durationSeconds,
            shortVideo.createdAt,
            case when count(codeBlock) > 0 then true else false end
        )
        from ShortVideo shortVideo
        left join shortVideo.codeBlocks codeBlock
        where shortVideo.createdAt < :createdAt
            or (shortVideo.createdAt = :createdAt and shortVideo.id < :id)
        group by
            shortVideo.id,
            shortVideo.title,
            shortVideo.summary,
            shortVideo.videoKey,
            shortVideo.thumbnailKey,
            shortVideo.durationSeconds,
            shortVideo.createdAt
        order by shortVideo.createdAt desc, shortVideo.id desc
        """,
    )
    fun findFeedItemsAfter(
        @Param("createdAt") createdAt: Instant,
        @Param("id") id: Long,
        pageable: Pageable,
    ): List<ShortVideoFeedProjection>
}
