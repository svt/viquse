package se.svt.oss.viquse.model.vmaf

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import javax.persistence.Embedded

data class JobResult(
    val version: String,
    val params: Map<String, Any>,
    val metrics: List<String>,
    val frames: List<Frame>,
    @JsonProperty("VMAF score")
    val vmafScore: Double,
    @JsonProperty("ExecFps")
    val execFps: Double
) {

    val model: String
        get() = params["model"] as String
    val scaledWidth: Int
        get() = params["scaledWidth"] as Int
    val scaledHeight: Int
        get() = params["scaledHeight"] as Int
    val subsample: Int
        get() = params["subsample"] as Int
    val pool: String
        get() = params["pool"] as String
}

data class Frame(
    val frameNum: Int,
    val metrics: Metrics
)

data class Metrics(
    val adm2: Double,
    val motion2: Double,
    val vif_scale0: Double,
    val vif_scale1: Double,
    val vif_scale2: Double,
    val vif_scale3: Double,
    val vmaf: Double
)
