package se.svt.oss.viquse.controller

import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import se.svt.oss.viquse.model.Status
import se.svt.oss.viquse.repository.ViquseJobRepository
import se.svt.oss.viquse.services.graph.GraphService
import java.io.File
import java.nio.file.Path

@RestController
class ViquseController(
    val viquseJobRepository: ViquseJobRepository,
    val graphService: GraphService
) {

    private val logger = KotlinLogging.logger { }

    @GetMapping("/generatePlot/{externalId}")
    fun plotVmafResult(@PathVariable("externalId") externalId: String): ResponseEntity<String> {
        try {
            val jobList = viquseJobRepository.findByExternalIdIn(listOf(externalId))
            logger.debug { "Found list containing ${jobList.size} jobs" }

            if (jobList.isEmpty()) return ResponseEntity.notFound().build()

            val job = jobList
                .filter { it.status == Status.SUCCESSFUL }
                .sortedByDescending { it.lastModifiedDate }
                .first()
            logger.debug { "Chose job $job with lastModifiedDate ${job.lastModifiedDate}" }
            val fileName = File(job.transcodedFile).nameWithoutExtension
            val frameNumbers = job.resultSummary?.frameResults?.map { it.frameNumber } ?: emptyList()
            val vmafScores = job.resultSummary?.frameResults?.map { it.vmaf } ?: emptyList()
            val destination = graphService.plotLines(frameNumbers, vmafScores, Path.of(fileName))
            return ResponseEntity.ok("Created plot with filename: $fileName at destination $destination")
        } catch (e: Exception) {
            logger.error(e) { "Error encountered when trying to generate plot: ${e.message}" }
            return ResponseEntity.badRequest().body("Error: ${e.message}")
        }
    }
}
