package com.node5.supportservice.search.application.reindex;

import java.time.LocalDateTime;

public record ReindexStatus(
	ReindexState state,
	LocalDateTime startedAt,
	LocalDateTime finishedAt,
	long processedCount,
	String lastError
) {
}
