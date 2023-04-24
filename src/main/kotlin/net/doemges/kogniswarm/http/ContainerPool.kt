package net.doemges.kogniswarm.http

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Semaphore

class ContainerPool(private val poolSize: Int) {

    val pool = ConcurrentLinkedQueue<BrowserContainer>()
    private val available = Semaphore(poolSize)
    private val logger = LoggerFactory.getLogger(ContainerPool::class.java)

    @Scheduled(fixedRate = 10_000)
    private fun logStatus() {
        if (available.availablePermits() < poolSize) {
            logger.info("Container status: in-use=${poolSize - available.availablePermits()}, available=${available.availablePermits()}")
            pool.forEach { container ->
                logger.info("Container id=${container.id}, isStarted=${container.isStarted}")
            }
        }
    }

    fun getContainer(): BrowserContainer {
        available.acquire()
        val container = pool.poll() ?: createContainer()
        logger.info("Allocating container: id=${container.id}, available=${available.availablePermits()}")
        return container
    }

    fun returnContainer(container: BrowserContainer) {
        pool.add(container)
        available.release()
        logger.info("Releasing container: id=${container.id}, available=${available.availablePermits()}")
    }

    fun closeAll() {
        pool.forEach { it.close() }
        pool.clear()
    }

    private fun createContainer(): BrowserContainer = BrowserContainer.create()

}
