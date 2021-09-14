package pl.polsl.bol.krzysztof.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.polsl.bol.krzysztof.backend.models.dtos.SpatialObjectDto;
import pl.polsl.bol.krzysztof.backend.models.entities.ObjectType;
import pl.polsl.bol.krzysztof.backend.models.entities.SpatialObject;
import pl.polsl.bol.krzysztof.backend.models.entities.User;
import pl.polsl.bol.krzysztof.backend.services.ObjectTypeService;
import pl.polsl.bol.krzysztof.backend.services.SpatialObjectService;
import pl.polsl.bol.krzysztof.backend.services.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/spatial-object")
public class SpatialObjectController extends AbstractController<SpatialObject, SpatialObjectService, SpatialObjectDto> {

    private final UserService userService;

    private final ObjectTypeService typeService;

    public SpatialObjectController(final SpatialObjectService service, final UserService userService, final ObjectTypeService typeService) {
        super(service);
        this.userService = userService;
        this.typeService = typeService;
    }

    @GetMapping(path = "/all", params = "layerUuid")
    public ResponseEntity<List<SpatialObjectDto>> handleGetAllByLayer(@RequestParam final UUID layerUuid,
                                                                      @RequestParam(defaultValue = "false") final boolean withHierarchicalLayers) {
        final List<SpatialObjectDto> all = this.service.getAllByLayer(layerUuid, withHierarchicalLayers)
                .stream().map(this::convertModelToDto).collect(Collectors.toList());
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @GetMapping(path = "/all", params = "typeUuid")
    public ResponseEntity<List<SpatialObjectDto>> handleGetAllByType(@RequestParam final UUID typeUuid) {
        final List<SpatialObjectDto> all = this.service.getAllByType(typeUuid)
                .stream().map(this::convertModelToDto).collect(Collectors.toList());
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @GetMapping(path = "/all", params = "creatorUuid")
    public ResponseEntity<List<SpatialObjectDto>> handleGetAllByCreator(@RequestParam final UUID creatorUuid) {
        final List<SpatialObjectDto> all = this.service.getAllByCreator(creatorUuid)
                .stream().map(this::convertModelToDto).collect(Collectors.toList());
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @PutMapping(path = "/user/{uuid}")
    @PreAuthorize("@permissionEvaluatorService.hasPrivilege(authentication, #data.getCreatorUuid())")
    public ResponseEntity<HttpStatus> handlePutForUser(@PathVariable final UUID uuid,
                                                       @RequestBody @Valid final SpatialObjectDto data) {
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

    @PostMapping(path = "/all-update", params = "userUuid")
    @PreAuthorize("@permissionEvaluatorService.hasPrivilege(authentication, #userUuid, #dtoList)")
    public ResponseEntity<HttpStatus> handleUpdateAll(@RequestParam final UUID userUuid,
                                                      @RequestBody @Valid final List<SpatialObjectDto> dtoList) {
        final List<SpatialObject> modelList = dtoList.stream().map(this::convertDtoToModel).collect(Collectors.toList());
        this.service.updateAll(modelList);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public Class<SpatialObject> getModelClass() {
        return SpatialObject.class;
    }

    @Override
    public Class<SpatialObjectDto> getDtoClass() {
        return SpatialObjectDto.class;
    }

    @Override
    public SpatialObject convertDtoToModel(final SpatialObjectDto dto) {
        final SpatialObject model = super.convertDtoToModel(dto);
        final User creator = this.userService.findModelByUuidOrThrowException(dto.getCreatorUuid());
        final ObjectType type = this.typeService.findModelByUuidOrThrowException(dto.getTypeUuid());
        model.setCreator(creator);
        model.setType(type);
        return model;
    }
}
