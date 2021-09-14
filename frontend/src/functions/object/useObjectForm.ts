import { computed, reactive, Ref } from '@vue/composition-api';
import { maxLength, required, requiredIf } from '@vuelidate/validators';
import useVuelidate from '@vuelidate/core';
import { SpatialObject } from '@/models/SpatialObject';
import { Messages } from '@/models/constants/Messages';

export default function useObjectForm(typeNotRequired: Ref<boolean>) {
  const obj = reactive(new SpatialObject());
  const rules = {
    name: { required, maxLength: maxLength(10) },
    typeUuid: {
      requiredIf: requiredIf(() => !typeNotRequired.value)
    }
  };
  const $v = useVuelidate(rules, obj);

  function isInvalid(): boolean {
    return $v.value.name.$invalid || $v.value.typeUuid.$invalid;
  }

  function getNameErrors(name: any, errors: Array<string>): string[] {
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

  const typeErrors = computed(() => {
    const errors = new Array<string>();
    const typeUuid = $v.value.typeUuid;
    if (!typeUuid.$dirty) return errors;
    if (typeUuid.requiredIf.$invalid) errors.push(Messages.REQUIRED_VALUE);
    return errors;
  });

  function resetValidator() {
    $v.value.$reset();
  }

  function touchValidator() {
    $v.value.$touch();
  }

  return {
    obj,
    nameErrors,
    typeErrors,
    $v,
    isInvalid,
    resetValidator,
    touchValidator
  };
}
