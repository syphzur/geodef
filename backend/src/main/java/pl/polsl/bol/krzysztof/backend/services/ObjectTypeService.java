package pl.polsl.bol.krzysztof.backend.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.polsl.bol.krzysztof.backend.models.entities.Layer;
import pl.polsl.bol.krzysztof.backend.models.entities.ObjectType;
import pl.polsl.bol.krzysztof.backend.repository.ObjectTypeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ObjectTypeService extends AbstractService<ObjectType, ObjectTypeRepository> {

    private final LayerService layerService;

    public ObjectTypeService(final ObjectTypeRepository repository, final LayerService layerService) {
        super(repository);
        this.layerService = layerService;
    }

    public List<ObjectType> getAllByLayer(final UUID layerUuid, final boolean withHierarchicalLayers) {
        final List<ObjectType> typeList = new ArrayList<>(this.repository.findAllByLayer_uuid(layerUuid));
        if (withHierarchicalLayers) {
            Layer layer = layerService.findModelByUuidOrThrowException(layerUuid);
            layer = layer.getPrevLayer();
            while (layer != null) {
                typeList.addAll(this.repository.findAllByLayer_uuid(layer.getUuid()));
                layer = layer.getPrevLayer();
            }
        }
        return typeList;
    }

    public Page<ObjectType> getPageByCreator(final Pageable pageable, final UUID creatorUuid) {
        return this.repository.findAllByCreator_uuid(pageable, creatorUuid);
    }

    public long countByLayer(final UUID layerUuid) {
        return this.repository.countByLayer_uuid(layerUuid);
    }
}


