<template>
  <v-container>
    <!-- manage users -->
    <v-card>
      <v-card-title>
        <v-icon large left> mdi-account-box </v-icon>
        <span class="title font-weight-light">Manage users</span>
      </v-card-title>

      <v-card-text>
        <v-data-table
          :headers="headers"
          :items="usersPage.content"
          :server-items-length="usersPage.totalElements"
          :loading="loading"
          :options.sync="pageInfo"
          class="elevation-1"
          @update:options="pageInfoUpdate"
        >
          <template v-slot:item.familyName="{ item }">
            <span>{{ !!item.familyName ? item.familyName : '-' }}</span>
          </template>
          <template v-slot:item.givenName="{ item }">
            <span>{{ !!item.givenName ? item.givenName : '-' }}</span>
          </template>
          <template v-slot:item.role="{ item }">
            <span>{{
              item.role === Role.ADMIN ? 'Administrator' : 'User'
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
                  :disabled="
                    item.role === Role.ADMIN || item.username === loggedUsername
                  "
                  v-on="on"
                  @click="changeRole(item, true)"
                >
                  mdi-chevron-double-up
                </v-icon>
              </template>
              <span class="text-center">Give administrator rights</span>
            </v-tooltip>
            <v-tooltip bottom>
              <template v-slot:activator="{ on, attrs }">
                <v-icon
                  class="mr-2"
                  color="primary"
                  v-bind="attrs"
                  small
                  :disabled="
                    item.role === Role.USER || item.username === loggedUsername
                  "
                  v-on="on"
                  @click="changeRole(item, false)"
                >
                  mdi-chevron-double-down
                </v-icon>
              </template>
              <span class="text-center">Revoke administrator rights</span>
            </v-tooltip>
            <v-tooltip bottom>
              <template v-slot:activator="{ on, attrs }">
                <v-icon
                  color="primary"
                  v-bind="attrs"
                  small
                  :disabled="item.username === loggedUsername"
                  v-on="on"
                  @click="deleteUser(item)"
                >
                  mdi-delete-alert-outline
                </v-icon>
              </template>
              <span class="text-center">Delete user</span>
            </v-tooltip>
          </template>
        </v-data-table>
      </v-card-text>
    </v-card>
    <DeleteModal
      :show-modal="showDeleteDialog"
      :model="userToDelete"
      :type="'user'"
      @close="onDeleteDialogClose"
    />
  </v-container>
</template>
<script lang="ts">
import { computed, defineComponent } from '@vue/composition-api';
import userService from '@/services/UserService';
import { Role } from '@/models/Role';
import { User } from '@/models/User';
import { DataTableHeader } from 'vuetify';
import AuthModule from '@/store/AuthModule';
import { getModule } from 'vuex-module-decorators';
import store from '@/store';
import usePagination from '@/functions/usePagination';
import useDeleteModal from '@/functions/useDeleteModal';
import DeleteModal from './DeleteModal.vue';

export default defineComponent({
  name: 'ManageUsersCard',
  components: { DeleteModal },
  setup() {
    const authModule = getModule(AuthModule, store);

    const loggedUsername = computed(() => authModule.profile?.username);
    const headers: Array<DataTableHeader> = [
      { text: 'Username', value: 'username', class: 'table-header-width' },
      { text: 'Email', value: 'email' },
      { text: 'Family name', value: 'familyName', class: 'table-header-width' },
      { text: 'Given name', value: 'givenName', class: 'table-header-width' },
      { text: 'Role', value: 'role' },
      {
        text: 'Actions',
        value: 'actions',
        sortable: false,
        class: 'three-icons-actions'
      }
    ];

    const {
      page: usersPage,
      pageInfo,
      loading,
      pageInfoUpdate,
      refreshData
    } = usePagination<User>(userService);

    const {
      object: userToDelete,
      deleteObject: deleteUser,
      onDeleteDialogClose,
      showDeleteDialog
    } = useDeleteModal<User>(new User(), userService, refreshData, false);

    function changeRole(user: User, toAdmin: boolean) {
      user.role = toAdmin ? Role.ADMIN : Role.USER;
      if (user.uuid) {
        userService.put(user.uuid, user).then(() => pageInfoUpdate(pageInfo));
      }
    }

    return {
      pageInfo,
      usersPage,
      headers,
      loading,
      pageInfoUpdate,
      changeRole,
      Role,
      loggedUsername,
      userToDelete,
      onDeleteDialogClose,
      showDeleteDialog,
      deleteUser
    };
  }
});
</script>
<style lang="scss">
.table-header-width {
  min-width: 120px;
}
</style>
