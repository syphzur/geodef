package pl.polsl.bol.krzysztof.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import pl.polsl.bol.krzysztof.backend.models.entities.GpsData;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GpsDataRepository extends EntityRepository<GpsData> {
    Page<GpsData> findAllByTracker_uuidIn(final Pageable pageable, final List<UUID> uuidList);

    Page<GpsData> findByDateTimeLessThanEqual(final Pageable pageable, final LocalDateTime dateTo);

    Page<GpsData> findByDateTimeGreaterThanEqual(final Pageable pageable, final LocalDateTime dateFrom);

    Page<GpsData> findByTracker_Uuid(final Pageable pageable, final UUID trackerUuid);

    Page<GpsData> findByDateTimeGreaterThanEqualAndTracker_Uuid(final Pageable pageable, final LocalDateTime dateFrom, final UUID trackerUuid);

    Page<GpsData> findByDateTimeLessThanEqualAndTracker_Uuid(final Pageable pageable, final LocalDateTime dateTo, final UUID trackerUuid);

    Page<GpsData> findByDateTimeGreaterThanEqualAndDateTimeLessThanEqual(final Pageable pageable, final LocalDateTime dateFrom, final LocalDateTime dateTo);

    Page<GpsData> findByDateTimeGreaterThanEqualAndDateTimeLessThanEqualAndTracker_Uuid(final Pageable pageable, final LocalDateTime dateFrom, final LocalDateTime dateTo, final UUID trackerUuid);

    GpsData findTopByTracker_uuidOrderByDateTimeDesc(final UUID trackerUuid);
}
