import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import vuetify from './plugins/vuetify'
import router from './routes/router'; // Import the router

createApp(App)
.use(router)  // Use the router
.use(vuetify, {
    rtl: true
}).mount('#app')
