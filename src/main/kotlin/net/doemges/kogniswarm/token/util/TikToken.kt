package net.doemges.kogniswarm.token.util

import net.doemges.kogniswarm.token.model.Encoding
import org.springframework.web.reactive.function.client.WebClient
import java.io.File

class TikToken(private val webClient: WebClient) {
    private val modelToEncoding = mapOf(
        "gpt-4" to "cl100k_base",
        "gpt-3.5-turbo" to "cl100k_base"
        // Add more models and encodings here
    )

    private val modelPrefixToEncoding = mapOf(
        "gpt-4-" to "cl100k_base",
        "gpt-3.5-turbo-" to "cl100k_base"
        // Add more model prefixes and encodings here
    )

    fun encodingForModel(modelName: String): Encoding {
        val encodingName = modelToEncoding[modelName] ?: modelPrefixToEncoding.entries.find { (prefix, _) ->
            modelName.startsWith(prefix)
        }?.value

        return when (encodingName) {
            "cl100k_base" -> cl100kBase()
            // Add more encodings here
            else -> throw IllegalArgumentException("Unknown model name: $modelName")
        }
    }

    fun cl100kBase(): Encoding {
        val mergeableRanks = downloadFile("https://openaipublic.blob.core.windows.net/encodings/cl100k_base.tiktoken")
        val specialTokens = mapOf(
            "ENDOFTEXT" to 100257,
            "FIM_PREFIX" to 100258,
            "FIM_MIDDLE" to 100259,
            "FIM_SUFFIX" to 100260,
            "ENDOFPROMPT" to 100276
        )
        return Encoding(name = "cl100k_base", mergeableRanks = mergeableRanks, specialTokens = specialTokens)
    }


    @Suppress("SameParameterValue")
    private fun downloadFile(url: String): String {
        val f: File = File("src/main/resources/" + url.substringAfterLast("/"))
        if (f.exists()) {
            return f.readText()
        } else {
            f.createNewFile()
        }
        return webClient.get()
            .uri(url)
            .retrieve()
            .bodyToMono(String::class.java)
            .block()
            ?.also {
                f.writeText(it)
            } ?: ""
    }
}
