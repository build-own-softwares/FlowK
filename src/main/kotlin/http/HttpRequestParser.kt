package http

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

object HttpRequestParser {

    fun parse(inputStream: InputStream): HttpRequest {
        return BufferedReader(InputStreamReader(inputStream)).use { reader ->
            val requestLine = reader.readLine()

            var readLine: String
            val requestHeaders = mutableListOf<String>()
            while (reader.readLine().also { readLine = it } != null && readLine.isNotEmpty()) {
                requestHeaders.add(readLine)
            }

            val requestBodyBuilder = StringBuilder()
            while (reader.ready()) {
                requestBodyBuilder.append(reader.readLine())
            }
            val requestBody = requestBodyBuilder.toString()
            HttpRequest(requestLine, requestHeaders, requestBody)
        }
    }
}
