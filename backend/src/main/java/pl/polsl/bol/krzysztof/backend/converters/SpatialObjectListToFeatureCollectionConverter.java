package pl.polsl.bol.krzysztof.backend.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.wololo.geojson.Feature;
import org.wololo.geojson.FeatureCollection;
import org.wololo.jts2geojson.GeoJSONWriter;
import pl.polsl.bol.krzysztof.backend.models.entities.SpatialObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SpatialObjectListToFeatureCollectionConverter implements Converter<List<SpatialObject>, FeatureCollection> {

    private final GeoJSONWriter writer;

    public SpatialObjectListToFeatureCollectionConverter(final GeoJSONWriter writer) {
        this.writer = writer;
    }

    @Override
    public FeatureCollection convert(final List<SpatialObject> spatialObjects) {
        final List<Feature> features = new ArrayList<>();
        spatialObjects.forEach(obj -> {
            final Map<String, Object> properties = new HashMap<>();
            properties.put("uuid", obj.getUuid());
            properties.put("name", obj.getName());
            properties.put("layerUuid", obj.getType().getLayer().getUuid());
            properties.put("layerName", obj.getType().getLayer().getName());
            properties.put("typeName", obj.getType().getName());
            properties.put("typeUuid", obj.getType().getUuid());
            properties.put("creatorUsername", obj.getCreator().getUsername());
            properties.put("creatorUuid", obj.getCreator().getUuid());
            features.add(new Feature(writer.write(obj.getGeometry()), properties));
        });
        return new FeatureCollection(features.toArray(new Feature[0]));
    }
}
