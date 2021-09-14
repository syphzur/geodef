import { MapDefaults } from '@/models/constants/MapDefaults';
import store from '@/store';
import AuthModule from '@/store/AuthModule';
import { Feature, FeatureCollection } from 'geojson';
import { getModule } from 'vuex-module-decorators';

export type Overlay =
  | google.maps.Marker
  | google.maps.Polygon
  | google.maps.Polyline
  | google.maps.Rectangle
  | google.maps.Circle;

export class GMapUtils {
  static getInitialStyle(
    feature: google.maps.Data.Feature,
    loggedUuid: string
  ): google.maps.Data.StyleOptions {
    const uuid = feature.getProperty('creatorUuid');
    const canEdit = loggedUuid === uuid;
    return {
      editable: canEdit,
      draggable: canEdit,
      strokeColor: canEdit
        ? MapDefaults.CAN_EDIT_COLOR
        : MapDefaults.CANNOT_EDIT_COLOR,
      icon: {
        url: canEdit ? MapDefaults.CAN_EDIT_ICON : MapDefaults.CANNOT_EDIT_ICON,
        scaledSize: MapDefaults.MARKER_SIZE
      }
    };
  }

  static getRandomInt(min: number, max: number) {
    return Math.round(Math.random() * Math.abs(max - min) + min);
  }

  static getRandomLatLng() {
    const lat = GMapUtils.getRandomInt(-50, 50);
    const lng = GMapUtils.getRandomInt(-100, 100);
    return new google.maps.LatLng(lat, lng);
  }

  static convertDrawEventToGeometry(
    o: Overlay
  ): google.maps.Data.Geometry | undefined {
    let geom: google.maps.Data.Geometry | undefined;
    switch (true) {
      case o instanceof google.maps.Marker: {
        const pos = (o as google.maps.Marker).getPosition();
        if (pos) {
          geom = new google.maps.Data.Point(pos);
        }
        break;
      }
      case o instanceof google.maps.Rectangle: {
        const b = (o as google.maps.Rectangle).getBounds(),
          p = [
            b.getSouthWest(),
            {
              lat: b.getSouthWest().lat(),
              lng: b.getNorthEast().lng()
            },
            b.getNorthEast(),
            {
              lng: b.getSouthWest().lng(),
              lat: b.getNorthEast().lat()
            }
          ];
        geom = new google.maps.Data.Polygon([p]);
        break;
      }
      case o instanceof google.maps.Polygon: {
        geom = new google.maps.Data.Polygon([
          (o as google.maps.Polygon).getPath().getArray()
        ]);
        break;
      }
      case o instanceof google.maps.Polyline:
        geom = new google.maps.Data.LineString(
          (o as google.maps.Polyline).getPath().getArray()
        );
        break;
      default:
        throw new Error('Not defined type');
    }
    return geom;
  }

  static getDrawingManager(): google.maps.drawing.DrawingManager {
    return new google.maps.drawing.DrawingManager({
      drawingControl: true,
      drawingControlOptions: {
        position: google.maps.ControlPosition.TOP_CENTER,
        drawingModes: [
          google.maps.drawing.OverlayType.MARKER,
          google.maps.drawing.OverlayType.POLYGON,
          google.maps.drawing.OverlayType.POLYLINE,
          google.maps.drawing.OverlayType.RECTANGLE
        ]
      },
      markerOptions: {
        icon: {
          url: MapDefaults.DATA_TO_SAVE_ICON,
          scaledSize: MapDefaults.MARKER_SIZE
        }
      },
      polygonOptions: {
        strokeColor: MapDefaults.DATA_TO_SAVE_COLOR
      },
      polylineOptions: {
        strokeColor: MapDefaults.DATA_TO_SAVE_COLOR
      },
      rectangleOptions: {
        strokeColor: MapDefaults.DATA_TO_SAVE_COLOR
      }
    });
  }

  static getDirectionsRenderer(): google.maps.DirectionsRenderer {
    return new google.maps.DirectionsRenderer({
      polylineOptions: { strokeColor: 'pink' },
      suppressMarkers: true
    });
  }

  static getMarkerContentForObject(e: google.maps.Data.MouseEvent): string {
    const authModule = getModule(AuthModule, store);
    const f = e.feature;
    const buttons =
      authModule.profile?.uuid === f.getProperty('creatorUuid')
        ? '<br>' +
          '<button class="v-btn v-btn--contained v-size--small secondary mt-2" onclick="onDelete(\'' +
          f.getId() +
          '\')">Delete</button>' +
          '<button class="v-btn v-btn--contained v-size--small secondary mt-2 ml-2" onclick="onDataEdit(\'' +
          f.getId() +
          '\')">Edit data</button>'
        : '';
    return (
      '<b>Name: </b>' +
      f.getProperty('name') +
      '<br>' +
      '<b>Type: </b>' +
      f.getProperty('typeName') +
      '<br>' +
      '<b>Layer: </b>' +
      f.getProperty('layerName') +
      '<br>' +
      '<b>Creator: </b>' +
      f.getProperty('creatorUsername') +
      buttons
    );
  }

  static getCenterOfFeature(f: google.maps.Data.Feature): google.maps.LatLng {
    const bounds = new google.maps.LatLngBounds();
    f.getGeometry().forEachLatLng((latLng) => {
      bounds.extend(latLng);
    });
    return bounds.getCenter();
  }

  static getInitialFeatureState(): Feature {
    return { type: 'Feature', geometry: {}, properties: {} } as Feature;
  }

  static getInitialFeatureCollectionState(): FeatureCollection {
    return {
      type: 'FeatureCollection',
      features: []
    };
  }
}
