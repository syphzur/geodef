package pl.polsl.bol.krzysztof.backend.models.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue
    protected Long id;

    @Column(columnDefinition = "uuid", unique = true, nullable = false)
    protected UUID uuid = UUID.randomUUID();
}
