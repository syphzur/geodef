package pl.polsl.bol.krzysztof.backend.models.dtos;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.spatial4j.io.jackson.GeometryAsGeoJSONSerializer;
import org.locationtech.spatial4j.io.jackson.GeometryDeserializer;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Getter
@Setter
public class SpatialObjectDto extends BaseDto {

    @NotNull
    private UUID creatorUuid;

    @NotBlank
    @Size(max = 20)
    private String name;

    @JsonSerialize(using = GeometryAsGeoJSONSerializer.class)
    @JsonDeserialize(using = GeometryDeserializer.class)
    @NotNull
    private Geometry geometry;

    @NotNull
    private UUID typeUuid;

    // not validated - readonly fields

    private String typeName;

    private String creatorUsername;
}
