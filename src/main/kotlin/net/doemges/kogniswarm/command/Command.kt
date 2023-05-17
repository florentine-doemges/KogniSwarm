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
    description = "Analyzes and suggests improvements for the provided code. Useful for code optimization.",
    args = mapOf("code" to "The code string for evaluation.")
) {
    override fun execute(args: Map<String, List<String>>): String {
        return "Not implemented yet."
    }
}

@Component
class ExecuteCodeCommand : BaseCommand(
    name = "executeCode",
    description = "Executes the provided code snippet. Useful for dynamic code execution.",
    args = mapOf("code" to "File name to execute.")
) {
    override fun execute(args: Map<String, List<String>>): String {
        return "Not implemented yet."
    }
}

@Component
class ImproveCodeCommand : BaseCommand(
    name = "improveCode",
    description = "Improves code based on provided suggestions. Useful for code enhancement.",
    args = mapOf(
        "code" to "Code to improve.",
        "suggestions" to "Improvement suggestions."
    )
) {
    override fun execute(args: Map<String, List<String>>): String {
        return "Not implemented yet."
    }
}

@Component
class WriteTestCommand : BaseCommand(
    name = "writeTest",
    description = "Generates testable Kotlin code for the provided code. Useful for code testing automation.",
    args = mapOf("code" to "Code to test.")
) {
    override fun execute(args: Map<String, List<String>>): String {
        return "Not implemented yet."
    }
}

@Component
class SummarizeTextCommand : BaseCommand(
    name = "summarizeText",
    description = "Creates a summary of the provided text. Useful for condensing lengthy text.",
    args = mapOf("text" to "Text to summarize.")
) {
    override fun execute(args: Map<String, List<String>>): String {
        return "Not implemented yet."
    }
}

@Component
class WriteToFileCommand : BaseCommand(
    name = "writeToFile",
    description = "Writes the provided text to a specified file. Useful for data archiving.",
    args = mapOf(
        "text" to "Text to write.",
        "filename" to "Target file."
    )

) {
    override fun execute(args: Map<String, List<String>>): String {
        return "Not implemented yet."
    }
}

@Component
class ReadFromFileCommand : BaseCommand(
    name = "readFromFile",
    description = "Reads and returns the content of a specified file. Useful for data retrieval.",
    args = mapOf("filename" to "File to read.")
) {
    override fun execute(args: Map<String, List<String>>): String {
        return "Not implemented yet."
    }
}

@Component
class CloneGitRepositoryCommand : BaseCommand(
    name = "cloneGitRepository",
    description = "Clones a Git repository from a URL to a specified path. Useful for project setup.",
    args = mapOf(
        "url" to "Repository URL.",
        "path" to "Target path."
    )

) {
    override fun execute(args: Map<String, List<String>>): String {
        return "Not implemented yet."
    }
}

@Component
class GoogleSearchCommand : BaseCommand(
    name = "googleSearch",
    description = "Performs a Google search and returns results. Useful for web-based research.",
    args = mapOf("query" to "Search query.")
) {
    override fun execute(args: Map<String, List<String>>): String {
        return "Not implemented yet."
    }
}

@Component
class SendTweetCommand : BaseCommand(
    name = "sendTweet",
    description = "Sends a tweet. Useful for social media updates.",
    args = mapOf("tweet" to "Tweet content.")
) {
    override fun execute(args: Map<String, List<String>>): String {
        return "Not implemented yet."
    }
}

@Component
class DownloadFileCommand : BaseCommand(
    name = "downloadFile",
    description = "Downloads a file from a URL to a specified path. Useful for data acquisition.",
    args = mapOf("url" to "File URL.", "path" to "Target path.")
) {
    override fun execute(args: Map<String, List<String>>): String {
        return "Not implemented yet."
    }
}

@Component
class ScrapeTextCommand : BaseCommand(
    name = "scrapeText",
    description = "Extracts and returns the text content of a website. Useful for web content extraction.",
    args = mapOf("url" to "Website URL.")
) {
    override fun execute(args: Map<String, List<String>>): String {
        return "Not implemented yet."
    }
}

@Component
class ScrapeLinksCommand : BaseCommand(
    name = "scrapeLinks",
    description = "Extracts and returns all links from a website. Useful for website crawling.",
    args = mapOf("url" to "Website URL.")
) {
    override fun execute(args: Map<String, List<String>>): String {
        return "Not implemented yet."
    }
}
