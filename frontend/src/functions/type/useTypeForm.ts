import { computed, reactive, Ref } from '@vue/composition-api';
import { maxLength, required, requiredIf } from '@vuelidate/validators';
import useVuelidate from '@vuelidate/core';
import { ObjectType } from '@/models/ObjectType';
import { Messages } from '@/models/constants/Messages';

export default function useTypeForm(typeRequired?: Ref<boolean>) {
  const type = reactive(new ObjectType());
  const rules = {
    name: {
      requiredIf: requiredIf(() => !typeRequired || typeRequired.value),
      maxLength: maxLength(10)
    },
    layerUuid: { required }
  };
  const $v = useVuelidate(rules, type);

  function isInvalid(): boolean {
    return $v.value.name.$invalid || $v.value.layerUuid.$invalid;
  }

  function getNameErrors(name: any, errors: Array<string>): string[] {
    if (!name.$dirty) return errors;
    if (name.maxLength.$invalid) errors.push(name.maxLength.$message);
    if (name.requiredIf.$invalid) errors.push(Messages.REQUIRED_VALUE);
    return errors;
  }

  const nameErrors = computed(() => {
    const errors = new Array<string>();
    const name = $v.value.name;
    return getNameErrors(name, errors);
  });

  function getLayerErrors(layerUuid: any, errors: Array<string>): string[] {
    if (!layerUuid.$dirty) return errors;
    if (layerUuid.required.$invalid) errors.push(layerUuid.required.$message);
    return errors;
  }

  const layerErrors = computed(() => {
    const errors = new Array<string>();
    const layerUuid = $v.value.layerUuid;
    return getLayerErrors(layerUuid, errors);
  });

  function resetValidator() {
    $v.value.$reset();
  }

  return {
    type,
    nameErrors,
    layerErrors,
    $v,
    isInvalid,
    resetValidator
  };
}
