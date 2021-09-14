package pl.polsl.bol.krzysztof.backend.services;

import org.springframework.stereotype.Service;
import pl.polsl.bol.krzysztof.backend.models.entities.User;
import pl.polsl.bol.krzysztof.backend.repository.UserRepository;

@Service
public class UserService extends AbstractService<User, UserRepository> {

    public UserService(final UserRepository repository) {
        super(repository);
    }

}
