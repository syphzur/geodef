import objectTypeService from '@/services/ObjectTypeService';
import useTypeForm from './useTypeForm';

export default function useEditType(callback: Function, byUser: boolean) {
  const { type, nameErrors, layerErrors, $v, isInvalid } = useTypeForm();

  function saveEditedType() {
    $v.value.$touch();
    if (!isInvalid() && type.uuid) {
      if (byUser) {
        objectTypeService.putForUser(type.uuid, type).then(() => callback());
      } else {
        objectTypeService.put(type.uuid, type).then(() => callback());
      }
    }
  }

  return {
    type,
    saveEditedType,
    nameErrors,
    layerErrors
  };
}
