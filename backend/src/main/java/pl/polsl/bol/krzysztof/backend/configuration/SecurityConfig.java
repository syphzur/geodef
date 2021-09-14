package pl.polsl.bol.krzysztof.backend.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import pl.polsl.bol.krzysztof.backend.security.GoogleAuthenticationFilter;
import pl.polsl.bol.krzysztof.backend.security.GoogleAuthorizationFilter;
import pl.polsl.bol.krzysztof.backend.security.UserDetailsServiceImpl;

import java.io.*;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsServiceImpl userDetailsService;

    private final ObjectMapper objectMapper;

    private final GoogleClientSecrets clientSecrets;

    public SecurityConfig(final UserDetailsServiceImpl userDetailsService, final ObjectMapper objectMapper) throws IOException {
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
        final InputStream secretInputStream = new ClassPathResource("secrets.json").getInputStream();
        this.clientSecrets = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(),
                new BufferedReader(new InputStreamReader(secretInputStream)));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf()
                .disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/**")
                .authenticated()
                .antMatchers("/user/logged")
                .authenticated()
                .antMatchers("/**")
                .permitAll()
                .and()
                .exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .and()
                .addFilterBefore(new GoogleAuthenticationFilter(this.userDetailsService, this.objectMapper, this.clientSecrets), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new GoogleAuthorizationFilter(this.userDetailsService, this.objectMapper, this.authenticationManager(), this.clientSecrets), BasicAuthenticationFilter.class);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("authorization", "content-type", "x-auth-token", "access-code", "location", "x-xsrf-token", "uuid"));
        configuration.setExposedHeaders(List.of("x-auth-token", "location", "authorization", "uuid"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
