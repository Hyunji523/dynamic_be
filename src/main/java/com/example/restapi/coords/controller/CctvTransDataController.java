package com.example.restapi.coords.controller;

import com.example.restapi.config.ScheduledTasks;
import com.example.restapi.config.SseEmitterManager;
import com.example.restapi.coords.entity.CctvTransData;
import com.example.restapi.coords.service.CctvTransDataService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@RestController
public class CctvTransDataController {
    private final CctvTransDataService cctvTransDataService;

    @Autowired
    public CctvTransDataController(CctvTransDataService cctvTransDataService) {
        this.cctvTransDataService = cctvTransDataService;
    }
    @Autowired
    private  ScheduledTasks scheduledTasks;

    @Autowired
    private SseEmitterManager sseEmitterManager;

    @GetMapping(value ="/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter handle(HttpServletRequest request) {
        return sseEmitterManager.createEmitter(request);
    }

    @GetMapping("/all")
    public List<CctvTransData> getAllCctvTransData(){
        return cctvTransDataService.getCctvTransDataByTime("");
    }

    @GetMapping("/sse")
    public SseEmitter streamSseMvc(@RequestParam("time") String time) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        //별도의 스레드에서 작업하도록
        ExecutorService sseMvcExecutor = Executors.newSingleThreadExecutor();
        sseMvcExecutor.execute(() -> {
            try {
                //시간에 대한 좌표데이터
                List<CctvTransData> coordsList = cctvTransDataService.getCctvTransDataByTime(time);
                for (CctvTransData data : coordsList){
                    SseEmitter.SseEventBuilder event = SseEmitter.event()
                            //.data(System.currentTimeMillis()); //현재 시간 반환
                            .data(data);
                    emitter.send(event);
                    Thread.sleep(500);
                }
                emitter.complete();
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        });
        return emitter;
    }

    @GetMapping("/save")
    public void save() {
        scheduledTasks.resetStartTime();
        scheduledTasks.insertData();

    }
}
