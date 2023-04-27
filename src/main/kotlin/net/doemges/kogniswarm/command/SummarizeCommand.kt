package net.doemges.kogniswarm.command

import net.doemges.kogniswarm.summary.SummaryMode
import net.doemges.kogniswarm.summary.SummaryService
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption
import java.io.File

@ShellComponent
class SummarizeCommand(private val summaryService: SummaryService) {
    @ShellMethod("Summarize a text file")
    fun summarize(@ShellOption(value = ["-f"]) file: String): String =
        summaryService.summarizeText(File(file).readText(), 2048, SummaryMode.BULLETED)
}