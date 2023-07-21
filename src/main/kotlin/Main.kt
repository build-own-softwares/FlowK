import context.FlowKContext
import server.FlowKServer
import server.ServerGroupRouter

fun main(args: Array<String>) {
    val flowKContext = FlowKContext()
    val serverGroups = flowKContext.getServerGroups()
    val flowKServer = FlowKServer(serverGroupRouter = ServerGroupRouter(serverGroups))
    flowKServer.run()
}
