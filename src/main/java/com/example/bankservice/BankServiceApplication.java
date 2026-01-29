package com.example.bankservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * BankServiceApplication (메인 애플리케이션)
 *
 * @EnableScheduling:
 * - Spring의 스케줄러 기능을 활성화
 * - @Scheduled 어노테이션이 붙은 메서드들이 자동으로 실행됨
 * - InterestScheduler의 이자 적용 메서드가 1시간마다 자동 실행
 */
@EnableScheduling
@SpringBootApplication
public class BankServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankServiceApplication.class, args);
    }

}
