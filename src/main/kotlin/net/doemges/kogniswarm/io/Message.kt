package net.doemges.kogniswarm.io

open class Message<T>(val payload: T, val headers: Map<String, Any> = emptyMap()){
    override fun toString(): String {
        return "Message(payload=$payload, headers=$headers)"
    }
}