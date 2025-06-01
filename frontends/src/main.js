import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import vuetify from './plugins/vuetify'
import router from './routes/router'; // Import the router
import mitt from 'mitt';
import '@mdi/font/css/materialdesignicons.css'


const app = createApp(App)

// Create an event emitter
const emitter = mitt();

// Provide the emitter globally
app.config.globalProperties.$emitter = emitter;

export { emitter };
  
app.use(router)  // Use the router
.use(vuetify, {
    rtl: true
}).mount('#app')
