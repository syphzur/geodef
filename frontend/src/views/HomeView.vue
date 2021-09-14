<template>
  <v-slide-x-transition appear>
    <v-container fill-height class="justify-center align-center">
      <v-row>
        <v-col cols="12" sm="6" class="mr-10">
          <v-container fill-height class="justify-center align-center">
            <div>
              <h3 class="display-3 mb-2">Welcome</h3>
              <p>
                geo<span class="primary--text">def</span> is a platform for
                defining geospatial objects and controlling their mutual
                location.
              </p>
              <p>
                Draw objects, navigate beetween them, change layers and manage
                data from your GPS tracker.
              </p>
              <v-divider class="my-5"></v-divider>
              <div class="title mb-3">Sign in to start drawing!</div>
              <v-btn class="mx-0" color="primary" large link to="/draw">
                Draw
              </v-btn>
            </div>
          </v-container>
        </v-col>
        <v-col cols="12" sm="5">
          <v-container fill-height class="justify-center align-center">
            <v-img contain src="../assets/destination.svg"></v-img>
          </v-container>
        </v-col>
      </v-row>
      <v-row>
        <v-col>
          <v-container fill-height class="justify-center align-center">
            <v-card>
              <v-carousel
                id="carousel"
                cycle
                show-arrows-on-hover
                height="auto"
              >
                <v-carousel-item v-for="(image, i) in images" :key="i" eager>
                  <v-img :src="image" eager></v-img>
                </v-carousel-item>
              </v-carousel>
            </v-card>
          </v-container>
        </v-col>
      </v-row>
    </v-container>
  </v-slide-x-transition>
</template>
<script lang="ts">
import { defineComponent } from '@vue/composition-api';
export default defineComponent({
  name: 'Home',
  setup() {
    const images = [
      require('../assets/carousel2.webp'),
      require('../assets/carousel1.webp'),
      require('../assets/carousel3.webp')
    ];
    return { images };
  }
});
</script>
<style lang="scss">
#carousel {
  max-height: 700px;
}
</style>
