<template>
  <v-dialog v-model="showDialog" max-width="400" persistent>
    <v-card>
      <v-card-title class="headline"> Delete {{ type }} </v-card-title>
      <v-card-text class="text-center pt-4">
        Are you sure you want to delete
        {{ model.name }} {{ type }}?
        <div v-if="!!appendMsg">
          {{ appendMsg }}
        </div>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="secondary" text @click="close(true)"> Yes </v-btn>
        <v-btn color="secondary" text @click="close(false)"> No </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
<script lang="ts">
import { defineComponent, PropType, ref, watch } from '@vue/composition-api';
import { Entity } from '@/models/Entity';

export default defineComponent({
  name: 'DeleteModal',
  props: {
    model: Object as PropType<Entity>,
    type: String,
    showModal: Boolean,
    appendMessage: String
  },
  setup(props, ctx) {
    const showDialog = ref(props.showModal);
    const appendMsg = ref(props.appendMessage);

    watch(
      () => props.showModal,
      (showModal) => {
        showDialog.value = showModal;
      }
    );

    function close(del: boolean) {
      showDialog.value = false;
      ctx.emit('close', del);
    }

    return { close, showDialog, appendMsg };
  }
});
</script>
