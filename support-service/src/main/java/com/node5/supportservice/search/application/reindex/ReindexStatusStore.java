package com.node5.supportservice.search.application.reindex;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Component;

@Component
public class ReindexStatusStore {

	private final AtomicReference<ReindexState> state = new AtomicReference<>(ReindexState.IDLE);
	private final AtomicReference<LocalDateTime> startedAt = new AtomicReference<>(null);
	private final AtomicReference<LocalDateTime> finishedAt = new AtomicReference<>(null);
	private final AtomicLong processed = new AtomicLong(0);
	private final AtomicReference<String> lastError = new AtomicReference<>(null);

	public ReindexStatus snapshot() {
		return new ReindexStatus(
			state.get(),
			startedAt.get(),
			finishedAt.get(),
			processed.get(),
			lastError.get()
		);
	}

	public boolean tryStart() {
		boolean ok = state.compareAndSet(ReindexState.IDLE, ReindexState.RUNNING)
			|| state.compareAndSet(ReindexState.SUCCESS, ReindexState.RUNNING)
			|| state.compareAndSet(ReindexState.FAILED, ReindexState.RUNNING);

		if (ok) {
			startedAt.set(LocalDateTime.now());
			finishedAt.set(null);
			processed.set(0);
			lastError.set(null);
		}
		return ok;
	}

	public void addProcessed(long delta) {
		processed.addAndGet(delta);
	}

	public void markSuccess() {
		state.set(ReindexState.SUCCESS);
		finishedAt.set(LocalDateTime.now());
	}

	public void markFailed(Exception e) {
		state.set(ReindexState.FAILED);
		finishedAt.set(LocalDateTime.now());
		lastError.set(e.getClass().getSimpleName() + ": " + e.getMessage());
	}

	public boolean isRunning() {
		return state.get() == ReindexState.RUNNING;
	}
}
