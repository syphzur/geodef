import { reactive, ref } from '@vue/composition-api';
import spatialObjectService from '@/services/SpatialObjectService';
import { SpatialObject } from '@/models/SpatialObject';

export default function useGetObjectsByType() {
  const objects = reactive<Array<SpatialObject>>([]);
  const loading = ref(false);

  function fetchObjects(typeUuid: string) {
    loading.value = true;
    spatialObjectService
      .getAllByType(typeUuid)
      .then((resp) => {
        if (resp?.data) {
          objects.splice(0, objects.length, ...resp.data);
        }
      })
      .finally(() => (loading.value = false));
  }

  return {
    objects,
    fetchObjects,
    loading
  };
}
