package com.swiftly.swiftlyserver.shorts

import org.springframework.data.jpa.repository.JpaRepository

interface ShortVideoSceneRepository : JpaRepository<ShortVideoScene, Long> {
    fun countByShortVideoVideoKey(videoKey: String): Long
}
