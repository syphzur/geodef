import VueRouter from 'vue-router';
import HomeView from '@/views/HomeView.vue';
import store from '@/store';
import AuthModule from '@/store/AuthModule';
import { getModule } from 'vuex-module-decorators';

const authModule = getModule(AuthModule, store);

const router = new VueRouter({
  routes: [
    {
      path: '/',
      name: 'Home',
      component: HomeView
    },
    {
      path: '/track',
      name: 'Track',
      component: () =>
        import(
          /* webpackChunkName: "TrackerData" */ '@/views/TrackerDataView.vue'
        ),
      beforeEnter: (to, from, next) => {
        if (authModule.isSignedIn) {
          next();
        } else {
          next('/');
        }
      }
    },
    {
      path: '/draw',
      name: 'Draw',
      component: () =>
        import(
          /* webpackChunkName: "DrawObject" */ '@/views/DrawObjectView.vue'
        )
    },
    {
      path: '/my-types',
      name: 'Manage types',
      component: () =>
        import(
          /* webpackChunkName: "ManageUserTypes" */ '@/views/ManageUserTypesView.vue'
        ),
      beforeEnter: (to, from, next) => {
        if (authModule.isSignedIn) {
          next();
        } else {
          next('/');
        }
      }
    },
    {
      path: '/my-trackers',
      name: 'Manage trackers',
      component: () =>
        import(
          /* webpackChunkName: "ManageUserTrackers" */ '@/views/ManageUserTrackersView.vue'
        ),
      beforeEnter: (to, from, next) => {
        if (authModule.isSignedIn) {
          next();
        } else {
          next('/');
        }
      }
    },
    {
      path: '/admin-panel',
      name: 'AdminPanel',
      component: () =>
        import(
          /* webpackChunkName: "AdminPanelView" */ '@/views/AdminPanelView.vue'
        ),
      beforeEnter: (to, from, next) => {
        if (authModule.isAdmin) {
          next();
        } else {
          next('/');
        }
      }
    }
  ],
  mode: 'history'
});

export default router;
