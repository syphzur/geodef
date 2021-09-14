import { reactive, ref } from '@vue/composition-api';
import { Page } from '@/models/Page';
import { GenericHttpService } from '@/services/GenericHttpService';
import { DataOptions } from 'vuetify';

export default function usePagination<T>(
  service: GenericHttpService<T>,
  onRefresh?: Function
) {
  const pageInfo = reactive({
    page: 1,
    itemsPerPage: 10
  } as DataOptions);
  const page = reactive(new Page<T>());
  const loading = ref(false);

  function pageInfoUpdate(newValue: DataOptions) {
    loading.value = true;
    Object.assign(pageInfo, newValue);
    service
      .getPage(pageInfo)
      .then((resp) => {
        Object.assign(page, resp.data);
      })
      .finally(() => (loading.value = false));
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
