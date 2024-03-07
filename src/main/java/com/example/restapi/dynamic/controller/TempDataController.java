package com.example.restapi.dynamic.controller;

import com.example.restapi.config.SSEScheduler;
import com.example.restapi.config.SseEmitterManager;
import com.example.restapi.dynamic.entity.TempData;
import com.example.restapi.dynamic.service.TempDataService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;


import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/")
public class TempDataController {

    @Autowired
    private TempDataService tempDataService;

    @Autowired
    private SseEmitterManager sseEmitterManager;

    @Autowired
    private SSEScheduler sseScheduler;


    private static int count=0;
    //SSE 실시간 데이터
    @GetMapping("/sse-real/")
    public SseEmitter connect(HttpServletRequest request) {
        SseEmitter sseEmitter = sseEmitterManager.createEmitter();
        count++;
        System.out.println("*** sse 성공 : " + count + sseEmitter );
        //현재 시간
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//        Timestamp timestamp = new Timestamp(dateFormat.parse("2024-02-25 00:00").getTime());
        String timestampString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
        timestampString += ".000"; // 밀리초를 0으로 설정
        Timestamp newTimestamp = Timestamp.valueOf(timestampString);



        sseScheduler.startScheduler(newTimestamp);

        return sseEmitter;
    }
    @GetMapping("/sse-past/")
    public SseEmitter getTempDataByTime(@RequestParam("time")String inputTime ) {
        SseEmitter sseEmitter = sseEmitterManager.createEmitter();
        //현재시간
        Timestamp nowTimestamp = tempDataService.fomatTimestamp(new Timestamp(System.currentTimeMillis()));
        //입력받은 시간
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Timestamp inputTimestamp;
        try {
            inputTimestamp = new Timestamp(dateFormat.parse(inputTime).getTime());

            //Timestamp timestamp = tempDataService.fomatTimestamp(TempDataService.convertToTimestamp2(dayNumber, secondsOfDay));

            //입력받은 시간(과거)부터 스케쥴러 시작
            if (!inputTimestamp.equals(nowTimestamp)) {
                sseScheduler.startScheduler(inputTimestamp);
            } else {
                sseScheduler.stopScheduler();
            }
            return sseEmitter;

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }


    @GetMapping("/stop/")
    public void stopScheduler() {
        sseScheduler.stopScheduler();
    }

    @GetMapping("/data/")
    public List<TempData> getTempDataByTime2(@RequestParam("dayNumber") int dayNumber, @RequestParam("secondsOfDay") double secondsOfDay) {
        Timestamp timestamp = Timestamp.valueOf(tempDataService.convertToTimestamp(dayNumber, secondsOfDay));
        return tempDataService.getTempDataByTime(timestamp);
    }





}