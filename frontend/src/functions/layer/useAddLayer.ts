import layerService from '@/services/LayerService';
import useLayerForm from './useLayerForm';

export default function useAddLayer(callback: Function) {
  const { layer, nameErrors, prevLayerErrors, $v, isInvalid } = useLayerForm();

  function addLayer() {
    $v.value.$touch();
    if (!isInvalid()) {
      layerService.post(layer).then(() => callback());
    }
  }

  return {
    layer,
    addLayer,
    nameErrors,
    prevLayerErrors
  };
}
