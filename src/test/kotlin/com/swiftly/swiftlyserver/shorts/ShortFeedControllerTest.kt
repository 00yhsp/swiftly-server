package com.swiftly.swiftlyserver.shorts

import com.jayway.jsonpath.JsonPath
import com.ninjasquad.springmockk.MockkBean
import com.swiftly.swiftlyserver.storage.R2PresignedUrlService
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.every
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
class ShortFeedControllerTest : DescribeSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var shortVideoRepository: ShortVideoRepository

    @MockkBean
    private lateinit var r2PresignedUrlService: R2PresignedUrlService

    init {
        beforeTest {
            shortVideoRepository.deleteAll()
            every { r2PresignedUrlService.createGetUrl(any()) } answers {
                "https://signed.example/${invocation.args[0]}"
            }
        }

        describe("GET /shorts/feed") {
            it("피드 아이템에 제목, 코드블록 유무, 썸네일, 영상 URL, 길이를 포함한다") {
                saveShort(
                    title = "첫 번째 영상",
                    videoKey = "videos/first.mp4",
                    thumbnailKey = "thumbnails/first.jpg",
                    durationSeconds = 42,
                    hasCodeBlock = false,
                )
                saveShort(
                    title = "두 번째 영상",
                    videoKey = "videos/second.mp4",
                    thumbnailKey = "thumbnails/second.jpg",
                    durationSeconds = 78,
                    hasCodeBlock = true,
                )

                mockMvc
                    .get("/shorts/feed") {
                        param("size", "20")
                    }.andExpect {
                        status { isOk() }
                        jsonPath("$.items.length()") { value(2) }
                        jsonPath("$.items[0].title") { value("두 번째 영상") }
                        jsonPath("$.items[0].has_code_blocks") { value(true) }
                        jsonPath("$.items[0].thumbnail_url") {
                            value("https://signed.example/thumbnails/second.jpg")
                        }
                        jsonPath("$.items[0].video_url") {
                            value("https://signed.example/videos/second.mp4")
                        }
                        jsonPath("$.items[0].duration_seconds") { value(78) }
                        jsonPath("$.has_next") { value(false) }
                    }
            }

            it("cursor 기반으로 다음 페이지를 중복 없이 조회한다") {
                val first = saveShort("첫 번째 영상", "videos/first.mp4", "thumbnails/first.jpg", 30, false)
                val second = saveShort("두 번째 영상", "videos/second.mp4", "thumbnails/second.jpg", 40, false)
                val third = saveShort("세 번째 영상", "videos/third.mp4", "thumbnails/third.jpg", 50, false)

                val firstPage =
                    mockMvc
                        .get("/shorts/feed") {
                            param("size", "2")
                        }.andExpect {
                            status { isOk() }
                            jsonPath("$.items.length()") { value(2) }
                            jsonPath("$.items[0].id") { value(third.id) }
                            jsonPath("$.items[1].id") { value(second.id) }
                            jsonPath("$.has_next") { value(true) }
                        }.andReturn()

                val nextCursor = JsonPath.read<String>(firstPage.response.contentAsString, "$.next_cursor")

                mockMvc
                    .get("/shorts/feed") {
                        param("size", "2")
                        param("cursor", nextCursor)
                    }.andExpect {
                        status { isOk() }
                        jsonPath("$.items.length()") { value(1) }
                        jsonPath("$.items[0].id") { value(first.id) }
                        jsonPath("$.has_next") { value(false) }
                    }
            }

            it("잘못된 cursor는 400을 반환한다") {
                mockMvc
                    .get("/shorts/feed") {
                        param("cursor", "bad-cursor")
                    }.andExpect {
                        status { isBadRequest() }
                    }
            }
        }
    }

    private fun saveShort(
        title: String,
        videoKey: String,
        thumbnailKey: String,
        durationSeconds: Int,
        hasCodeBlock: Boolean,
    ): ShortVideo {
        val shortVideo =
            ShortVideo(
                title = title,
                summary = "$title 요약",
                videoKey = videoKey,
                thumbnailKey = thumbnailKey,
                durationSeconds = durationSeconds,
            )

        if (hasCodeBlock) {
            shortVideo.codeBlocks.add(
                ShortVideoCodeBlock(
                    shortVideo = shortVideo,
                    title = "Sample",
                    code = "let sample = true",
                    sortOrder = 0,
                ),
            )
        }

        return shortVideoRepository.saveAndFlush(shortVideo)
    }
}
