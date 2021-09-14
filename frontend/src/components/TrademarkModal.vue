<template>
  <v-dialog v-model="showModal" max-width="400" persistent>
    <v-card>
      <v-card-title class="headline"> Trademark information </v-card-title>
      <v-card-text class="text-center pt-4">
        ©2018 Google LLC All rights reserved. Google and the Google logo are
        registered trademarks of Google LLC.
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="secondary" text @click="close"> Close </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<script lang="ts">
import { defineComponent } from '@vue/composition-api';

export default defineComponent({
  name: 'TrademarkModal',
  props: {
    showModal: Boolean
  },
  setup(props, ctx) {
    function close() {
      ctx.emit('close');
    }
    return { close };
  }
});
</script>
