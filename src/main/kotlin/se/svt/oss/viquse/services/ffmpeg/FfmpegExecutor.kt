package se.svt.oss.viquse.services.ffmpeg

import mu.KotlinLogging
import org.springframework.stereotype.Service
import se.svt.oss.viquse.entities.ViquseJob
import java.io.File
import java.util.concurrent.TimeUnit

@Service
class FfmpegExecutor() {

    private val log = KotlinLogging.logger { }

    private val logLevelRegex = Regex(".*\\[(?<level>debug|info|warning|error)].*")

    fun getLoglevel(line: String) = logLevelRegex.matchEntire(line)?.groups?.get("level")?.value
    val progressRegex = Regex(".*frame= *(?<frame>[\\d+]+) fps= *(?<fps>[\\d.+]+) .* speed= *(?<speed>[0-9.e-]+x) *")

    fun run(
        viquseJob: ViquseJob,
        workDir: File,
        command: List<String>
    ): List<String> {

        return try {
            runFfmpeg(command, workDir, 100) {
                log.info { "Progress: $it" }
            }
            emptyList()
        } catch (e: Exception) {
            log.error(e) { "Failed Job" }
            throw e
        }
    }

    private fun runFfmpeg(
        command: List<String>,
        workDir: File,
        numFrames: Int,
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
                val progress = getProgress(numFrames, line)
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

    private fun totalProgress(subtaskProgress: Int, subtaskIndex: Int, subtaskCount: Int) =
        (subtaskIndex * 100 + subtaskProgress) / subtaskCount

    private fun getProgress(numFrames: Int, line: String): Int? {
        return if (numFrames > 0) {
            progressRegex.matchEntire(line)?.let { matchResult ->
                val frame = matchResult.groups["frame"]?.value?.toInt() ?: 0
                100 * frame / numFrames
            }
        } else {
            null
        }
    }
}
