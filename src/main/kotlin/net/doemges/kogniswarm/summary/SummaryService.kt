package net.doemges.kogniswarm.summary

import net.doemges.kogniswarm.chat.ChatService
import net.doemges.kogniswarm.chat.model.ChatMessageBundle
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class SummaryService(private val chatService: ChatService, restTemplate: RestTemplate) {

    private val defaultMode = SummaryMode.WEIGHTED

    private val tokenizer = Tokenizer("gpt-3.5-turbo", restTemplate)

    fun summarizeText(
        text: String,
        maxChunkSize: Int = 4096,
        summaryMode: SummaryMode = defaultMode
    ): String =
        text.takeIf { it.isNotEmpty() }
            ?.let {
                val tokenized = tokenizer.tokenize(it)
                val chunked = tokenized.chunked(maxChunkSize)
                chunked
                    .also { chunks ->
                        println("TextSummarizer: ${chunks.size} chunks")
                    }
                    .let { chunks ->
                        summarizeChunks(chunks, summaryMode, maxChunkSize)
                    }
            }
            ?: ""

    private fun summarizeChunks(
        chunks: List<List<String>>,
        summaryMode: SummaryMode,
        maxChunkSize: Int
    ) = chunks
        .map { chunkTokens ->
            val chunk = chunkTokens.joinToString("")
            chatService.sendToChatGpt(
                ChatMessageBundle.fromInput(
                    "$summaryMode: $chunk",
                    """"Create the summary by identifying key points, 
                    | removing redundancy, using clear and concise language, and organizing with bullet points. 
                    | Combine related information, prioritize crucial details, and revise for accuracy and clarity. 
                    | If input tokens are less than $maxChunkSize, return the original text. 
                    | Otherwise, provide a summary with a focus on important details, 
                    | ensuring the response has exactly $maxChunkSize tokens. 
                    | Preserve code at full length."""".trimMargin()
                )
            )
        }
        .let { summaries -> combineSummaries(summaries, maxChunkSize, summaryMode) }

    private fun combineSummaries(
        summaries: List<String>,
        maxChunkSize: Int = 2048,
        summaryMode: SummaryMode = defaultMode
    ): String =
        summaries
            .joinToString(" ")
            .let { combined ->
                if (combined.length <= maxChunkSize) {
                    combined
                } else {
                    summarizeText(combined, maxChunkSize, summaryMode)
                }
            }
}

enum class SummaryMode(private val prompt: String) {
    EXTRACTIVE("Provide an extractive summary of this text"),
    ABSTRACTIVE("Provide an abstractive summary of this text"),
    KEYWORDS("Summarize this text using keywords"),
    TOPIC_MODELING("Summarize this text using topic modeling"),
    GRAPH("Generate a graph-based summary of this text"),
    BULLETED("Summarize this text in a bulleted list format"),
    VISUAL("Generate a visual summary of this text"),
    SENTIMENT("Provide a sentiment-focused summary of this text"),
    TIMELINE("Summarize this text with a timeline-based approach"),
    QUESTION_ANSWER("Summarize this text in a question and answer format"),
    COMPARATIVE("Provide a comparative summary of this text"),
    CAUSAL("Summarize this text by highlighting causal relationships"),
    THEMATIC("Summarize this text based on its themes"),
    ANNOTATED("Provide an annotated summary of this text"),
    STATISTICAL("Summarize this text using statistical information"),
    HIERARCHICAL("Create a hierarchical summary of this text"),
    NARRATIVE("Summarize this text in a narrative form"),
    DIALOGUE("Provide a dialogue-based summary of this text"),
    PARAPHRASE("Summarize this text by paraphrasing it"),
    LOGS(
        "Generate an application log-like summary for this text. " +
            "Include a timestamp for each entry in the conversation, " +
            "formatted as \"YYYY-MM-DDTHH:mm:ss.SSSSSS\". "
    ),
    STRUCTURED("Provide a structured summary of this text"),
    BRAIN("Generate a summary of this text like the brain would"),
    WEIGHTED(
        "Please provide a weighted summarization of the following text. " +
            "Identify and emphasize the main points or factors based on their prominence and relevance in the text. " +
            "Text"
    );

    override fun toString(): String = prompt
}

