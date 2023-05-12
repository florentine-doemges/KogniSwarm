package net.doemges.kogniswarm.memory

import net.doemges.kogniswarm.memory.model.Entity

interface MemoryConverter<T> {
    fun convert(ownerId: String, content: T): List<Entity>
}