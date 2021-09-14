package pl.polsl.bol.krzysztof.backend.exceptions;

public class NotFoundException extends BaseException {

    public NotFoundException(final String message, final String causeClassType) {
        super(message, causeClassType);
    }
}
