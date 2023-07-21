package server

class ServerPool(
    private val servers: MutableList<Server>,
) {

    fun getRegisteredServerCount(): Int = servers.size

    fun getServer(index: Int) = servers[index]

    fun getServers() = servers

    companion object {
        fun fixture(servers: MutableList<Server> = mutableListOf()) = ServerPool(servers)
    }
}
