package net.doemges.kogniswarm.command

import org.springframework.stereotype.Component

interface Command {
    val name: String
    val description: String
    val args: Map<String, String>
    fun execute(args: Map<String, List<String>>): String
}

abstract class BaseCommand(
    override val name: String, //name of the command
    override val description: String, //description of the command
    override val args: Map<String, String> //description of the arguments (key = argument name, value = argument description)
) : Command {

    //Returns a condensed description with name, description and arguments
    override fun toString(): String = "$name: $description\n${
        args.map { "  ${it.key}: ${it.value}" }
            .joinToString("\n")
    }"
}

@Component
class AnalyzeCodeCommand : BaseCommand(
    name = "analyzeCode",
    description = "A function accepting a string (code), returning a create chat completion API response and code improvement suggestions.",
    args = mapOf("code" to "The code string for evaluation.")
) {
    override fun execute(args: Map<String, List<String>>): String {
        return "Not implemented yet."
    }
}

@Component
class ExecuteCodeCommand : BaseCommand(
    name = "executeCode",
    description = "A function accepting a string (code) and executing it.",
    args = mapOf("code" to "The name of the file to execute")
) {
    override fun execute(args: Map<String, List<String>>): String {
        return "Not implemented yet."
    }
}

@Component
class ImproveCodeCommand : BaseCommand(
    name = "improveCode",
    description = "A function that takes in code and suggestions and returns improved code.",
    args = mapOf("code" to "The code string for evaluation.", "suggestions" to "The suggestions for improvement.")
) {
    override fun execute(args: Map<String, List<String>>): String {
        return "Not implemented yet."
    }
}

@Component
class WriteTestCommand : BaseCommand(
    name = "writeTest",
    description = "A function accepting a string (code), returning a create chat completion API response and code improvement suggestions.",
    args = mapOf("code" to "The code string for evaluation.")
) {
    override fun execute(args: Map<String, List<String>>): String {
        return "Not implemented yet."
    }
}

@Component
class SummarizeTextCommand : BaseCommand(
    name = "summarizeText",
    description = "A function accepting a string (text), returning a summary of the text.",
    args = mapOf("text" to "The text to summarize.")
) {
    override fun execute(args: Map<String, List<String>>): String {
        return "Not implemented yet."
    }
}

@Component
class WriteToFileCommand : BaseCommand(
    name = "writeToFile",
    description = "A function accepting a string (text) and a string (filename), writing the text to the file.",
    args = mapOf(
        "text" to "The text to write.",
        "filename" to "The name of the file to write to."
    )
) {
    override fun execute(args: Map<String, List<String>>): String {
        return "Not implemented yet."
    }
}

@Component
class ReadFromFileCommand : BaseCommand(
    name = "readFromFile",
    description = "A function accepting a string (filename), returning the content of the file.",
    args = mapOf("filename" to "The name of the file to read from.")
) {
    override fun execute(args: Map<String, List<String>>): String {
        return "Not implemented yet."
    }
}

@Component
class CloneGitRepositoryCommand : BaseCommand(
    name = "cloneGitRepository",
    description = "A function accepting a string (url) and a string (path), cloning the git repository to the path.",
    args = mapOf("url" to "The url of the git repository to clone.", "path" to "The path to clone the repository to.")
) {
    override fun execute(args: Map<String, List<String>>): String {
        return "Not implemented yet."
    }
}

@Component
class GoogleSearchCommand : BaseCommand(
    name = "googleSearch",
    description = "A function accepting a string (query), returning a list of search results.",
    args = mapOf("query" to "The query to search for.")
) {
    override fun execute(args: Map<String, List<String>>): String {
        return "Not implemented yet."
    }
}

@Component
class SendTweetCommand : BaseCommand(
    name = "sendTweet",
    description = "A function accepting a string (tweet), sending the tweet.",
    args = mapOf("tweet" to "The tweet to send.")
) {
    override fun execute(args: Map<String, List<String>>): String {
        return "Not implemented yet."
    }
}

@Component
class DownloadFileCommand : BaseCommand(
    name = "downloadFile",
    description = "A function accepting a string (url) and a string (path), downloading the file to the path.",
    args = mapOf("url" to "The url of the file to download.", "path" to "The path to download the file to.")
) {
    override fun execute(args: Map<String, List<String>>): String {
        return "Not implemented yet."
    }
}

@Component
class ScrapeTextCommand : BaseCommand(
    name = "scrapeText",
    description = "A function accepting a string (url), returning the text of the website.",
    args = mapOf("url" to "The url of the website to scrape.")
) {
    override fun execute(args: Map<String, List<String>>): String {
        return "Not implemented yet."
    }
}

@Component
class ScrapeLinksCommand : BaseCommand(
    name = "scrapeLinks",
    description = "A function accepting a string (url), returning a list of links on the website.",
    args = mapOf("url" to "The url of the website to scrape.")
) {
    override fun execute(args: Map<String, List<String>>): String {
        return "Not implemented yet."
    }
}
