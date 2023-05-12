package net.doemges.kogniswarm.memory

import net.doemges.kogniswarm.chat.ChatService
import net.doemges.kogniswarm.chat.model.ChatMessageBundle
import net.doemges.kogniswarm.data.DependencySorter
import net.doemges.kogniswarm.memory.converter.StringConverter
import net.doemges.kogniswarm.summary.SummaryService
import kotlin.reflect.KClass

class Memory(private val ownerId: String, memoryService: MemoryService) {

    private val entityService = memoryService.entityService
    private val objectMapper = memoryService.objectMapper
    private val summaryService: SummaryService = memoryService.summaryService
    private val chatService: ChatService = memoryService.chatService

    private val converters: Map<KClass<*>, MemoryConverter<*>> = mapOf(
        String::class to StringConverter(),
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
                        """Please provide a brief description based on the information given in the database entry below:
                            | ${objectMapper.writeValueAsString(entity)}""".trimMargin(),
                        """Provide a simple description based on the given entry in the database. 
                            | Focus on the content of the entry and not on the technical details.""".trimMargin()
                    )
                )
                entity.summary = summary
            }
        entityService.commit(sorted)
    }

    fun getContext(maxTokens: Int = 2000): String = entityService
        .findByOwnerId(ownerId)
        .takeIf { it.isNotEmpty() }
        ?.let { list ->
            val text = list.joinToString("\n") { entity -> entity.summary ?: "" }
            if (summaryService.lengthInTokens(text) > maxTokens)
                summaryService.summarizeText(
                    text, maxTokens
                ) else text
        } ?: "I have no memories yet."
}


