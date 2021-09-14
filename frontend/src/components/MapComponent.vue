<template>
  <div id="map"></div>
</template>

<script lang="ts">
import { FeatureCollection } from 'geojson';
import {
  defineComponent,
  onMounted,
  PropType,
  watch
} from '@vue/composition-api';
import {} from 'googlemaps';
import { GMapUtils } from '@/functions/GMapUtils';
declare const google: any;

export default defineComponent({
  name: 'MapComponent',
  props: {
    data: Object as PropType<FeatureCollection>,
    getMarkerContent: {
      type: Function as PropType<(e: google.maps.Data.MouseEvent) => string>,
      required: true
    }
  },
  setup(props, { emit }) {
    onMounted(() => {
      const mapOptions: google.maps.MapOptions = {
        center: GMapUtils.getRandomLatLng(),
        zoom: GMapUtils.getRandomInt(1.5, 5),
        minZoom: 1.5,
        maxZoom: 18,
        disableDoubleClickZoom: false
      };

      const map = new google.maps.Map(
        document.getElementById('map') as HTMLElement,
        mapOptions
      );

      emit('map-initialized', map);

      const stdOptions: google.maps.InfoWindowOptions = {
        pixelOffset: new google.maps.Size(0, 0)
      };

      const markerOptions: google.maps.InfoWindowOptions = {
        pixelOffset: new google.maps.Size(0, -40)
      };

      const infowindow = new google.maps.InfoWindow(stdOptions);
      const infowindow2 = new google.maps.InfoWindow(stdOptions);

      function closeInfoWindow() {
        infowindow.close();
      }

      function closeInfoWindows() {
        infowindow.close();
        infowindow2.close();
      }

      function openInfoWindow(
        window: google.maps.InfoWindow,
        e: google.maps.Data.MouseEvent
      ) {
        const f = e.feature;
        const markerContent = props.getMarkerContent(e);
        window.setContent(markerContent);
        const type = f.getGeometry().getType();
        if (type === 'Point') {
          window.setOptions(markerOptions);
        } else {
          window.setOptions(stdOptions);
        }
        window.setPosition(e.latLng);
        window.open(map);
      }

      map.data.addListener(
        'openSecondInfoWindow',
        (e: google.maps.Data.MouseEvent) => {
          openInfoWindow(infowindow2, e);
        }
      );

      map.data.addListener('click', (e: google.maps.Data.MouseEvent) => {
        openInfoWindow(infowindow, e);
      });

      map.data.addListener('dblclick', (e: google.maps.Data.MouseEvent) => {
        const f = e.feature;
        const bounds = new google.maps.LatLngBounds();
        f.getGeometry().forEachLatLng((latLng) => {
          bounds.extend(latLng);
        });
        map.fitBounds(bounds);
        e.stop();
      });

      // close infowindow after deleting object
      map.data.addListener('removefeature', () => {
        closeInfoWindow();
      });

      map.data.addListener('closeInfoWindows', () => {
        closeInfoWindows();
      });

      function parseData(data: FeatureCollection) {
        if (data.features.length > 0) {
          map.data.addGeoJson(data, { idPropertyName: 'uuid' });
          const bounds = new google.maps.LatLngBounds();
          map.data.forEach((feature) => {
            const geo = feature.getGeometry();
            geo.forEachLatLng((latLng) => {
              bounds.extend(latLng);
            });
          });
          map.fitBounds(bounds);
        }
      }

      function clearCurrentData() {
        map.data.forEach((feature) => {
          map.data.remove(feature);
        });
      }

      if (props.data) {
        parseData(props.data);
      }

      watch(
        () => props.data,
        (data) => {
          clearCurrentData();
          closeInfoWindow();
          if (data) {
            parseData(data);
          }
        },
        {
          deep: true
        }
      );
    });
  }
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss">
#map {
  height: 60vh;
}
</style>
