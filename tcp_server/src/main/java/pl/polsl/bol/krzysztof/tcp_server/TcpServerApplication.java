package pl.polsl.bol.krzysztof.tcp_server;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import pl.polsl.bol.krzysztof.tcp_server.configuration.ConfigProperties;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableJpaRepositories(basePackages = {"pl.polsl.bol.krzysztof.backend.repository"})
@EntityScan(basePackages = {"pl.polsl.bol.krzysztof.backend.models.entities"})
@EnableConfigurationProperties(ConfigProperties.class)
@Import({pl.polsl.bol.krzysztof.backend.configuration.BeanConfiguration.class,
        pl.polsl.bol.krzysztof.backend.converters.StringToGpsDataConverter.class,
        pl.polsl.bol.krzysztof.backend.validation.validators.GpsMessageValidator.class,
        pl.polsl.bol.krzysztof.backend.services.GpsTrackerService.class})
public class TcpServerApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(TcpServerApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

}
