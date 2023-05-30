package net.doemges.kogniswarm.extraction

import jakarta.annotation.PreDestroy
import kotlinx.coroutines.runBlocking
import net.doemges.kogniswarm.containers.ContainerPool
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