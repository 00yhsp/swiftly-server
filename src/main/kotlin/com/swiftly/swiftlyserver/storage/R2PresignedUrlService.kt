package com.swiftly.swiftlyserver.storage

import org.springframework.stereotype.Service
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Configuration
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import java.net.URI

@Service
class R2PresignedUrlService(
    private val r2Properties: R2Properties,
) {
    private val presigner: S3Presigner by lazy {
        validateConfiguration()

        S3Presigner
            .builder()
            .endpointOverride(URI.create(r2Properties.endpoint))
            .region(Region.of("auto"))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(r2Properties.accessKeyId, r2Properties.secretAccessKey),
                ),
            ).serviceConfiguration(
                S3Configuration
                    .builder()
                    .pathStyleAccessEnabled(true)
                    .build(),
            ).build()
    }

    fun createGetUrl(key: String): String {
        val getObjectRequest =
            GetObjectRequest
                .builder()
                .bucket(r2Properties.bucket)
                .key(key)
                .build()
        val presignRequest =
            GetObjectPresignRequest
                .builder()
                .signatureDuration(r2Properties.presignedUrlTtl)
                .getObjectRequest(getObjectRequest)
                .build()

        return presigner.presignGetObject(presignRequest).url().toString()
    }

    private fun validateConfiguration() {
        require(r2Properties.endpoint.isNotBlank()) { "R2 endpoint is required" }
        require(r2Properties.bucket.isNotBlank()) { "R2 bucket is required" }
        require(r2Properties.accessKeyId.isNotBlank()) { "R2 access key id is required" }
        require(r2Properties.secretAccessKey.isNotBlank()) { "R2 secret access key is required" }
    }
}
