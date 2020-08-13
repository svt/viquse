// SPDX-FileCopyrightText: 2020 Sveriges Television AB
//
// SPDX-License-Identifier: EUPL-1.2

package se.svt.oss.viquse.services.callback

import mu.KotlinLogging
import org.springframework.stereotype.Service
import se.svt.oss.viquse.entities.ViquseJob
import se.svt.oss.viquse.model.callback.JobProgress

@Service
class CallbackService(private val callbackClient: CallbackClient) {

    private val log = KotlinLogging.logger {}

    fun sendProgressCallback(viquseJob: ViquseJob) {
        viquseJob.progressCallbackUri?.let {
            try {
                callbackClient.sendProgressCallback(
                    callbackUri = it,
                    progress = JobProgress(
                        jobId = viquseJob.jobId,
                        externalId = viquseJob.externalId,
                        progress = viquseJob.progress,
                        status = viquseJob.status,
                        resultSummary = viquseJob.resultSummary?.copy(frameResults = emptyList())
                    )
                )
            } catch (e: Exception) {
                log.debug(e) { "Sending progress callback failed" }
            }
        }
    }
}
