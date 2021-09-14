package pl.polsl.bol.krzysztof.backend.models.entities;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Table(name = "gps_trackers")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GpsTracker extends BaseEntity {

    @Column(length = 12, unique = true, nullable = false)
    private String imei;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User owner;
}
