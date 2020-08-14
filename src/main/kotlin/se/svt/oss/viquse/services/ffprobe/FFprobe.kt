package se.svt.oss.viquse.services.ffprobe

import java.lang.RuntimeException

val ffprobeCommand: List<String> = (
    "ffprobe -v error -select_streams v:0 -show_entries stream=duration -print_format" +
        " default=nokey=1:noprint_wrappers=1"
    ).split(" ")

fun getDuration(file: String): Double {
    val args = ffprobeCommand + file
    val process = ProcessBuilder(*args.toTypedArray())
        .redirectErrorStream(true)
        .start()

    val exitCode = process.waitFor()
    val output = process.inputStream.bufferedReader().readText()
    if (exitCode != 0) throw RuntimeException("Running ffprobe failed: $output")
    return output.trim().toDouble()
}
