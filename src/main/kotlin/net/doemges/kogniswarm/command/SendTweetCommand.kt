package net.doemges.kogniswarm.command

import org.springframework.stereotype.Component

@Component
class SendTweetCommand : BaseCommand(
    name = "sendTweet",
    description = "Sends a tweet. Useful for social media updates.",
    args = mapOf("tweet" to "Tweet content.")
) {
    override fun execute(args: Map<String, List<String>>): CommandOutput {
        TODO("Not yet implemented")
    }
}