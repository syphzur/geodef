package pl.polsl.bol.krzysztof.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import pl.polsl.bol.krzysztof.backend.models.entities.ObjectType;

import java.util.List;
import java.util.UUID;

@Repository
public interface ObjectTypeRepository extends EntityRepository<ObjectType> {
    List<ObjectType> findAllByLayer_uuid(final UUID layerUuid);
    Page<ObjectType> findAllByCreator_uuid(final Pageable pageable, final UUID creatorUuid);
    long countByLayer_uuid(final UUID layerUuid);
}
