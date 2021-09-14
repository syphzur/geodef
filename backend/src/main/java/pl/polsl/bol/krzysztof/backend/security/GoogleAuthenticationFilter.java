package pl.polsl.bol.krzysztof.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import lombok.SneakyThrows;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pl.polsl.bol.krzysztof.backend.models.dtos.ErrorDto;
import pl.polsl.bol.krzysztof.backend.models.entities.User;
import pl.polsl.bol.krzysztof.backend.models.enums.Role;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GoogleAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final String GET_TOKEN_URL = "/user/get-token";

    private final UserDetailsServiceImpl userDetailsService;

    private final ObjectMapper objectMapper;

    private final GoogleClientSecrets clientSecrets;


    @SneakyThrows
    public GoogleAuthenticationFilter(final UserDetailsServiceImpl userDetailsService, final ObjectMapper objectMapper,
                                      final GoogleClientSecrets clientSecrets) {
        super();
        this.clientSecrets = clientSecrets;
        this.setFilterProcessesUrl(GET_TOKEN_URL);
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(final HttpServletRequest req, final HttpServletResponse resp) {
        final String accessCode = req.getHeader("access-code");
        if (accessCode == null || accessCode.isEmpty()) {
            this.writeNotAuthorizedResponse(resp, "Bad access code.");
            return null;
        }
        final NetHttpTransport transport = new NetHttpTransport();
        final JsonFactory jsonFactory = new GsonFactory();
        final GoogleTokenResponse tokenResponse =
                new GoogleAuthorizationCodeTokenRequest(
                        transport,
                        jsonFactory,
                        clientSecrets.getDetails().getClientId(),
                        clientSecrets.getDetails().getClientSecret(),
                        accessCode,
                        "postmessage")
                        .execute();

        final GoogleIdToken idToken = tokenResponse.parseIdToken();
        final GoogleIdToken.Payload payload = idToken.getPayload();
        CustomUserDetails foundUser = this.userDetailsService.loadUserByGoogleSubject(payload.getSubject());
        User user;
        if (foundUser == null) {
            user = User.builder()
                    .googleUserSubject(payload.getSubject())
                    .email(payload.getEmail())
                    .username((String) payload.get("name"))
                    .pictureUrl((String) payload.get("picture"))
                    .familyName((String) payload.get("family_name"))
                    .givenName((String) payload.get("given_name"))
                    .role(Role.ROLE_USER)
                    .build();
        } else {
            user = User.builder()
                    .googleUserSubject(foundUser.getGoogleUserSubject())
                    .email(payload.getEmail())
                    .username((String) payload.get("name"))
                    .pictureUrl((String) payload.get("picture"))
                    .familyName((String) payload.get("family_name"))
                    .givenName((String) payload.get("given_name"))
                    .role(foundUser.getRole())
                    .build();
            user.setUuid(foundUser.getUuid());
            user.setId(foundUser.getId());
        }
        this.userDetailsService.registerOrUpdateUser(user);
        foundUser = this.userDetailsService.loadUserByGoogleSubject(payload.getSubject());
        resp.addHeader("Authorization", tokenResponse.getIdToken());
        final Authentication authToken = new UsernamePasswordAuthenticationToken(foundUser, null, foundUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        return authToken;
    }


    @SneakyThrows
    @Override
    protected void successfulAuthentication(final HttpServletRequest req, final HttpServletResponse resp,
                                            final FilterChain chain, final Authentication authResult) {
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void writeNotAuthorizedResponse(final HttpServletResponse resp, final String message) throws IOException {
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        final ErrorDto errorDto = ErrorDto.builder()
                .message(message)
                .build();
        resp.getOutputStream().println(objectMapper.writeValueAsString(errorDto));
    }
}
