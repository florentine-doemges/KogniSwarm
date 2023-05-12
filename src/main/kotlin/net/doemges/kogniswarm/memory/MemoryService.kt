package net.doemges.kogniswarm.memory

import com.fasterxml.jackson.databind.ObjectMapper
import net.doemges.kogniswarm.chat.ChatService
import net.doemges.kogniswarm.summary.SummaryService
import org.springframework.stereotype.Service

@Service
class MemoryService(
    val entityService: EntityService,
    val objectMapper: ObjectMapper,
    val summaryService: SummaryService,
    val chatService: ChatService
) {
    fun createMemory(ownerId: String): Memory = Memory(ownerId, this)

}
