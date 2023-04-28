package net.doemges.kogniswarm.memory

import org.springframework.stereotype.Service

@Service
class MemoryService {
    fun <T> createMemory(): Memory<T> {
        TODO("Not yet implemented")
    }

}

interface Memory<T> {
    fun commit(message: T)

}
