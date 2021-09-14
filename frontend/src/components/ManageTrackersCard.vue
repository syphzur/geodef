<template>
  <v-container>
    <v-card>
      <v-card-title>
        <v-icon large left> mdi-crosshairs-gps </v-icon>
        <span class="title font-weight-light">Manage trackers</span>
        <v-spacer></v-spacer>
        <v-btn color="secondary" text @click="fetchTrackers">
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
              :items="trackers"
              :loading="loading"
              class="elevation-1"
            >
              <template v-slot:item.leatestDataDateTime="{ item }">
                <span>{{
                  item.leatestDataDateTime
                    ? item.leatestDataDateTime.toLocaleString()
                    : '-'
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
                      @click="editTracker(item)"
                    >
                      mdi-clipboard-edit-outline
                    </v-icon>
                  </template>
                  <span class="text-center">Edit tracker</span>
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
                      @click="deleteTracker(item)"
                    >
                      mdi-delete-alert-outline
                    </v-icon>
                  </template>
                  <span class="text-center">Delete tracker</span>
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
                v-if="!showEditTracker"
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
                          Editing <b>{{ editedTrackerImei }}</b> tracker
                        </span>
                        <v-text-field
                          v-model="trackerToEdit.imei"
                          :error-messages="editImeiErrors"
                          :counter="12"
                          label="Imei"
                          clearable
                        ></v-text-field>
                      </v-col>
                    </v-row>
                  </form>
                </v-card-text>
                <v-card-actions>
                  <v-container class="text-right pt-0">
                    <v-btn color="secondary" @click="saveEditedTracker"
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
                        v-model="trackerToAdd.imei"
                        :error-messages="addImeiErrors"
                        :counter="12"
                        label="Imei"
                        clearable
                      ></v-text-field>
                    </v-col>
                  </v-row>
                </form>
              </v-card-text>
              <v-card-actions>
                <v-container class="text-right pt-0">
                  <v-btn color="secondary" @click="addTracker">Save</v-btn>
                </v-container>
              </v-card-actions>
            </v-card>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>
    <DeleteModal
      :show-modal="showDeleteDialog"
      :model="trackerToDelete"
      :type="'tracker'"
      @close="onDeleteDialogClose"
    />
  </v-container>
</template>
<script lang="ts">
import { defineComponent, ref } from '@vue/composition-api';
import { DataTableHeader } from 'vuetify';
import DeleteModal from './DeleteModal.vue';
import useAddTracker from '@/functions/tracker/useAddTracker';
import useEditTracker from '@/functions/tracker/useEditTracker';
import useGetTrackersByOwner from '@/functions/tracker/useGetTrackersByOwner';
import useDeleteModal from '@/functions/useDeleteModal';
import gpsTrackerService from '@/services/GpsTrackerService';
import { GpsTracker } from '@/models/GpsTracker';

export default defineComponent({
  name: 'ManageLayersCard',
  components: { DeleteModal },
  setup() {
    const showEditTracker = ref(false);
    const editedTrackerImei = ref('');
    const headers: Array<DataTableHeader> = [
      { text: 'Imei', value: 'imei' },
      { text: 'Leatest data date', value: 'leatestDataDateTime' },
      {
        text: 'Actions',
        value: 'actions',
        class: 'three-icons-actions',
        sortable: false
      }
    ];

    const { trackers, fetchTrackers, loading } = useGetTrackersByOwner();

    const {
      tracker: trackerToAdd,
      imeiErrors: addImeiErrors,
      addTracker
    } = useAddTracker(fetchTrackers);

    const {
      tracker: trackerToEdit,
      saveEditedTracker,
      imeiErrors: editImeiErrors
    } = useEditTracker(fetchTrackers);

    const {
      object: trackerToDelete,
      deleteObject: deleteTracker,
      onDeleteDialogClose,
      showDeleteDialog
    } = useDeleteModal<GpsTracker>(
      new GpsTracker(),
      gpsTrackerService,
      fetchTrackers,
      false
    );

    function editTracker(t: GpsTracker) {
      Object.assign(trackerToEdit, t);
      if (t) {
        if (t.imei) {
          editedTrackerImei.value = t.imei;
        }
        showEditTracker.value = true;
      }
    }

    fetchTrackers();
    return {
      headers,
      loading,
      deleteTracker,
      editTracker,
      showDeleteDialog,
      trackerToDelete,
      trackerToEdit,
      trackerToAdd,
      onDeleteDialogClose,
      showEditTracker,
      addImeiErrors,
      editImeiErrors,
      saveEditedTracker,
      trackers,
      addTracker,
      editedTrackerImei,
      fetchTrackers
    };
  }
});
</script>
