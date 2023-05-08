package net.doemges.kogniswarm.http

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.stereotype.Component
import java.io.File

data class CacheEntry @JsonCreator constructor(
    @JsonProperty("timestamp") val timestamp: Long, @JsonProperty("content") val content: String
)

@Component
class UrlContentCache(
    private val objectMapper: ObjectMapper
) {
    companion object {
        const val CACHE_DURATION_IN_SECONDS = 3600L
        const val MILLISECONDS_IN_SECOND = 1000
    }

    private val cache = HashMap<String, CacheEntry>()
    private val jsonFile = File("src/main/resources/url-content-cache.json")

    init {
        Runtime.getRuntime()
            .addShutdownHook(Thread { persistCacheToFile() })
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
        objectMapper.writerWithDefaultPrettyPrinter()
            .writeValue(jsonFile, cache)
    }

    fun get(url: String): String? {
        val cachedResponse = cache[url]
        return if (cachedResponse != null &&
            (System.currentTimeMillis() - cachedResponse.timestamp) / MILLISECONDS_IN_SECOND < CACHE_DURATION_IN_SECONDS
        ) {
            cachedResponse.content
        } else {
            null
        }
    }

    fun put(url: String, content: String) {
        cache[url] = CacheEntry(System.currentTimeMillis(), content)
    }
}
