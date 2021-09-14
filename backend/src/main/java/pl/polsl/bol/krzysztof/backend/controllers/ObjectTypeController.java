package pl.polsl.bol.krzysztof.backend.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.polsl.bol.krzysztof.backend.models.dtos.ObjectTypeDto;
import pl.polsl.bol.krzysztof.backend.models.entities.Layer;
import pl.polsl.bol.krzysztof.backend.models.entities.ObjectType;
import pl.polsl.bol.krzysztof.backend.models.entities.User;
import pl.polsl.bol.krzysztof.backend.services.LayerService;
import pl.polsl.bol.krzysztof.backend.services.ObjectTypeService;
import pl.polsl.bol.krzysztof.backend.services.SpatialObjectService;
import pl.polsl.bol.krzysztof.backend.services.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/object-type")
public class ObjectTypeController extends AbstractController<ObjectType, ObjectTypeService, ObjectTypeDto> {

    private final LayerService layerService;

    private final UserService userService;

    private final SpatialObjectService spatialObjectService;

    public ObjectTypeController(final ObjectTypeService service, final LayerService layerService,
                                final UserService userService, final SpatialObjectService spatialObjectService) {
        super(service);
        this.layerService = layerService;
        this.userService = userService;
        this.spatialObjectService = spatialObjectService;
    }

    @GetMapping(path = "/all", params = "layerUuid")
    public ResponseEntity<List<ObjectTypeDto>> handleGetAllByLayer(@RequestParam final UUID layerUuid,
                                                                   @RequestParam(defaultValue = "false") final boolean withHierarchicalLayers) {
        final List<ObjectTypeDto> spatialObjectList = this.service.getAllByLayer(layerUuid, withHierarchicalLayers)
                .stream().map(this::convertModelToDto).collect(Collectors.toList());
        return new ResponseEntity<>(spatialObjectList, HttpStatus.OK);
    }

    @GetMapping(path = "/all", params = "creatorUuid")
    public ResponseEntity<Page<ObjectTypeDto>> handleGetPageByCreator(@RequestParam final UUID creatorUuid,
                                                                      final Pageable pageable) {
        final Page<ObjectTypeDto> spatialObjectList = this.service.getPageByCreator(pageable, creatorUuid).map(this::convertModelToDto);
        return new ResponseEntity<>(spatialObjectList, HttpStatus.OK);
    }

    @PutMapping(path = "/user/{uuid}")
    @PreAuthorize("@permissionEvaluatorService.hasPrivilege(authentication, #data.getCreatorUuid())")
    public ResponseEntity<HttpStatus> handlePutForUser(@PathVariable final UUID uuid,
                                                       @RequestBody @Valid final ObjectTypeDto data) {
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

    @Override
    public Class<ObjectType> getModelClass() {
        return ObjectType.class;
    }

    @Override
    public Class<ObjectTypeDto> getDtoClass() {
        return ObjectTypeDto.class;
    }

    @Override
    public ObjectType convertDtoToModel(final ObjectTypeDto dto) {
        final ObjectType model = super.convertDtoToModel(dto);
        final Layer layer = this.layerService.findModelByUuidOrThrowException(dto.getLayerUuid());
        final User creator = this.userService.findModelByUuidOrThrowException(dto.getCreatorUuid());
        model.setLayer(layer);
        model.setCreator(creator);
        return model;
    }

    @Override
    public ObjectTypeDto convertModelToDto(final ObjectType model) {
        final ObjectTypeDto dto = super.convertModelToDto(model);
        dto.setObjectsNumber(spatialObjectService.countByType(model.getUuid()));
        return dto;
    }
}
