package net.doemges.kogniswarm.shell

import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import net.doemges.kogniswarm.io.Request
import net.doemges.kogniswarm.io.Response
import org.springframework.stereotype.Service

@Service
class ShellService(private val messageChannel: Channel<Request<String>>) {

    private final val scope = CoroutineScope(Dispatchers.IO)

    @PostConstruct
    fun setup() {
        scope.launch {
            for (request in messageChannel) {
                request.response.send(Response(executeCommand(request.message)))
            }
        }
    }

    private fun executeCommand(message: String): String {
        TODO("Not yet implemented")
    }
}