package se.svt.oss.viquse.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonTypeName
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import se.svt.oss.viquse.model.vmaf.StoredJobResult
import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("ViquseJob")
data class ViquseJob(
    val referenceFile: String,
    val transcodedFile: String
) {
    @Id
    val jobId: UUID = UUID.randomUUID()

    var externalId: String? = null

    @CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    var lastModifiedDate: LocalDateTime = LocalDateTime.now()

    @Enumerated(EnumType.STRING)
    var status: Status = Status.NEW

    var message: String? = null

    @ElementCollection
    val jobResults: List<StoredJobResult> = emptyList()

    val contextMap: Map<String, String>
        @JsonIgnore
        get() = mapOf(
            "JobId" to jobId.toString(),
            "ExternalId" to externalId.toString()
        )
}
