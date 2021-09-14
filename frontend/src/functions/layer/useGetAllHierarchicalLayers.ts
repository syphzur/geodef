import { Layer } from '@/models/Layer';
import { reactive } from '@vue/composition-api';
import layerService from '@/services/LayerService';

export default function useGetAllHierarchicalLayers() {
  const layers = reactive<Array<Layer>>([]);

  function fetchLayers() {
    layerService.getAllHierarchical().then((resp) => {
      if (resp?.data?.length > 0) {
        layers.splice(0, layers.length, ...resp.data);
      }
    });
  }

  return {
    layers,
    fetchLayers
  };
}
