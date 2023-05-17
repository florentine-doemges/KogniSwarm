package net.doemges.kogniswarm.core.producer

import kotlinx.coroutines.flow.Flow
import net.doemges.kogniswarm.core.Component
import net.doemges.kogniswarm.core.Message

interface Producer : Component {
    fun output(): Flow<Message<*>>
}