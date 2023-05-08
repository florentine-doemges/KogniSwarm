package net.doemges.kogniswarm.discord

import net.doemges.kogniswarm.io.OutputProcessor
import net.dv8tion.jda.api.JDA

class DiscordOutputProcessor(private val jda: JDA) :
    OutputProcessor<DiscordRequest, DiscordResponse> {
    override fun processRequest(payload: DiscordRequest): DiscordResponse =
        DiscordResponse("Sent message", payload).also{
            jda.getTextChannelById(payload.channelId!!)
                ?.sendMessage(payload.message!!)
                ?.queue()
        }

}
