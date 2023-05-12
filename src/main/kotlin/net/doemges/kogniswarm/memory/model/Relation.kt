package net.doemges.kogniswarm.memory.model

import org.springframework.data.neo4j.core.schema.CompositeProperty
import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Property
import org.springframework.data.neo4j.core.schema.RelationshipProperties
import org.springframework.data.neo4j.core.schema.TargetNode

@RelationshipProperties
data class Relation(
    @Id @GeneratedValue val id: Long? = null,
    @Property("relationship_type") val relationshipType: String,
    @CompositeProperty val properties: MutableMap<String, String> = mutableMapOf(),
    @TargetNode val endNode: Entity
)