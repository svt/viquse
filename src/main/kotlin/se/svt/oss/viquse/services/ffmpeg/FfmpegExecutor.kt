// SPDX-FileCopyrightText: 2020 Sveriges Television AB
//
// SPDX-License-Identifier: EUPL-1.2

package se.svt.oss.viquse.services.ffmpeg

import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.sendBlocking
import mu.KotlinLogging
import org.springframework.stereotype.Service
import se.svt.oss.viquse.entities.ViquseJob
import se.svt.oss.viquse.services.ffprobe.getDuration
import java.io.File
import java.util.concurrent.TimeUnit

@Service
class FfmpegExecutor() {

    private val log = KotlinLogging.logger { }

    private val logLevelRegex = Regex(".*\\[(?<level>debug|info|warning|error)].*")

    fun getLoglevel(line: String) = logLevelRegex.matchEntire(line)?.groups?.get("level")?.value
    val progressRegex = Regex(
        ".*frame= *(?<frame>[\\d+]+) fps= *(?<fps>[\\d.+]+) .*" +
            " time= *(?<hours>[\\d]+):(?<minutes>[\\d]+):(?<seconds>[\\d.]+) .*" +
            " speed= *(?<speed>[0-9.e-]+x) *"
    )

    fun run(
        viquseJob: ViquseJob,
        workDir: File,
        command: List<String>,
        progressChannel: SendChannel<Int>
    ): List<String> {

        return try {
            val duration = getDuration(viquseJob.referenceFile)

            runFfmpeg(command, workDir, duration) {
                log.info { "Progress: $it" }
                progressChannel.sendBlocking(it)
            }
            progressChannel.close()
            emptyList()
        } catch (e: Exception) {
            log.error(e) { "Failed Job" }
            throw e
        }
    }

    private fun runFfmpeg(
        command: List<String>,
        workDir: File,
        duration: Double,
        onProgress: (Int) -> Unit
    ) {
        log.info { "Running" }
        log.info { command.joinToString(" ") }

        val ffmpegProcess = ProcessBuilder(command)
            .directory(workDir)
            .redirectErrorStream(true)
            .start()

        val errorLines = mutableListOf<String>()
        ffmpegProcess.inputStream.bufferedReader().useLines { lines ->
            lines.forEach { line ->
                val progress = getProgress(duration, line)
                if (progress != null) {
                    onProgress(progress)
                } else {
                    when (getLoglevel(line)) {
                        "warning" -> {
                            log.warn { line }
                        }
                        "error" -> {
                            log.warn { line }
                            errorLines.add(line)
                        }
                        else -> log.info { line }
                    }
                }
            }
        }

        finishProcess(ffmpegProcess, errorLines, onProgress)

        log.info { "Done" }
    }

    // https://stackoverflow.com/questions/37043114/how-to-stop-a-command-being-executed-after-4-5-seconds-through-process-builder
    private fun finishProcess(
        ffmpegProcess: Process,
        errorLines: MutableList<String>,
        onProgress: (Int) -> Unit
    ) {
        ffmpegProcess.waitFor(1L, TimeUnit.MINUTES)
        ffmpegProcess.destroy()

        val exitCode = ffmpegProcess.waitFor()
        if (exitCode != 0) {
            throw RuntimeException(
                "Error running ffmpeg (exit code $exitCode) :\n${errorLines.reversed().joinToString("\n")}"
            )
        }
        onProgress(100)
    }

    private fun getProgress(duration: Double, line: String): Int? {
        return if (duration > 0) {
            progressRegex.matchEntire(line)?.let { matchResult ->
                val hours = matchResult.groups["hours"]?.value?.toInt() ?: 0
                val minutes = matchResult.groups["minutes"]?.value?.toInt() ?: 0
                val seconds = matchResult.groups["seconds"]?.value?.toDouble() ?: 0.0
                val positionInSeconds = hours * 3600 + minutes * 60 + seconds
                (100 * positionInSeconds / duration).toInt()
            }
        } else {
            null
        }
    }
}
