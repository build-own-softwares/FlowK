package server

import java.net.URI

data class ExecuteCommand(
    val requestFullUrl: URI,
    val requestMethod: String,
    val httpRequest: http.HttpRequest,
)
