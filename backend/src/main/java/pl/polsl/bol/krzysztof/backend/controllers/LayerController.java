package pl.polsl.bol.krzysztof.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.polsl.bol.krzysztof.backend.models.dtos.LayerDto;
import pl.polsl.bol.krzysztof.backend.models.entities.Layer;
import pl.polsl.bol.krzysztof.backend.services.LayerService;
import pl.polsl.bol.krzysztof.backend.services.ObjectTypeService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/layer")
public class LayerController extends AbstractController<Layer, LayerService, LayerDto> {

    private final ObjectTypeService objectTypeService;

    public LayerController(final LayerService service, final ObjectTypeService objectTypeService) {
        super(service);
        this.objectTypeService = objectTypeService;
    }

    @GetMapping(path = "/all/hierarchical")
    public ResponseEntity<List<LayerDto>> handleGetAllHierarchical() {
        final List<LayerDto> all = this.service.getAllHierarchical().stream().map(this::convertModelToDto).collect(Collectors.toList());
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    @Override
    public Class<Layer> getModelClass() {
        return Layer.class;
    }

    @Override
    public Class<LayerDto> getDtoClass() {
        return LayerDto.class;
    }

    @Override
    public Layer convertDtoToModel(final LayerDto dto) {
        final Layer model = super.convertDtoToModel(dto);
        final UUID prevLayerUuid = dto.getPrevLayerUuid();
        if (prevLayerUuid != null) {
            final Layer prevLayer = this.service.findModelByUuidOrThrowException(dto.getPrevLayerUuid());
            model.setPrevLayer(prevLayer);
        }
        return model;
    }

    /**
     * https://github.com/modelmapper/modelmapper/issues/192
     * Model mapper does not work with self-reference
     */
    @Override
    public LayerDto convertModelToDto(final Layer model) {
        final LayerDto dto = super.convertModelToDto(model);
        final Layer prevLayer = model.getPrevLayer();
        if (prevLayer != null) {
            dto.setPrevLayerName(model.getPrevLayer().getName());
            dto.setPrevLayerUuid(model.getPrevLayer().getUuid());
        }
        dto.setTypesCount(objectTypeService.countByLayer(model.getUuid()));
        return dto;
    }
}
