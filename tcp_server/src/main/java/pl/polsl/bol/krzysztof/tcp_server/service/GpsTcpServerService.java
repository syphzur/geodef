package pl.polsl.bol.krzysztof.tcp_server.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.polsl.bol.krzysztof.backend.converters.StringToGpsDataConverter;
import pl.polsl.bol.krzysztof.backend.exceptions.NotFoundException;
import pl.polsl.bol.krzysztof.backend.repository.GpsDataRepository;
import pl.polsl.bol.krzysztof.backend.validation.validators.GpsMessageValidator;

@Slf4j
@Service
public class GpsTcpServerService {

    private final GpsMessageValidator validator;

    private final GpsDataRepository repository;

    private final StringToGpsDataConverter converter;

    public GpsTcpServerService(final GpsMessageValidator validator, final GpsDataRepository repository, final StringToGpsDataConverter converter) {
        this.validator = validator;
        this.repository = repository;
        this.converter = converter;
    }

    public void save(final String message) {
        try {
            if (this.validator.isValid(message, null)) {
                this.repository.save(this.converter.convert(message));
            } else {
                log.error("Bad message format! Following message is not valid:" + message);
            }
        }
        catch (final NotFoundException e) {
            log.error(e.getMessage());
        }
    }
}
