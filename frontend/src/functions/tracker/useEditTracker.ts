import gpsTrackerService from '@/services/GpsTrackerService';
import useTrackerForm from './useTrackerForm';

export default function useEditTracker(callback: Function) {
  const { tracker, imeiErrors, $v, isInvalid } = useTrackerForm();

  function saveEditedTracker() {
    $v.value.$touch();
    if (!isInvalid() && tracker.uuid) {
      gpsTrackerService
        .putForUser(tracker.uuid, tracker)
        .then(() => callback());
    }
  }

  return {
    tracker,
    saveEditedTracker,
    imeiErrors
  };
}
