package net.doemges.kogniswarm.agent

import org.springframework.shell.InputProvider
import org.springframework.shell.ResultHandler

interface ShellOperator : InputProvider, ResultHandler<Any> {
    val customResultHandler: ResultHandler<Any>?
    suspend fun start()
}
