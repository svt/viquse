package se.svt.oss.viquse.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import se.svt.oss.viquse.entities.ResultSummary


@RepositoryRestResource
interface ResultSummaryRepository : JpaRepository<ResultSummary, Long>