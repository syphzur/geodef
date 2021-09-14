package pl.polsl.bol.krzysztof.backend.services;

import org.springframework.stereotype.Service;
import pl.polsl.bol.krzysztof.backend.exceptions.NotFoundException;
import pl.polsl.bol.krzysztof.backend.models.entities.GpsTracker;
import pl.polsl.bol.krzysztof.backend.repository.GpsTrackerRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GpsTrackerService extends AbstractService<GpsTracker, GpsTrackerRepository> {

    public GpsTrackerService(final GpsTrackerRepository repository) {
        super(repository);
    }

    public GpsTracker findModelByImeiOrThrowException(final String imei) {
        final Optional<GpsTracker> foundModel = this.repository.findByImei(imei);
        if (foundModel.isEmpty()) {
            throw new NotFoundException("No " + this.modelClassName +
                    " entity with given imei: " + imei, this.modelClassName);
        }
        return foundModel.get();
    }

    public List<GpsTracker> getAllByOwner(final UUID ownerUuid) {
        return this.repository.findAllByOwner_uuid(ownerUuid);
    }

}
