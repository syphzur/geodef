import gpsTrackerService from '@/services/GpsTrackerService';
import store from '@/store';
import AuthModule from '@/store/AuthModule';
import { getModule } from 'vuex-module-decorators';
import useTrackerForm from './useTrackerForm';

export default function useAddTracker(callback: Function) {
  const { tracker, imeiErrors, $v, isInvalid } = useTrackerForm();
  const authModule = getModule(AuthModule, store);

  function addTracker() {
    $v.value.$touch();
    if (!isInvalid()) {
      tracker.ownerUuid = authModule.profile?.uuid;
      gpsTrackerService.post(tracker).then(() => callback());
    }
  }

  return {
    tracker,
    addTracker,
    imeiErrors
  };
}
