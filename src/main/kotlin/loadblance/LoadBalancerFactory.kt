package loadblance

import server.ServerPool

object LoadBalancerFactory {

    fun getLoadBalancer(strategy: LoadBalancerStrategy, serverPool: ServerPool): LoadBalancer =
        when (strategy) {
            LoadBalancerStrategy.ROUND_ROBIN -> {
                RoundRobinLoadBalancer(serverPool)
            }
        }

    fun default(serverPool: ServerPool) = RoundRobinLoadBalancer(serverPool)
}
