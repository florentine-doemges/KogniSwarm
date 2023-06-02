package net.doemges.kogniswarm.extraction.service

import jakarta.annotation.PreDestroy
import kotlinx.coroutines.runBlocking
import net.doemges.kogniswarm.extraction.util.ContainerPool
import net.doemges.kogniswarm.extraction.util.PooledWebDriver
import org.springframework.stereotype.Service

@Service
class BrowserService {
    private val containerPool = ContainerPool(5)

    fun getWebDriver(): PooledWebDriver = containerPool.getWebDriver()

    @PreDestroy
    fun closeAll() = runBlocking {
        containerPool.closeAll()
    }
}