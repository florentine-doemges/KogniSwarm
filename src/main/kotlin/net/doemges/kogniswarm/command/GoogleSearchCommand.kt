package net.doemges.kogniswarm.command

import org.springframework.stereotype.Component

@Component
class GoogleSearchCommand : BaseCommand(
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


    override fun execute(commandInput: CommandInput): CommandOutput {
        TODO("Not yet implemented")
    }
}