<template>
  <v-container>
    <v-card>
      <v-card-title>
        <v-icon large left> mdi-folder-cog </v-icon>
        <span class="title font-weight-light">Manage types</span>
        <v-spacer></v-spacer>
        <v-btn color="secondary" text @click="refreshData">
          Refresh
          <v-icon right> mdi-refresh </v-icon>
        </v-btn>
      </v-card-title>
      <v-card-text>
        <v-row>
          <v-col cols="auto" md="8" sm="12">
            <v-data-table
              :headers="headers"
              :items="typesPage.content"
              :server-items-length="typesPage.totalElements"
              :loading="loading"
              :options.sync="pageInfo"
              class="elevation-1"
              @update:options="pageInfoUpdate"
            >
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
                      @click="editType(item)"
                    >
                      mdi-clipboard-edit-outline
                    </v-icon>
                  </template>
                  <span class="text-center">Edit type</span>
                </v-tooltip>
                <v-tooltip bottom :disabled="showDeleteDialog">
                  <template v-slot:activator="{ on, attrs }">
                    <v-icon
                      class="mr-2"
                      color="primary"
                      v-bind="attrs"
                      small
                      :disabled="item.objectsNumber !== 0"
                      v-on="on"
                      @click="deleteType(item)"
                    >
                      mdi-delete-alert-outline
                    </v-icon>
                  </template>
                  <span class="text-center">Delete type</span>
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
                v-if="!showEditType"
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
                          Editing <b>{{ editedTypeName }}</b> type
                        </span>
                        <v-text-field
                          v-model="typeToEdit.name"
                          :error-messages="editNameErrors"
                          :counter="10"
                          label="Name"
                          clearable
                        ></v-text-field>
                        <v-select
                          v-model="typeToEdit.layerUuid"
                          :error-messages="editLayerErrors"
                          :items="layers"
                          item-text="name"
                          item-value="uuid"
                          label="Layer"
                        >
                        </v-select>
                      </v-col>
                    </v-row>
                  </form>
                </v-card-text>
                <v-card-actions class="pt-0">
                  <v-container class="text-right pt-0">
                    <v-btn color="secondary" @click="saveEditedType"
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
                        v-model="typeToAdd.name"
                        :error-messages="addNameErrors"
                        :counter="10"
                        label="Name"
                        clearable
                      ></v-text-field>
                      <v-select
                        v-model="typeToAdd.layerUuid"
                        :error-messages="addLayerErrors"
                        :items="layers"
                        item-text="name"
                        item-value="uuid"
                        label="Layer"
                      >
                      </v-select>
                    </v-col>
                  </v-row>
                </form>
              </v-card-text>
              <v-card-actions class="pt-0">
                <v-container class="text-right pt-0">
                  <v-btn color="secondary" @click="addType">Save</v-btn>
                </v-container>
              </v-card-actions>
            </v-card>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>
    <DeleteModal
      :show-modal="showDeleteDialog"
      :model="typeToDelete"
      :type="'type'"
      @close="onDeleteDialogClose"
    />
    <TypeInfoModal
      :show-modal="showInfoDialog"
      :type="typeToViewInfo"
      @close="showInfoDialog = false"
    />
  </v-container>
</template>
<script lang="ts">
import { defineComponent, reactive, ref } from '@vue/composition-api';
import { DataTableHeader } from 'vuetify';
import { ObjectType } from '@/models/ObjectType';
import DeleteModal from './DeleteModal.vue';
import TypeInfoModal from './TypeInfoModal.vue';
import usePagination from '@/functions/usePagination';
import useGetTypePageByCreator from '@/functions/type/useGetTypePageByCreator';
import useGetAllLayers from '@/functions/layer/useGetAllLayers';
import useAddType from '@/functions/type/useAddType';
import useEditType from '@/functions/type/useEditType';
import useDeleteModal from '@/functions/useDeleteModal';
import objectTypeService from '@/services/ObjectTypeService';
import { getModule } from 'vuex-module-decorators';
import AuthModule from '@/store/AuthModule';
import store from '@/store';

export default defineComponent({
  name: 'ManageTypesCard',
  components: { DeleteModal, TypeInfoModal },
  setup() {
    const authModule = getModule(AuthModule, store);

    const showEditType = ref(false);
    const showInfoDialog = ref(false);
    const editedTypeName = ref('');

    const typeToViewInfo = reactive(new ObjectType());

    const headers: Array<DataTableHeader> = [
      { text: 'Name', value: 'name' },
      { text: 'Layer', value: 'layerName' },
      { text: 'Creator', value: 'creatorUsername' },
      {
        text: 'Objects number',
        value: 'objectsNumber',
        class: 'table-header-width'
      },
      {
        text: 'Actions',
        value: 'actions',
        class: 'three-icons-actions',
        sortable: false
      }
    ];

    const { layers, fetchLayers } = useGetAllLayers();

    const {
      page: typesPage,
      pageInfo,
      loading,
      pageInfoUpdate,
      refreshData
    } = authModule.isAdmin
      ? usePagination<ObjectType>(objectTypeService, fetchLayers)
      : useGetTypePageByCreator(fetchLayers);

    const {
      type: typeToAdd,
      addType,
      nameErrors: addNameErrors,
      layerErrors: addLayerErrors
    } = useAddType(refreshData);

    const {
      type: typeToEdit,
      saveEditedType,
      nameErrors: editNameErrors,
      layerErrors: editLayerErrors
    } = useEditType(refreshData, !authModule.isAdmin);

    const {
      object: typeToDelete,
      deleteObject: deleteType,
      onDeleteDialogClose,
      showDeleteDialog
    } = useDeleteModal<ObjectType>(
      new ObjectType(),
      objectTypeService,
      refreshData,
      !authModule.isAdmin
    );

    function editType(type: ObjectType) {
      Object.assign(typeToEdit, type);
      if (type.name) {
        editedTypeName.value = type.name;
      }
      showEditType.value = true;
    }

    function showInfo(type: ObjectType) {
      Object.assign(typeToViewInfo, type);
      showInfoDialog.value = true;
    }

    fetchLayers();
    return {
      pageInfo,
      typesPage,
      headers,
      loading,
      pageInfoUpdate,
      layers,
      editedTypeName,
      typeToAdd,
      typeToDelete,
      typeToEdit,
      addType,
      saveEditedType,
      addNameErrors,
      addLayerErrors,
      editNameErrors,
      editLayerErrors,
      showEditType,
      editType,
      deleteType,
      onDeleteDialogClose,
      refreshData,
      showDeleteDialog,
      showInfoDialog,
      showInfo,
      typeToViewInfo
    };
  }
});
</script>
<style lang="scss">
.table-header-width {
  min-width: 140px;
}
</style>
