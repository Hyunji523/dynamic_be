package com.example.restapi.config;

import com.example.restapi.dynamic.service.TempDataService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Calendar;

@Component
@EnableScheduling
public class SSEScheduler {

    private final SseEmitterManager sseEmitterManager;
    private Timestamp inputTime;

    private volatile boolean running = true;

    public SSEScheduler(SseEmitterManager sseEmitterManager) {
        this.sseEmitterManager = sseEmitterManager;
    }

    @Scheduled(fixedRate = 1000) // 1초마다 실행
    public void sendTimestamp() {
        if (!running) {
            return;
        }
        if (inputTime == null){
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(inputTime);

        // 초를 +1
        calendar.add(Calendar.SECOND, 1);

        inputTime.setTime(calendar.getTimeInMillis());

        // SSE 데이터 전송
        sseEmitterManager.sendData(inputTime);
    }

    public void stopScheduler() {
        running = false;
        System.out.println("===STOP Scheduler===");
    }

    public void startScheduler(Timestamp time) {
        running = true;
        inputTime = time;
    }

}

