package pl.polsl.bol.krzysztof.backend.models.dtos;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Getter
@Setter
public class ObjectTypeDto extends BaseDto {

    @NotNull
    private UUID creatorUuid;

    @NotBlank
    @Size(max = 10)
    private String name;

    @NotNull
    private UUID layerUuid;

    // not validated - readonly fields

    private String layerName;

    private String creatorUsername;

    private long objectsNumber;

}
