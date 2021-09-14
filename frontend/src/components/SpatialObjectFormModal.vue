<template>
  <v-dialog v-model="showForm" max-width="450">
    <v-card>
      <v-card-title class="headline justify-center">
        Create object
      </v-card-title>
      <v-card-text>
        <form>
          <v-text-field
            v-model="object.name"
            :error-messages="objNameErrors"
            :counter="10"
            label="Name"
            clearable
          ></v-text-field>
          <v-select
            v-model="object.typeUuid"
            :error-messages="typeErrors"
            :items="types"
            :disabled="showAddNewType"
            item-text="name"
            item-value="uuid"
            label="Type"
          ></v-select>

          <v-expand-transition>
            <div v-show="showAddNewType">
              <v-divider></v-divider>

              <v-text-field
                v-model="newType.name"
                :error-messages="newTypeNameErrors"
                :counter="10"
                label="New type name"
                clearable
              ></v-text-field>
            </div>
          </v-expand-transition>
          <v-card-actions>
            <v-btn color="secondary" @click="close(true)"> Save </v-btn>
            <v-btn color="secondary" @click="close(false)"> Cancel </v-btn>
            <v-spacer></v-spacer>
            <v-btn
              text
              color="secondary"
              @click="showAddNewType = !showAddNewType"
              >Add new type
              <v-icon>{{
                showAddNewType ? 'mdi-chevron-down' : 'mdi-chevron-up'
              }}</v-icon>
            </v-btn>
          </v-card-actions>
        </form>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { SpatialObject } from '@/models/SpatialObject';
import { defineComponent, PropType, ref, watch } from '@vue/composition-api';
import useAddType from '@/functions/type/useAddType';
import useObjectForm from '@/functions/object/useObjectForm';
import useGetTypesAllOrByLayer from '@/functions/type/useGetTypesAllOrByLayer';

export default defineComponent({
  name: 'SpatialObjectFormModal',
  props: {
    layerUuid: String,
    showForm: Boolean,
    initialData: Object as PropType<SpatialObject>
  },
  setup(props, ctx) {
    const showAddNewType = ref(false);

    const {
      obj: object,
      nameErrors: objNameErrors,
      typeErrors,
      isInvalid: isObjectInvalid,
      resetValidator: resetObjValidator,
      touchValidator: touchObjValidator
    } = useObjectForm(showAddNewType);

    const {
      type: newType,
      addType,
      nameErrors: newTypeNameErrors,
      resetValidator: resetTypeValidator
    } = useAddType((resp) => {
      object.typeUuid = resp.headers.uuid;
      object.typeName = newType.name;
      ctx.emit('close', object);
    });

    const { types, fetchTypes } = useGetTypesAllOrByLayer();

    function restoreInitialState(data: SpatialObject | undefined) {
      Object.assign(object, data);
      showAddNewType.value = false;
      resetObjValidator();
      resetTypeValidator();
    }

    //watching on data to edit
    watch(
      () => props.showForm,
      (show) => {
        if (show) {
          restoreInitialState(props.initialData);
        }
      }
    );

    watch(
      () => props.layerUuid,
      () => {
        fetchTypes(props.layerUuid);
      }
    );

    function getTypeNameByUuid(uuid: string): string | undefined {
      const obj = types.find((type) => type.uuid === uuid);
      return obj?.name;
    }

    async function close(created: boolean) {
      if (!created) {
        ctx.emit('close', null);
        return;
      }
      touchObjValidator();

      if (isObjectInvalid()) {
        return;
      }
      if (!showAddNewType.value && object.typeUuid) {
        // creating object without adding new type
        object.typeName = getTypeNameByUuid(object.typeUuid);
        ctx.emit('close', object);
      } else {
        newType.layerUuid = props.layerUuid;
        addType();
        // objectTypeService.post(newType).then((resp) => {
        //   object.typeUuid = resp.headers.uuid;
        //   object.typeName = newType.name;
        //   ctx.emit('close', object);
        // });
      }
    }

    fetchTypes();
    return {
      types,
      close,
      object,
      newType,
      objNameErrors,
      typeErrors,
      newTypeNameErrors,
      showAddNewType
    };
  }
});
</script>
