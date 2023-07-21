package healthcheck

import server.Server

interface HealthChecker {
    suspend fun perform()
    fun sendHealthCheck(server: Server): Boolean
}
