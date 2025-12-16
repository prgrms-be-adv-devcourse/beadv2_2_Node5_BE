package com.node5.billingservice.wallet.application;

import com.node5.billingservice.IntegrationTestSupport;
import com.node5.billingservice.wallet.application.dto.WalletSettleCommand;
import com.node5.billingservice.wallet.application.dto.WalletWithdrawCommand;
import com.node5.billingservice.wallet.domain.Wallet;
import com.node5.billingservice.wallet.infrastructure.WalletRepositoryAdapter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class WalletConcurrencyTest extends IntegrationTestSupport {

    @Autowired
    WalletService walletService;

    @Autowired
    WalletRepositoryAdapter walletRepositoryAdapter;

    Logger log = LoggerFactory.getLogger(getClass());

    @Test
    @DisplayName("동시에 100회의 출금 요청이 들어와도 잔액은 정확하게 처리된다.")
    void concurrentWithdrawals() throws InterruptedException {

        // given
        UUID memberId = UUID.randomUUID();
        walletService.createWallet(memberId);

        walletService.settleWallet(memberId,
                new WalletSettleCommand(UUID.randomUUID(), 1000L));

        int numberOfThreads = 100;
        Thread[] threads = new Thread[numberOfThreads];

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        long start = System.currentTimeMillis();

        // when
        for (int i = 0; i < numberOfThreads; i++) {

            threads[i] = new Thread(() -> {
                try {
                    WalletWithdrawCommand cmd =
                            new WalletWithdrawCommand(UUID.randomUUID(), 2L);

                    walletService.withdrawWallet(memberId, cmd);
                    successCount.incrementAndGet();

                } catch (Exception e) {
                    failCount.incrementAndGet();
                }
            });

            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        long end = System.currentTimeMillis();
        long elapsed = end - start;

        // then
        Wallet wallet = walletRepositoryAdapter.findByMemberId(memberId).orElseThrow();
        long finalBalance = wallet.getBalance();

        log.info("====== 동시성 출금 테스트 결과 ======");
        log.info("총 요청 수         = {}", numberOfThreads);
        log.info("성공 요청 수       = {}", successCount.get());
        log.info("실패 요청 수       = {}", failCount.get());
        log.info("최종 잔액          = {}", finalBalance);
        log.info("총 수행시간(ms)     = {}", elapsed);
        log.info("총 수행시간(sec)    = {}", elapsed / 1000.0);

        assertThat(successCount.get()).isLessThanOrEqualTo(500);
        assertThat(finalBalance).isEqualTo(1000L - successCount.get() * 2L);
        assertThat(finalBalance).isGreaterThanOrEqualTo(0L);
        assertThat(successCount.get() + failCount.get()).isEqualTo(numberOfThreads);
    }

    @Test
    @DisplayName("동시에 입금과 출금 요청이 들어와도 잔액은 정확하게 처리된다.")
    void concurrentDepositAndWithdraw() throws InterruptedException {

        Logger log = LoggerFactory.getLogger(getClass());

        // given
        UUID memberId = UUID.randomUUID();
        walletService.createWallet(memberId);

        // 초기 잔액 1000원
        walletService.settleWallet(memberId, new WalletSettleCommand(UUID.randomUUID(), 1000L));

        int depositThreads = 100;
        int withdrawThreads = 100;

        Thread[] threads = new Thread[depositThreads + withdrawThreads];

        AtomicInteger depositSuccess = new AtomicInteger();
        AtomicInteger withdrawSuccess = new AtomicInteger();
        AtomicInteger withdrawFail = new AtomicInteger();

        long start = System.currentTimeMillis();

        // 입금 스레드 생성
        for (int i = 0; i < depositThreads; i++) {
            threads[i] = new Thread(() -> {
                try {
                    WalletSettleCommand cmd =
                            new WalletSettleCommand(UUID.randomUUID(), 1L); // 1원 입금

                    walletService.settleWallet(memberId, cmd);
                    depositSuccess.incrementAndGet();
                } catch (Exception e) {
                    // 실패 없음
                }
            });
        }

        // 출금 스레드 생성
        for (int i = 0; i < withdrawThreads; i++) {
            threads[depositThreads + i] = new Thread(() -> {
                try {
                    WalletWithdrawCommand cmd =
                            new WalletWithdrawCommand(UUID.randomUUID(), 2L); // 2원 출금

                    walletService.withdrawWallet(memberId, cmd);
                    withdrawSuccess.incrementAndGet();
                } catch (Exception e) {
                    withdrawFail.incrementAndGet();
                }
            });
        }

        // 모든 스레드 시작
        for (Thread thread : threads) {
            thread.start();
        }

        // 종료 대기
        for (Thread thread : threads) {
            thread.join();
        }

        long end = System.currentTimeMillis();
        long elapsed = end - start;

        // then
        Wallet wallet = walletRepositoryAdapter.findByMemberId(memberId)
                .orElseThrow();

        long finalBalance = wallet.getBalance();

        log.info("====== 동시성 입금 + 출금 테스트 결과 ======");
        log.info("입금 성공 수            = {}", depositSuccess.get());
        log.info("출금 성공 수            = {}", withdrawSuccess.get());
        log.info("출금 실패 수            = {}", withdrawFail.get());
        log.info("최종 잔액                = {}", finalBalance);
        log.info("총 수행시간(ms)          = {}", elapsed);
        log.info("총 수행시간(sec)         = {}", elapsed / 1000.0);

        // 정상적인 기대 결과:
        // 초기 1000 + 입금 100 - 출금 100*2 = 1000 + 100 - 200 = 900

        long expectedBalance = 1000 + depositSuccess.get() - (withdrawSuccess.get() * 2);

        assertThat(finalBalance).isEqualTo(expectedBalance);
    }
}
