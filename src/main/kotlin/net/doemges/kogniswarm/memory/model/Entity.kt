package net.doemges.kogniswarm.memory.model

import org.springframework.data.neo4j.core.schema.CompositeProperty
import org.springframework.data.neo4j.core.schema.DynamicLabels
import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Property
import org.springframework.data.neo4j.core.schema.Relationship
import java.time.Instant

@Node
data class Entity(
    @Id @GeneratedValue val id: Long? = null,
    @Property("owner_id") val ownerId: String,
    @Property("timestamp") val timestamp: Instant = Instant.now(),
    @Property("summary") var summary: String? = null,
    @DynamicLabels val labels: List<String> = emptyList(),
    @CompositeProperty val properties: MutableMap<String, String> = mutableMapOf(),
    @Relationship(
        type = "RELATED_TO",
        direction = Relationship.Direction.OUTGOING
    ) val relationships: MutableList<Relation> = mutableListOf()
)

