package se.svt.oss.viquse.services.job

import org.springframework.stereotype.Service
import se.svt.oss.viquse.repository.ViquseJobRepository

@Service
class JobService(private val repository: ViquseJobRepository) {
    fun runNextJob() {
        TODO("Not yet implemented")
    }
}
