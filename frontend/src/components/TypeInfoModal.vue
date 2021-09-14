<template>
  <v-dialog
    v-model="showDialog"
    max-width="500"
    content-class="modal-max-height"
    persistent
  >
    <v-card>
      <v-card-title class="title">
        {{ objectType.name }}
        <v-spacer></v-spacer>
        <v-btn color="secondary" icon @click="close">
          <v-icon> mdi-close </v-icon>
        </v-btn>
      </v-card-title>
      <v-card-text>
        <div>Layer: {{ objectType.layerName }}</div>
        <div>Creator: {{ objectType.creatorUsername }}</div>
        <div>Objects:</div>
        <v-tabs color="primary">
          <v-tab
            v-for="item in tabItems"
            :key="item.name"
            :disabled="item.name === 'map' && !!objects && objects.length === 0"
            >{{ item.name }}</v-tab
          >

          <v-tab-item v-for="item in tabItems" :key="item.name">
            <template v-if="item.name === 'table'">
              <v-data-table
                :headers="headers"
                :items="objects"
                :items-per-page="5"
                :loading="loading"
                class="elevation-1 mt-3"
              ></v-data-table>
            </template>
            <template v-else>
              <MapComponent
                :data="data"
                :get-marker-content="getMarkerContent"
                class="mt-3"
              ></MapComponent>
            </template>
          </v-tab-item>
        </v-tabs>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
interface Tab {
  name: string;
}

import { ObjectType } from '@/models/ObjectType';
import {
  defineComponent,
  PropType,
  reactive,
  ref,
  watch
} from '@vue/composition-api';
import useGetObjectsByType from '@/functions/object/useGetObjectsByType';
import useGetGeoJSONObjectsByType from '@/functions/geojson/useGetGeoJSONObjectsByType';
import { DataTableHeader } from 'vuetify';
import MapComponent from './MapComponent.vue';

export default defineComponent({
  name: 'TypeInfoModal',
  components: { MapComponent },
  props: {
    type: {
      type: Object as PropType<ObjectType>,
      required: true
    },
    showModal: Boolean
  },
  setup(props, ctx) {
    const objectType = reactive(props.type);
    const showDialog = ref(props.showModal);

    const { objects, fetchObjects, loading } = useGetObjectsByType();

    const { data, fetchGeoJSONObjects } = useGetGeoJSONObjectsByType();

    const headers: Array<DataTableHeader> = [
      { text: 'Name', value: 'name' },
      { text: 'Type', value: 'typeName' },
      { text: 'Creator', value: 'creatorUsername' }
    ];

    const tabItems: Array<Tab> = [{ name: 'table' }, { name: 'map' }];

    function close() {
      ctx.emit('close');
      // vuetify tooltip issue fix
      setTimeout(() => (document.activeElement as HTMLElement).blur());
    }

    function getMarkerContent(e: any): string {
      const f = e.feature as google.maps.Data.Feature;
      return (
        'Name: ' +
        f.getProperty('name') +
        '<br>' +
        'Type: ' +
        f.getProperty('typeName') +
        '<br>' +
        'Creator: ' +
        f.getProperty('creatorUsername')
      );
    }

    watch(
      () => props.showModal,
      (showModal) => {
        showDialog.value = showModal;
        if (props.showModal && props.type.uuid) {
          fetchObjects(props.type.uuid);
          fetchGeoJSONObjects(props.type.uuid);
        }
      }
    );

    return {
      objectType,
      objects,
      headers,
      close,
      showDialog,
      loading,
      tabItems,
      data,
      getMarkerContent
    };
  }
});
</script>
<style lang="scss">
.modal-max-height {
  max-height: 500px;
  min-width: 450px;
}
</style>
