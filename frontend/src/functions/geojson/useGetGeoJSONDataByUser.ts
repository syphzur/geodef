import geoJSONService from '@/services/GeoJSONService';
import store from '@/store';
import AuthModule from '@/store/AuthModule';
import { reactive } from '@vue/composition-api';
import { FeatureCollection } from 'geojson';
import { getModule } from 'vuex-module-decorators';
import { GMapUtils } from '../GMapUtils';

export default function useGetGeoJSONObjectsByUser() {
  const data: FeatureCollection = reactive(
    GMapUtils.getInitialFeatureCollectionState()
  );
  const authModule = getModule(AuthModule, store);

  function fetchGeoJSONData(
    dateFrom?: string,
    dateTo?: string,
    trackerUuid?: string
  ) {
    const userUuid = authModule.profile.uuid;
    if (userUuid) {
      geoJSONService
        .getAllByUser(userUuid, dateFrom, dateTo, trackerUuid)
        .then((resp) => {
          if (resp?.data) {
            Object.assign(data, resp.data);
          }
        });
    }
  }

  return {
    data,
    fetchGeoJSONData
  };
}
