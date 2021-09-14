import { reactive, ref } from '@vue/composition-api';
import { Page } from '@/models/Page';
import { DataOptions } from 'vuetify';
import { ObjectType } from '@/models/ObjectType';
import objectTypeService from '@/services/ObjectTypeService';
import { getModule } from 'vuex-module-decorators';
import AuthModule from '@/store/AuthModule';
import store from '@/store';

export default function useGetTypePageByCreator(onRefresh?: Function) {
  const pageInfo = reactive({
    page: 1,
    itemsPerPage: 10
  } as DataOptions);
  const page = reactive(new Page<ObjectType>());
  const loading = ref(false);
  const authModule = getModule(AuthModule, store);

  function pageInfoUpdate(newValue: DataOptions) {
    loading.value = true;
    Object.assign(pageInfo, newValue);
    if (authModule.profile.uuid) {
      objectTypeService
        .getPageByCreator(authModule.profile.uuid, pageInfo)
        .then((resp) => {
          Object.assign(page, resp.data);
        })
        .finally(() => (loading.value = false));
    }
  }

  function refreshData() {
    pageInfoUpdate(pageInfo);
    if (onRefresh) {
      onRefresh();
    }
  }

  return {
    page,
    pageInfo,
    loading,
    pageInfoUpdate,
    refreshData
  };
}
