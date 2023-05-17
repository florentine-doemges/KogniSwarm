package net.doemges.kogniswarm.core

import net.dv8tion.jda.api.events.message.MessageReceivedEvent

data class Message<X>(val payload: X)

