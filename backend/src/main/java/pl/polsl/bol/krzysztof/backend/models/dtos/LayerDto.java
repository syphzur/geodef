package pl.polsl.bol.krzysztof.backend.models.dtos;

import lombok.*;
import pl.polsl.bol.krzysztof.backend.validation.annotations.ValidLayersHierarchicalStructure;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.UUID;

@Getter
@Setter
@ValidLayersHierarchicalStructure
public class LayerDto extends BaseDto {

    @NotBlank
    @Size(max = 10)
    private String name;

    private UUID prevLayerUuid;

    // not validated - readonly fields

    private String prevLayerName;

    private long typesCount;
}
