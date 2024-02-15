package com.example.restapi.config;

import java.sql.Timestamp;
import java.util.Random;

import com.example.restapi.coords.entity.CctvTransData;
import com.example.restapi.coords.repository.CctvTransDataRepository;
import com.example.restapi.coords.service.CctvTransDataService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    @Autowired
    private  CctvTransDataService cctvTransDataService;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    private long startTime;

    @PostConstruct
    public void init() {
        this.startTime = System.currentTimeMillis();
    }
    @Scheduled(fixedRate = 1000)
    public void insertData() {
        // 5초가 지났는지 확인
        if (System.currentTimeMillis() - startTime >= 5 * 1000) {
            return;
        }

        Random random = new Random();

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        double x = 126.198 + random.nextDouble() * 0.01;
        double y = 37.38 + random.nextDouble() * 0.01;

        CctvTransData data = new CctvTransData();
        data.setTime(timestamp);
        data.setX(x);
        data.setY(y);

        cctvTransDataService.save(data);

        //applicationEventPublisher.publishEvent(data);

    }
    public void resetStartTime() {
        this.startTime = System.currentTimeMillis();
    }
}
