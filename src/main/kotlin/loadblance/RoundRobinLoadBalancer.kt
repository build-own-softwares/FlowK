package loadblance

import context.Refreshable
import server.Server
import server.ServerPool
import java.util.concurrent.atomic.AtomicInteger

class RoundRobinLoadBalancer(
    private val serverPool: ServerPool,
) : LoadBalancer, Refreshable {
    private var currentWeight: AtomicInteger = AtomicInteger(0)
    private var serverCount: Int = serverPool.getRegisteredServerCount()

    override fun refresh() {
        currentWeight = AtomicInteger(0)
        serverCount = serverPool.getRegisteredServerCount()
    }

    override fun getTargetServer(): Server {
        var nextIndex = getNextIndex()
        var server = serverPool.getServer(nextIndex)

        var isNotIsAvailable = server.isAvailable.get().not()
        while (isNotIsAvailable) {
            nextIndex = getNextIndex()
            server = serverPool.getServer(nextIndex)
            isNotIsAvailable = server.isAvailable.get().not()
        }

        return server
    }

    override fun getNextIndex(): Int {
        currentWeight.updateAndGet { it + 1 }
        return currentWeight.get() % serverCount
    }
}
