package net.doemges.kogniswarm.core.processor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.camel.AsyncCallback
import org.apache.camel.AsyncProcessor
import org.apache.camel.Exchange
import java.util.concurrent.CompletableFuture

abstract class CoroutineAsyncProcessor(
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : AsyncProcessor,
    CoroutineScope by scope {

    abstract suspend fun processSuspend(exchange: Exchange)
    override fun process(exchange: Exchange, callback: AsyncCallback): Boolean {

        launch {
            try {
                processSuspend(exchange)
                callback.done(false)
            } catch (e: Exception) {
                exchange.setException(e)
                callback.done(false)
            }
        }
        return false
    }

    override fun process(exchange: Exchange) {
        launch {
            try {
                processSuspend(exchange)
            } catch (e: Exception) {
                exchange.setException(e)
            }
        }
    }

    override fun processAsync(exchange: Exchange): CompletableFuture<Exchange> {
        val future = CompletableFuture<Exchange>()

        launch {
            try {
                processSuspend(exchange)
                future.complete(exchange)
            } catch (e: Exception) {
                future.completeExceptionally(e)
            }
        }

        return future
    }
}