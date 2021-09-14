package pl.polsl.bol.krzysztof.backend.validation.validators;

import org.springframework.stereotype.Component;
import pl.polsl.bol.krzysztof.backend.validation.annotations.GpsMessage;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GpsMessageValidator implements ConstraintValidator<GpsMessage, String> {

    private final Pattern pattern = Pattern.compile("^\\d{12}[A-Z]{2}\\d{8}[A-Z]\\d{4}[.]\\d{4}[WSEN]\\d{5}[.]\\d{4}[WSEN]\\d{3}[.]\\d{10}[.]\\w{19}");

    @Override
    public void initialize(final GpsMessage gpsMessage) {

    }

    @Override
    public boolean isValid(final String data, final ConstraintValidatorContext constraintValidatorContext) {
        final Matcher matcher = pattern.matcher(data);
        return matcher.matches();
    }
}
