package net.doemges.kogniswarm.http

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.exp

class DomainDelayManager {
    private val lastAccessTimes = ConcurrentHashMap<String, Long>()
    fun waitBeforeAccessing(domain: Domain) {
        val lastAccessTime = lastAccessTimes.getOrDefault(domain.host, 0L)
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastAccessTime < domain.delay) {
            val mean = 0.5 // mean of the natural logarithm of the delay times (you can adjust this value)
            val stdDev =
                0.25 // standard deviation of the natural logarithm of the delay times (you can adjust this value)
            val randomDelay = exp(
                ThreadLocalRandom
                    .current()
                    .nextGaussian() * stdDev + mean
            ) * 1000 // in milliseconds
            val actualDelay = randomDelay
                .toLong()
                .coerceAtLeast(domain.minDelay)
                .coerceAtMost(domain.maxDelay)
            try {
                Thread.sleep(actualDelay)
            } catch (e: InterruptedException) {
                // Handle the InterruptedException as needed
                Thread.currentThread().interrupt()
            }
        }
        lastAccessTimes[domain.host] = System.currentTimeMillis()
    }
}
