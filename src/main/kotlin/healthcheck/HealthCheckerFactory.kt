package healthcheck

import server.ServerPool

object HealthCheckerFactory {

    fun getHealthChecker(strategy: HealthCheckStrategy, serverPool: ServerPool): HealthChecker =
        when (strategy) {
            HealthCheckStrategy.PING -> {
                PingHealthChecker(serverPool)
            }
        }

    fun default(serverPool: ServerPool) = PingHealthChecker(serverPool)
}
