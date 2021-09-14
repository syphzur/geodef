package pl.polsl.bol.krzysztof.backend.validation.annotations;

import pl.polsl.bol.krzysztof.backend.validation.validators.GpsMessageValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = GpsMessageValidator.class)
public @interface GpsMessage {
    String message() default "Bad GPS message format!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
