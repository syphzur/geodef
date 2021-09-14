package pl.polsl.bol.krzysztof.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.polsl.bol.krzysztof.backend.validation.validators.GpsMessageValidator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
public class GpsMessageValidatorTests {

    @Autowired
    private GpsMessageValidator gpsMessageValidator;

    @Test
    public void dataShouldMatch() {
        String data = "072100786862BR01200811A5022.9561N01925.1261E000.1205604000.0000000000L00000000";
        assertTrue(this.gpsMessageValidator.isValid(data, null));
        data = "092100786862BR00200805A5022.9559N01925.1286E000.1194339000.0000000000L00000000";
        assertTrue(this.gpsMessageValidator.isValid(data, null));
        data = "072100786862BR01200811A5022.9576N01925.1284E000.0200539000.0000000000L00000000";
        assertTrue(this.gpsMessageValidator.isValid(data, null));
    }

    @Test
    public void dataShouldNotMatch() {
        String data = "69210078-861BR00200813A5022.9572N01925.1275E000.2171334000.0000000000L00000000";
        assertFalse(this.gpsMessageValidator.isValid(data, null));
        data = "692100781861BR00200813A5022.9572N01925.1275E000.2171334000.0000000000L0000000";
        assertFalse(this.gpsMessageValidator.isValid(data, null));
        data = "6921007818611R00200813A5022.9572N01925.1275E000.2171334000.0000000000L00000001";
        assertFalse(this.gpsMessageValidator.isValid(data, null));

    }
}
