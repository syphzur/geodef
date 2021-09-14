import { reactive, ref } from '@vue/composition-api';
import { GpsTracker } from '@/models/GpsTracker';
import gpsTrackerService from '@/services/GpsTrackerService';
import { getModule } from 'vuex-module-decorators';
import AuthModule from '@/store/AuthModule';
import store from '@/store';
import { plainToClass } from 'class-transformer';

export default function useGetTrackersByOwner() {
  const trackers = reactive<Array<GpsTracker>>([]);
  const loading = ref(false);
  const authModule = getModule(AuthModule, store);

  function fetchTrackers() {
    if (authModule.profile?.uuid) {
      loading.value = true;
      gpsTrackerService
        .getAllByOwner(authModule.profile.uuid)
        .then((resp) => {
          if (resp?.data) {
            const data = resp.data.map((el) => plainToClass(GpsTracker, el));
            trackers.splice(0, trackers.length, ...data);
          }
        })
        .finally(() => (loading.value = false));
    }
  }

  return {
    trackers,
    fetchTrackers,
    loading
  };
}
