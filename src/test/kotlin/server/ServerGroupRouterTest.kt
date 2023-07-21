package server

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ServerGroupRouterTest {

    @Test
    fun `getServerGroup - url에 해당하는 서버 그룹이 있다면 정상적으로 조회된다`() {
        // Arrange
        val serverGroups = listOf(
            ServerGroup.fixture(urlPattern = "/hello".toRegex()),
            ServerGroup.fixture(urlPattern = "/bye".toRegex()),
        )
        val serverGroupRouter = ServerGroupRouter(serverGroups)

        // Act
        val serverGroup = serverGroupRouter.getServerGroup("/hello")

        // Assert
        assertNotNull(serverGroup)
    }

    @Test
    fun `getServerGroup - url에 해당하는 서버 그룹이 없다면 조회되지 않는다`() {
        // Arrange
        val serverGroups = listOf(ServerGroup.fixture(urlPattern = "/hello".toRegex()))
        val serverGroupRouter = ServerGroupRouter(serverGroups)

        // Act
        val serverGroup = serverGroupRouter.getServerGroup("/bye")

        // Assert
        assertNull(serverGroup)
    }

    @Test
    fun `getServerGroup - url 패턴에 매칭되는 서버 그룹들이 있다면 등록된 순서를 따른다`() {
        // Arrange
        val expectServerGroup = ServerGroup.fixture(urlPattern = "/hello".toRegex())
        val serverGroups = listOf(
            expectServerGroup,
            ServerGroup.fixture(urlPattern = "/hello/abc".toRegex()),
        )
        val serverGroupRouter = ServerGroupRouter(serverGroups)

        // Act
        val serverGroup = serverGroupRouter.getServerGroup("/hello")

        // Assert
        assertEquals(expectServerGroup, serverGroup)
    }
}
