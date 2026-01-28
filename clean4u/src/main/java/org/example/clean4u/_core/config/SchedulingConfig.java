package org.example.clean4u._core.config;

import lombok.RequiredArgsConstructor;
import org.example.clean4u.customer.CustomerService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulingConfig {
    private final CustomerService customerService;

    @Scheduled(cron = "0 0 3 L * *")
    public void runInactivateCustomers() {
        System.out.println("장기 미이용자 비활성화 시작");
        customerService.processInactiveCustomers();
        System.out.println("장기 미이용자 비활성화 완료");
    }
}
