package net.doemges.kogniswarm.discord

import net.dv8tion.jda.api.events.message.MessageReceivedEvent

data class DiscordRequest(
    val event: MessageReceivedEvent? = null,
    val message: String? = null,
    val channelId: String? = null
){
    override fun toString(): String {
        return "DiscordRequest(event=${event.toString()}, message=$message, channelId=$channelId)"
    }
}
