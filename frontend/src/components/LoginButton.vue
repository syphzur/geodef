<template>
  <div>
    <div v-if="!userProfile" id="g-button" class="nav-link" @click="signIn()">
      <div class="g-icon"></div>
      <span id="long-text"> Sign in with Google </span>
      <span id="short-text"> Sign in </span>
    </div>
    <template v-else>
      <v-menu
        transition="scroll-y-transition"
        offset-y
        attach
        open-on-hover
        @mouseenter.native="flipped = true"
        @mouseleave.native="flipped = false"
      >
        <template v-slot:activator="{ on, attrs }">
          <v-btn
            text
            class="grey--text text--lighten-3 px-2"
            v-bind="attrs"
            v-on="on"
            @mouseenter.native="flipped = true"
            @mouseleave.native="flipped = false"
          >
            <v-avatar id="avatar" size="34px">
              <img
                :src="userProfile.pictureUrl"
                alt=""
                referrerpolicy="no-referrer"
              />
            </v-avatar>
            <span class="font-weight-light text-lowercase accent--text">{{
              userProfile.username
            }}</span>
            <v-icon :class="{ flipped: flipped }" color="primary"
              >mdi-chevron-down</v-icon
            >
          </v-btn>
        </template>

        <v-list>
          <v-list-item link to="/my-types">
            <v-list-item-title>Manage types</v-list-item-title>
          </v-list-item>
          <v-list-item link to="/my-trackers">
            <v-list-item-title>Manage trackers</v-list-item-title>
          </v-list-item>
          <v-divider></v-divider>
          <v-list-item link @click="signOut()">
            <v-list-item-title>Logout</v-list-item-title>
            <v-list-item-icon><v-icon>mdi-logout</v-icon></v-list-item-icon>
          </v-list-item>
        </v-list>
      </v-menu>
    </template>
  </div>
</template>
<script lang="ts">
import store from '@/store';
import AuthModule from '@/store/AuthModule';
import { computed, defineComponent, ref } from '@vue/composition-api';
import { getModule } from 'vuex-module-decorators';

export default defineComponent({
  name: 'LoginButton',
  setup() {
    const authModule = getModule(AuthModule, store);
    const signIn = computed(() => authModule.signIn);
    const signOut = computed(() => authModule.signOut);
    const isSignedIn = computed(() => authModule.isSignedIn);
    const userProfile = computed(() => authModule.profile);
    const flipped = ref(false);

    return {
      signIn,
      signOut,
      isSignedIn,
      userProfile,
      flipped
    };
  }
});
</script>
<style lang="scss" scoped>
#avatar {
  margin-right: 8px;
}

#g-button {
  background-color: white;
  border-radius: 5px;
  border: 1px solid #c9c9c9;
  display: flex;
  align-items: center;
  justify-content: center;
  padding-right: 25px;
  padding-left: 8px;
  font-weight: 500;
  color: #5f5f5fba;
  box-shadow: 1px 2px 5px 0 #5f5f5f;
  max-width: 230px;
  cursor: pointer;
  text-transform: uppercase;
  font-size: 14px;
  height: 40px;
  transition: all 0.5s ease;

  &:hover {
    filter: brightness(70%);
  }

  @media (min-width: 641px) {
    #short-text {
      display: none;
    }
  }

  @media (max-width: 640px) {
    #long-text {
      display: none;
    }
  }
}

.g-icon {
  background: url('./../assets/g-logo.png');
  background-size: 18px;
  height: 18px;
  width: 18px;
  margin: 0 24px 0 0;
}
</style>
