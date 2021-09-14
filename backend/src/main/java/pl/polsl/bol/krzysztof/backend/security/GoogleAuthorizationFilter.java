package pl.polsl.bol.krzysztof.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import pl.polsl.bol.krzysztof.backend.models.dtos.ErrorDto;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class GoogleAuthorizationFilter extends BasicAuthenticationFilter {
    private final UserDetailsServiceImpl userDetailsService;

    private final ObjectMapper objectMapper;

    private final GoogleClientSecrets clientSecrets;

    public GoogleAuthorizationFilter(final UserDetailsServiceImpl userDetailsService, final ObjectMapper objectMapper,
                                     final AuthenticationManager authenticationManager, final GoogleClientSecrets clientSecrets) {
        super(authenticationManager);
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
        this.clientSecrets = clientSecrets;
    }

    @SneakyThrows
    @Override
    public void doFilterInternal(final HttpServletRequest req, final HttpServletResponse resp,
                                 final FilterChain filterChain) {
        final String token = req.getHeader("Authorization");

        if (token == null || token.isEmpty()) {
            filterChain.doFilter(req, resp);
            return;
        }

        // use verifier as singleton
        final GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(this.clientSecrets.getDetails().getClientId()))
                .build();

        if (verifier == null) {
            this.writeNotAuthorizedResponse(resp, "Please login.");
            return;
        }

        final GoogleIdToken idToken = verifier.verify(token);
        if (idToken == null) {
            this.writeNotAuthorizedResponse(resp, "Token expired. Please login again.");
            return;
        }
        final GoogleIdToken.Payload payload = idToken.getPayload();
        UserDetails foundUser = this.userDetailsService.loadUserByGoogleSubject(payload.getSubject());

        final Authentication auth = new UsernamePasswordAuthenticationToken(foundUser, null,
                foundUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(req, resp);
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
