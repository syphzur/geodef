package pl.polsl.bol.krzysztof.backend.converters;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import pl.polsl.bol.krzysztof.backend.exceptions.BadMessageFormatException;
import pl.polsl.bol.krzysztof.backend.models.entities.GpsData;
import pl.polsl.bol.krzysztof.backend.models.entities.GpsTracker;
import pl.polsl.bol.krzysztof.backend.services.GpsTrackerService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Component
public class StringToGpsDataConverter implements Converter<String, GpsData> {

    private final int WGS84_SRID = 4326;

    private final int GEO_POS_ROUNDING_PLACES = 6;

    private final GeometryFactory gf = new GeometryFactory(new PrecisionModel(), WGS84_SRID);

    private final GpsTrackerService gpsTrackerService;

    public StringToGpsDataConverter(final GpsTrackerService gpsTrackerService) {
        this.gpsTrackerService = gpsTrackerService;
        this.gpsTrackerService.setModelClassName(GpsTracker.class.getSimpleName());
    }

    @Override
    public GpsData convert(final String data) {
        if (!data.startsWith("BR00", 12)) {
            throw new BadMessageFormatException("Message must have 'BR00' command");
        }

        int year, month, day, hour, min, sec;
        double latitude, longitude, speed;
        String imei;

        try {
            imei = data.substring(0, 12);
            year = 2000 + Integer.parseInt(data.substring(16, 18));
            month = Integer.parseInt(data.substring(18, 20));
            day = Integer.parseInt(data.substring(20, 22));
            latitude = Integer.parseInt(data.substring(23, 25));
            latitude += (Double.parseDouble(data.substring(25, 32)) / 60.0);
            if (data.charAt(32) == 'S') {
                latitude = -latitude;
            }
            longitude = Integer.parseInt(data.substring(34, 36));
            longitude += (Double.parseDouble(data.substring(36, 43)) / 60.0);
            if (data.charAt(43) == 'W') {
                longitude = -longitude;
            }
            speed = Double.parseDouble(data.substring(44, 49));
            hour = Integer.parseInt(data.substring(49, 51));
            min = Integer.parseInt(data.substring(51, 53));
            sec = Integer.parseInt(data.substring(53, 55));

            longitude = this.roundDouble(longitude, this.GEO_POS_ROUNDING_PLACES);
            longitude = this.roundDouble(longitude, this.GEO_POS_ROUNDING_PLACES);
        } catch (final NumberFormatException e) {
            throw new BadMessageFormatException("Bad message format! Exception caught while parsing message:" + data);
        }

        return GpsData.builder()
                .tracker(this.gpsTrackerService.findModelByImeiOrThrowException(imei))
                .dateTime(LocalDateTime.of(year, month, day, hour, min, sec))
                .speed(speed)
                .point(gf.createPoint(new Coordinate(longitude, latitude)))
                .build();
    }

    private double roundDouble(final double value, final int places) {
        return new BigDecimal(value)
                .setScale(places, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
