package pl.polsl.bol.krzysztof.backend.models.entities;

import lombok.*;
import pl.polsl.bol.krzysztof.backend.models.enums.Role;

import javax.persistence.*;

@Table(name = "users")
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {

    @Column(length = 30, unique = true, nullable = false)
    protected String username;

    @Column(length = 254, unique = true, nullable = false)
    protected String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    protected Role role;

    @Column(unique = true, nullable = false)
    private String googleUserSubject;

    private String pictureUrl;

    private String familyName;

    private String givenName;

}
