package com.swiftly.swiftlyserver.shorts

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "short_video_code_blocks")
class ShortVideoCodeBlock(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "short_video_id", nullable = false)
    val shortVideo: ShortVideo,
    @Column(nullable = false)
    val title: String,
    @Column(nullable = false, columnDefinition = "text")
    val code: String,
    @Column(name = "sort_order", nullable = false)
    val sortOrder: Int,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}
