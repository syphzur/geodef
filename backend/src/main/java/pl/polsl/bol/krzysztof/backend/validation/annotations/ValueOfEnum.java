package pl.polsl.bol.krzysztof.backend.validation.annotations;

import pl.polsl.bol.krzysztof.backend.validation.validators.ValueOfEnumValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = ValueOfEnumValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValueOfEnum {
    Class<? extends Enum<?>> enumClass();

    String message() default "Must be any of enum {enumClass}.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
