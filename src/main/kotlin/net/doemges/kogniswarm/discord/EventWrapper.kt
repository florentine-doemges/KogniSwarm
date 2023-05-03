package net.doemges.kogniswarm.discord

import net.dv8tion.jda.api.events.message.MessageReceivedEvent

data class EventWrapper(val event: MessageReceivedEvent, var reaction: Reaction<String>? = null)