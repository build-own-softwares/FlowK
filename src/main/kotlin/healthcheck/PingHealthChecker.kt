package healthcheck

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import server.Server
import server.ServerPool
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class PingHealthChecker(
    private val serverPool: ServerPool,
    private val maxFailureCount: Int = 3,
) : HealthChecker {
    private val healthCheckScope = CoroutineScope(Dispatchers.IO)
    private val httpClient: HttpClient = HttpClient.newHttpClient()

    init {
        healthCheckScope.launch { perform() }
    }

    override suspend fun perform() {
        serverPool.getServers().forEach { server ->
            if(server.isUnavailable(maxFailureCount)) {
                server.changeToUnavailable()
                server.resetFailures()
            }

            delay(300L)
            val isAvailable = sendHealthCheck(server)
            if (isAvailable) {
                server.resetFailures()
                server.changeToAvailable()
                return@forEach
            }

            server.incrementFailures()
        }
    }

    override fun sendHealthCheck(server: Server): Boolean = try {
        val hostname = URI(server.hostname)
        val request = HttpRequest.newBuilder(hostname).build()
        val responseType = HttpResponse.BodyHandlers.ofString()

        val response = httpClient.send(request, responseType)
        response.statusCode() == server.successStateCode
    } catch (exception: Exception) {
        false
    }
}
