package pl.polsl.bol.krzysztof.backend.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.polsl.bol.krzysztof.backend.exceptions.NotFoundException;
import pl.polsl.bol.krzysztof.backend.models.entities.BaseEntity;
import pl.polsl.bol.krzysztof.backend.repository.EntityRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class AbstractService<T extends BaseEntity, R extends EntityRepository<T>> {

    protected String modelClassName;

    protected final R repository;

    public AbstractService(final R repository) {
        this.repository = repository;
    }

    public T getSingle(final UUID uuid) {
        return this.findModelByUuidOrThrowException(uuid);
    }

    public List<T> getAll() {
        return this.repository.findAll();
    }

    public Page<T> getPage(final Pageable pageable) {
        return this.repository.findAll(pageable);
    }

    public void save(final T data) {
        this.repository.save(data);
    }

    public void delete(final UUID uuid) {
        this.repository.delete(this.findModelByUuidOrThrowException(uuid));
    }

    public void update(final UUID uuid, final T data) {
        final T model = this.findModelByUuidOrThrowException(uuid);
        data.setId(model.getId());
        this.repository.save(data);
    }

    public void updateAll(final List<T> dataList) {
        dataList.forEach(it -> this.update(it.getUuid(), it));
    }

    public T findModelByUuidOrThrowException(final UUID uuid) {
        final Optional<T> foundModel = this.repository.findByUuid(uuid);
        if (foundModel.isEmpty()) {
            if (this.modelClassName == null) {
                throw new NotFoundException("No entity with given uuid: " + uuid, null);
            } else {
                throw new NotFoundException("No " + this.modelClassName +
                        " entity with given uuid: " + uuid, this.modelClassName);
            }

        }
        return foundModel.get();
    }

    public void setModelClassName(final String modelClassName) {
        this.modelClassName = modelClassName;
    }

    public String getModelClassName() {
        return this.modelClassName;
    }

}
