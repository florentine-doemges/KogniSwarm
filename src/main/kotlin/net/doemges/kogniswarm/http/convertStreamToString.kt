package net.doemges.kogniswarm.http

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

fun convertStreamToString(inputStream: InputStream): String {
    val bufferedReader = BufferedReader(InputStreamReader(inputStream))
    val stringBuilder = StringBuilder()
    var line: String?
    while (bufferedReader.readLine().also { line = it } != null) {
        stringBuilder.append(line)
    }
    return stringBuilder.toString()
}