import { reactive, ref } from '@vue/composition-api';
import { ObjectType } from '@/models/ObjectType';
import objectTypeService from '@/services/ObjectTypeService';
import { AxiosResponse } from 'axios';

export default function useGetTypesAllOrByLayer() {
  const types = reactive<Array<ObjectType>>([]);
  const loading = ref(false);

  function respondToFetch(resp: AxiosResponse<ObjectType[]>) {
    if (resp?.data) {
      types.splice(0, types.length, ...resp.data);
    }
  }

  function fetchByLayer(layerUuid: string) {
    objectTypeService
      .getAllByLayer(layerUuid)
      .then((resp) => {
        respondToFetch(resp);
      })
      .finally(() => (loading.value = false));
  }

  function fetchAll() {
    objectTypeService.getAll().then((resp) => {
      respondToFetch(resp);
    });
  }

  function fetchTypes(layerUuid?: string) {
    loading.value = true;
    if (layerUuid) {
      fetchByLayer(layerUuid);
    } else {
      fetchAll();
    }
  }

  return {
    types,
    fetchTypes,
    loading
  };
}
