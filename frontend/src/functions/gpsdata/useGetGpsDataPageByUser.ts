import { reactive, ref } from '@vue/composition-api';
import { Page } from '@/models/Page';
import { DataOptions } from 'vuetify';
import { getModule } from 'vuex-module-decorators';
import AuthModule from '@/store/AuthModule';
import store from '@/store';
import { GpsData } from '@/models/GpsData';
import gpsDataService from '@/services/GpsDataService';
import { plainToClass } from 'class-transformer';

export default function useGetGpsDataPageByUser() {
  const pageInfo = reactive({
    page: 1,
    itemsPerPage: 10
  } as DataOptions);
  const page = reactive(new Page<GpsData>());
  const loading = ref(false);
  const authModule = getModule(AuthModule, store);

  function pageInfoUpdate(
    newValue: DataOptions,
    dateFrom?: string,
    dateTo?: string,
    trackerUuid?: string
  ) {
    loading.value = true;
    Object.assign(pageInfo, newValue);
    if (authModule.profile.uuid) {
      gpsDataService
        .getPageByUser(
          authModule.profile.uuid,
          pageInfo,
          dateFrom,
          dateTo,
          trackerUuid
        )
        .then((resp) => {
          const newPage = resp.data;
          newPage.content = resp.data.content.map((el) =>
            plainToClass(GpsData, el)
          );
          Object.assign(page, newPage);
        })
        .finally(() => (loading.value = false));
    }
  }

  function refreshData(
    dateFrom?: string,
    dateTo?: string,
    trackerUuid?: string
  ) {
    pageInfoUpdate(pageInfo, dateFrom, dateTo, trackerUuid);
  }

  return {
    page,
    pageInfo,
    loading,
    pageInfoUpdate,
    refreshData
  };
}
