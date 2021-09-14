package pl.polsl.bol.krzysztof.backend.repository;

import org.springframework.stereotype.Repository;
import pl.polsl.bol.krzysztof.backend.models.entities.SpatialObject;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpatialObjectRepository extends EntityRepository<SpatialObject> {
    List<SpatialObject> findAllByType_uuid(final UUID typeUuid);
    List<SpatialObject> findAllByCreator_uuid(final UUID creatorUuid);
    long countByType_uuid(final UUID typeUuid);
}
