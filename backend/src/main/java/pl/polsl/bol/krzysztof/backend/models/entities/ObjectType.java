package pl.polsl.bol.krzysztof.backend.models.entities;

import lombok.*;

import javax.persistence.*;

@Table(name = "object_types")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ObjectType extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private User creator;

    @Column(length = 10, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private Layer layer;

}
