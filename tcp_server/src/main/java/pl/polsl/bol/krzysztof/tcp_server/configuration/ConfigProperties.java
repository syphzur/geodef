package pl.polsl.bol.krzysztof.tcp_server.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Configuration
@Data
@ConfigurationProperties(prefix = "gps")
public class ConfigProperties {

    @Min(1025)
    @Max(65536)
    private int tcpServerPort = 5000;

    private boolean tcpServerRun = false;
}
