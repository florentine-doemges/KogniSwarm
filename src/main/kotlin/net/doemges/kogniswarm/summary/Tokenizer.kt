package net.doemges.kogniswarm.summary

import org.springframework.web.client.RestTemplate

class Tokenizer(modelName: String, restTemplate: RestTemplate) {
    fun tokenize(input: String): List<String> {
        var out = input.toCharArray()
                .map { it.toString() }

        mergeableTokens.asSequence()
                .map { mergeableToken ->
                    mergeableToken.toCharArray()
                            .map { it.toString() }
                }
                .forEach { mergeableToken ->
                    out = out.searchAndReplaceSublist(mergeableToken) { listOf(it.joinToString("")) }
                }
        return out
    }

    private fun <T> List<T>.searchAndReplaceSublist(sublist: List<T>, replacement: (List<T>) -> List<T>): List<T> {
        if (this.isEmpty() || sublist.isEmpty() || this.size < sublist.size) return this

        val result = mutableListOf<T>()
        var i = 0

        while (i < this.size) {
            if (i + sublist.size <= this.size && this.subList(i, i + sublist.size) == sublist) {
                val replaced = replacement(sublist)
                println ("replaced: $sublist -> $replaced")
                result.addAll(replaced)
                i += sublist.size
            } else {
                val currentElement = this[i]
                result.add(currentElement)
                i++
            }
        }

        return result
    }

    private val encoding: Encoding = TikToken(restTemplate).encodingForModel(modelName)

    private val mergeableTokens = encoding.mergeableRanks.split("\n")
            .map { it.substringBefore(" ") }

}
