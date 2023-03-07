package datamaintain.test

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.mockserver.client.MockServerClient
import org.testcontainers.containers.MockServerContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
abstract class AbstractMonitoringSendWithHttpTest {
    companion object {
        private val mockServerImage: DockerImageName = DockerImageName
            .parse("mockserver/mockserver")
            .withTag("mockserver-5.15.0")

        @Container
        private val mockServerContainer = MockServerContainer(mockServerImage)

        // Setup in BeforeAll, don't worry
        @JvmStatic
        protected lateinit var mockServerUrl: String
        @JvmStatic
        protected lateinit var mockServerClient: MockServerClient

        @BeforeAll()
        @JvmStatic
        fun startMockServer() {
            mockServerContainer.start()
            mockServerUrl = mockServerContainer.endpoint
            mockServerClient = MockServerClient(mockServerContainer.host, mockServerContainer.serverPort)
        }

        @AfterAll()
        @JvmStatic
        fun closeMockServer() {
            mockServerContainer.close()
        }
    }

    @AfterEach()
    fun resetMockServerClient() {
        mockServerClient.reset()
    }
}