package com.example.hw4;

import com.example.hw4.service.FlashSaleService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@SpringBootTest
@Slf4j
class FlashSaleConcurrentTest {

    @Autowired
    private FlashSaleService flashSaleService;

    @Test
    void testCacheStampedePrevention() {
        int numberOfThreads = 50;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        Long productId = 999L;

        long startTime = System.currentTimeMillis();

        // Gửi 50 request đồng thời (Concurrent Requests)
        CompletableFuture<?>[] futures = IntStream.range(0, numberOfThreads)
                .mapToObj(i -> CompletableFuture.runAsync(() -> {
                    String result = flashSaleService.getProductInfo(productId);
                    log.info("Thread {} nhận kết quả: {}", Thread.currentThread().getName(), result);
                }, executor))
                .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(futures).join();
        executor.shutdown();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        log.info("Tổng thời gian thực thi 50 luồng: {} ms", duration);
        // Thời gian thực thi dự kiến khoảng > 2000ms một chút thay vì 100,000ms.
    }
}
