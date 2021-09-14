package pl.polsl.bol.krzysztof.backend.validation.validators;

import pl.polsl.bol.krzysztof.backend.validation.annotations.ValueOfEnum;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ValueOfEnumValidator implements ConstraintValidator<ValueOfEnum, String> {

    private Set<String> acceptedValues;

    @Override
    public void initialize(final ValueOfEnum constraintAnnotation) {
        acceptedValues = Stream.of(constraintAnnotation.enumClass().getEnumConstants()).map(Enum::name).
                collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(final String s, final ConstraintValidatorContext constraintValidatorContext) {
        return acceptedValues.stream().anyMatch(value -> value.equalsIgnoreCase(s));
    }
}
