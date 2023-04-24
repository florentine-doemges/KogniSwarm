package net.doemges.kogniswarm.chat.model


/**
 * ChatCompletionRequest represents the request body for the Chat API.
 *
 * @property model ID of the model to use. See the model endpoint compatibility table for details on which models work with the Chat API. (Required)
 * @property messages The messages to generate chat completions for, in the chat format. (Required)
 * @property temperature What sampling temperature to use, between 0 and 2. Higher values like 0.8 will make the output more random, while lower values like 0.2 will make it more focused and deterministic. (Optional, defaults to 0.7)
 * @property top_p An alternative to sampling with temperature, called nucleus sampling, where the model considers the results of the tokens with top_p probability mass. So 0.1 means only the tokens comprising the top 10% probability mass are considered. (Optional, defaults to 1)
 * @property n How many chat completion choices to generate for each input message. (Optional, defaults to 1)
 * @property stream If set, partial message deltas will be sent, like in ChatGPT. Tokens will be sent as data-only server-sent events as they become available, with the stream terminated by a data: [DONE] message. (Optional, defaults to false)
 * @property stop Up to 4 sequences where the API will stop generating further tokens. (Optional, defaults to null)
 * @property max_tokens The maximum number of tokens to generate in the chat completion. (Optional, defaults to Int.MAX_VALUE)
 * @property presence_penalty Number between -2.0 and 2.0. Positive values penalize new tokens based on whether they appear in the text so far, increasing the model's likelihood to talk about new topics. (Optional, defaults to 0)
 * @property frequency_penalty Number between -2.0 and 2.0. Positive values penalize new tokens based on their existing frequency in the text so far, decreasing the model's likelihood to repeat the same line verbatim. (Optional, defaults to 0)
 * @property logit_bias Modify the likelihood of specified tokens appearing in the completion. Accepts a json object that maps tokens (specified by their token ID in the tokenizer) to an associated bias value from -100 to 100. (Optional, defaults to null)
 * @property user A unique identifier representing your end-user, which can help OpenAI to monitor and detect abuse. (Optional, defaults to null)
 */
data class ChatCompletionRequest(
    val model: String,
    val messages: List<Map<String, String>>,
    val temperature: Double? = 0.7,
    val topP: Double? = 1.0,
    val n: Int? = 1,
    val stream: Boolean? = false,
    val stop: List<String>? = null,
    val maxTokens: Int? = 2048,
    val presencePenalty: Double? = 0.0,
    val frequencyPenalty: Double? = 0.0,
    val logitBias: Map<Int, Double>? = null,
    val user: String? = null
)