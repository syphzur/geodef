<template>
  <div>
    <ManageTypesCard></ManageTypesCard>
  </div>
</template>
<script lang="ts">
import { defineComponent } from '@vue/composition-api';
import ManageTypesCard from '@/components/ManageTypesCard.vue';

export default defineComponent({
  name: 'ManageUserTypesView',
  components: { ManageTypesCard }
});
</script>
