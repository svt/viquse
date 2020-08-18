package se.svt.oss.viquse.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import se.svt.oss.viquse.repository.ViquseJobRepository
import se.svt.oss.viquse.services.graph.GraphService
import java.io.File
import java.nio.file.Path
import java.util.UUID

@RestController
class ViquseController(
    val viquseJobRepository: ViquseJobRepository,
    val graphService: GraphService
) {

    @GetMapping("/generatePlot/{externalId}")
    fun plotVmafResult(@PathVariable("externalId") externalId: UUID): ResponseEntity<String> {
        val optionalJob = viquseJobRepository.findById(externalId)
        if (optionalJob.isEmpty) return ResponseEntity.notFound().build()

        val job = optionalJob.get()
        val fileName = File(job.transcodedFile).nameWithoutExtension
        val frameNumbers = job.resultSummary?.frameResults?.map { it.frameNumber } ?: emptyList()
        val vmafScores = job.resultSummary?.frameResults?.map { it.vmaf } ?: emptyList()
        graphService.plotLines(frameNumbers, vmafScores, Path.of(fileName))
        return ResponseEntity.ok("Created plot with filename: $fileName")
    }
}
