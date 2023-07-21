package http

data class HttpRequest(
    val requestLine: String,
    val requestHeaders: MutableList<String> = mutableListOf(),
    val requestBody: String = "",
)
