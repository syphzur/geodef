package pl.polsl.bol.krzysztof.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
public interface EntityRepository<T> extends CrudRepository<T, Long> {
    List<T> findAll();

    Page<T> findAll(final Pageable page);

    Optional<T> findByUuid(final UUID uuid);

}
