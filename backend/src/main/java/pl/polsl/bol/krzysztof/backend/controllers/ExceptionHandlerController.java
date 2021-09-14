package pl.polsl.bol.krzysztof.backend.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.polsl.bol.krzysztof.backend.exceptions.BadMessageFormatException;
import pl.polsl.bol.krzysztof.backend.exceptions.BaseException;
import pl.polsl.bol.krzysztof.backend.exceptions.NotFoundException;
import pl.polsl.bol.krzysztof.backend.models.dtos.ErrorDto;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerController {

    /**
     * Handles exception and cuts message from:
     * "ERROR: duplicate key value violates unique constraint "uk_meb3tm159kt0clyot5mmv8oht" Detail: Key (organization_name)=(Villa College QI Campus) already exists."
     * to: "Key (organization_name)=(Villa College QI Campus) already exists."
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    private ResponseEntity<ErrorDto> handlePSQLException(final DataIntegrityViolationException e) {
        log.warn("{} handled.", e.getClass().getSimpleName());
        log.warn(e.getMessage());
        String message = e.getMostSpecificCause().getMessage();
        final int detailsMessageBeginningIndex = message.lastIndexOf(":");
        if (detailsMessageBeginningIndex >= 0) {
            message = message.substring(detailsMessageBeginningIndex + 2);
        }
        final ErrorDto error = ErrorDto.builder()
                .exceptionTypeName(e.getClass().getSimpleName())
                .message(message)
                .build();
        return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    private ResponseEntity<ErrorDto> handleHttpMessageNotReadableException(final HttpMessageNotReadableException e) {
        log.warn("{} handled.", e.getClass().getSimpleName());
        log.warn(e.getMessage());
        final ErrorDto error = ErrorDto.builder()
                .exceptionTypeName(e.getClass().getSimpleName())
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    private ResponseEntity<List<ErrorDto>> handleConstraintViolationException(final ConstraintViolationException e) {
        log.warn("{} handled.", e.getClass().getSimpleName());
        final List<ErrorDto> errorDtoList = new ArrayList<>();
        final Set<ConstraintViolation<?>> errorList = e.getConstraintViolations();
        errorList.forEach(error -> {
            final String message = "Validation failed on field: " + error.getPropertyPath() + ". " +
                    "Invalid value: " + error.getInvalidValue();
            errorDtoList.add(
                    ErrorDto.builder()
                            .exceptionTypeName(e.getClass().getSimpleName())
                            .message(message)
                            .objectTypeName(error.getRootBean().toString())
                            .build()
            );
            log.warn(message);
        });
        return new ResponseEntity<>(errorDtoList, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(NotFoundException.class)
    private ResponseEntity<ErrorDto> handleNotFoundException(final NotFoundException e) {
        return this.handleExtendingBaseException(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadMessageFormatException.class)
    private ResponseEntity<ErrorDto> handleBadMessageFormatException(final BadMessageFormatException e) {
        return this.handleExtendingRuntimeException(e, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<List<ErrorDto>> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.warn("{} handled.", e.getClass().getSimpleName());
        final List<ErrorDto> errorDtoList = new ArrayList<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            final String message = "Validation failed on field: " + error.getField() + ". " + error.getDefaultMessage();
            errorDtoList.add(
                    ErrorDto.builder()
                            .exceptionTypeName(e.getClass().getSimpleName())
                            .message(message)
                            .objectTypeName(error.getObjectName())
                            .build()
            );
            log.warn(message);
        });

        e.getBindingResult().getAllErrors().forEach(error -> {
            final String message = "Validation failed: " + error.getDefaultMessage();
            errorDtoList.add(
                    ErrorDto.builder()
                            .exceptionTypeName(e.getClass().getSimpleName())
                            .message(message)
                            .objectTypeName(error.getObjectName())
                            .build()
            );
            log.warn(message);
        });

        return new ResponseEntity<>(errorDtoList, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    private <E extends RuntimeException> ResponseEntity<ErrorDto> handleExtendingRuntimeException(final E e, final HttpStatus status) {
        log.warn("{} handled.", e.getClass().getSimpleName());
        log.warn(e.getMessage());
        final ErrorDto error = ErrorDto.builder()
                .exceptionTypeName(e.getClass().getSimpleName())
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(error, status);
    }

    private <E extends BaseException> ResponseEntity<ErrorDto> handleExtendingBaseException(final E e, final HttpStatus status) {
        log.warn("{} handled for type: {}.", e.getClass().getSimpleName(), e.getCauseClassTypeName());
        log.warn(e.getMessage());
        final ErrorDto error = ErrorDto.builder()
                .exceptionTypeName(e.getClass().getSimpleName())
                .objectTypeName(e.getCauseClassTypeName())
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(error, status);
    }
}
