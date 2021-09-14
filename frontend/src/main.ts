import Vue from 'vue';
import VueRouter from 'vue-router';
import App from './App.vue';
import store from './store';
import router from './router';
import { getModule } from 'vuex-module-decorators';
import AuthModule from './store/AuthModule';
import { VuelidatePlugin } from '@vuelidate/core';
import Vuetify, { colors } from 'vuetify/lib';
import { loadAuth2WithProps } from 'gapi-script';
import { GAuthOptions } from './models/constants/GAuthOptions';

import 'vuetify/dist/vuetify.min.css';
import '@mdi/font/css/materialdesignicons.css';

loadAuth2WithProps(GAuthOptions.gauthOption).then(() => {
  const authModule = getModule(AuthModule, store);
  authModule.init();
});

Vue.use(VuelidatePlugin);
Vue.use(VueRouter);
Vue.use(Vuetify);

const vuetify = new Vuetify({
  theme: {
    themes: {
      light: {
        primary: colors.blue.lighten1,
        secondary: colors.grey.darken3,
        accent: colors.grey.lighten2
      }
    }
  }
});

new Vue({
  router,
  store,
  vuetify: vuetify,
  render: (h) => h(App)
}).$mount('#app');
