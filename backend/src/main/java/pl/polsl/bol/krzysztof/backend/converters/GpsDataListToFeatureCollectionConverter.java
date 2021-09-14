package pl.polsl.bol.krzysztof.backend.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.wololo.geojson.Feature;
import org.wololo.geojson.FeatureCollection;
import org.wololo.jts2geojson.GeoJSONWriter;
import pl.polsl.bol.krzysztof.backend.models.entities.GpsData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GpsDataListToFeatureCollectionConverter implements Converter<List<GpsData>, FeatureCollection> {

    private final GeoJSONWriter writer;

    public GpsDataListToFeatureCollectionConverter(final GeoJSONWriter writer) {
        this.writer = writer;
    }

    @Override
    public FeatureCollection convert(final List<GpsData> gpsDataList) {
        final List<Feature> features = new ArrayList<>();
        gpsDataList.forEach(gpsData -> {
            final Map<String, Object> properties = new HashMap<>();
            properties.put("imei", gpsData.getTracker().getImei());
            properties.put("dateTime", gpsData.getDateTime());
            properties.put("speed", gpsData.getSpeed());
            properties.put("uuid", gpsData.getUuid());
            features.add(new Feature(writer.write(gpsData.getPoint()), properties));
        });
        return new FeatureCollection(features.toArray(new Feature[0]));
    }
}
