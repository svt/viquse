package se.svt.oss.viquse.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("graph")
class GraphProperties {
    var destinationPath: String = "/shares/test_nas/core-acc/VMAF/"
}
