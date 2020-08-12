// SPDX-FileCopyrightText: 2020 Sveriges Television AB
//
// SPDX-License-Identifier: EUPL-1.2

package se.svt.oss.viquse.scheduling

import mu.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import se.svt.oss.viquse.services.job.JobService

@Service
class PollScheduler(
    private val jobService: JobService
) {

    private val log = KotlinLogging.logger { }

    @Scheduled(fixedDelayString = "\${poll.job.delay}", initialDelayString = "\${poll.job.initial-delay}")
    fun pollJobs() {
        jobService.runNextJob()
    }
}
