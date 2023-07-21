package server

import healthcheck.HealthCheckerFactory
import loadblance.LoadBalancerFactory
import org.apache.log4j.BasicConfigurator
import org.junit.jupiter.api.Test

class FlowKServerTest {

    init {
        BasicConfigurator.configure()
    }

    @Test
    fun loadContext() {
        val server = Server("http://localhost:8090", 200)
        val serverPool = ServerPool(mutableListOf(server))
        val serverGroup = ServerGroup(
            "/".toRegex(),
            LoadBalancerFactory.default(serverPool),
            HealthCheckerFactory.default(serverPool),
            serverPool,
        )
        val serverGroupRouter = ServerGroupRouter(listOf(serverGroup))

        val flowServer = FlowKServer(serverGroupRouter = serverGroupRouter, port = 8080)
        flowServer.run()
        Thread.sleep(1000)
    }
}
