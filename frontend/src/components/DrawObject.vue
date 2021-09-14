<template>
  <v-container fluid>
    <v-expansion-panels v-model="panels" accordion multiple>
      <v-expansion-panel>
        <v-expansion-panel-header class="py-0 font-weight-bold"
          >Layer settings</v-expansion-panel-header
        >
        <v-expansion-panel-content>
          <v-row align="center">
            <v-col cols="6" class="py-0">
              <v-select
                v-model="selectedLayer"
                return-object
                :items="layers"
                item-text="name"
                prepend-icon="mdi-map"
                label="Select layer"
                @change="onLayerChange"
              >
                <template v-slot:append-item>
                  <v-divider></v-divider>
                  <v-list-item link @click="clearLayer">
                    <v-list-item-content>
                      <v-list-item-title> None </v-list-item-title>
                    </v-list-item-content>
                  </v-list-item>
                </template>
              </v-select>
            </v-col>
            <v-col class="py-0">
              <v-checkbox
                v-model="withHierarchicalLayers"
                :label="'With hierarchical layers'"
                @change="onLayerChange"
              ></v-checkbox>
            </v-col>
          </v-row>
        </v-expansion-panel-content>
      </v-expansion-panel>

      <v-expansion-panel>
        <v-expansion-panel-header class="font-weight-bold"
          >Navigation</v-expansion-panel-header
        >
        <v-expansion-panel-content>
          <v-row align="center">
            <v-col cols="12" sm="4" class="py-0">
              <v-select
                return-object
                :items="data.features"
                item-text="properties.name"
                label="Select first object"
                @change="onOriginObjChange"
              >
              </v-select>
            </v-col>
            <v-col cols="12" sm="4" class="py-0">
              <v-select
                return-object
                :items="data.features"
                item-text="properties.name"
                label="Select second object"
                @change="onDestinationObjChange"
              >
              </v-select>
            </v-col>
            <v-col>
              <v-radio-group v-model="travelMode" column>
                <v-tooltip bottom>
                  <template v-slot:activator="{ on, attrs }">
                    <v-radio value="DRIVING">
                      <template v-slot:label>
                        <v-icon v-bind="attrs" v-on="on">
                          mdi-car-outline
                        </v-icon>
                      </template>
                    </v-radio>
                  </template>
                  <span>Travel by car</span>
                </v-tooltip>
                <v-tooltip bottom>
                  <template v-slot:activator="{ on, attrs }">
                    <v-radio value="BICYCLING">
                      <template v-slot:label>
                        <v-icon v-bind="attrs" v-on="on"> mdi-bike </v-icon>
                      </template>
                    </v-radio>
                  </template>
                  <span>Travel by bicycle</span>
                </v-tooltip>
                <v-tooltip bottom>
                  <template v-slot:activator="{ on, attrs }">
                    <v-radio value="WALKING">
                      <template v-slot:label>
                        <v-icon v-bind="attrs" v-on="on"> mdi-walk </v-icon>
                      </template>
                    </v-radio>
                  </template>
                  <span>Travel on foot</span>
                </v-tooltip>
              </v-radio-group>
            </v-col>
            <v-col class="text-right py-0">
              <v-btn
                color="secondary"
                :disabled="!navigateEnabled"
                @click="navigate"
                >Navigate</v-btn
              >
            </v-col>
          </v-row>
        </v-expansion-panel-content>
      </v-expansion-panel>
    </v-expansion-panels>
    <v-row>
      <v-col class="text-right">
        <v-btn
          :disabled="!anyObjectEdited"
          color="secondary"
          @click="saveEditedGeometries"
          >Save edited geometries</v-btn
        >
      </v-col>
    </v-row>
    <v-row class="pa-0">
      <v-col class="py-0">
        <MapComponent
          :data="data"
          :get-marker-content="getMarkerContent"
          @map-initialized="init"
        ></MapComponent>
      </v-col>
      <v-expand-x-transition>
        <v-col v-show="routePanelVisible" cols="12" md="3" class="py-0">
          <div id="nav-panel"></div>
        </v-col>
      </v-expand-x-transition>
    </v-row>
    <SpatialObjectFormModal
      :layer-uuid="selectedLayer.uuid"
      :initial-data="objToEdit"
      :show-form="showForm"
      @close="onFormClose"
    />
    <DeleteModal
      :show-modal="showDeleteDialog"
      :model="objToDelete"
      :type="'object'"
      :append-message="Messages.APPEND_DELETE_OBJECT"
      @close="onDeleteDialogClose"
    />
  </v-container>
