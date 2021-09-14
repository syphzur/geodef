<template>
  <TrackerData></TrackerData>
</template>

<script lang="ts">
import TrackerData from '@/components/TrackerData.vue';
import { defineComponent } from '@vue/composition-api';

export default defineComponent({
  name: 'TrackerDataView',
  components: { TrackerData }
});
</script>
