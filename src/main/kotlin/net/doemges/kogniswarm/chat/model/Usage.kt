package net.doemges.kogniswarm.chat.model

/**
 * Usage represents the token usage information for the request.
 *
 * @property prompt_tokens The number of tokens in the input message.
 * @property completion_tokens The number of tokens in the completion.
 * @property total_tokens The total number of tokens used (prompt_tokens + completion_tokens).
 */
data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)