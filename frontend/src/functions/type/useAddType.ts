import objectTypeService from '@/services/ObjectTypeService';
import store from '@/store';
import AuthModule from '@/store/AuthModule';
import { getModule } from 'vuex-module-decorators';
import useTypeForm from './useTypeForm';

export default function useAddType(callback?: Function) {
  const {
    type,
    nameErrors,
    layerErrors,
    $v,
    isInvalid,
    resetValidator
  } = useTypeForm();
  const authModule = getModule(AuthModule, store);

  function addType() {
    $v.value.$touch();
    if (!isInvalid()) {
      type.creatorUuid = authModule.profile?.uuid;
      objectTypeService.post(type).then((resp) => {
        if (callback) {
          callback(resp);
        }
      });
    }
  }

  return {
    type,
    addType,
    nameErrors,
    layerErrors,
    resetValidator
  };
}
