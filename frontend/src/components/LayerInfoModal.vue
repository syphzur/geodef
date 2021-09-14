<template>
  <v-dialog
    v-model="showDialog"
    max-width="450"
    content-class="modal-max-height"
    persistent
  >
    <v-card>
      <v-card-title class="title">
        {{ layer.name }}
        <v-spacer></v-spacer>
        <v-btn color="secondary" icon @click="close">
          <v-icon> mdi-close </v-icon>
        </v-btn>
      </v-card-title>
      <v-card-text>
        <div>Previous layer in hierarchy: {{ layer.prevLayerName }}</div>
        <div>Types:</div>
        <v-data-table
          :headers="headers"
          :items="types"
          :items-per-page="5"
          :loading="loading"
          class="elevation-1 mt-3"
        >
        </v-data-table>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import {
  defineComponent,
  PropType,
  reactive,
  ref,
  watch
} from '@vue/composition-api';
import useGetTypesAllOrByLayer from '@/functions/type/useGetTypesAllOrByLayer';
import { DataTableHeader } from 'vuetify';
import { Layer } from '@/models/Layer';

export default defineComponent({
  name: 'LayerInfoModal',
  props: {
    model: {
      type: Object as PropType<Layer>,
      required: true
    },
    showModal: Boolean
  },
  setup(props, ctx) {
    const layer = reactive(props.model);
    const showDialog = ref(props.showModal);

    const { types, fetchTypes, loading } = useGetTypesAllOrByLayer();

    const headers: Array<DataTableHeader> = [
      { text: 'Name', value: 'name' },
      { text: 'Creator', value: 'creatorUsername' },
      {
        text: 'Objects number',
        value: 'objectsNumber',
        class: 'table-header-width'
      }
    ];

    watch(
      () => props.showModal,
      (showModal) => {
        showDialog.value = showModal;
        if (props.showModal && props.model.uuid) {
          fetchTypes(props.model.uuid);
        }
      }
    );

    function close() {
      ctx.emit('close');
      // vuetify tooltip issue fix
      setTimeout(() => (document.activeElement as HTMLElement).blur());
    }

    return {
      layer,
      types,
      headers,
      close,
      showDialog,
      loading
    };
  }
});
</script>
<style lang="scss">
.modal-max-height {
  max-height: 500px;
  min-width: 450px;
}

.table-header-width {
  min-width: 140px;
}
</style>
