package net.doemges.kogniswarm.extraction

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import org.apache.tika.Tika
import org.jsoup.Jsoup
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import reactor.core.publisher.Mono
import java.util.regex.Matcher
import java.util.regex.Pattern


@OptIn(FlowPreview::class)
class ContentExtractor(private val contentType: ExtractionContentType, private val tika: Tika = Tika()) {

    fun extract(result: Mono<String>): Flow<Extract> = result.flatMapMany { content ->
        Flux.create { sink ->
            val mimeType = tika.detect(content.byteInputStream())
            when {
                mimeType.startsWith("text/html") -> extractFromHtml(content, sink)
                mimeType.startsWith("text/plain") -> extractFromPlainText(content, sink)
                else -> extractElse(content, sink)
            }
            sink.complete()
        }
    }
        .asFlow()
        .flatMapConcat { extractItems(it) }
        .map { Extract(it) }

    private fun extractItems(it: String): Flow<String> = when (contentType) {
        ExtractionContentType.TEXT -> Flux.just(it)
            .asFlow()

        else -> Flux.fromIterable(extractUrls(it))
            .asFlow()
    }

    private fun extractElse(content: String, sink: FluxSink<String>) {
        tika.parseToString(content.byteInputStream())
            .also { sink.next(it) }
    }

    private fun extractFromPlainText(content: String, sink: FluxSink<String>) {
        sink.next(content)
    }

    private fun extractUrls(text: String): List<String> {
        val containedUrls: MutableList<String> = ArrayList()
        val urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)"
        val pattern: Pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE)
        val urlMatcher: Matcher = pattern.matcher(text)
        while (urlMatcher.find()) {
            containedUrls.add(
                text.substring(
                    urlMatcher.start(0),
                    urlMatcher.end(0)
                )
            )
        }
        return containedUrls
    }

    private fun extractFromHtml(content: String, sink: FluxSink<String>) {
        when (contentType) {
            ExtractionContentType.TEXT -> Jsoup
                .parse(content)
                .body()
                .text()
                .also { sink.next(it) }

            ExtractionContentType.IMAGES -> Jsoup
                .parse(content)
                .select("img[src]")
                .map { it.attr("abs:src") }
                .forEach { sink.next(it) }

            ExtractionContentType.LINKS -> Jsoup
                .parse(content)
                .select("a[href]")
                .map { it.attr("abs:href") }
                .forEach { sink.next(it) }
        }
    }
}