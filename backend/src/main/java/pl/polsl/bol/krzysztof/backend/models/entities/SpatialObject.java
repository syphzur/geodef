package pl.polsl.bol.krzysztof.backend.models.entities;

import lombok.*;
import org.hibernate.annotations.Type;
import org.locationtech.jts.geom.Geometry;

import javax.persistence.*;

@Table(name = "spatial_objects")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SpatialObject extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private User creator;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(columnDefinition = "geography", nullable = false)
    @Type(type = "jts_geometry")
    private Geometry geometry;

    @ManyToOne(fetch = FetchType.LAZY)
    private ObjectType type;
}
