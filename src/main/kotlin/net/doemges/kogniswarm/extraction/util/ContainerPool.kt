package net.doemges.kogniswarm.extraction.util

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.slf4j.LoggerFactory

@OptIn(ExperimentalCoroutinesApi::class)
class ContainerPool(poolSize: Int) {
    private val pool = Channel<BrowserContainer>(poolSize)
    private val containers = mutableListOf<BrowserContainer>()
    private val mutex = Mutex()

    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        logger.debug("Initializing ContainerPool with size $poolSize")
        Runtime.getRuntime()
            .addShutdownHook(Thread {
                runBlocking {
                    closeAll()
                }
            })
    }

    fun getWebDriver(): PooledWebDriver = PooledWebDriver(this::returnContainer, this::getContainer)

    private suspend fun getContainer(): BrowserContainer = mutex.withLock {
        if (!pool.isEmpty) {
            pool.receive()
        } else {
            createContainer()
        }
    }

    private suspend fun returnContainer(container: BrowserContainer) {
        pool.send(container)
        logger.debug("Releasing WebDriver: id=${container.id}")
    }

    fun closeAll() {
        for (container in containers) {
            container.stop()
            container.close()
        }
        pool.close()
    }

    private fun createContainer(): BrowserContainer = BrowserContainer
        .create()
        .also {
            containers.add(it)
            logger.debug("Created WebDriver: id=${it.id}")
        }
}

