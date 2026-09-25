package com.example.hw4.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FlashSaleService {

    @Cacheable(value = "flash-sale", key = "#id", sync = true)
    public String getProductInfo(Long id) {
        log.info("Fetching from Database for product {}", id);
        try {
            // Giả lập thời gian tái tạo cache (Query Database phức tạp mất 2 giây)
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "Thông tin sản phẩm HOT: " + id;
    }
}
