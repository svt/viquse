package se.svt.oss.viquse.model.vmaf

import com.fasterxml.jackson.annotation.JsonProperty

data class JobResult(
    val version: String,
    val params: Map<String, Any>,
    val metrics: List<String>,
    val frames: List<Frame>,
    @JsonProperty("VMAF score")
    val vmafScore: Float,
    @JsonProperty("ExecFps")
    val execFps: Float
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
    val adm2: Float,
    val motion2: Float,
    val vif_scale0: Float,
    val vif_scale1: Float,
    val vif_scale2: Float,
    val vif_scale3: Float,
    val vmaf: Float
)
