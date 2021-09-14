import geoJSONService from '@/services/GeoJSONService';
import { reactive } from '@vue/composition-api';
import { FeatureCollection } from 'geojson';
import { GMapUtils } from '../GMapUtils';

export default function useGetGeoJSONObjectsByLayer() {
  const data: FeatureCollection = reactive(
    GMapUtils.getInitialFeatureCollectionState()
  );

  function fetchGeoJSONObjects(
    layerUuid?: string,
    withHierarchicalLayers = false
  ) {
    if (layerUuid) {
      geoJSONService
        .getAllByLayer(layerUuid, withHierarchicalLayers)
        .then((resp) => {
          if (resp?.data) {
            Object.assign(data, resp.data);
          }
        });
    } else {
      geoJSONService.getAllSpatialObjects().then((resp) => {
        if (resp?.data) {
          Object.assign(data, resp.data);
        }
      });
    }
  }

  return {
    data,
    fetchGeoJSONObjects
  };
}
