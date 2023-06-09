package net.doemges.kogniswarm.template

class BracketStyles private constructor() {
    companion object {
        val CURLY = BracketStyle("{", "}")
        val SQUARE = BracketStyle("[", "]")
        val ANGLE = BracketStyle("<", ">")
        val ROUND = BracketStyle("(", ")")
    }
}