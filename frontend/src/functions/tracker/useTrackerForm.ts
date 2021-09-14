import { computed, reactive } from '@vue/composition-api';
import { maxLength, minLength, numeric, required } from '@vuelidate/validators';
import useVuelidate from '@vuelidate/core';
import { GpsTracker } from '@/models/GpsTracker';

export default function useTrackerForm() {
  const tracker = reactive(new GpsTracker());

  const rules = {
    imei: {
      required,
      maxLength: maxLength(12),
      minLength: minLength(12),
      numeric
    }
  };
  const $v = useVuelidate(rules, tracker);

  function isInvalid(): boolean {
    return $v.value.imei.$invalid;
  }

  function getImeiErrors(imei: any, errors: Array<string>): Array<string> {
    if (!imei.$dirty) return errors;
    if (imei.maxLength.$invalid) errors.push(imei.maxLength.$message);
    if (imei.minLength.$invalid) errors.push(imei.minLength.$message);
    if (imei.required.$invalid) errors.push(imei.required.$message);
    if (imei.numeric.$invalid) errors.push(imei.numeric.$message);
    return errors;
  }

  const imeiErrors = computed(() => {
    const errors = new Array<string>();
    const imei = $v.value.imei;
    return getImeiErrors(imei, errors);
  });

  return {
    tracker,
    imeiErrors,
    $v,
    isInvalid
  };
}
