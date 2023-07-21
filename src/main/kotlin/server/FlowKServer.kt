package server

import context.Refreshable
import http.HttpRequestParser
import mu.KLogger
import mu.KotlinLogging
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.OutputStreamWriter
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

class FlowKServer(
    private val port: Int = 8080,
    private val backlog: Int = 16,
    private val address: InetAddress = InetAddress.getLoopbackAddress(),
    private val serverGroupRouter: ServerGroupRouter,
) : Refreshable {
    private val logger: KLogger = KotlinLogging.logger { }
    private val threadPool = Executors.newFixedThreadPool(backlog)
    private val httpClient: HttpClient = HttpClient.newHttpClient()

    fun run() {
        CompletableFuture.runAsync {
            logger.info { "Started FlowKServer $address:$port" }
            ServerSocket(port, backlog, address).use { serverSocket ->
                while (true) {
                    val clientSocket = serverSocket.accept()
                    threadPool.submit { handleRequest(clientSocket) }
                }
            }
        }
    }

    private fun handleRequest(clientSocket: Socket) {
        try {
            BufferedInputStream(clientSocket.getInputStream()).use { inputStream ->
                val httpRequest = HttpRequestParser.parse(inputStream)

                val serverGroup = serverGroupRouter.getServerGroup(httpRequest.requestLine)
                    ?: throw RuntimeException("throw Not Registered Location Error")
                val targetServer = serverGroup.loadBalancer.getTargetServer()

                val requestOperation: List<String> = httpRequest.requestLine.split(" ")
                val request = generateExecuteRequest(requestOperation, targetServer, httpRequest)
                val response = execute(request)
                OutputStreamWriter(BufferedOutputStream(clientSocket.getOutputStream())).use { writer ->
                    writeHeaderResponseHeaders(writer, response, httpRequest)
                    writeHttpResponseBody(writer, response)
                }
            }
        } catch (exception: Exception) {
            logger.info { "FlowKServer.handleRequest() exception = $exception" }
        } finally {
            clientSocket.close()
        }
    }

    private fun generateExecuteRequest(
        requestOperation: List<String>,
        targetServer: Server,
        httpRequest: http.HttpRequest,
    ): ExecuteCommand {
        val requestUri: String = requestOperation.first { it.contains("/") }
        val requestFullUrl: URI = URI.create(targetServer.hostname.plus(requestUri))
        val requestMethod: String = requestOperation.first()

        return ExecuteCommand(requestFullUrl, requestMethod, httpRequest)
    }

    private fun execute(executeCommand: ExecuteCommand): HttpResponse<ByteArray> {
        val (requestFullUrl, requestMethod, httpRequest) = executeCommand
        val httpBody = HttpRequest.BodyPublishers.ofByteArray(httpRequest.requestBody.toByteArray())
        val sendHttpRequest = HttpRequest.newBuilder(requestFullUrl)
            .version(HttpClient.Version.HTTP_1_1)
            .method(requestMethod, httpBody)
            .build()

        return httpClient.send(sendHttpRequest, HttpResponse.BodyHandlers.ofByteArray())
    }

    private fun writeHeaderResponseHeaders(
        writer: OutputStreamWriter,
        response: HttpResponse<ByteArray>,
        httpRequest: http.HttpRequest,
    ) {
        writer.write("HTTP/1.1 ${response.statusCode()} OK\r\n")
        writer.write("Date: ${{ Date() }}\r\n")
        writer.write("Content-length: ${response.body().size}\r\n")
        for (requestHeader in httpRequest.requestHeaders) {
            writer.write("$requestHeader\r\n")
        }
    }

    private fun writeHttpResponseBody(
        writer: OutputStreamWriter,
        response: HttpResponse<ByteArray>,
    ) {
        writer.write(CharArray(response.body().size) { response.body()[it].toInt().toChar() })
        writer.flush()
    }

    override fun refresh() {
    }
}
