package net.doemges.kogniswarm.memory

import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.support.UUIDStringGenerator
import java.time.Instant
import java.util.*

@Node("Memento")
data class Memento(
    @Id
    @GeneratedValue(UUIDStringGenerator::class)
    val uuid: String = UUID.randomUUID()
            .toString(),

    val agentId: String,
    val authorId: String,
    val content: String,
    val timestamp: Instant = Instant.now()
)
