package pl.polsl.bol.krzysztof.backend.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.polsl.bol.krzysztof.backend.models.dtos.BaseDto;
import pl.polsl.bol.krzysztof.backend.models.entities.BaseEntity;
import pl.polsl.bol.krzysztof.backend.repository.EntityRepository;
import pl.polsl.bol.krzysztof.backend.services.AbstractService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Validated
public abstract class AbstractController<T extends BaseEntity, S extends AbstractService<T, ? extends EntityRepository<T>>, D extends BaseDto> {

    @Autowired
    protected ModelMapper modelMapper;

    protected final S service;

    private final Class<D> dtoClass;

    private final Class<T> modelClass;

    protected AbstractController(final S service) {
        this.service = service;
        this.modelClass = getModelClass();
        this.dtoClass = getDtoClass();
        this.service.setModelClassName(this.modelClass.getSimpleName());
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("@permissionEvaluatorService.hasPrivilege(authentication, this.getService().getModelClassName(), 'get')")
    public ResponseEntity<D> handleGetSingle(@PathVariable final UUID uuid) {
        final D dto = this.convertModelToDto(this.service.getSingle(uuid));
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/all")
    @PreAuthorize("@permissionEvaluatorService.hasPrivilege(authentication, this.getService().getModelClassName(), 'get')")
    public ResponseEntity<List<D>> handleGetAll() {
        final List<D> all = this.service.getAll().stream().map(this::convertModelToDto).collect(Collectors.toList());
        return new ResponseEntity<>(all, HttpStatus.OK);
    }

    // params - page, size, sort
    @GetMapping("/")
    @PreAuthorize("@permissionEvaluatorService.hasPrivilege(authentication, this.getService().getModelClassName(), 'get')")
    public ResponseEntity<Page<D>> handleGetPage(final Pageable pageable) {
        final Page<D> page = this.service.getPage(pageable).map(this::convertModelToDto);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @PostMapping("/")
    @PreAuthorize("@permissionEvaluatorService.hasPrivilege(authentication, this.getService().getModelClassName(), 'post')")
    public ResponseEntity<HttpStatus> handlePost(@RequestBody @Valid final D data) {
        final T model = this.convertDtoToModel(data);
        this.service.save(model);
        final UUID uuid = model.getUuid();
        final HttpHeaders headers = new HttpHeaders();
        final URI uri = linkTo(methodOn(this.getClass()).handleGetSingle(uuid)).toUri();
        headers.add("Location", uri.toASCIIString());
        headers.add("Uuid", uuid.toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PutMapping("/{uuid}")
    @PreAuthorize("@permissionEvaluatorService.hasPrivilege(authentication, this.getService().getModelClassName(), 'put')")
    public ResponseEntity<HttpStatus> handlePut(@PathVariable final UUID uuid, @RequestBody @Valid final D data) {
        this.service.update(uuid, this.convertDtoToModel(data));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{uuid}")
    @PreAuthorize("@permissionEvaluatorService.hasPrivilege(authentication, this.getService().getModelClassName(), 'delete')")
    public ResponseEntity<HttpStatus> handleDelete(@PathVariable final UUID uuid) {
        this.service.delete(uuid);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    public abstract Class<T> getModelClass();

    public abstract Class<D> getDtoClass();

    public D convertModelToDto(final T model) {
        return this.modelMapper.map(model, this.dtoClass);
    }

    public T convertDtoToModel(final D dto) {
        return this.modelMapper.map(dto, this.modelClass);
    }

    public S getService()
    {
        return this.service;
    }
}
