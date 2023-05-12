package net.doemges.kogniswarm.memory.converter

import net.doemges.kogniswarm.agent.model.AgentResponse
import net.doemges.kogniswarm.memory.model.Entity
import net.doemges.kogniswarm.memory.MemoryConverter

class AgentResponseConverter : MemoryConverter<AgentResponse> {
    override fun convert(ownerId: String, content: AgentResponse): List<Entity> =
        listOf(Entity(ownerId = ownerId, labels = listOf("AgentResponse")).apply {
            properties["content"] = content.response
            properties["channelId"] = content.request.message.channel.id
            properties["guildId"] = content.request.message.guild.id
        })
}