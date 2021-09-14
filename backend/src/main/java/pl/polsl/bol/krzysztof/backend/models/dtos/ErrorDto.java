package pl.polsl.bol.krzysztof.backend.models.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorDto {

    private String exceptionTypeName;

    private String objectTypeName;

    private String message;
}
