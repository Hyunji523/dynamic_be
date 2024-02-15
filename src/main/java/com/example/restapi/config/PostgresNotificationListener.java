package com.example.restapi.config;

import jakarta.annotation.PostConstruct;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;

@Component
public class PostgresNotificationListener {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SseEmitterManager sseEmitterManager;

    @PostConstruct
    public void init() {
        Runnable notificationHandler = createNotificationHandler((PGNotification noti) -> {
            System.out.println("====Received noti: " + noti.getName() + ", payload: " + noti.getParameter() + "====");
            // sse 로 내보내는 로직 추가
            String payload = noti.getParameter();
            // 각 emitter에 payload를 전송
            sseEmitterManager.getEmitters().forEach(sseEmitter -> {
                try {
                    sseEmitter.send(payload, MediaType.APPLICATION_JSON);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        });
        Thread thread = new Thread(notificationHandler);
        thread.start();
    }

    public Runnable createNotificationHandler(Consumer<PGNotification> consumer) {
        return () -> {
            jdbcTemplate.execute((Connection c) -> {
                try {
                    c.createStatement().execute("LISTEN new_data_inserted");
                    System.out.println("====LISTEN====");

                    PGConnection pgconn = c.unwrap(PGConnection.class);
                    while (!Thread.currentThread().isInterrupted()) {
                        PGNotification[] nts = pgconn.getNotifications(10000);
                        if (nts == null || nts.length == 0) {
                            continue;
                        }
                        for (PGNotification nt : nts) {
                            consumer.accept(nt);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return 0;
            });
        };
    }


}
