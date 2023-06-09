package net.doemges.kogniswarm.weaviate.service

import io.weaviate.client.WeaviateClient
import jakarta.annotation.PostConstruct
import net.doemges.kogniswarm.weaviate.util.TestableWeaviateClient
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("!test")
class WeaviateConnectionCheckerService(private val client: WeaviateClient) {

    private val logger = LoggerFactory.getLogger(WeaviateConnectionCheckerService::class.java)

    @PostConstruct
    fun testConnection() {
        try {
            val status = client.misc()
                .liveChecker()
                .run()
            if (!status.result) {
                error("Unable to connect to Weaviate")
            }
            logger.debug("Successfully connected to Weaviate")
        } catch (exception: Exception) {
            logger.error("Unable to connect to Weaviate", exception)
        }
    }
}