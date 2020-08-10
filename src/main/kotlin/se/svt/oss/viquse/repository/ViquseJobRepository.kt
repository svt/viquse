package se.svt.oss.viquse.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import se.svt.oss.viquse.model.Status
import se.svt.oss.viquse.model.ViquseJob
import java.util.UUID

@RepositoryRestResource
interface ViquseJobRepository : JpaRepository<ViquseJob, UUID> {

    fun findFirstByStatusIn(statuses: List<Status>): List<ViquseJob>

    fun findByStatusInOrderByCreatedDate(statuses: List<Status>): List<ViquseJob>
}
