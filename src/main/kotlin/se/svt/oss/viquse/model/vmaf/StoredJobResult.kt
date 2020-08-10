package se.svt.oss.viquse.model.vmaf

import javax.persistence.Access
import javax.persistence.AccessType
import javax.persistence.Embeddable

@Embeddable
@Access(AccessType.FIELD)
data class StoredJobResult(
    val version: String,
    val vmafScore: Double,
    val execFps: Double,
    val model: String,
    val scaledWidth: Int,
    val scaledHeight: Int,
    val subsample: Int,
    val pool: String,
    val frameCount: Int
)
