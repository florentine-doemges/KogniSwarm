package net.doemges.kogniswarm.thinking

class ThoughtProcess(
    val thoughts: String,
    val reasoning: String,
    val plan: List<String>,
    val criticism: String,
    val nextAction: String = plan.first()
)
