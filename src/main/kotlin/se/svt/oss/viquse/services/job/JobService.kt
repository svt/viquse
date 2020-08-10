package se.svt.oss.viquse.services.job

import mu.KotlinLogging
import org.springframework.stereotype.Service
import se.svt.oss.viquse.model.Status
import se.svt.oss.viquse.model.ViquseJob
import se.svt.oss.viquse.repository.ViquseJobRepository
import se.svt.oss.viquse.services.ffmpeg.FfmpegExecutor
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

@Service
class JobService(
        private val repository: ViquseJobRepository,
        private val ffmpegExecutor: FfmpegExecutor) {

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
            repository.saveAndFlush(newJob)
            logger.info { File("$workDir/vmaf.log").readText() }
        }
    }

    // ffmpeg -i BRATEST-FARGDANS.mxf -i output_w320_crf40.mp4  -lavfi "[1:v]scale=1920:-1[distorted];[distorted][0:v]libvmaf=log_fmt=json:log_path=./vmaf.log:n_subsample=25:psnr=false:ssim=false" -f null -
    private fun inputParams(job: ViquseJob, logPath: Path): List<String> {
        val inputParams = listOf(
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

        return inputParams
    }
}
