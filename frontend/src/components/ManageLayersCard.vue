<template>
  <v-container>
    <v-card>
      <v-card-title>
        <v-icon large left> mdi-map </v-icon>
        <span class="title font-weight-light">Manage layers</span>
        <v-spacer></v-spacer>
        <v-btn color="secondary" text @click="refreshData">
          Refresh
          <v-icon right> mdi-refresh </v-icon>
        </v-btn>
      </v-card-title>
      <v-card-text>
        <v-row>
          <v-col cols="auto" md="7" sm="12">
            <v-data-table
              id="layer-table"
              :headers="headers"
              :items="layersPage.content"
              :server-items-length="layersPage.totalElements"
              :loading="loading"
              :options.sync="pageInfo"
              class="elevation-1"
              @update:options="pageInfoUpdate"
            >
              <template v-slot:item.prevLayerName="{ item }">
                <span>{{
                  !!item.prevLayerName ? item.prevLayerName : '-'
                }}</span>
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
                      @click="showInfo(item)"
                    >
                      mdi-information-outline
                    </v-icon>
                  </template>
                  <span class="text-center">View detailed information</span>
                </v-tooltip>
                <v-tooltip bottom>
                  <template v-slot:activator="{ on, attrs }">
                    <v-icon
                      class="mr-2"
                      color="primary"
                      v-bind="attrs"
                      small
                      v-on="on"
                      @click="editLayer(item)"
                    >
                      mdi-clipboard-edit-outline
                    </v-icon>
                  </template>
                  <span class="text-center">Edit layer</span>
                </v-tooltip>
                <v-tooltip bottom>
                  <template v-slot:activator="{ on, attrs }">
                    <v-icon
                      class="mr-2"
                      color="primary"
                      v-bind="attrs"
                      small
                      :disabled="item.typesCount > 0"
                      v-on="on"
                      @click="deleteLayer(item)"
                    >
                      mdi-delete-alert-outline
                    </v-icon>
                  </template>
                  <span class="text-center">Delete layer</span>
                </v-tooltip>
              </template>
            </v-data-table>
          </v-col>
          <v-col>
            <v-card>
              <v-card-title>
                <v-icon left> mdi-clipboard-edit </v-icon>
                <span class="title font-weight-light">Edit</span>
              </v-card-title>
              <v-card-text
                v-if="!showEditLayer"
                class="d-flex justify-center align-center pb-10"
              >
                <h2 class="font-weight-thin grey--text text--lighten-2">
                  Click on <b>edit</b> action
                </h2>
              </v-card-text>
              <template v-else>
                <v-card-text class="pb-0">
                  <form>
                    <v-row>
                      <v-col class="py-0">
                        <span
                          class="font-weight-thin grey--text text--lighten-1"
                        >
                          Editing <b>{{ editedLayerName }}</b> layer
                        </span>
                        <v-text-field
                          v-model="layerToEdit.name"
                          :error-messages="editNameErrors"
                          :counter="10"
                          label="Name"
                          clearable
                        ></v-text-field>
                        <v-select
                          v-model="layerToEdit.prevLayerUuid"
                          :error-messages="editPrevLayerErrors"
                          :items="layers"
                          item-text="name"
                          item-value="uuid"
                          label="Previous layer in hierarchy"
                        >
                          <template v-slot:prepend-item>
                            <v-list-item @click="clearPrevLayer(layerToEdit)">
                              <v-list-item-action>
                                <v-icon> mdi-close </v-icon>
                              </v-list-item-action>
                              <v-list-item-content>
                                <v-list-item-title>
                                  No previous layer
                                </v-list-item-title>
                              </v-list-item-content>
                            </v-list-item>
                            <v-divider class="mt-2"></v-divider>
                          </template>
                        </v-select>
                      </v-col>
                    </v-row>
                  </form>
                </v-card-text>
                <v-card-actions class="pt-0">
                  <v-container class="text-right pt-0">
                    <v-btn color="secondary" @click="saveEditedLayer"
                      >Save</v-btn
                    >
                  </v-container>
                </v-card-actions>
              </template>
            </v-card>
            <v-card class="mt-6">
              <v-card-title>
                <v-icon left> mdi-plus-box </v-icon>
                <span class="title font-weight-light">Add</span>
              </v-card-title>
              <v-card-text class="pb-0">
                <form>
                  <v-row>
                    <v-col class="py-0">
                      <v-text-field
                        v-model="layerToAdd.name"
                        :error-messages="addNameErrors"
                        :counter="10"
                        label="Name"
                        clearable
                      ></v-text-field>
                      <v-select
                        v-model="layerToAdd.prevLayerUuid"
                        :error-messages="addPrevLayerErrors"
                        :items="layers"
                        item-text="name"
                        item-value="uuid"
                        label="Previous layer in hierarchy"
                      >
                        <template v-slot:prepend-item>
                          <v-list-item @click="clearPrevLayer(layerToAdd)">
                            <v-list-item-action>
                              <v-icon> mdi-close </v-icon>
                            </v-list-item-action>
                            <v-list-item-content>
                              <v-list-item-title>
                                No previous layer
                              </v-list-item-title>
                            </v-list-item-content>
                          </v-list-item>
                          <v-divider class="mt-2"></v-divider>
                        </template>
                      </v-select>
                    </v-col>
                  </v-row>
                </form>
              </v-card-text>
              <v-card-actions class="pt-0">
                <v-container class="text-right pt-0">
                  <v-btn color="secondary" @click="addLayer">Save</v-btn>
                </v-container>
              </v-card-actions>
            </v-card>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>
    <DeleteModal
      :show-modal="showDeleteDialog"
      :model="layerToDelete"
      :type="'layer'"
      @close="onDeleteDialogClose"
    />
    <LayerInfoModal
      :show-modal="showInfoDialog"
      :model="layerToViewInfo"
      @close="showInfoDialog = false"
    />
  </v-container>
