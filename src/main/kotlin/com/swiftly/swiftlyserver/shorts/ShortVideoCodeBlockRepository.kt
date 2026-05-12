package com.swiftly.swiftlyserver.shorts

import org.springframework.data.jpa.repository.JpaRepository

interface ShortVideoCodeBlockRepository : JpaRepository<ShortVideoCodeBlock, Long> {
    fun countByShortVideoVideoKey(videoKey: String): Long
}
