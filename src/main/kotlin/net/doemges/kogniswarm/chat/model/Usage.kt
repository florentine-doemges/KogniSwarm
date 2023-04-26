package net.doemges.kogniswarm.chat.model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Usage represents the token usage information for the request.
 *
 * @property prompt_tokens The number of tokens in the input message.
 * @property completion_tokens The number of tokens in the completion.
 * @property total_tokens The total number of tokens used (prompt_tokens + completion_tokens).
 */
data class Usage(
    @JsonProperty("prompt_tokens") val promptTokens: Int,
    @JsonProperty("completion_tokens") val completionTokens: Int,
    @JsonProperty("total_tokens") val totalTokens: Int
)