</template>
<script lang="ts">
import SpatialObjectFormModal from '@/components/SpatialObjectFormModal.vue';
import MapComponent from '@/components/MapComponent.vue';
import DeleteModal from '@/components/DeleteModal.vue';
import { SpatialObject } from '@/models/SpatialObject';
import { Feature, FeatureCollection } from 'geojson';
import {
  defineComponent,
  reactive,
  ref,
  watchEffect
} from '@vue/composition-api';
import { getModule } from 'vuex-module-decorators';
import AuthModule from '@/store/AuthModule';
import store from '@/store';
import spatialObjectService from '@/services/SpatialObjectService';
import notificationService from '@/services/NotificationService';
import useGetAllHierarchicalLayers from '@/functions/layer/useGetAllHierarchicalLayers';
import useGetGeoJSONObjectsByLayer from '@/functions/geojson/useGetGeoJSONObjectsByLayer';
import { GMapUtils, Overlay } from '@/functions/GMapUtils';
import { GeoJSONUtils } from '@/functions/GeoJSONUtils';
import useDeleteModal from '@/functions/useDeleteModal';
import { SpatialObjectFormModalMode } from '@/models/SpatialObjectFormModalMode';
import { Messages } from '@/models/constants/Messages';
import useNavigation from '@/functions/useNavigation';
import useDrawing from '@/functions/useDrawing';
import { Layer } from '@/models/Layer';

declare const google: any;

declare global {
  interface Window {
    onDelete: (uuid: string) => void;
    onDataEdit: (uuid: string) => void;
  }
}

