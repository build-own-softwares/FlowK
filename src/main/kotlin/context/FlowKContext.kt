package context

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import healthcheck.HealthCheckerFactory
import loadblance.LoadBalancerFactory
import org.apache.log4j.BasicConfigurator
import server.Server
import server.ServerGroup
import server.ServerPool
import java.nio.file.Files
import java.nio.file.Path

class FlowKContext : Refreshable {
    private val objectMapper = ObjectMapper(YAMLFactory())
    private lateinit var config: FlowKConfig

    init {
        initializeLogger()
        initializeConfig()
    }

    private fun initializeLogger() {
        BasicConfigurator.configure()
    }

    private fun initializeConfig() {
        val content = Files.readString(Path.of("./config.yaml"))
        config = objectMapper.readValue(content, FlowKConfig::class.java)
    }

    fun getServerGroups(): List<ServerGroup> {
        return config.serverGroups.map {
            val (routingUrl, healthCheckStrategy, loadBalancerStrategy, serverConfigs) = it

            val servers = serverConfigs.map { serverConfig ->
                Server(serverConfig.getServerHost(), serverConfig.successCode.toInt())
            }.toMutableList()

            val serverPool = ServerPool(servers)
            val loadBalancer =
                LoadBalancerFactory.getLoadBalancer(loadBalancerStrategy, serverPool)
            val healthChecker =
                HealthCheckerFactory.getHealthChecker(healthCheckStrategy, serverPool)

            ServerGroup(routingUrl.toRegex(), loadBalancer, healthChecker, serverPool)
        }
    }

    override fun refresh() {
    }
}
