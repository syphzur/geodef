package pl.polsl.bol.krzysztof.backend.repository;

import org.springframework.stereotype.Repository;
import pl.polsl.bol.krzysztof.backend.models.entities.User;

import java.util.Optional;

@Repository
public interface UserRepository extends EntityRepository<User> {
    Optional<User> findByUsername(final String username);

    Optional<User> findByGoogleUserSubject(final String googleUserSubject);

}
