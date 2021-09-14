<template>
  <div>
    <ManageUsersCard></ManageUsersCard>
    <ManageLayersCard></ManageLayersCard>
    <ManageTypesCard></ManageTypesCard>
  </div>
</template>
<script lang="ts">
import { defineComponent } from '@vue/composition-api';
import ManageUsersCard from '@/components/ManageUsersCard.vue';
import ManageLayersCard from '@/components/ManageLayersCard.vue';
import ManageTypesCard from '@/components/ManageTypesCard.vue';

export default defineComponent({
  name: 'AdminPanelView',
  components: { ManageUsersCard, ManageLayersCard, ManageTypesCard }
});
</script>
