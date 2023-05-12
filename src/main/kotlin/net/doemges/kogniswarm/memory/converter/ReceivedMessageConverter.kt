package net.doemges.kogniswarm.memory.converter

import net.doemges.kogniswarm.memory.model.Entity
import net.doemges.kogniswarm.memory.MemoryConverter
import net.dv8tion.jda.internal.entities.ReceivedMessage

class ReceivedMessageConverter : MemoryConverter<ReceivedMessage> {

    override fun convert(ownerId: String, content: ReceivedMessage): List<Entity> =
        listOf(Entity(ownerId = ownerId, labels = listOf("ReceivedMessage")).apply {
            properties["content"] = content.contentRaw
            properties["channelId"] = content.channel.id
            properties["guildId"] = content.guild.id
        })
}