package pl.polsl.bol.krzysztof.backend.validation.validators;

import org.springframework.stereotype.Component;
import pl.polsl.bol.krzysztof.backend.models.dtos.LayerDto;
import pl.polsl.bol.krzysztof.backend.models.entities.Layer;
import pl.polsl.bol.krzysztof.backend.services.LayerService;
import pl.polsl.bol.krzysztof.backend.validation.annotations.ValidLayersHierarchicalStructure;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.UUID;

@Component
public class LayerValidator implements ConstraintValidator<ValidLayersHierarchicalStructure, LayerDto> {

    private final LayerService layerService;

    public LayerValidator(final LayerService layerService) {
        this.layerService = layerService;
    }

    @Override
    public boolean isValid(final LayerDto layerDto, final ConstraintValidatorContext constraintValidatorContext) {
        final UUID prevLayerUuid = layerDto.getPrevLayerUuid();
        if (prevLayerUuid != null) {
            Layer prevLayer = layerService.findModelByUuidOrThrowException(prevLayerUuid);

            while (prevLayer != null) {
                if (prevLayer.getUuid().equals(layerDto.getUuid())) {
                    return false;
                }
                prevLayer = prevLayer.getPrevLayer();
            }
        }

        return true;
    }
}
