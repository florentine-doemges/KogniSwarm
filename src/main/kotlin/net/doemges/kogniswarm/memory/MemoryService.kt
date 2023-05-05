package net.doemges.kogniswarm.memory

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MemoryService(
    private val mementoRepository: MementoRepository
) {
    private val logger: Logger = LoggerFactory.getLogger(MemoryService::class.java)

    fun createMemory(id: String): Memory<String> = Memory(id) { message: Memento ->
        saveToNeo4j(message)
    }

    private fun saveToNeo4j(memento: Memento) {
        mementoRepository.save(memento)
        logger.info("Saved memento to Neo4j with id: ${memento.uuid} and content '${memento.content}'")
    }
}

class Memory<T>(private val id: String, private val onSave: (message: Memento) -> Unit) {

    private val logger: Logger = LoggerFactory.getLogger(Memory::class.java)
    fun commit(message: Memento) {
        logger.info("Committing message $message to memory $id")
        onSave(message)
    }
}
