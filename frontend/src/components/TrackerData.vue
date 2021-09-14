<template>
  <v-container fluid>
    <v-expansion-panels v-model="panels" accordion multiple>
      <v-expansion-panel>
        <v-expansion-panel-header class="py-0 font-weight-bold"
          >Filter data</v-expansion-panel-header
        >
        <v-expansion-panel-content>
          <v-row>
            <v-col class="py-0" cols="12" sm="3">
              <v-menu
                ref="dateFromMenu"
                v-model="showFromDatePicker"
                :close-on-content-click="false"
                :return-value.sync="dateFrom"
                transition="scale-transition"
                offset-y
                min-width="290px"
              >
                <template v-slot:activator="{ on, attrs }">
                  <v-text-field
                    v-model="dateFrom"
                    clearable
                    label="Start date"
                    prepend-icon="mdi-calendar"
                    readonly
                    v-bind="attrs"
                    v-on="on"
                  ></v-text-field>
                </template>
                <v-date-picker v-model="dateFrom" no-title scrollable>
                  <v-spacer></v-spacer>
                  <v-btn
                    text
                    color="primary"
                    @click="showFromDatePicker = false"
                  >
                    Cancel
                  </v-btn>
                  <v-btn
                    text
                    color="primary"
                    @click="$refs.dateFromMenu.save(dateFrom)"
                  >
                    OK
                  </v-btn>
                </v-date-picker>
              </v-menu>
            </v-col>
            <v-col class="py-0" cols="12" sm="3">
              <v-menu
                ref="dateToMenu"
                v-model="showToDatePicker"
                :close-on-content-click="false"
                :return-value.sync="dateTo"
                transition="scale-transition"
                offset-y
                min-width="290px"
              >
                <template v-slot:activator="{ on, attrs }">
                  <v-text-field
                    v-model="dateTo"
                    clearable
                    label="End date"
                    prepend-icon="mdi-calendar"
                    readonly
                    v-bind="attrs"
                    v-on="on"
                  ></v-text-field>
                </template>
                <v-date-picker v-model="dateTo" no-title scrollable>
                  <v-spacer></v-spacer>
                  <v-btn text color="primary" @click="showToDatePicker = false">
                    Cancel
                  </v-btn>
                  <v-btn
                    text
                    color="primary"
                    @click="$refs.dateToMenu.save(dateTo)"
                  >
                    OK
                  </v-btn>
                </v-date-picker>
              </v-menu>
            </v-col>
            <v-col class="py-0" cols="12" sm="3">
              <v-select
                v-model="selectedTrackerUuid"
                clearable
                :items="trackers"
                item-text="imei"
                item-value="uuid"
                label="Tracker"
              ></v-select>
            </v-col>
            <v-col class="text-right py-0">
              <v-btn color="secondary" @click="searchData">Search</v-btn>
            </v-col>
          </v-row>
        </v-expansion-panel-content>
      </v-expansion-panel>
    </v-expansion-panels>
    <v-row>
      <v-col>
        <MapComponent
          :data="data"
          :get-marker-content="getMarkerContent"
          @map-initialized="setMap"
        ></MapComponent>
      </v-col>
    </v-row>
    <v-data-table
      :headers="headers"
      :items="page.content"
      :server-items-length="page.totalElements"
      :loading="loading"
      :options.sync="pageInfo"
      class="elevation-1"
      @update:options="pageInfoUpdate"
    >
      <template v-slot:item.dateTime="{ item }">
        <span>{{ item.dateTime.toLocaleString() }}</span>
      </template>
      <template v-slot:item.actions="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on, attrs }">
            <v-icon
              class="mr-2"
              color="primary"
              v-bind="attrs"
              small
              v-on="on"
              @click="zoomOnMap(item)"
            >
              mdi-map-outline
            </v-icon>
          </template>
          <span class="text-center">See on map</span>
        </v-tooltip>
      </template>
    </v-data-table>
  </v-container>
</template>

<script lang="ts">
import MapComponent from '@/components/MapComponent.vue';
import { defineComponent, ref } from '@vue/composition-api';
import useGetTrackersByOwner from '@/functions/tracker/useGetTrackersByOwner';
import { DataTableHeader } from 'vuetify';
import useGetGpsDataPageByUser from '@/functions/gpsdata/useGetGpsDataPageByUser';
import useGetGeoJSONDataByUser from '@/functions/geojson/useGetGeoJSONDataByUser';
import { GpsData } from '@/models/GpsData';
declare const google: any;

export default defineComponent({
  name: 'TrackerData',
  components: { MapComponent },
  setup() {
    const panels = [0];

    const dateFrom = ref<string>();
    const dateTo = ref<string>();
    const selectedTrackerUuid = ref<string>();

    const showFromDatePicker = ref(false);
    const showToDatePicker = ref(false);

    let map: google.maps.Map;

    const headers: Array<DataTableHeader> = [
      { text: 'Tracker imei', value: 'trackerImei' },
      { text: 'Date', value: 'dateTime' },
      { text: 'Speed [km/h]', value: 'speed' },
      {
        text: 'Actions',
        value: 'actions',
        class: 'three-icons-actions',
        sortable: false
      }
    ];

    const { trackers, fetchTrackers } = useGetTrackersByOwner();

    const {
      page,
      pageInfo,
      loading,
      pageInfoUpdate,
      refreshData: refreshTableData
    } = useGetGpsDataPageByUser();

    const { data, fetchGeoJSONData } = useGetGeoJSONDataByUser();

    function searchData() {
      refreshTableData(dateFrom.value, dateTo.value, selectedTrackerUuid.value);
      fetchGeoJSONData(dateFrom.value, dateTo.value, selectedTrackerUuid.value);
    }

    function getMarkerContent(e: any): string {
      const date = new Date(e.feature.getProperty('dateTime'));
      return (
        '<b>Imei:</b> ' +
        e.feature.getProperty('imei') +
        '<br>' +
        '<b>Date:</b> ' +
        date.toLocaleString() +
        '<br>' +
        '<b>Speed: </b>' +
        e.feature.getProperty('speed') +
        ' km/h'
      );
    }

    function zoomOnMap(data: GpsData) {
      map.data.toGeoJson((json) => {
        console.log(json);
      });
      const f = map.data.getFeatureById(data.uuid);
      const bounds = new google.maps.LatLngBounds();
      f.getGeometry().forEachLatLng((latLng) => {
        bounds.extend(latLng);
      });
      map.fitBounds(bounds);
      google.maps.event.trigger(map.data, 'click', {
        feature: f,
        latLng: bounds.getCenter()
      });
    }

    function setMap(initializedMap: google.maps.Map) {
      map = initializedMap;
    }

    fetchTrackers();
    searchData();
    return {
      data,
      getMarkerContent,
      showFromDatePicker,
      showToDatePicker,
      dateFrom,
      dateTo,
      panels,
      trackers,
      page,
      pageInfo,
      loading,
      pageInfoUpdate,
      headers,
      searchData,
      selectedTrackerUuid,
      setMap,
      zoomOnMap
    };
  }
});
</script>
