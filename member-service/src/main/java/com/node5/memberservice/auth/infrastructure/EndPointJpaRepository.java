package com.node5.memberservice.auth.infrastructure;

import com.node5.memberservice.auth.domain.Endpoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface EndPointJpaRepository extends JpaRepository<Endpoint, UUID> {

    @Query(
            value = """
                    select exists (
                        SELECT 1
                        FROM member.endpoint e
                        JOIN member.member m ON m.id = :memberId
                        WHERE m.deleted_at IS NULL
                        AND e.http_method = :method
                        AND jsonb_exists(m.roles, e.role)
                        AND :path LIKE e.path_pattern
                    )
                    """,
            nativeQuery = true
    )
    boolean authorize(
            @Param("memberId") UUID memberId,
            @Param("method") String method,
            @Param("path") String path
    );
}
