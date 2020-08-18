package se.svt.oss.viquse.service

import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import se.svt.oss.viquse.services.graph.GraphService
import java.nio.file.Path

@ExtendWith(MockKExtension::class)
class GraphServiceTest {

    @InjectMockKs
    lateinit var graphService: GraphService

    @Disabled
    @Test
    fun testPlot() {

        graphService.plotLines(
            xFrames = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
            yVMAF = listOf(90.0f, 89.3f, 25.3f, 45.0f, 99.3f, 89.3f, 89.5f, 90.3f, 66.66f, 90.3f),
            destination = Path.of("~/Desktop/plottest/testplot.html")
        )
    }
}
