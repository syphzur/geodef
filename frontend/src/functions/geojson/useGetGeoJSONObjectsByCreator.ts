import geoJSONService from '@/services/GeoJSONService';
import { reactive } from '@vue/composition-api';
import { FeatureCollection } from 'geojson';

export default function useGetGeoJSONObjectsByCreator() {
  const data: FeatureCollection = reactive({
    type: 'FeatureCollection',
    features: []
  });

  function fetchGeoJSONObjects(creatorUuid: string) {
    geoJSONService.getAllByCreator(creatorUuid).then((resp) => {
      if (resp?.data) {
        Object.assign(data, resp.data);
      }
    });
  }

  return {
    data,
    fetchGeoJSONObjects
  };
}
