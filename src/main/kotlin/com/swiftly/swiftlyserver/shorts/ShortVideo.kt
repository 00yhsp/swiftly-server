package com.swiftly.swiftlyserver.shorts

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.Instant

@Entity
@Table(
    name = "short_videos",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_short_videos_video_key",
            columnNames = ["video_key"],
        ),
    ],
)
class ShortVideo(
    @Column(nullable = false)
    var title: String,
    @Column(nullable = false, columnDefinition = "text")
    var summary: String,
    @Column(name = "video_key", nullable = false)
    val videoKey: String,
    @Column(name = "thumbnail_key", nullable = false)
    var thumbnailKey: String,
    @Column(name = "duration_seconds", nullable = false)
    var durationSeconds: Int,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now()

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()

    @OneToMany(mappedBy = "shortVideo", cascade = [CascadeType.ALL], orphanRemoval = true)
    val codeBlocks: MutableList<ShortVideoCodeBlock> = mutableListOf()

    fun replaceContent(
        title: String,
        summary: String,
        thumbnailKey: String,
        durationSeconds: Int,
        codeBlocks: List<ShortVideoCodeBlock>,
    ) {
        this.title = title
        this.summary = summary
        this.thumbnailKey = thumbnailKey
        this.durationSeconds = durationSeconds
        this.codeBlocks.clear()
        this.codeBlocks.addAll(codeBlocks)
        this.updatedAt = Instant.now()
    }
}
