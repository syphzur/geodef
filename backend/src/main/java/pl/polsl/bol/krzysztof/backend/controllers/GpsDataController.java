package pl.polsl.bol.krzysztof.backend.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.polsl.bol.krzysztof.backend.models.dtos.GpsDataDto;
import pl.polsl.bol.krzysztof.backend.models.entities.GpsData;
import pl.polsl.bol.krzysztof.backend.models.entities.GpsTracker;
import pl.polsl.bol.krzysztof.backend.services.GpsDataService;
import pl.polsl.bol.krzysztof.backend.services.GpsTrackerService;
import pl.polsl.bol.krzysztof.backend.validation.annotations.GpsMessage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/gps-data")
public class GpsDataController extends AbstractController<GpsData, GpsDataService, GpsDataDto> {

    private final GpsTrackerService gpsTrackerService;

    public GpsDataController(final GpsDataService gpsDataService, final GpsTrackerService gpsTrackerService) {
        super(gpsDataService);
        this.gpsTrackerService = gpsTrackerService;
    }

    @PostMapping("/message")
    public ResponseEntity<HttpStatus> saveMessage(@RequestBody @Valid @GpsMessage final String data) {
        this.service.save(data);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(path = "/all", params = "userUuid")
    @PreAuthorize("@permissionEvaluatorService.hasPrivilege(authentication, #userUuid)")
    public ResponseEntity<Page<GpsDataDto>> handleGetPageByUser(@RequestParam final UUID userUuid,
                                                                final Pageable pageable,
                                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate dateFrom,
                                                                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate dateTo,
                                                                @RequestParam(required = false) final UUID trackerUuid) {
        final Page<GpsDataDto> spatialObjectList = this.service.getPageByUser(pageable, userUuid, dateFrom, dateTo, trackerUuid).map(this::convertModelToDto);
        return new ResponseEntity<>(spatialObjectList, HttpStatus.OK);
    }

    @DeleteMapping(path = "/user/{uuid}", params = "creatorUuid")
    @PreAuthorize("@permissionEvaluatorService.hasPrivilege(authentication, #creatorUuid)")
    public ResponseEntity<HttpStatus> handleDeleteForUser(@RequestParam final UUID creatorUuid,
                                                          @PathVariable final UUID uuid) {
        this.service.delete(uuid);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public Class<GpsData> getModelClass() {
        return GpsData.class;
    }

    @Override
    public Class<GpsDataDto> getDtoClass() {
        return GpsDataDto.class;
    }

    @Override
    public GpsData convertDtoToModel(final GpsDataDto dto) {
        final GpsData model = super.convertDtoToModel(dto);
        final GpsTracker gpsTracker = this.gpsTrackerService.findModelByUuidOrThrowException(dto.getTrackerUuid());
        model.setTracker(gpsTracker);
        return model;
    }

}
