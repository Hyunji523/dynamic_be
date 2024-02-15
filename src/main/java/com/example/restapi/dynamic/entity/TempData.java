package com.example.restapi.dynamic.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.locationtech.jts.geom.*;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "temp_data", schema = "public")
public class TempData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "time", nullable = false)
    private Timestamp time;

    @Column(name = "value", nullable = false)
    private Integer value;

    @Column(name = "lon", nullable = false)
    private Double lon;

    @Column(name = "lat", nullable = false)
    private Double lat;

    @Column(name = "geom", columnDefinition = "geometry")
    private Geometry geom;

    @Column(name = "kind_seq", nullable = false)
    private Integer kindSeq;

    @Column(name = "kind_name", nullable = false)
    private String kindName;

    @Column(name = "kind_num", nullable = false)
    private Integer kindNum;

}
