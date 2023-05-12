package net.doemges.kogniswarm.memory

import net.doemges.kogniswarm.agent.AgentRequest
import net.doemges.kogniswarm.agent.AgentResponse
import net.doemges.kogniswarm.chat.ChatService
import net.doemges.kogniswarm.chat.model.ChatMessageBundle
import net.doemges.kogniswarm.data.DependencySorter
import net.doemges.kogniswarm.discord.DiscordRequest
import net.doemges.kogniswarm.summary.SummaryService
import net.dv8tion.jda.internal.entities.ReceivedMessage
import kotlin.reflect.KClass

class Memory(private val ownerId: String, memoryService: MemoryService) {

    private val entityService = memoryService.entityService
    private val objectMapper = memoryService.objectMapper
    private val summaryService: SummaryService = memoryService.summaryService
    private val chatService: ChatService = memoryService.chatService

    private val converters: Map<KClass<*>, MemoryConverter<*>> = mapOf(
        AgentRequest::class to AgentRequestConverter(),
        AgentResponse::class to AgentResponseConverter(),
        ReceivedMessage::class to ReceivedMessageConverter(),
        DiscordRequest::class to DiscordRequestConverter()
    )


    fun <T> commit(content: T) {
        @Suppress("UNCHECKED_CAST", "UNNECESSARY_NOT_NULL_ASSERTION") val converter =
            converters[content!!::class] as? MemoryConverter<T> ?: error("No converter found for ${content!!::class}")
        val entities = converter.convert(ownerId, content)
        val sorted = DependencySorter.sortWithDependency(entities) { entity, other ->
            entity.relationships.any { it.endNode == other } ?: false
        }
            .onEach { entity ->
                val summary = chatService.sendToChatGpt(
                    ChatMessageBundle.fromInput(
                        "The item below is an entry in a database. Identify the relevant content and summarize it.\n ${
                            objectMapper.writeValueAsString(
                                entity
                            )
                        }"
                    )
                )
                entity.summary = summary
            }
        entityService.commit(sorted)
    }

    fun getContext(maxTokens: Int = 2048): String = summaryService
        .summarizeText(
            entityService.findByOwnerId(ownerId)
                .joinToString("\n") { entity -> entity.summary ?: "" }, maxTokens
        )
}

class AgentRequestConverter : MemoryConverter<AgentRequest> {
    override fun convert(ownerId: String, content: AgentRequest): List<Entity> =
        listOf(Entity(ownerId = ownerId, labels = listOf("AgentRequest")).apply {
            properties["content"] = content.content
            properties["channelId"] = content.message.channel.id
            properties["guildId"] = content.message.guild.id
        })


}

class DiscordRequestConverter : MemoryConverter<DiscordRequest> {
    override fun convert(ownerId: String, content: DiscordRequest): List<Entity> =
        listOf(Entity(ownerId = ownerId, labels = listOf("DiscordRequest")).apply {
            properties["content"] = content.message ?: ""
            properties["channelId"] = content.channelId ?: ""
        })

}


interface MemoryConverter<T> {
    fun convert(ownerId: String, content: T): List<Entity>
}

class AgentResponseConverter : MemoryConverter<AgentResponse> {
    override fun convert(ownerId: String, content: AgentResponse): List<Entity> =
        listOf(Entity(ownerId = ownerId, labels = listOf("AgentResponse")).apply {
            properties["content"] = content.response
            properties["channelId"] = content.request.message.channel.id
            properties["guildId"] = content.request.message.guild.id
        })
}

class ReceivedMessageConverter : MemoryConverter<ReceivedMessage> {

    override fun convert(ownerId: String, content: ReceivedMessage): List<Entity> =
        listOf(Entity(ownerId = ownerId, labels = listOf("ReceivedMessage")).apply {
            properties["content"] = content.contentRaw
            properties["channelId"] = content.channel.id
            properties["guildId"] = content.guild.id
        })
}