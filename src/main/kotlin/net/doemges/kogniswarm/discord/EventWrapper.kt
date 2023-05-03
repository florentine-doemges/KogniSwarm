package net.doemges.kogniswarm.discord

import dev.kord.core.event.Event

data class EventWrapper(val event: Event, var reaction: Reaction<String>? = null)