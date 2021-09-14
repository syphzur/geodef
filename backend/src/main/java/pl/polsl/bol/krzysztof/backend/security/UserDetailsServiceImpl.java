package pl.polsl.bol.krzysztof.backend.security;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import pl.polsl.bol.krzysztof.backend.models.entities.User;
import pl.polsl.bol.krzysztof.backend.repository.UserRepository;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    public UserDetailsServiceImpl(final UserRepository userRepository, final ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) {
        final Optional<User> foundUser = userRepository.findByUsername(username);
        if (foundUser.isEmpty()) {
            return null;
        }
        final CustomUserDetails userDetails = new CustomUserDetails();
        this.modelMapper.map(foundUser.get(), userDetails);
        return userDetails;
    }

    public CustomUserDetails loadUserByGoogleSubject(final String googleUserSubject) {
        final Optional<User> foundUser = userRepository.findByGoogleUserSubject(googleUserSubject);
        if (foundUser.isEmpty()) {
            return null;
        }
        final CustomUserDetails userDetails = new CustomUserDetails();
        this.modelMapper.map(foundUser.get(), userDetails);
        return userDetails;
    }

    public void registerOrUpdateUser(final User user) {
        this.userRepository.save(user);
    }
}
