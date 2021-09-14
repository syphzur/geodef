import { reactive, ref } from '@vue/composition-api';
import { Entity } from '@/models/Entity';
import { GenericHttpService } from '@/services/GenericHttpService';
import { HttpServiceWithUserEndpoints } from '@/services/HttpServiceWithUserEndpoints';
import { getModule } from 'vuex-module-decorators';
import AuthModule from '@/store/AuthModule';
import store from '@/store';

export default function useDeleteModal<T>(
  entity: Entity,
  service: GenericHttpService<T> | HttpServiceWithUserEndpoints<T>,
  callback: Function,
  byUser: boolean
) {
  const object = reactive(entity);
  const showDeleteDialog = ref(false);
  const authModule = getModule(AuthModule, store);

  function deleteObject(obj: T) {
    Object.assign(object, obj);
    showDeleteDialog.value = true;
  }

  function onDeleteDialogClose(del: boolean) {
    showDeleteDialog.value = false;
    if (del && object.uuid) {
      if (!byUser) {
        service.delete(object.uuid).then(() => callback());
      } else {
        const s = service as HttpServiceWithUserEndpoints<T>;
        if (authModule.profile.uuid) {
          s.deleteForUser(object.uuid, authModule.profile.uuid).then(() =>
            callback()
          );
        }
      }
    }
    // vuetify tooltip issue fix
    setTimeout(() => (document.activeElement as HTMLElement).blur());
  }

  return {
    object,
    deleteObject,
    onDeleteDialogClose,
    showDeleteDialog
  };
}
