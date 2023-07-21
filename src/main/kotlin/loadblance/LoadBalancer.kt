package loadblance

import server.Server

interface LoadBalancer {
    fun getTargetServer(): Server
    fun getNextIndex(): Int
}
