package pl.polsl.bol.krzysztof.backend.repository;

import org.springframework.stereotype.Repository;
import pl.polsl.bol.krzysztof.backend.models.entities.Layer;

import java.util.List;

@Repository
public interface LayerRepository extends EntityRepository<Layer> {
    List<Layer> getAllByOrderByName();
}
