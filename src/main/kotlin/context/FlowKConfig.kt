package context

import healthcheck.HealthCheckStrategy
import loadblance.LoadBalancerStrategy

data class FlowKConfig(
    val serverGroups: Array<ServerGroupConfig>,
)

data class ServerGroupConfig(
    val routingUrl: String,
    val healthCheckStrategy: HealthCheckStrategy = HealthCheckStrategy.PING,
    val loadBalancerStrategy: LoadBalancerStrategy = LoadBalancerStrategy.ROUND_ROBIN,
    val servers: Array<ServerConfig>,
)

data class ServerConfig(
    val ip: String,
    val port: String,
    val healthCheckUrl: String,
    val successCode: String,
) {

    fun getServerHost(): String = ip.plus(port)
}
