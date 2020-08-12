// SPDX-FileCopyrightText: 2020 Sveriges Television AB
//
// SPDX-License-Identifier: EUPL-1.2

package se.svt.oss.viquse

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.Resource
import se.svt.oss.viquse.entities.ViquseJob
import se.svt.oss.viquse.repository.ViquseJobRepository
import se.svt.oss.viquse.services.job.JobService

@SpringBootTest
class ViquseApplicationTests {
    @Autowired
    lateinit var repository: ViquseJobRepository

    @Autowired
    lateinit var jobService: JobService

    @Value("classpath:test.mp4")
    lateinit var referenceFile: Resource

    @Value("classpath:test2.mp4")
    lateinit var transcodedFile: Resource

    @Test
    fun integrationTest() {
        val job = ViquseJob(referenceFile.file.absolutePath, transcodedFile.file.absolutePath)
        repository.saveAndFlush(job)

        jobService.runNextJob()
    }
}
