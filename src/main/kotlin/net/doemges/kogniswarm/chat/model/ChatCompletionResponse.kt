package net.doemges.kogniswarm.chat.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty


/**
 * ChatCompletionResponse represents the response body for the Chat API.
 *
 * @property id The unique identifier for the completion.
 * @property responseType The type of object, which is "chat.completion" in this case.
 * @property created A Unix timestamp indicating when the completion was created.
 * @property model The ID of the model used for generating the completion.
 * @property usage Information about the token usage for the request.
 * @property choices A list of choices generated for the given input message.
 */
data class ChatCompletionResponse @JsonCreator constructor(
    @JsonProperty("id") val id: String,
    @JsonProperty("object") val responseType: String,
    @JsonProperty("created") val created: Int,
    @JsonProperty("model") val model: String,
    @JsonProperty("usage") val usage: Usage,
    @JsonProperty("choices") val choices: List<Choice>
)