import layerService from '@/services/LayerService';
import useLayerForm from './useLayerForm';

export default function useEditLayer(callback: Function) {
  const { layer, nameErrors, prevLayerErrors, $v, isInvalid } = useLayerForm();

  function saveEditedLayer() {
    $v.value.$touch();
    if (!isInvalid() && layer.uuid) {
      layerService.put(layer.uuid, layer).then(() => callback());
    }
  }

  return {
    layer,
    saveEditedLayer,
    nameErrors,
    prevLayerErrors
  };
}
