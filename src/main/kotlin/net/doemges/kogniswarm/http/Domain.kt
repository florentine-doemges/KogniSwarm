package net.doemges.kogniswarm.http

import java.net.URI

data class Domain(val host: String, val minDelay: Long, val maxDelay: Long) {
    val delay: Long = (maxDelay - minDelay) / 10
    companion object {
        fun fromUrl(url: String) = Domain(URI(url).host ?: "", 100, 3000)
    }
}