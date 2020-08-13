// SPDX-FileCopyrightText: 2020 Sveriges Television AB
//
// SPDX-License-Identifier: EUPL-1.2

package se.svt.oss.viquse.model.callback

import se.svt.oss.viquse.entities.ResultSummary
import se.svt.oss.viquse.model.Status
import java.util.UUID

data class JobProgress(
    val jobId: UUID,
    val externalId: String?,
    val progress: Int,
    val status: Status,
    val resultSummary: ResultSummary?
)
