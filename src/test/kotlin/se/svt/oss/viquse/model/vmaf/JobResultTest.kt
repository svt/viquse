package se.svt.oss.viquse.model.vmaf

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.json.JsonTest
import org.springframework.boot.test.json.JacksonTester
import org.springframework.core.io.Resource
import se.svt.oss.Assertions.assertThat

@JsonTest
class JobResultTest {

    @Autowired
    lateinit var jacksonTester: JacksonTester<JobResult>

    @Value("classpath:vmaf-log.json")
    lateinit var vmafLog: Resource

    @Test
    fun testSerialize() {
        val result = jacksonTester.parse(vmafLog.file.readText()).`object`

        assertThat(result).hasVersion("1.5.1")
        assertThat(result).hasMetrics(
            "adm2",
            "motion2",
            "psnr",
            "ssim",
            "vif_scale0",
            "vif_scale1",
            "vif_scale2",
            "vif_scale3",
            "vmaf"
        )
        assertThat(result.frames).hasSize(25)
        assertThat(result).hasFrames(
            Frame(
                frameNum = 0,
                metrics = Metrics(
                    adm2 = 0.99559,
                    motion2 = 0.0,
                    vif_scale0 = 0.94209,
                    vif_scale1 = 0.98624,
                    vif_scale2 = 0.99132,
                    vif_scale3 = 0.99447,
                    vmaf = 95.54361
                )
            )
        )
        assertThat(result).hasVmafScore(96.05584654566408)
        assertThat(result).hasExecFps(15.86690462640109)
    }
}
