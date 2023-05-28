package net.doemges.kogniswarm.weaviate

import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class WeaviateConnectionChecker(private val client: TestableWeaviateClient) {

    private val logger = LoggerFactory.getLogger(WeaviateConnectionChecker::class.java)

    @PostConstruct
    fun testConnection() {
        try {
            val status = client.misc()
                .liveChecker()
                .run()
            if (!status.result) {
                error("Unable to connect to Weaviate")
            }
            logger.info("Successfully connected to Weaviate")
        } catch (exception: Exception) {
            logger.error("Unable to connect to Weaviate", exception)
        }
    }
}