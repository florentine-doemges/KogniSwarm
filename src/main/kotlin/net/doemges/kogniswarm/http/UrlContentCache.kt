package net.doemges.kogniswarm.http

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.stereotype.Component
import java.io.File

data class CacheEntry(val timestamp: Long, val content: String)

@Component
class UrlContentCache(
    private val objectMapper: ObjectMapper
) {
    private val cacheDurationInSeconds: Long = 3600
    private val cache = HashMap<String, CacheEntry>()
    private val jsonFile = File("src/main/resources/url-content-cache.json")

    init {
        Runtime.getRuntime().addShutdownHook(Thread { persistCacheToFile() })
    }

    @PostConstruct
    fun loadCacheFromFile() {
        if (jsonFile.exists()) {
            val loadedCache: HashMap<String, CacheEntry> = objectMapper.readValue(jsonFile)
            cache.putAll(loadedCache)
        }
    }

    @PreDestroy
    fun persistCacheToFile() {
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, cache)
    }

    fun get(url: String): String? {
        val cachedResponse = cache[url]
        return if (cachedResponse != null && (System.currentTimeMillis() - cachedResponse.timestamp) / 1000 < cacheDurationInSeconds) {
            cachedResponse.content
        } else {
            null
        }
    }

    fun put(url: String, content: String) {
        cache[url] = CacheEntry(System.currentTimeMillis(), content)
    }
}
