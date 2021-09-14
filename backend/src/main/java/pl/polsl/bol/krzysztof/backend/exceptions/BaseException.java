package pl.polsl.bol.krzysztof.backend.exceptions;

import lombok.Getter;

public abstract class BaseException extends RuntimeException {
    @Getter
    private String causeClassTypeName;

    public BaseException(final String message, final String causeClassTypeName) {
        super(message);
        this.causeClassTypeName = causeClassTypeName;
    }
}
