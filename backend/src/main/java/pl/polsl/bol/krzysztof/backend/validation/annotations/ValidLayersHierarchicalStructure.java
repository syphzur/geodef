package pl.polsl.bol.krzysztof.backend.validation.annotations;

import pl.polsl.bol.krzysztof.backend.validation.validators.LayerValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LayerValidator.class)
public @interface ValidLayersHierarchicalStructure {
    String message() default "Please make sure that hierarchical structure of layers is correct.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