export default defineComponent({
  name: 'DrawObject',
  components: { SpatialObjectFormModal, MapComponent, DeleteModal },
  setup() {
    const authModule = getModule(AuthModule, store);

    const showForm = ref(false); // show obj data form
    const panels = [0]; // active panel initial value

    // layer settings panel variables
    const selectedLayer = reactive(new Layer()); // layer currently selected by user
    const withHierarchicalLayers = ref(false);

    const { layers, fetchLayers } = useGetAllHierarchicalLayers();
    const { data, fetchGeoJSONObjects } = useGetGeoJSONObjectsByLayer();
    const {
      destinationObj,
      originObj,
      clearNavObjects,
      travelMode,
      navigateEnabled,
      onOriginObjChange,
      onDestinationObjChange,
      setMap: setNavMap,
      setPanel: setNavPanel,
      routePanelVisible,
      navigate
    } = useNavigation();
    const {
      anyObjectEdited,
      drawingManager,
      objToEdit,
      setMap: setDrawMap,
      addListeners: addDrawListeners
    } = useDrawing();

    let map: google.maps.Map;
    let leatestOverlay: Overlay;
    let formMode = SpatialObjectFormModalMode.ADD;

    const getMarkerContent = GMapUtils.getMarkerContentForObject;

    function onLayerChange(newLayer: Layer) {
      Object.assign(selectedLayer, newLayer);
      clearNavObjects();
      fetchGeoJSONObjects(selectedLayer.uuid, withHierarchicalLayers.value);
    }

    function clearLayer() {
      onLayerChange(new Layer());
    }

    function refreshData() {
      fetchLayers();
      fetchGeoJSONObjects(selectedLayer.uuid, withHierarchicalLayers.value);
    }

    const {
      object: objToDelete,
      deleteObject,
      onDeleteDialogClose,
      showDeleteDialog
    } = useDeleteModal<SpatialObject>(
      new SpatialObject(),
      spatialObjectService,
      () => {
        if (objToDelete.uuid) {
          const f = map.data.getFeatureById(objToDelete.uuid);
          map.data.remove(f);
        }
      },
      !authModule.isAdmin
    );

    function onDelete(featureId: string) {
      const f = map.data.getFeatureById(featureId);
      const obj = new SpatialObject(f.getProperty('name'), featureId);
      Object.assign(objToDelete, obj);
      if (anyObjectEdited.value) {
        notificationService.showInfo(Messages.SAVE_GEOMETRIES_BEFORE_DELETING);
      }
      deleteObject(objToDelete);
    }

    function onDataEdit(featureId: string) {
      map.data.getFeatureById(featureId).toGeoJson((f) => {
        const feature = f as Feature;
        objToEdit.geometry = feature.geometry;
        Object.assign(objToEdit, feature.properties);
        if (anyObjectEdited.value) {
          notificationService.showInfo(Messages.SAVE_GEOMETRIES_BEFORE_EDITING);
        }
        formMode = SpatialObjectFormModalMode.EDIT;
        showForm.value = true;
      });
    }

    window.onDelete = onDelete;
    window.onDataEdit = onDataEdit;

    function onFormClose(obj: SpatialObject) {
      showForm.value = false;
      if (obj) {
        if (obj.uuid) {
          // was editing
          spatialObjectService.put(obj.uuid, obj).then(() => refreshData());
        } else {
          const feature = new google.maps.Data.Feature({
            geometry: GMapUtils.convertDrawEventToGeometry(leatestOverlay)
          });
          feature.toGeoJson((json) => {
            obj.geometry = (json as Feature).geometry;
            obj.creatorUuid = authModule.profile.uuid;
            spatialObjectService.post(obj).then(() => refreshData());
          });
        }
      } else {
        if (formMode === SpatialObjectFormModalMode.ADD) {
          leatestOverlay.setMap(null);
        }
      }
    }

    function saveEditedGeometries() {
      map.data.toGeoJson((fc) => {
        const editedObjects = GeoJSONUtils.toSpatialObjects(
          fc as FeatureCollection
        );
        const userUuid = authModule.profile?.uuid;
        if (userUuid) {
          spatialObjectService.postUpdated(editedObjects, userUuid).then(() => {
            anyObjectEdited.value = false;
            refreshData();
          });
        }
      });
    }

    function refreshStyle() {
      map.data.revertStyle();
      map.data.setStyle((feature) => {
        return GMapUtils.getInitialStyle(
          feature,
          authModule.profile?.uuid || ''
        );
      });
    }

    refreshData();
    // function called after map component is initialized
    function init(initializedMap: google.maps.Map) {
      // init map
      map = initializedMap;
      map.data.setStyle((feature) => {
        return GMapUtils.getInitialStyle(
          feature,
          authModule.profile?.uuid || ''
        );
      });

      // init drawing
      setDrawMap(map);
      addDrawListeners();
      drawingManager.addListener(
        'overlaycomplete',
        (e: google.maps.drawing.OverlayCompleteEvent) => {
          leatestOverlay = e.overlay;
          Object.assign(objToEdit, new SpatialObject());
          formMode = SpatialObjectFormModalMode.ADD;
          if (anyObjectEdited.value) {
            notificationService.showInfo(
              Messages.SAVE_GEOMETRIES_BEFORE_ADDING
            );
          }
          showForm.value = true;
        }
      );

      // init navigation
      setNavMap(map);
      setNavPanel(document.getElementById('nav-panel') as HTMLElement);

      // enable and disable drawing option
      watchEffect(() => {
        if (authModule.isSignedIn && !!selectedLayer.uuid) {
          drawingManager.setOptions({
            drawingControl: true
          });
        } else {
          drawingManager.setOptions({
            drawingControl: false
          });
        }
        refreshStyle();
      });
    }

    return {
      showForm,
      onFormClose,
      onLayerChange,
      layers,
      selectedLayer,
      withHierarchicalLayers,
      data,
      getMarkerContent,
      init,
      anyObjectEdited,
      showDeleteDialog,
      objToDelete,
      onDeleteDialogClose,
      saveEditedGeometries,
      objToEdit,
      Messages,
      navigate,
      panels,
      destinationObj,
      originObj,
      routePanelVisible,
      onOriginObjChange,
      onDestinationObjChange,
      navigateEnabled,
      travelMode,
      clearLayer
    };
  }
});
</script>
<style scoped>
#nav-panel {
  max-height: 60vh;
  overflow: auto;
}
</style>
