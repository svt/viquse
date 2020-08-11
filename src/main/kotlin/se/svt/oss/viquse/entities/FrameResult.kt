package se.svt.oss.viquse.entities

import javax.persistence.Access
import javax.persistence.AccessType
import javax.persistence.Embeddable

@Embeddable

data class FrameResult(
        @Access(AccessType.PROPERTY)
        val frameNumber: Int,
        val adm2: Double,
        val motion2: Double,
        val vif_scale0: Double,
        val vif_scale1: Double,
        val vif_scale2: Double,
        val vif_scale3: Double,
        val vmaf: Double
)
