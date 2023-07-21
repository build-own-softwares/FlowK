package server

import healthcheck.HealthChecker
import healthcheck.HealthCheckerFactory
import loadblance.LoadBalancer
import loadblance.LoadBalancerFactory

class ServerGroup(
    private val urlPattern: Regex,
    val loadBalancer: LoadBalancer,
    val healthChecker: HealthChecker,
    val serverPool: ServerPool,
) {

    fun isSupportedRoutingUrl(requestUrl: String) = urlPattern.containsMatchIn(requestUrl)

    companion object {
        fun fixture(
            urlPattern: Regex = Regex("/"),
            loadBalancer: LoadBalancer = LoadBalancerFactory.default(ServerPool(mutableListOf())),
            healthChecker: HealthChecker = HealthCheckerFactory.default(ServerPool(mutableListOf())),
            serverPool: ServerPool = ServerPool(mutableListOf()),
        ): ServerGroup = ServerGroup(urlPattern, loadBalancer, healthChecker, serverPool)
    }
}
