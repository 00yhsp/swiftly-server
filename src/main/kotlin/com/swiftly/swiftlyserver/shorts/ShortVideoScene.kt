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
import java.math.BigDecimal

@Entity
@Table(name = "short_video_scenes")
class ShortVideoScene(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "short_video_id", nullable = false)
    val shortVideo: ShortVideo,
    @Column(name = "scene_index", nullable = false)
    val sceneIndex: Int,
    @Column(name = "start_time", nullable = false, precision = 10, scale = 3)
    val startTime: BigDecimal,
    @Column(name = "end_time", nullable = false, precision = 10, scale = 3)
    val endTime: BigDecimal,
    @Column(nullable = false, precision = 10, scale = 3)
    val duration: BigDecimal,
    @Column(name = "origin_script", nullable = false, columnDefinition = "text")
    val originScript: String,
    @Column(name = "image_url", nullable = false, columnDefinition = "text")
    val imageUrl: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}
