package com.swiftly.swiftlyserver.internal.n8n

import com.swiftly.swiftlyserver.shorts.ShortVideoCodeBlockRepository
import com.swiftly.swiftlyserver.shorts.ShortVideoRepository
import com.swiftly.swiftlyserver.shorts.ShortVideoSceneRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
class N8nShortIngestControllerTest
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
        private val shortVideoRepository: ShortVideoRepository,
        private val shortVideoSceneRepository: ShortVideoSceneRepository,
        private val shortVideoCodeBlockRepository: ShortVideoCodeBlockRepository,
    ) : DescribeSpec({
            extension(SpringExtension)

            beforeTest {
                shortVideoRepository.deleteAll()
            }

            describe("POST /internal/n8n/shorts") {
                it("토큰이 없으면 401을 반환한다") {
                    mockMvc
                        .post("/internal/n8n/shorts") {
                            contentType = MediaType.APPLICATION_JSON
                            content = validPayload()
                        }.andExpect {
                            status { isUnauthorized() }
                        }
                }

                it("토큰이 틀리면 401을 반환한다") {
                    mockMvc
                        .post("/internal/n8n/shorts") {
                            header(N8nIngestTokenInterceptor.INGEST_TOKEN_HEADER, "wrong-token")
                            contentType = MediaType.APPLICATION_JSON
                            content = validPayload()
                        }.andExpect {
                            status { isUnauthorized() }
                        }
                }

                it("신규 payload 배열을 저장한다") {
                    mockMvc
                        .post("/internal/n8n/shorts") {
                            header(N8nIngestTokenInterceptor.INGEST_TOKEN_HEADER, "test-ingest-token")
                            contentType = MediaType.APPLICATION_JSON
                            content = validPayload()
                        }.andExpect {
                            status { isOk() }
                            jsonPath("$.created_count") { value(1) }
                            jsonPath("$.updated_count") { value(0) }
                            jsonPath("$.short_ids.length()") { value(1) }
                        }

                    val savedShort = shortVideoRepository.findByVideoKey(VIDEO_KEY)

                    requireNotNull(savedShort)
                    savedShort.title shouldBe "차세대 CarPlay 디자인 시스템을 만나보세요"
                    savedShort.thumbnailKey shouldBe THUMBNAIL_KEY
                    shortVideoSceneRepository.countByShortVideoVideoKey(VIDEO_KEY) shouldBe 2
                    shortVideoCodeBlockRepository.countByShortVideoVideoKey(VIDEO_KEY) shouldBe 1
                }

                it("같은 video_key가 다시 오면 기존 레코드를 갱신한다") {
                    mockMvc
                        .post("/internal/n8n/shorts") {
                            header(N8nIngestTokenInterceptor.INGEST_TOKEN_HEADER, "test-ingest-token")
                            contentType = MediaType.APPLICATION_JSON
                            content = validPayload()
                        }.andExpect {
                            status { isOk() }
                        }

                    mockMvc
                        .post("/internal/n8n/shorts") {
                            header(N8nIngestTokenInterceptor.INGEST_TOKEN_HEADER, "test-ingest-token")
                            contentType = MediaType.APPLICATION_JSON
                            content = updatedPayload()
                        }.andExpect {
                            status { isOk() }
                            jsonPath("$.created_count") { value(0) }
                            jsonPath("$.updated_count") { value(1) }
                        }

                    shortVideoRepository.count() shouldBe 1

                    val updatedShort = shortVideoRepository.findByVideoKey(VIDEO_KEY)

                    requireNotNull(updatedShort)
                    updatedShort.title shouldBe "업데이트된 제목"
                    shortVideoSceneRepository.countByShortVideoVideoKey(VIDEO_KEY) shouldBe 1
                    shortVideoCodeBlockRepository.countByShortVideoVideoKey(VIDEO_KEY) shouldBe 0
                }

                it("잘못된 R2 key prefix는 400을 반환한다") {
                    mockMvc
                        .post("/internal/n8n/shorts") {
                            header(N8nIngestTokenInterceptor.INGEST_TOKEN_HEADER, "test-ingest-token")
                            contentType = MediaType.APPLICATION_JSON
                            content = validPayload().replace(VIDEO_KEY, "bad/path.mp4")
                        }.andExpect {
                            status { isBadRequest() }
                        }
                }

                it("scene end_time이 start_time 이하이면 400을 반환한다") {
                    mockMvc
                        .post("/internal/n8n/shorts") {
                            header(N8nIngestTokenInterceptor.INGEST_TOKEN_HEADER, "test-ingest-token")
                            contentType = MediaType.APPLICATION_JSON
                            content = validPayload().replace("\"end_time\": 2.3", "\"end_time\": 0")
                        }.andExpect {
                            status { isBadRequest() }
                        }
                }
            }
        }) {
        companion object {
            private const val VIDEO_KEY = "videos/6u7ZkZ09GwUij0ZR0bpdT_output.mp4"
            private const val THUMBNAIL_KEY = "thumbnails/DDIlC25ehUyV4hy5FiNmN_first_frame.jpg"

            private fun validPayload(): String =
                """
                [
                  {
                    "title": "차세대 CarPlay 디자인 시스템을 만나보세요",
                    "summary": "차세대 CarPlay의 핵심 디자인 시스템을 소개합니다.",
                    "video_key": "$VIDEO_KEY",
                    "thumbnail_key": "$THUMBNAIL_KEY",
                    "codeBlocks": [
                      {
                        "title": "Sample code",
                        "code": "let carPlay = true"
                      }
                    ],
                    "scenes": [
                      {
                        "scene_index": 1,
                        "start_time": 0,
                        "end_time": 2.3,
                        "duration": 2.3,
                        "origin_script": "카플레이가 이렇게까지 달라진다고요?",
                        "image_url": "https://example.com/scene-1.jpg"
                      },
                      {
                        "scene_index": 2,
                        "start_time": 2.301,
                        "end_time": 6.8,
                        "duration": 4.499,
                        "origin_script": "오늘은 차세대 카플레이 디자인 시스템을 소개합니다.",
                        "image_url": "https://example.com/scene-2.jpg"
                      }
                    ]
                  }
                ]
                """.trimIndent()

            private fun updatedPayload(): String =
                """
                [
                  {
                    "title": "업데이트된 제목",
                    "summary": "업데이트된 요약입니다.",
                    "video_key": "$VIDEO_KEY",
                    "thumbnail_key": "$THUMBNAIL_KEY",
                    "codeBlocks": [],
                    "scenes": [
                      {
                        "scene_index": 1,
                        "start_time": 0,
                        "end_time": 3.5,
                        "duration": 3.5,
                        "origin_script": "업데이트된 첫 장면입니다.",
                        "image_url": "https://example.com/updated-scene.jpg"
                      }
                    ]
                  }
                ]
                """.trimIndent()
        }
    }
