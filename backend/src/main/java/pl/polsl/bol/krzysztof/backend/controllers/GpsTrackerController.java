package pl.polsl.bol.krzysztof.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.polsl.bol.krzysztof.backend.models.dtos.GpsTrackerDto;
import pl.polsl.bol.krzysztof.backend.models.entities.GpsData;
import pl.polsl.bol.krzysztof.backend.models.entities.GpsTracker;
import pl.polsl.bol.krzysztof.backend.models.entities.User;
import pl.polsl.bol.krzysztof.backend.services.GpsDataService;
import pl.polsl.bol.krzysztof.backend.services.GpsTrackerService;
import pl.polsl.bol.krzysztof.backend.services.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/gps-tracker")
public class GpsTrackerController extends AbstractController<GpsTracker, GpsTrackerService, GpsTrackerDto> {

    private final UserService userService;

    private final GpsDataService gpsDataService;

    public GpsTrackerController(final GpsTrackerService service, final UserService userService,
                                final GpsDataService gpsDataService) {
        super(service);
        this.userService = userService;
        this.gpsDataService = gpsDataService;
    }

    @PutMapping(path = "/user/{uuid}")
    @PreAuthorize("@permissionEvaluatorService.hasPrivilege(authentication, #data.getOwnerUuid())")
    public ResponseEntity<HttpStatus> handlePutForUser(@PathVariable final UUID uuid,
                                                       @RequestBody @Valid final GpsTrackerDto data) {
        this.service.update(uuid, this.convertDtoToModel(data));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(path = "/user/{uuid}", params = "creatorUuid")
    @PreAuthorize("@permissionEvaluatorService.hasPrivilege(authentication, #creatorUuid)")
    public ResponseEntity<HttpStatus> handleDeleteForUser(@RequestParam final UUID creatorUuid,
                                                          @PathVariable final UUID uuid) {
        this.service.delete(uuid);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(path = "/all", params = "ownerUuid")
    @PreAuthorize("@permissionEvaluatorService.hasPrivilege(authentication, #ownerUuid)")
    public ResponseEntity<List<GpsTrackerDto>> handleGetAllByOwner(@RequestParam final UUID ownerUuid) {
        final List<GpsTrackerDto> all = this.service.getAllByOwner(ownerUuid)
                .stream().map(this::convertModelToDto).collect(Collectors.toList());
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @Override
    public Class<GpsTracker> getModelClass() {
        return GpsTracker.class;
    }

    @Override
    public Class<GpsTrackerDto> getDtoClass() {
        return GpsTrackerDto.class;
    }

    @Override
    public GpsTrackerDto convertModelToDto(GpsTracker model) {
        final GpsTrackerDto dto = super.convertModelToDto(model);
        final GpsData leatestData = gpsDataService.getLeatestByTrackerUuid(dto.getUuid());
        if (leatestData != null) {
            dto.setLeatestDataDateTime(leatestData.getDateTime());
        }
        return dto;
    }

    @Override
    public GpsTracker convertDtoToModel(final GpsTrackerDto dto) {
        final GpsTracker model = super.convertDtoToModel(dto);
        final User owner = userService.findModelByUuidOrThrowException(dto.getOwnerUuid());
        model.setOwner(owner);
        return model;
    }
}
