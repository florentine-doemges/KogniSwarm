package net.doemges.kogniswarm.http

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.exp

class DomainDelayManager {
    companion object {
        const val MEAN = 0.5
        const val STD_DEV = 0.25
        const val MILLIS_IN_SECOND = 1000
    }

    private val lastAccessTimes = ConcurrentHashMap<String, Long>()
    fun waitBeforeAccessing(domain: Domain) {
        val lastAccessTime = lastAccessTimes.getOrDefault(domain.host, 0L)
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastAccessTime < domain.delay) {
            val randomDelay = exp(
                ThreadLocalRandom
                    .current()
                    .nextGaussian() * STD_DEV + MEAN
            ) * MILLIS_IN_SECOND // in milliseconds
            val actualDelay = randomDelay
                .toLong()
                .coerceAtLeast(domain.minDelay)
                .coerceAtMost(domain.maxDelay)
            try {
                Thread.sleep(actualDelay)
            } catch (e: InterruptedException) {
                // Handle the InterruptedException as needed
                Thread.currentThread()
                    .interrupt()
            }
        }
        lastAccessTimes[domain.host] = System.currentTimeMillis()
    }
}
