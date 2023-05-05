package net.doemges.kogniswarm.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "spring.neo4j")
data class Neo4jProperties @ConstructorBinding constructor(
    val uri: String,
    val authentication: Authentication
) {
    data class Authentication(
        val username: String,
        val password: String
    )
}
