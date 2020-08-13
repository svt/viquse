// SPDX-FileCopyrightText: 2020 Sveriges Television AB
//
// SPDX-License-Identifier: EUPL-1.2

package se.svt.oss.viquse.services.job

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import se.svt.oss.viquse.entities.ResultSummary
import se.svt.oss.viquse.entities.ViquseJob
import se.svt.oss.viquse.model.Status
import se.svt.oss.viquse.model.vmaf.JobResult
import se.svt.oss.viquse.repository.ResultSummaryRepository
import se.svt.oss.viquse.repository.ViquseJobRepository
import se.svt.oss.viquse.services.callback.CallbackService
import se.svt.oss.viquse.services.ffmpeg.FfmpegExecutor
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

@Service
class JobService(
    private val repository: ViquseJobRepository,
    private val callbackService: CallbackService,
    private val resultSummaryRepository: ResultSummaryRepository,
    private val ffmpegExecutor: FfmpegExecutor
) {
    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val logger = KotlinLogging.logger { }

    fun runNextJob() {
        val newJob = repository.findByStatusInOrderByCreatedDate(listOf(Status.NEW)).firstOrNull()

        if (newJob != null) {
            try {
                val coroutineJob = Job()
                logger.debug { "Launching job $newJob" }
                newJob.status = Status.IN_PROGRESS
                repository.saveAndFlush(newJob)
                val workDir = Files.createTempDirectory("viquseJob")
                val command = inputParams(newJob, workDir.toAbsolutePath())

                runBlocking(coroutineJob + MDCContext()) {
                    val progressChannel = Channel<Int>()
                    handleProgress(progressChannel, newJob)
                    // ffmpegExecutor.run(encoreJob, profile, outputs, progressChannel)
                    ffmpegExecutor.run(
                        newJob,
                        workDir = workDir.toFile(),
                        command = command,
                        progressChannel = progressChannel
                    )
                }

                newJob.status = Status.SUCCESSFUL

                val logFile = File("$workDir/vmaf.log").readText()
                val jobResult = objectMapper.readValue<JobResult>(logFile)
                val resultSummary = ResultSummary.fromJobResult(jobResult)
                resultSummaryRepository.saveAndFlush(resultSummary)
                newJob.resultSummary = resultSummary
                repository.saveAndFlush(newJob)
                logger.info { logFile }
            } catch (exception: Exception) {
                logger.error(exception) { "Job failed" }
                repository.save(
                    newJob.apply {
                        status = Status.FAILED
                        message = exception.message
                    }
                )
            } finally {
                callbackService.sendProgressCallback(newJob)
            }
        }
    }

    private fun CoroutineScope.handleProgress(
        progressChannel: ReceiveChannel<Int>,
        viquseJob: ViquseJob
    ) {
        launch {
            progressChannel.consumeAsFlow()
                .conflate()
                .distinctUntilChanged()
                .sample(10_000)
                .collect {
                    logger.info { "RECEIVED PROGRESS $it" }
                    try {
                        repository.saveAndFlush(viquseJob.apply { progress = it })
                        callbackService.sendProgressCallback(viquseJob)
                    } catch (e: Exception) {
                        logger.warn(e) { "Error updating progress!" }
                    }
                }
        }
    }

    // ffmpeg -i BRATEST-FARGDANS.mxf -i output_w320_crf40.mp4  -lavfi "[1:v]scale=1920:-1[distorted];[distorted][0:v]libvmaf=log_fmt=json:log_path=./vmaf.log:n_subsample=25:psnr=false:ssim=false" -f null -
    private fun inputParams(job: ViquseJob, logPath: Path): List<String> = listOf(
        "ffmpeg",
        "-hide_banner",
        "-loglevel",
        "+level",
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
