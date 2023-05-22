package net.doemges.kogniswarm.command

import org.springframework.stereotype.Component

@Component
class SendTweetCommand : BaseCommand(
    name = "sendTweet",
    description = "Sends a tweet. Useful for social media updates.",
    args = mapOf("tweet" to "Tweet content.")
) {
    override suspend fun execute(commandInput: CommandInput): CommandOutput {
        TODO("Not yet implemented")
    }
}