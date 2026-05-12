package com.swiftly.swiftlyserver.internal.n8n

import com.fasterxml.jackson.annotation.JsonProperty

data class N8nShortIngestResponse(
    @get:JsonProperty("created_count")
    val createdCount: Int,
    @get:JsonProperty("updated_count")
    val updatedCount: Int,
    @get:JsonProperty("short_ids")
    val shortIds: List<Long>,
)
