package net.doemges.kogniswarm.shell

import kotlin.script.experimental.api.ScriptDiagnostic

class ScriptExecutionException(reports: List<ScriptDiagnostic>) :
    RuntimeException("Script execution failed: ${reports.joinToString("\n")}")