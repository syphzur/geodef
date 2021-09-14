<template>
  <div id="app">
    <v-app>
      <v-app-bar app clipped-left color="secondary">
        <v-app-bar-nav-icon
          color="primary"
          @click.stop="showDrawer = !showDrawer"
        ></v-app-bar-nav-icon>

        <v-toolbar-title class="text-lowercase">
          <span class="font-weight-thin accent--text">geo</span>
          <span class="font-weight-bolder primary--text">def</span>
        </v-toolbar-title>
        <v-spacer></v-spacer>
        <v-toolbar-items id="toolbar-items">
          <LoginButton></LoginButton>
        </v-toolbar-items>
      </v-app-bar>
      <v-navigation-drawer
        id="drawer"
        v-model="showDrawer"
        :mini-variant="showDrawer && !isMobile"
        app
        clipped
        :permanent="!isMobile"
      >
        <v-list rounded>
          <v-list-item
            v-for="item in navItems"
            :key="item.title"
            link
            :to="item.route"
          >
            <v-list-item-icon>
              <v-icon>{{ item.icon }}</v-icon>
            </v-list-item-icon>
            <v-list-item-content>
              <v-list-item-title>{{ item.title }}</v-list-item-title>
            </v-list-item-content>
          </v-list-item>

          <!-- visible only for logged user -->
          <v-list-item v-if="isLoggedIn" link :to="'/track'">
            <v-list-item-icon>
              <v-icon>mdi-crosshairs-gps</v-icon>
            </v-list-item-icon>
            <v-list-item-content>
              <v-list-item-title>Tracker data</v-list-item-title>
            </v-list-item-content>
          </v-list-item>

          <!-- visible only for admin -->
          <v-list-item v-if="isAdmin" link :to="'/admin-panel'">
            <v-list-item-icon>
              <v-icon>mdi-account-cog-outline</v-icon>
            </v-list-item-icon>
            <v-list-item-content>
              <v-list-item-title>Admin panel</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </v-list>
      </v-navigation-drawer>
      <v-main id="app-content">
        <router-view />
      </v-main>

      <v-footer id="footer" app absolute padless color="secondary">
        <v-col class="accent--text">
          <div class="text-center">
            © {{ new Date().getFullYear() }} — <strong>Geodef</strong>
          </div>
          <div id="trademark-info" class="text-right">
            <button @click="showTrademarkModal = true">
              Trademark information
            </button>
          </div>
        </v-col>
      </v-footer>
      <TrademarkModal
        :show-modal="showTrademarkModal"
        @close="showTrademarkModal = false"
      />
    </v-app>
  </div>
</template>
<script lang="ts">
import { computed, defineComponent, ref } from '@vue/composition-api';
import { getModule } from 'vuex-module-decorators';
import store from '@/store';
import AuthModule from '@/store/AuthModule';
import LoginButton from '@/components/LoginButton.vue';
import TrademarkModal from '@/components/TrademarkModal.vue';
import { Role } from './models/Role';

interface NavItem {
  title: string;
  icon: string;
  route: string;
}

export default defineComponent({
  name: 'App',
  components: { LoginButton, TrademarkModal },
  setup(props, ctx) {
    // init auth module for child components
    const authModule = getModule(AuthModule, store);

    const showTrademarkModal = ref(false);

    const isAdmin = computed(() => authModule.profile?.role === Role.ADMIN);
    const isLoggedIn = computed(() => authModule.isSignedIn);
    const isMobile = computed(() => ctx.root.$vuetify.breakpoint.xsOnly);
    const showDrawer = ref(!isMobile.value);
    const navItems: Array<NavItem> = [
      { title: 'Home', icon: 'mdi-home-city', route: '/' },
      { title: 'Draw objects', icon: 'mdi-draw', route: '/draw' }
    ];

    return {
      showDrawer,
      isMobile,
      navItems,
      isAdmin,
      isLoggedIn,
      showTrademarkModal
    };
  }
});
</script>
<style lang="scss">
$navbar-height: 56px;

#drawer {
  max-height: 100% !important;
}

#footer {
  z-index: 4;
}

#app-content {
  z-index: 1;
}

.flipped {
  transform: rotate(180deg);
}

// vuetify override
.v-application--is-ltr .v-list-item__action:first-child,
.v-application--is-ltr .v-list-item__icon:first-child,
.v-navigation-drawer--mini-variant .v-list-item > :first-child {
  transition: all 0.5s;
  margin-left: -8px !important;
}

#toolbar-items {
  display: flex;
  align-items: center;
}

#trademark-info {
  opacity: 0.7;
  font-size: 0.7rem;
}
</style>
