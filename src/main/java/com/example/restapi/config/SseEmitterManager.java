package com.example.restapi.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SseEmitterManager {

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter createEmitter(HttpServletRequest request) {
        System.out.println("====SSE stream 접근 : " + request.getRemoteAddr());
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        this.emitters.add(emitter);
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connected!", MediaType.APPLICATION_JSON));
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

}
