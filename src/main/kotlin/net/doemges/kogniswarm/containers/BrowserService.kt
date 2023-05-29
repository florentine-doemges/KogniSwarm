package net.doemges.kogniswarm.containers

import org.springframework.stereotype.Service

@Service
class BrowserService {
    private val containerPool = ContainerPool(5)

    fun getWebDriver(): PooledWebDriver = containerPool.getWebDriver()
}