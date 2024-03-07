package com.example.restapi.config;

import com.example.restapi.dynamic.entity.TempData;
import com.example.restapi.dynamic.service.TempDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SseEmitterManager {

    @Autowired
    private SSEScheduler sseScheduler;

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter createEmitter() {

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        this.emitters.add(emitter);
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connected!", MediaType.APPLICATION_JSON));
            System.out.println("=== EMITTER : " + emitters.size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        emitter.onCompletion(() -> this.emitters.remove(emitter));
        emitter.onTimeout(() -> this.emitters.remove(emitter));

        return emitter;
    }

    public CopyOnWriteArrayList<SseEmitter> getEmitters() {
        return this.emitters;
    }

    @Autowired
    private TempDataService tempDataService;

    public void sendData( Timestamp timestamp) {
        List<TempData> tempDataList = tempDataService.getTempDataByTime(timestamp);
        System.out.println("==emitters : " + emitters.size() + "  " + timestamp);
        // 모든 SseEmitter 객체에 데이터를 전송
        if (emitters.size() != 0) {
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .data(tempDataList, MediaType.APPLICATION_JSON));

                } catch (IOException e) {
//                    e.printStackTrace();
                }
            }
        } else {
            sseScheduler.stopScheduler();
        }
    }

}
