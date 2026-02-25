package com.example.jobportal.payment;

import com.example.jobportal.entity.Plan;
import com.example.jobportal.repository.PlanRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataLoader implements CommandLineRunner {

    private final PlanRepository planRepository;

    public DataLoader(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (planRepository.count() == 0) {
            planRepository.save(
                    Plan.builder()
                            .name("MONTHLY")
                            .durationDays(30)
                            .price(new BigDecimal("100.00"))
                            .jobLimit(10)
                            .active(true)
                            .build()
            );
            planRepository.save(
                    Plan.builder()
                            .name("SIX_MONTH")
                            .durationDays(180)
                            .price(new BigDecimal("500.00"))
                            .jobLimit(100)
                            .active(true)
                            .build()
            );
            planRepository.save(
                    Plan.builder()
                            .name("YEARLY")
                            .durationDays(365)
                            .price(new BigDecimal("900.00"))
                            .jobLimit(9999)
                            .active(true)
                            .build()
            );
        }
    }
}
