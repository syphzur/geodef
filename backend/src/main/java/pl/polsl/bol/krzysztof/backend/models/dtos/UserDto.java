package pl.polsl.bol.krzysztof.backend.models.dtos;

import lombok.Getter;
import lombok.Setter;
import pl.polsl.bol.krzysztof.backend.models.enums.Role;
import pl.polsl.bol.krzysztof.backend.validation.annotations.ValueOfEnum;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UserDto extends BaseDto {

    @Email(message = "Please add valid e-mail address.")
    private String email;

    private String pictureUrl;

    @NotBlank(message = "Please add valid Google id.")
    private String googleUserSubject;

    @NotBlank(message = "Please add valid username.")
    @Size(max = 30, min = 6, message = "Please add valid username.")
    private String username;

    private String familyName;

    private String givenName;

    @ValueOfEnum(enumClass = Role.class, message = "Please specify valid role.")
    private String role;

}
