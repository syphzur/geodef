package pl.polsl.bol.krzysztof.backend.models.entities;

import lombok.*;

import javax.persistence.*;

@Table(name = "layers")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Layer extends BaseEntity {

    @Column(length = 10, nullable = false)
    private String name;

    @OneToOne
    private Layer prevLayer;
}
