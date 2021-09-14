package pl.polsl.bol.krzysztof.backend.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.polsl.bol.krzysztof.backend.converters.StringToGpsDataConverter;
import pl.polsl.bol.krzysztof.backend.models.entities.BaseEntity;
import pl.polsl.bol.krzysztof.backend.models.entities.GpsData;
import pl.polsl.bol.krzysztof.backend.models.entities.GpsTracker;
import pl.polsl.bol.krzysztof.backend.repository.GpsDataRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GpsDataService extends AbstractService<GpsData, GpsDataRepository> {

    private final StringToGpsDataConverter gpsDataConverter;

    private final GpsTrackerService gpsTrackerService;

    public GpsDataService(final GpsDataRepository gpsDataRepository, final StringToGpsDataConverter gpsDataConverter,
                          final GpsTrackerService gpsTrackerService) {
        super(gpsDataRepository);
        this.gpsDataConverter = gpsDataConverter;
        this.gpsTrackerService = gpsTrackerService;
    }

    public Page<GpsData> getPageByUser(final Pageable pageable, final UUID userUuid, final LocalDate dateFrom, final LocalDate dateTo, final UUID trackerUuid) {
        if (dateFrom != null && dateTo != null && trackerUuid != null) {
            return this.repository.findByDateTimeGreaterThanEqualAndDateTimeLessThanEqualAndTracker_Uuid(pageable, dateFrom.atStartOfDay(), dateTo.atStartOfDay(), trackerUuid);
        }
        if (dateFrom != null && dateTo != null) {
            return this.repository.findByDateTimeGreaterThanEqualAndDateTimeLessThanEqual(pageable, dateFrom.atStartOfDay(), dateTo.atStartOfDay());
        }
        if (dateFrom != null && trackerUuid != null) {
            return this.repository.findByDateTimeGreaterThanEqualAndTracker_Uuid(pageable, dateFrom.atStartOfDay(), trackerUuid);
        }
        if (dateTo != null && trackerUuid != null) {
            return this.repository.findByDateTimeLessThanEqualAndTracker_Uuid(pageable, dateTo.atStartOfDay(), trackerUuid);
        }
        if (dateFrom != null) {
            return this.repository.findByDateTimeGreaterThanEqual(pageable, dateFrom.atStartOfDay());
        }
        if (dateTo != null) {
            return this.repository.findByDateTimeLessThanEqual(pageable, dateTo.atStartOfDay());
        }
        if (trackerUuid != null) {
            return this.repository.findByTracker_Uuid(pageable, trackerUuid);
        }
        final List<GpsTracker> trackerList = this.gpsTrackerService.getAllByOwner(userUuid);
        final List<UUID> trackerUuidList = trackerList.stream().map(BaseEntity::getUuid).collect(Collectors.toList());
        return this.repository.findAllByTracker_uuidIn(pageable, trackerUuidList);
    }

    public List<GpsData> getAllByUser(final UUID userUuid, final LocalDate dateFrom, final LocalDate dateTo, final UUID trackerUuid) {
        return this.getPageByUser(Pageable.unpaged(), userUuid, dateFrom, dateTo, trackerUuid).toList();
    }

    public void save(final String data) {
        this.repository.save(this.gpsDataConverter.convert(data));
    }

    public GpsData getLeatestByTrackerUuid(final UUID trackerUuid) {
        return this.repository.findTopByTracker_uuidOrderByDateTimeDesc(trackerUuid);
    }

}
