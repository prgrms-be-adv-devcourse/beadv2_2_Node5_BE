package com.node5.memberservice.endpoint.infrastructure;

import com.node5.memberservice.endpoint.domain.Endpoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface EndPointJpaRepository extends JpaRepository<Endpoint, UUID> {

    @Query(
            value = """
                    SELECT e.*
                    FROM member.endpoint e
                    WHERE e.http_method = :httpMethod
                      AND jsonb_exists(CAST(:roles AS jsonb), e.role)
                    """,
            nativeQuery = true
    )
    List<Endpoint> findAllowedEndpoints(
            @Param("roles") String roles,
            @Param("httpMethod") String httpMethod
    );
}
