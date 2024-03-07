package com.example.restapi.dynamic.service;

import com.example.restapi.dynamic.entity.TempData;
import com.example.restapi.dynamic.repository.TempDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class TempDataService {

    @Autowired
    private TempDataRepository tempDataRepository;

    public List<TempData> getTempDataByTime(Timestamp time) {
        return tempDataRepository.findAllByTime(time);
    }

    public LocalDateTime convertToTimestamp(int dayNumber, double secondsOfDay) {
        // Julian Day Number에서 LocalDate로 변환
        LocalDate date = LocalDate.ofEpochDay(dayNumber - 2440588);

        // 초 단위의 시간에서 LocalTime으로 변환
        int hour = (int) (secondsOfDay / 3600);
        int minute = (int) ((secondsOfDay % 3600) / 60);
        int second = (int) (secondsOfDay % 60);
        int nano = (int) ((secondsOfDay % 1) * 1_000_000_000);
        LocalTime time = LocalTime.of(hour, minute, second, 0);

        // LocalDate와 LocalTime을 합쳐 LocalDateTime으로 변환
        return LocalDateTime.of(date, time);
    }

    public static Timestamp convertToTimestamp2(int dayNumber, double secondsOfDay) {
        // Julian Day Number를 이용하여 날짜 생성
        long millis = (dayNumber - 2440588L) * 24L * 60L * 60L * 1000L; // Unix epoch(1970-01-01 00:00:00 UTC)에서의 밀리초 단위로 계산
        Timestamp timestamp = new Timestamp(millis);

        // 초 단위의 시간을 시, 분, 초로 변환
        int hour = (int) (secondsOfDay / 3600);
        int minute = (int) ((secondsOfDay % 3600) / 60);
        int second = (int) (secondsOfDay % 60);

        // Timestamp 객체에 시간 설정
        timestamp.setHours(hour);
        timestamp.setMinutes(minute);
        timestamp.setSeconds(second);

        return timestamp;
    }


    public Timestamp fomatTimestamp(Timestamp timestamp){
        String timestampString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
        timestampString += ".000"; // 밀리초를 0으로 설정
        return Timestamp.valueOf(timestampString);
    }
}
