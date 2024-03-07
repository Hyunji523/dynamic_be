package com.example.restapi.dynamic.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.locationtech.jts.geom.*;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Data
@Entity
@Table(name = "temp_data", schema = "public")
public class TempData {


    @Column(name = "time", nullable = false)
    private Timestamp time;

    @Id
    @Column(name = "value", nullable = false)
    private Integer value;

    @Column(name = "lon", nullable = false)
    private Double lon;

    @Column(name = "lat", nullable = false)
    private Double lat;

    @Column(name = "geom", columnDefinition = "GEOMETRY")
    private Point geom;

    @Column(name = "kind_seq", nullable = false)
    private Integer kindSeq;

    @Column(name = "kind_name", nullable = false)
    private String kindName;

    @Column(name = "kind_num", nullable = false)
    private Integer kindNum;

    public Map<String, Double> getGeom() {
        Map<String, Double> map = new HashMap<>();
        if (this.geom != null) {
            map.put("x", this.geom.getX());
            map.put("y", this.geom.getY());
        }
        return map;
    }

}
