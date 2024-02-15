package com.example.restapi.coords.repository;

import com.example.restapi.coords.entity.CctvTransData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface CctvTransDataRepository extends JpaRepository<CctvTransData, Double> {

    List<CctvTransData> findByTimeAfter(Timestamp startTime);

    List<CctvTransData> findByTimeBetween(Timestamp start, Timestamp end);


}
