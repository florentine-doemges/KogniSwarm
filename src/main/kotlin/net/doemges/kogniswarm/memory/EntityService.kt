package net.doemges.kogniswarm.memory

import net.doemges.kogniswarm.memory.model.Entity
import org.springframework.stereotype.Service

@Service
class EntityService(private val entityRepository: EntityRepository) {
    fun commit(sorted: List<Entity>) {
        sorted.forEach { entityRepository.save(it) }
    }

    fun findByOwnerId(ownerId: String) = entityRepository.findByOwnerIdOrderByTimestamp(ownerId)

}
