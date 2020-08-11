package se.svt.oss.viquse.entities

import se.svt.oss.viquse.model.vmaf.JobResult
import java.util.UUID
import javax.persistence.Access
import javax.persistence.AccessType
import javax.persistence.Embeddable
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class ResultSummary(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,
        val version: String,
        val vmafScore: Double,
        val execFps: Double,
        val model: String,
        val scaledWidth: Int,
        val scaledHeight: Int,
        val subsample: Int,
        val pool: String,
        val frameCount: Int
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
                                frameCount = jobResult.frames.size
                        )
        }
}