package net.doemges.kogniswarm.memory.converter

import net.doemges.kogniswarm.memory.model.Entity
import net.doemges.kogniswarm.memory.MemoryConverter

class StringConverter : MemoryConverter<String> {
    override fun convert(ownerId: String, content: String): List<Entity> = listOf(Entity(ownerId = ownerId).apply {
        properties["content"] = content
    })

}