</template>
<script lang="ts">
import { defineComponent, reactive, ref } from '@vue/composition-api';
import layerService from '@/services/LayerService';
import { DataTableHeader } from 'vuetify';
import { Layer } from '@/models/Layer';
import DeleteModal from './DeleteModal.vue';
import LayerInfoModal from './LayerInfoModal.vue';
import useAddLayer from '@/functions/layer/useAddLayer';
import useEditLayer from '@/functions/layer/useEditLayer';
import usePagination from '@/functions/usePagination';
import useGetAllLayers from '@/functions/layer/useGetAllLayers';
import useDeleteModal from '@/functions/useDeleteModal';

export default defineComponent({
  name: 'ManageLayersCard',
  components: { DeleteModal, LayerInfoModal },
  setup() {
    const showEditLayer = ref(false);
    const showInfoDialog = ref(false);
    const editedLayerName = ref('');

    const layerToViewInfo = reactive(new Layer());

    const headers: Array<DataTableHeader> = [
      { text: 'Name', value: 'name' },
      { text: 'Previous layer', value: 'prevLayerName' },
      { text: 'Types number', value: 'typesCount', sortable: false },
      {
        text: 'Actions',
        value: 'actions',
        class: 'three-icons-actions',
        sortable: false
      }
    ];

    const { layers, fetchLayers } = useGetAllLayers();

    const {
      page: layersPage,
      pageInfo,
      loading,
      pageInfoUpdate,
      refreshData
    } = usePagination<Layer>(layerService, fetchLayers);

    const {
      layer: layerToAdd,
      addLayer,
      nameErrors: addNameErrors,
      prevLayerErrors: addPrevLayerErrors
    } = useAddLayer(refreshData);

    const {
      layer: layerToEdit,
      saveEditedLayer,
      nameErrors: editNameErrors,
      prevLayerErrors: editPrevLayerErrors
    } = useEditLayer(refreshData);

    const {
      object: layerToDelete,
      deleteObject: deleteLayer,
      onDeleteDialogClose,
      showDeleteDialog
    } = useDeleteModal<Layer>(new Layer(), layerService, refreshData, false);

    function editLayer(layer: Layer) {
      Object.assign(layerToEdit, layer);
      if (layer.name) {
        editedLayerName.value = layer.name;
      }
      fetchLayers();
      if (layer) {
        showEditLayer.value = true;
      }
    }

    function showInfo(layer: Layer) {
      Object.assign(layerToViewInfo, layer);
      showInfoDialog.value = true;
    }

    function clearPrevLayer(l: Layer) {
      l.prevLayerUuid = null;
    }

    fetchLayers();
    return {
      pageInfo,
      layersPage,
      headers,
      loading,
      pageInfoUpdate,
      deleteLayer,
      editLayer,
      showDeleteDialog,
      layerToDelete,
      layerToEdit,
      onDeleteDialogClose,
      showEditLayer,
      addNameErrors,
      editNameErrors,
      saveEditedLayer,
      layers,
      clearPrevLayer,
      layerToAdd,
      addLayer,
      addPrevLayerErrors,
      editPrevLayerErrors,
      editedLayerName,
      refreshData,
      showInfoDialog,
      showInfo,
      layerToViewInfo
    };
  }
});
</script>
