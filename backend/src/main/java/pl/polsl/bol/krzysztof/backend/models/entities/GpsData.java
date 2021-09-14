package pl.polsl.bol.krzysztof.backend.models.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.locationtech.jts.geom.Point;

import lombok.*;
import org.hibernate.annotations.Type;
import org.locationtech.spatial4j.io.jackson.GeometryAsGeoJSONSerializer;
import org.locationtech.spatial4j.io.jackson.GeometryDeserializer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GpsData extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private GpsTracker tracker;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @JsonSerialize(using = GeometryAsGeoJSONSerializer.class)
    @JsonDeserialize(using = GeometryDeserializer.class)
    @Column(columnDefinition = "geography", nullable = false)
    @Type(type = "jts_geometry")
    private Point point;

    @Column(nullable = false)
    private double speed;

}
