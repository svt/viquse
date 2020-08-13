// SPDX-FileCopyrightText: 2020 Sveriges Television AB
//
// SPDX-License-Identifier: EUPL-1.2

package se.svt.oss.viquse.services.callback

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import se.svt.oss.viquse.model.callback.JobProgress
import java.net.URI

@FeignClient("callback")
interface CallbackClient {

    @PostMapping
    fun sendProgressCallback(callbackUri: URI, progress: JobProgress)
}
