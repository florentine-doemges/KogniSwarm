package net.doemges.kogniswarm.core.consumer

import kotlinx.coroutines.flow.Flow
import net.doemges.kogniswarm.core.Component
import net.doemges.kogniswarm.core.Message

interface Consumer : Component {
    var input: Flow<Message<*>>?
}