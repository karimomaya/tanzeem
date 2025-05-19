<template>
  <v-app>
    <v-main>
      <MainLayout />
    </v-main>
    <!-- Snackbar for global error messages -->
    <v-snackbar class="text-center" v-model="showError" color="red" timeout="5000">
      <p class="text-center font-weight-bold	">{{ errorMessage }}</p>
    </v-snackbar>
    <!-- Snackbar for global success messages -->
    <v-snackbar class="text-center" v-model="showSuccess" color="green" timeout="5000">
      <p class="text-center font-weight-bold	">{{ successMessage }}</p>
    </v-snackbar>
  </v-app>
</template>
  
<script>
import MainLayout from '@/components/layouts/MainLayout.vue';
export default {
  components: {
    MainLayout,
  },
  data() {
    return {
      showError: false,
      showSuccess: false,
      errorMessage: '',
    };
  },
  created() {
    // Listen for global errors using the emitter
    if (this.$emitter) {
      this.$emitter.on('global-error', (message) => {
        this.errorMessage = message;
        this.showError = true;
      });
      this.$emitter.on('global-success', (message) => {
        this.successMessage = message;
        this.showSuccess = true;
      });
    } else {
      console.error('Emitter is not available in App.vue');
    }
  },
  beforeUnmount() {
    // Clean up the listener
    if (this.$emitter) {
      this.$emitter.off('global-error');
      this.$emitter.off('global-success');
    }
  }
};
</script>
  
<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}
</style>