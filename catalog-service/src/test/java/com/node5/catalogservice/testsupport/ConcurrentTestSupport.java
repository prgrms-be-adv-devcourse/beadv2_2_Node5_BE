package com.node5.catalogservice.testsupport;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class ConcurrentTestSupport {

	private ConcurrentTestSupport() {
	}

	@FunctionalInterface
	public interface ThrowingRunnable {
		void run() throws Exception;
	}

	/**
	 * 같은 작업(task)을 threads 개수만큼 동시에 실행
	 * 예) 동시실행(100, () -> hold())
	 */
	public static void 동시실행(int threads, ThrowingRunnable task) throws Exception {
		ExecutorService pool = Executors.newFixedThreadPool(threads);
		CountDownLatch ready = new CountDownLatch(threads);
		CountDownLatch start = new CountDownLatch(1);
		CountDownLatch done = new CountDownLatch(threads);

		AtomicReference<Throwable> firstError = new AtomicReference<>(null);

		for (int i = 0; i < threads; i++) {
			pool.submit(() -> {
				ready.countDown();
				try {
					start.await();
					task.run();
				} catch (Throwable e) {
					firstError.compareAndSet(null, e);
				} finally {
					done.countDown();
				}
			});
		}

		ready.await();
		start.countDown();
		done.await();
		pool.shutdown();

		Throwable error = firstError.get();
		if (error != null) {
			throw new AssertionError("동시 실행 작업 중 예외가 발생했습니다.", error);
		}
	}

	/**
	 * 서로 다른 작업들을 각각 1개 스레드로 동시에 실행
	 * 예) 동시실행(() -> release반복(), () -> hold반복())
	 */
	public static void 동시실행(ThrowingRunnable... tasks) throws Exception {
		int threads = tasks.length;

		ExecutorService pool = Executors.newFixedThreadPool(threads);
		CountDownLatch ready = new CountDownLatch(threads);
		CountDownLatch start = new CountDownLatch(1);
		CountDownLatch done = new CountDownLatch(threads);

		AtomicReference<Throwable> firstError = new AtomicReference<>(null);

		for (ThrowingRunnable task : tasks) {
			pool.submit(() -> {
				ready.countDown();
				try {
					start.await();
					task.run();
				} catch (Throwable e) {
					firstError.compareAndSet(null, e);
				} finally {
					done.countDown();
				}
			});
		}

		ready.await();
		start.countDown();
		done.await();
		pool.shutdown();

		Throwable error = firstError.get();
		if (error != null) {
			throw new AssertionError("동시 실행 작업 중 예외가 발생했습니다.", error);
		}
	}
}
