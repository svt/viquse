// SPDX-FileCopyrightText: 2020 Sveriges Television AB
//
// SPDX-License-Identifier: EUPL-1.2

package se.svt.oss.viquse.entities

import se.svt.oss.viquse.model.vmaf.JobResult
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class ResultSummary(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val version: String,
    val vmafScore: Float,
    val execFps: Float,
    val model: String,
    val scaledWidth: Int,
    val scaledHeight: Int,
    val subsample: Int,
    val pool: String,
    val frameCount: Int,
    @ElementCollection(fetch = FetchType.EAGER)
    val frameResults: List<FrameResult>
) {

    companion object {
        fun fromJobResult(jobResult: JobResult) =
            ResultSummary(
                version = jobResult.version,
                vmafScore = jobResult.vmafScore,
                execFps = jobResult.execFps,
                model = jobResult.model,
                scaledWidth = jobResult.scaledWidth,
                scaledHeight = jobResult.scaledHeight,
                subsample = jobResult.subsample,
                pool = jobResult.pool,
                frameCount = jobResult.frames.size,
                frameResults = jobResult.frames.map { FrameResult.fromFrame(it) }
            )
    }
}
