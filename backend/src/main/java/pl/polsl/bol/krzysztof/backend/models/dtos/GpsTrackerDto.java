package pl.polsl.bol.krzysztof.backend.models.dtos;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class GpsTrackerDto extends BaseDto {

    @Pattern(regexp = "[0-9]{12}", message = "Please check your imei.")
    private String imei;

    @NotNull
    private UUID ownerUuid;

    // not validated - readonly fields
    private LocalDateTime leatestDataDateTime;
}
