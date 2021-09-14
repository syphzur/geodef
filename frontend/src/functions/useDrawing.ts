import { MapDefaults } from '@/models/constants/MapDefaults';
import { SpatialObject } from '@/models/SpatialObject';
import { reactive, ref } from '@vue/composition-api';
import { GMapUtils } from './GMapUtils';

export default function useDrawing() {
  const anyObjectEdited = ref(false);
  const drawingManager = GMapUtils.getDrawingManager();
  const objToEdit = reactive(new SpatialObject());

  let map: google.maps.Map;

  function setMap(initializedMap: google.maps.Map) {
    map = initializedMap;
    drawingManager.setMap(map);
  }

  function addListeners() {
    map.data.addListener(
      'setgeometry',
      (e: google.maps.Data.SetGeometryEvent) => {
        if (e.newGeometry.getType() === 'Point') {
          // override marker
          map.data.overrideStyle(e.feature, {
            icon: {
              url: MapDefaults.DATA_TO_SAVE_ICON,
              scaledSize: MapDefaults.MARKER_SIZE
            }
          });
        } else {
          map.data.overrideStyle(e.feature, {
            strokeColor: MapDefaults.DATA_TO_SAVE_COLOR
          });
        }
        anyObjectEdited.value = true;
      }
    );
  }

  return {
    anyObjectEdited,
    drawingManager,
    objToEdit,
    setMap,
    addListeners
  };
}
