package se.svt.oss.viquse.services.job

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import se.svt.oss.viquse.entities.ResultSummary
import se.svt.oss.viquse.model.Status
import se.svt.oss.viquse.entities.ViquseJob
import se.svt.oss.viquse.model.vmaf.JobResult
import se.svt.oss.viquse.repository.ResultSummaryRepository
import se.svt.oss.viquse.repository.ViquseJobRepository
import se.svt.oss.viquse.services.ffmpeg.FfmpegExecutor
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

@Service
class JobService(
    private val repository: ViquseJobRepository,
    private val resultSummaryRepository: ResultSummaryRepository,
    private val ffmpegExecutor: FfmpegExecutor
) {
    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val logger = KotlinLogging.logger { }

    fun runNextJob() {
        val newJob = repository.findByStatusInOrderByCreatedDate(listOf(Status.NEW)).firstOrNull()

        if (newJob != null) {
            logger.debug { "Launching job $newJob" }
            newJob.status = Status.IN_PROGRESS
            repository.saveAndFlush(newJob)
            val workDir = Files.createTempDirectory("viquseJob")
            val command = inputParams(newJob, workDir.toAbsolutePath())
            ffmpegExecutor.run(newJob, workDir = workDir.toFile(), command = command)
            newJob.status = Status.SUCCESSFUL

            val logFile = File("$workDir/vmaf.log").readText()
            val jobResult = objectMapper.readValue<JobResult>(logFile)
            val resultSummary = ResultSummary.fromJobResult(jobResult)
            resultSummaryRepository.saveAndFlush(resultSummary)
            newJob.resultSummary = resultSummary
            repository.saveAndFlush(newJob)
            logger.info { logFile }
        }
    }

    // ffmpeg -i BRATEST-FARGDANS.mxf -i output_w320_crf40.mp4  -lavfi "[1:v]scale=1920:-1[distorted];[distorted][0:v]libvmaf=log_fmt=json:log_path=./vmaf.log:n_subsample=25:psnr=false:ssim=false" -f null -
    private fun inputParams(job: ViquseJob, logPath: Path): List<String> = listOf(
        "ffmpeg",
        "-hide_banner",
        "-i",
        job.referenceFile,
        "-i",
        job.transcodedFile,
        "-lavfi",
        "[1:v]scale=1920:-1[distorted];[distorted][0:v]libvmaf=log_fmt=json:log_path=$logPath/vmaf.log:n_subsample=1:psnr=true:ssim=true",
        "-f",
        "null",
        "-"
    )
}
