package pl.polsl.bol.krzysztof.backend.repository;

import org.springframework.stereotype.Repository;
import pl.polsl.bol.krzysztof.backend.models.entities.GpsTracker;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GpsTrackerRepository extends EntityRepository<GpsTracker> {

    Optional<GpsTracker> findByImei(final String imei);
    List<GpsTracker> findAllByOwner_uuid(final UUID ownerUuid);
}

