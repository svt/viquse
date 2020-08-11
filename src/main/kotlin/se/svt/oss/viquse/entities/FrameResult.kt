package se.svt.oss.viquse.entities

import se.svt.oss.viquse.model.vmaf.Frame
import javax.persistence.Access
import javax.persistence.AccessType
import javax.persistence.Embeddable

@Embeddable
data class FrameResult(
    @Access(AccessType.FIELD)
    val frameNumber: Int,
    val adm2: Float,
    val motion2: Float,
    val vif_scale0: Float,
    val vif_scale1: Float,
    val vif_scale2: Float,
    val vif_scale3: Float,
    val vmaf: Float
) {
    companion object {
        fun fromFrame(frame: Frame) =
            FrameResult(
                frameNumber = frame.frameNum,
                adm2 = frame.metrics.adm2,
                motion2 = frame.metrics.motion2,
                vif_scale0 = frame.metrics.vif_scale0,
                vif_scale1 = frame.metrics.vif_scale1,
                vif_scale2 = frame.metrics.vif_scale2,
                vif_scale3 = frame.metrics.vif_scale3,
                vmaf = frame.metrics.vmaf
            )
    }
}
