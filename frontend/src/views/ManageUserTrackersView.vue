<template>
  <div>
    <ManageTrackersCard></ManageTrackersCard>
  </div>
</template>
<script lang="ts">
import { defineComponent } from '@vue/composition-api';
import ManageTrackersCard from '@/components/ManageTrackersCard.vue';

export default defineComponent({
  name: 'ManageUserTrackersView',
  components: { ManageTrackersCard }
});
</script>
