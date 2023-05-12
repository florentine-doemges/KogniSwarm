package net.doemges.kogniswarm.memory

import net.doemges.kogniswarm.memory.model.Entity
import org.springframework.data.neo4j.repository.Neo4jRepository

interface EntityRepository : Neo4jRepository<Entity, Long> {
    fun findByOwnerIdOrderByTimestamp(ownerId: String): List<Entity>
}