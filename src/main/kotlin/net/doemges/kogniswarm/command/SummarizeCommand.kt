package net.doemges.kogniswarm.command

import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption

@ShellComponent
class SummarizeCommand {
    @ShellMethod("Summarize a text file")
    fun summarize(@ShellOption(value = ["-f"]) file: String) {
    }
}