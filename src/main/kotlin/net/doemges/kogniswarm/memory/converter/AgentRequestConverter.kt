package net.doemges.kogniswarm.memory.converter

import net.doemges.kogniswarm.agent.model.AgentRequest
import net.doemges.kogniswarm.memory.model.Entity
import net.doemges.kogniswarm.memory.MemoryConverter

class AgentRequestConverter : MemoryConverter<AgentRequest> {
    override fun convert(ownerId: String, content: AgentRequest): List<Entity> =
        listOf(Entity(ownerId = ownerId, labels = listOf("AgentRequest")).apply {
            properties["content"] = content.content
            properties["channelId"] = content.message.channel.id
            properties["guildId"] = content.message.guild.id
        })


}