package pl.polsl.bol.krzysztof.backend.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.wololo.jts2geojson.GeoJSONWriter;

@Configuration
public class BeanConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        final ModelMapper modelMapper = new ModelMapper();
        modelMapper
                .getConfiguration()
                .setSkipNullEnabled(true)
                .setAmbiguityIgnored(true);
        return modelMapper;
    }

    @Bean
    public GeoJSONWriter geoJSONWriter() {
        return new GeoJSONWriter();
    }
}
