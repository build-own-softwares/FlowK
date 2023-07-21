package loadblance

import org.junit.jupiter.api.Test
import server.Server
import server.ServerPool
import kotlin.test.assertEquals

class RoundRobinLoadBalancerTest {

    @Test
    fun `getNextIndex - 최초 index는 0이 나와야 한다`() {
        // Arrange
        val servers = mutableListOf(Server.fixture())
        val serverPool = ServerPool.fixture(servers)
        val loadBalancer = RoundRobinLoadBalancer(serverPool)

        // Act
        val nextIndex = loadBalancer.getNextIndex()

        // Assert
        assertEquals(0, nextIndex)
    }


    @Test
    fun `getTargetServer`() {
        // Arrange
        val expectServer = Server.fixture()
        val servers = mutableListOf(expectServer)
        val serverPool = ServerPool.fixture(servers)
        val loadBalancer = RoundRobinLoadBalancer(serverPool)

        // Act
        val server = loadBalancer.getTargetServer()

        // Assert
        assertEquals(expectServer, server)
    }
}
