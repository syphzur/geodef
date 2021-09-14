<template>
  <DrawObject />
</template>
<script lang="ts">
import { defineComponent } from '@vue/composition-api';
import DrawObject from '@/components/DrawObject.vue';

export default defineComponent({
  name: 'DrawObjectView',
  components: { DrawObject }
});
</script>
