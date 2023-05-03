package net.doemges.kogniswarm.io

import kotlinx.coroutines.channels.SendChannel

data class Request<T>(val message: T, val response: SendChannel<Response<T>>)

