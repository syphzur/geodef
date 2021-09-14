package pl.polsl.bol.krzysztof.backend.services;

import org.springframework.stereotype.Service;
import pl.polsl.bol.krzysztof.backend.converters.FlatToHierarchicalStructureLayerConverter;
import pl.polsl.bol.krzysztof.backend.models.entities.Layer;
import pl.polsl.bol.krzysztof.backend.repository.LayerRepository;

import java.util.List;

@Service
public class LayerService extends AbstractService<Layer, LayerRepository> {

    final FlatToHierarchicalStructureLayerConverter converter;

    public LayerService(final LayerRepository repository, final FlatToHierarchicalStructureLayerConverter converter) {
        super(repository);
        this.converter = converter;
    }

    public List<Layer> getAllHierarchical() {
        return converter.convert(this.repository.getAllByOrderByName());
    }

}

