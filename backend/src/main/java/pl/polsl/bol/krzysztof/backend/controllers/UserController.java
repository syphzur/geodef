package pl.polsl.bol.krzysztof.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.polsl.bol.krzysztof.backend.security.CustomUserDetails;
import pl.polsl.bol.krzysztof.backend.models.dtos.UserDto;
import pl.polsl.bol.krzysztof.backend.models.entities.User;
import pl.polsl.bol.krzysztof.backend.services.UserService;

@RestController
@RequestMapping("/user")
public class UserController extends AbstractController<User, UserService, UserDto> {

    @RequestMapping("/logged")
    public ResponseEntity<CustomUserDetails> user(final Authentication auth) {
        return new ResponseEntity<>((CustomUserDetails) auth.getPrincipal(), HttpStatus.OK);
    }

    public UserController(final UserService service) {
        super(service);
    }

    @Override
    public Class<User> getModelClass() {
        return User.class;
    }

    @Override
    public Class<UserDto> getDtoClass() {
        return UserDto.class;
    }
}
