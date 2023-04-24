package net.doemges.kogniswarm.chat.model


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
data class ChatCompletionResponse(
    val id: String,
    val responseType: String,
    val created: Int,
    val model: String,
    val usage: Usage,
    val choices: List<Choice>
) {

}