package se.svt.oss.viquse

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
class ViquseApplication

fun main(args: Array<String>) {
    runApplication<ViquseApplication>(*args)
}
