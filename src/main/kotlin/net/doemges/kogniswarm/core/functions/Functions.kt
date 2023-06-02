package net.doemges.kogniswarm.core.functions

import net.doemges.kogniswarm.token.util.Tokenizer
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun formatTimestamp(timestamp: Long, format: String = "yyyy-MM-dd HH:mm:ss"): String {
    val zonedDateTime = ZonedDateTime.ofInstant(
        Instant.ofEpochMilli(timestamp),
        ZoneId.systemDefault()
    )
    val formatter = DateTimeFormatter.ofPattern(format)
    return zonedDateTime.format(formatter)
}

fun <T> List<T>.limitTokens(maxTokens: Int, tokenizer: Tokenizer, serializer: (MutableList<T>) -> String): List<T> {
    var tokensLeft = maxTokens
    return fold(mutableListOf()) { acc: MutableList<T>, element ->
        val tokenize = tokenizer.tokenize(serializer(acc))
        tokensLeft -= tokenize.size
        acc.apply { if (tokensLeft > 0) add(element) }
    }
}
