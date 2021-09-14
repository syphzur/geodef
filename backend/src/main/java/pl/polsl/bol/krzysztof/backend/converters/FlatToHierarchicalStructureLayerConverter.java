package pl.polsl.bol.krzysztof.backend.converters;

import com.google.gson.Gson;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import pl.polsl.bol.krzysztof.backend.models.entities.Layer;

import java.util.ArrayList;
import java.util.List;

@Component
public class FlatToHierarchicalStructureLayerConverter implements Converter<List<Layer>, List<Layer>> {

    final Gson gson = new Gson();

    @Override
    public List<Layer> convert(final List<Layer> flat) {
        final List<Layer> hierarchical = new ArrayList<>();
        for (final Layer layer : flat) {
            if (hierarchical.stream().noneMatch(it -> it.getUuid().equals(layer.getUuid()))) {
                hierarchical.add(layer);
            }
            Layer currLayer = layer;
            final StringBuilder prefix = new StringBuilder("\u00A0\u00A0");
            while (currLayer.getPrevLayer() != null) {
                currLayer = currLayer.getPrevLayer();
                Layer prevLayer = gson.fromJson(gson.toJson(currLayer), Layer.class);
                prevLayer.setName(prefix.toString() + prevLayer.getName());
                hierarchical.add(prevLayer);
                prefix.append("\u00A0\u00A0");
            }
        }

        return hierarchical;
    }
}

