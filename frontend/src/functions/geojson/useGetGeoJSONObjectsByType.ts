import geoJSONService from '@/services/GeoJSONService';
import { reactive } from '@vue/composition-api';
import { FeatureCollection } from 'geojson';

export default function useGetGeoJSONObjectsByType() {
  const data: FeatureCollection = reactive({
    type: 'FeatureCollection',
    features: []
  });

  function fetchGeoJSONObjects(typeUuid: string) {
    geoJSONService.getAllByType(typeUuid).then((resp) => {
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
