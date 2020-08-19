package se.svt.oss.viquse.services.graph

import hep.dataforge.meta.invoke
import kscience.plotly.Plotly
import kscience.plotly.makeFile
import kscience.plotly.models.ScatterMode
import kscience.plotly.page
import kscience.plotly.plot
import kscience.plotly.scatter
import org.springframework.stereotype.Service
import se.svt.oss.viquse.config.GraphProperties
import java.nio.file.Path

@Service
class GraphService(
    private val graphProperties: GraphProperties
) {

    fun plotLines(xFrames: List<Int>, yVMAF: List<Float>, filename: String): Path {
        val destinationPath = Path.of("${graphProperties.destinationPath}/$filename.html")
        Plotly.page {
            plot {
                scatter {
                    x(*xFrames.toTypedArray())
                    y(*yVMAF.toTypedArray())
                    mode = ScatterMode.`lines+markers`
                }

                layout {
                    title = "VMAF score per frame"
                }
            }
        }.makeFile(destinationPath, show = false)
        return destinationPath.toAbsolutePath()
    }
}
