package com.swiftly.swiftlyserver.storage

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.string.shouldContain
import java.time.Duration

class R2PresignedUrlServiceTest :
    DescribeSpec({
        describe("R2PresignedUrlService") {
            it("R2 object key로 GET presigned URL을 생성한다") {
                val service =
                    R2PresignedUrlService(
                        R2Properties(
                            endpoint = "https://example.r2.cloudflarestorage.com",
                            bucket = "swiftly-shorts",
                            accessKeyId = "access-key",
                            secretAccessKey = "secret-key",
                            presignedUrlTtl = Duration.ofMinutes(30),
                        ),
                    )

                val signedUrl = service.createGetUrl("videos/sample.mp4")

                signedUrl shouldContain "https://example.r2.cloudflarestorage.com/swiftly-shorts/videos/sample.mp4"
                signedUrl shouldContain "X-Amz-Signature"
            }
        }
    })
