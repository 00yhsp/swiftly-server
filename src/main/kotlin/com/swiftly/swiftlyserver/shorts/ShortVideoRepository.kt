package com.swiftly.swiftlyserver.shorts

import org.springframework.data.jpa.repository.JpaRepository

interface ShortVideoRepository : JpaRepository<ShortVideo, Long> {
    fun findByVideoKey(videoKey: String): ShortVideo?
}
