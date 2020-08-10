package se.svt.oss.viquse.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonTypeName
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.validation.annotation.Validated
import java.time.LocalDateTime
import java.util.UUID

@Validated
@Entity
@EntityListeners(AuditingEntityListener::class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("RerunnerJob")
data class ViquseJob(
    val programmeId: String
) {
    @Id
    val jobId: UUID = UUID.randomUUID()

    var flowId: UUID? = null

    @CreatedDate
    var createdDate: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    var lastModifiedDate: LocalDateTime = LocalDateTime.now()

    @Enumerated(EnumType.STRING)
    var status: Status = Status.NEW

    var message: String? = null

    val contextMap: Map<String, String>
        @JsonIgnore
        get() = mapOf(
            "ProgrammeId" to programmeId,
            "FlowId" to flowId.toString()
        )
}
