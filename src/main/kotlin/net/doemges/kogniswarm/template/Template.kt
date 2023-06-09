package net.doemges.kogniswarm.template

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class Template private constructor(
    private val encodedTemplate: String,
    bracketStyle: BracketStyle = BracketStyles.CURLY,
    private val nestedTemplates: Map<String, Template> = emptyMap(),
    private val replacements: Map<String, String> = emptyMap(),
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO),
    private val postProcessor: (suspend (String) -> String)? = null
) {
    private val open = bracketStyle.open
    private val close = bracketStyle.close

    companion object {
        @Suppress("unused")
        fun builder(block: Builder.() -> Unit = {}): Builder = Builder().apply(block)
    }

    class Builder {

        private val replacements: MutableMap<String, String> = mutableMapOf()
        private val nestedTemplates: MutableMap<String, Template> = mutableMapOf()
        private var encodedTemplate: String? = null
        private var bracketStyle: BracketStyle = BracketStyles.CURLY
        private var scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
        private var postProcessor: (suspend (String) -> String)? = null

        @Suppress("unused")
        fun replacement(key: String, value: String) = apply {
            replacements[key] = value
        }

        @Suppress("unused")
        fun template(key: String, template: Template) = apply {
            nestedTemplates[key] = template
        }

        @Suppress("unused")
        fun template(key: String, block: Builder.() -> Unit = {}) = apply {
            nestedTemplates[key] = builder(block).build()
        }

        @Suppress("unused")
        fun bracketStyle(bracketStyle: BracketStyle) = apply {
            this.bracketStyle = bracketStyle
        }

        @Suppress("unused")
        fun scope(scope: CoroutineScope) = apply {
            this.scope = scope
        }

        @Suppress("unused")
        fun postProcessor(postProcessor: (suspend (String) -> String)) = apply {
            this.postProcessor = postProcessor
        }

        @Suppress("unused")
        fun encodedTemplate(encodedTemplate: String) = apply {
            this.encodedTemplate = encodedTemplate
        }

        fun build(): Template = Template(
            encodedTemplate = requireNotNull(encodedTemplate) { "Template is missing" },
            bracketStyle = bracketStyle,
            nestedTemplates = nestedTemplates,
            replacements = replacements,
            scope = scope,
            postProcessor = postProcessor
        )

    }

    @Suppress("MemberVisibilityCanBePrivate")
    suspend fun replace(scope: CoroutineScope = this.scope): String = encodedTemplate
        .resolveNestedTemplates(scope)
        .resolveReplacements()
        .let { resolvedTemplate ->
            val unresolved = resolvedTemplate.getUnresolvedPlaceholders()
            require(unresolved.isEmpty()) { "Some replacements or sub-templates are missing: $unresolved" }
            postProcessor?.invoke(resolvedTemplate) ?: resolvedTemplate
        }


    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun String.resolveNestedTemplates(scope: CoroutineScope): String =
        resolveInParallel(scope, Mutex(), StringBuilder(this))

    private suspend fun resolveInParallel(scope: CoroutineScope, mutex: Mutex, result: StringBuilder): String =
        nestedTemplates
            .map { (name, template) ->
                scope.async {
                    val replacement = template.replace()
                    mutex.withLock { doReplace(result, name, replacement) }
                }
            }
            .let { replacements ->
                replacements.awaitAll()
                result.toString()
            }

    private fun doReplace(
        result: StringBuilder,
        name: String,
        replacement: String
    ): StringBuilder =
        result.replace(
            result.indexOf("$open$open$name$close$close"),
            result.indexOf("$open$open$name$close$close") + name.length + 4,
            replacement
        )

    private fun String.resolveReplacements() = replacements
        .entries
        .fold(this) { acc, (name, replacement) -> acc.replaceVariable(name, replacement) }


    private fun String.replaceSubTemplate(name: String, substitution: String) =
        replace("$open$open$name$close$close", substitution)

    private fun String.replaceVariable(name: String, substitution: String) =
        replace("$open$name$close", substitution)

    private fun String.getUnresolvedPlaceholders(): List<String> {
        val singleBrackets = Regex("\\$open.*\\$close").findAll(this)
            .map { it.value }
            .toList()
        val doubleBrackets = Regex("\\$open\\$open.*\\$close\\$close").findAll(this)
            .map { it.value }
            .toList()
        return singleBrackets + doubleBrackets
    }

}

