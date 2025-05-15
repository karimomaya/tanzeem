import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import vuetify from './plugins/vuetify'

createApp(App).use(vuetify, {
    rtl: true
}).mount('#app')
