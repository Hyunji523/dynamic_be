package com.example.restapi.coords.service;

import com.example.restapi.coords.entity.CctvTransData;
import com.example.restapi.coords.repository.CctvTransDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@Service
public class CctvTransDataService {

    private final CctvTransDataRepository cctvTransDataRepository;


    @Autowired
    public CctvTransDataService(CctvTransDataRepository cctvTransDataRepository) {
        this.cctvTransDataRepository = cctvTransDataRepository;
    }

    public List<CctvTransData> getAllCctvTransData(){
        return cctvTransDataRepository.findAll();
    }

    public List<CctvTransData> getCctvTransDataByTime(String inputTime){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Timestamp inputTimestamp;
        try {
            inputTimestamp = new Timestamp(dateFormat.parse(inputTime).getTime());
        } catch (ParseException e) {
            throw new RuntimeException("Failed to parse date string", e);
        }

        Timestamp startTimestamp = new Timestamp(inputTimestamp.getTime() - 60000);
        Timestamp endTimestamp = new Timestamp(inputTimestamp.getTime() + 600000000);

        // 시작 시간과 종료 시간 사이의 데이터를 가져옴
        return cctvTransDataRepository.findByTimeBetween(startTimestamp, endTimestamp);
    }

    public void save(CctvTransData data) {
        cctvTransDataRepository.save(data);
    }


}
