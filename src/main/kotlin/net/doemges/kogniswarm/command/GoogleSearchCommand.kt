package net.doemges.kogniswarm.command

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class GoogleSearchCommand(
    private val webClientBuilder: WebClient.Builder,
    @Value("\${google.search.custom.api.key}") val googleCustomSearchApiKey: String,
    @Value("\${google.search.custom.engine.id}") val googleCustomSearchEngineId: String
) : BaseCommand(
    name = "googleSearch",
    description = "Performs a Google search and returns results. Useful for web-based research.",
    args = mapOf("query" to "Search query.")
) {
    class Builder : CommandBuilder()

    class OutputBuilder : CommandOutputBuilder {
        private val results = mutableListOf<Result>()
        fun results(function: ResultsBuilder.() -> Unit) = apply {
            results.addAll(
                ResultsBuilder().apply(function)
                    .build()
            )
        }

        fun build(input: CommandInput) = CommandOutput(input, results)
    }

    class ResultsBuilder {
        private val results = mutableListOf<Result>()
        fun result(function: Result.Builder.() -> Unit) = apply {
            results.add(
                Result.Builder()
                    .apply(function)
                    .build()
            )
        }

        fun build() = results

    }

    class Result(val url: String, val title: String) {
        class Builder {
            private var url: String? = null
            private var title: String? = null

            fun url(url: String) = apply { this.url = url }

            fun title(title: String) = apply { this.title = title }

            fun build() = Result(
                url ?: error("url must be set"),
                title ?: error("title must be set")
            )

        }
    }


    override suspend fun execute(commandInput: CommandInput): CommandOutput {
        val query = commandInput.args["query"] ?: error("Search query must be set")

        val searchUrl = "https://www.googleapis.com/customsearch/v1"
        val webClient = webClientBuilder.baseUrl(searchUrl)
            .build()
        return webClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .queryParam("key", googleCustomSearchApiKey)
                    .queryParam("cx", googleCustomSearchEngineId)
                    .queryParam("q", query)
                    .build()
            }
            .retrieve()
            .bodyToMono(Search::class.java)
            .flatMap { search ->
                val outputBuilder = OutputBuilder()
                search.items?.forEach {
                    outputBuilder.results {
                        result {
                            url(it.link)
                            title(it.title)
                        }
                    }
                }
                Mono.just(outputBuilder.build(commandInput))
            }
            .awaitSingle()
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Search(
        val items: List<Item>?
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Item(
        val title: String,
        val link: String
    )
}