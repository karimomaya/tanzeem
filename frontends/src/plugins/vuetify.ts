import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'

export default createVuetify({
  components,
  directives,
  locale: {
    rtl: {
      ar: true, // Enable RTL for Arabic
    },
    locale: 'ar', // Default locale
  }
})