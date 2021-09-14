package pl.polsl.bol.krzysztof.backend.services;

import org.springframework.stereotype.Service;
import pl.polsl.bol.krzysztof.backend.models.entities.ObjectType;
import pl.polsl.bol.krzysztof.backend.models.entities.SpatialObject;
import pl.polsl.bol.krzysztof.backend.repository.SpatialObjectRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SpatialObjectService extends AbstractService<SpatialObject, SpatialObjectRepository> {

    private final ObjectTypeService objectTypeService;

    public SpatialObjectService(final SpatialObjectRepository repository, final ObjectTypeService objectTypeService) {
        super(repository);
        this.objectTypeService = objectTypeService;
    }

    public List<SpatialObject> getAllByLayer(final UUID layerUuid, final boolean withHierarchicalLayers) {
        final List<ObjectType> typeList = this.objectTypeService.getAllByLayer(layerUuid, withHierarchicalLayers);
        final List<SpatialObject> objectList = new ArrayList<>();
        typeList.forEach(type -> objectList.addAll(this.getAllByType(type.getUuid())));
        return objectList;
    }

    public List<SpatialObject> getAllByType(final UUID typeUuid) {
        return this.repository.findAllByType_uuid(typeUuid);
    }

    public List<SpatialObject> getAllByCreator(final UUID creatorUuid) {
        return this.repository.findAllByCreator_uuid(creatorUuid);
    }

    public long countByType(final UUID typeUuid) {
        return this.repository.countByType_uuid(typeUuid);
    }
}

