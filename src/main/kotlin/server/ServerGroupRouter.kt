package server


class ServerGroupRouter(
    private val serverGroups: List<ServerGroup>,
) {

    fun getServerGroup(requestUrl: String) = serverGroups.find { it.isSupportedRoutingUrl(requestUrl) }

    companion object {
        fun fixture(serverGroups: List<ServerGroup> = listOf()) = ServerGroupRouter(serverGroups)
    }
}
