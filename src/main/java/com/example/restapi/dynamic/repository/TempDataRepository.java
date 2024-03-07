package com.example.restapi.dynamic.repository;

import com.example.restapi.dynamic.entity.TempData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface TempDataRepository extends JpaRepository<TempData, Timestamp> {
    List<TempData> findAllByTime(Timestamp time);
}
