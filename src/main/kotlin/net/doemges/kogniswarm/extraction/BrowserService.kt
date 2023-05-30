package net.doemges.kogniswarm.containers

import jakarta.annotation.PreDestroy
import kotlinx.coroutines.runBlocking
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