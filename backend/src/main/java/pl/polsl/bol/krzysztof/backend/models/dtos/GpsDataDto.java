package pl.polsl.bol.krzysztof.backend.models.dtos;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.locationtech.jts.geom.Point;
import org.locationtech.spatial4j.io.jackson.GeometryAsGeoJSONSerializer;
import org.locationtech.spatial4j.io.jackson.GeometryDeserializer;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class GpsDataDto extends BaseDto {

    @NotNull
    private UUID trackerUuid;

    @PastOrPresent
    private LocalDateTime dateTime;

    @JsonSerialize(using = GeometryAsGeoJSONSerializer.class)
    @JsonDeserialize(using = GeometryDeserializer.class)
    @NotNull(message = "Point cannot be null.")
    private Point point;

    @PositiveOrZero
    private double speed;

    private String trackerImei;

}
