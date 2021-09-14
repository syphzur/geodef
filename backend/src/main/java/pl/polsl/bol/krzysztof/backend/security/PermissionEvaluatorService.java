package pl.polsl.bol.krzysztof.backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pl.polsl.bol.krzysztof.backend.models.dtos.SpatialObjectDto;
import pl.polsl.bol.krzysztof.backend.models.entities.User;
import pl.polsl.bol.krzysztof.backend.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PermissionEvaluatorService {

    private static final List<String> loggedOnlyUrls = List.of("spatialobject/post", "gpstracker/post", "objecttype/post");
    private static final List<String> publicUrls = List.of("layer/get", "spatialobject/get", "objecttype/get");
    private final UserRepository userRepository;

    public PermissionEvaluatorService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean hasPrivilege(final Authentication auth, final String modelClass, final String permission) {
        final String urlToAccess = modelClass + "/" + permission;
        if (publicUrls.contains(urlToAccess.toLowerCase())) {
            return true;
        }

        final CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        final String role = userDetails.getRole().toString();

        if (role.equals("ROLE_ADMIN")) {
            return true;
        } else if (role.equals("ROLE_USER")) {
            return loggedOnlyUrls.contains(urlToAccess.toLowerCase());
        }
        return false;
    }

    public boolean hasPrivilege(final Authentication auth, final UUID userUuid) {
        final Object principal = auth.getPrincipal();
        if (principal instanceof String) {
            // not logged user
            return false;
        }
        final CustomUserDetails userDetails = (CustomUserDetails) principal;
        final String role = userDetails.getRole().toString();
        if (role.equals("ROLE_ADMIN")) {
            return true;
        }
        final Optional<User> loggedUser = userRepository.findById(userDetails.getId());
        return loggedUser.map(user -> user.getUuid().equals(userUuid)).orElse(false);
    }


    public boolean hasPrivilege(final Authentication auth, final UUID userUuid, final List<SpatialObjectDto> dtoList) {
        final Object principal = auth.getPrincipal();
        if (principal instanceof String) {
            // not logged user
            return false;
        }
        final CustomUserDetails userDetails = (CustomUserDetails) principal;
        final String role = userDetails.getRole().toString();
        if (role.equals("ROLE_ADMIN")) {
            return true;
        }

        for (final SpatialObjectDto dto : dtoList) {
            if (!dto.getCreatorUuid().equals(userUuid)) {
                return false;
            }
        }

        final Optional<User> loggedUser = userRepository.findById(userDetails.getId());
        return loggedUser.map(user -> user.getUuid().equals(userUuid)).orElse(false);
    }


}
