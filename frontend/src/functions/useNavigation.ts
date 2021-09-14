import { Messages } from '@/models/constants/Messages';
import { SpatialObject } from '@/models/SpatialObject';
import notificationService from '@/services/NotificationService';
import { computed, reactive, ref } from '@vue/composition-api';
import { Feature } from 'geojson';
import { GMapUtils } from './GMapUtils';

export default function useNavigation() {
  const routePanelVisible = ref(false);
  const travelMode = ref(google.maps.TravelMode.DRIVING);
  const destinationObj = reactive(GMapUtils.getInitialFeatureState());
  const originObj = reactive(GMapUtils.getInitialFeatureState());
  const directionRenderer = GMapUtils.getDirectionsRenderer();
  const directionService = new google.maps.DirectionsService();

  let map: google.maps.Map;

  function setMap(initializedMap: google.maps.Map) {
    map = initializedMap;
    directionRenderer.setMap(map);
  }

  function setPanel(panel: HTMLElement) {
    directionRenderer.setPanel(panel);
  }

  function clearNavObjects() {
    Object.assign(destinationObj, GMapUtils.getInitialFeatureState());
    Object.assign(originObj, GMapUtils.getInitialFeatureState());
  }

  function navObjChange(
    feature: Feature,
    secondObjAsFeature: Feature,
    eventToTrigger: string
  ) {
    const obj = feature.properties as SpatialObject;
    if (obj.uuid) {
      const f = map.data.getFeatureById(obj.uuid);
      const bounds = new google.maps.LatLngBounds();
      f.getGeometry().forEachLatLng((latLng) => {
        bounds.extend(latLng);
      });
      const objCenter = bounds.getCenter();
      if (secondObjAsFeature.properties) {
        const secObj = secondObjAsFeature.properties as SpatialObject;
        if (secObj.uuid) {
          const f = map.data.getFeatureById(secObj.uuid);
          f.getGeometry().forEachLatLng((latLng) => {
            bounds.extend(latLng);
          });
        }
      }
      map.fitBounds(bounds);
      google.maps.event.trigger(map.data, eventToTrigger, {
        feature: f,
        latLng: objCenter
      });
    }
  }

  function onOriginObjChange(feature: Feature) {
    Object.assign(originObj, feature);
    navObjChange(feature, destinationObj, 'click');
  }

  function onDestinationObjChange(feature: Feature) {
    Object.assign(destinationObj, feature);
    navObjChange(feature, originObj, 'openSecondInfoWindow');
  }

  function navigate() {
    google.maps.event.trigger(map.data, 'closeInfoWindows');
    const from = originObj.properties as SpatialObject;
    const to = destinationObj.properties as SpatialObject;

    if (from.uuid && to.uuid) {
      const fFrom = map.data.getFeatureById(from.uuid);
      const fTo = map.data.getFeatureById(to.uuid);

      if (fFrom === fTo) {
        notificationService.showError(Messages.SELECT_DIFFERENT_OBJECT);
        return;
      }
      routePanelVisible.value = true;

      directionService.route(
        {
          origin: GMapUtils.getCenterOfFeature(fFrom),
          destination: GMapUtils.getCenterOfFeature(fTo),
          travelMode: travelMode.value
        },
        (response, status) => {
          if (status === google.maps.DirectionsStatus.OK) {
            directionRenderer.setDirections(response);
          } else {
            notificationService.showError(Messages.NAV_FAILED);
          }
        }
      );
    }
  }

  const navigateEnabled = computed(
    () => !!destinationObj.properties?.uuid && !!originObj.properties?.uuid
  );

  return {
    destinationObj,
    originObj,
    travelMode,
    clearNavObjects,
    navigateEnabled,
    onOriginObjChange,
    onDestinationObjChange,
    setMap,
    setPanel,
    routePanelVisible,
    navigate
  };
}
