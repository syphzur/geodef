import { Layer } from '@/models/Layer';
import { computed, reactive } from '@vue/composition-api';
import { maxLength, required } from '@vuelidate/validators';
import useVuelidate from '@vuelidate/core';
import { Messages } from '@/models/constants/Messages';

export default function useLayerForm() {
  const layer = reactive(new Layer());

  const rules = {
    name: { required, maxLength: maxLength(10) },
    prevLayerUuid: { prevLayer: (val) => (val ? val !== layer.uuid : true) }
  };
  const $v = useVuelidate(rules, layer);

  function isInvalid(): boolean {
    return $v.value.name.$invalid || $v.value.prevLayerUuid.$invalid;
  }

  function getNameErrors(name: any, errors: Array<string>): Array<string> {
    if (!name.$dirty) return errors;
    if (name.maxLength.$invalid) errors.push(name.maxLength.$message);
    if (name.required.$invalid) errors.push(name.required.$message);
    return errors;
  }

  const nameErrors = computed(() => {
    const errors = new Array<string>();
    const name = $v.value.name;
    return getNameErrors(name, errors);
  });

  function getPrevLayerErrors(
    prevLayerUuid: any,
    errors: Array<string>
  ): Array<string> {
    if (!prevLayerUuid.$dirty) return errors;
    if (prevLayerUuid.prevLayer.$invalid) errors.push(Messages.BAD_PREV_LAYER);
    return errors;
  }

  const prevLayerErrors = computed(() => {
    const errors = new Array<string>();
    const prevLayerUuid = $v.value.prevLayerUuid;
    return getPrevLayerErrors(prevLayerUuid, errors);
  });

  return {
    layer,
    nameErrors,
    prevLayerErrors,
    $v,
    isInvalid
  };
}
