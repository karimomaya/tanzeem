import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import { ar } from 'vuetify/locale'

const customArabic = {
  ...ar,
  open: 'فتح',
}

export default createVuetify({
  components,
  directives,
  theme: {
    defaultTheme: 'modernTheme',
    themes: {
      modernTheme: {
        dark: false,
        colors: {
          // Primary color family (Your requested #366091)
          primary: '#366091',
          'primary-hover': '#2f4e73',
          'primary-light': '#4a7bb7',
          'primary-dark': '#2a4d73',
          'primary-faded': '#f0f4f8',
          
          // Secondary colors inspired by modern apps
          secondary: '#6c757d',
          'secondary-light': '#8d9499',
          'secondary-dark': '#495057',
          
          // Status colors (Facebook/Instagram inspired)
          accent: '#fd7e14',
          success: '#28a745',
          'success-light': '#d4edda',
          warning: '#ffc107',
          'warning-light': '#fff3cd',
          error: '#dc3545',
          'error-light': '#f8d7da',
          info: '#17a2b8',
          'info-light': '#d1ecf1',
          
          // Purple for categories (Instagram inspired)
          purple: '#9c27b0',
          'purple-light': '#f3e5f5',
          
          // Neutral colors (Modern app aesthetic)
          'high-emphasis': '#212529',
          'medium-emphasis': '#6c757d',
          'low-emphasis': '#adb5bd',
          'disabled': '#e9ecef',
          
          // Background colors
          background: '#f8f9fa',
          surface: '#ffffff',
          'surface-light': '#f8f9fa',
          'surface-variant': '#e9ecef',
          
          // Border colors
          border: '#dee2e6',
          'border-light': '#f8f9fa',
          outline: '#ced4da',
          
          // Additional semantic colors
          'on-primary': '#ffffff',
          'on-secondary': '#ffffff',
          'on-surface': '#212529',
          'on-background': '#212529',
          'on-error': '#ffffff',
          'on-warning': '#212529',
          'on-info': '#ffffff',
          'on-success': '#ffffff',
        },
        variables: {
          // Border radius (Modern, rounded design)
          'border-radius-root': '12px',
          'border-radius-sm': '8px',
          'border-radius-md': '12px',
          'border-radius-lg': '16px',
          'border-radius-xl': '20px',
          'btn-hover-bg': '#2f4e73',
          
          // Spacing system (8px base)
          'spacing-xs': '4px',
          'spacing-sm': '8px',
          'spacing-md': '16px',
          'spacing-lg': '24px',
          'spacing-xl': '32px',
          'spacing-2xl': '48px',
          
          // Typography
          'font-size-root': '16px',
          'line-height-root': '1.5',
          
          // Shadows (Depth like Facebook/Instagram)
          'shadow-key-umbra-opacity': '0.08',
          'shadow-key-penumbra-opacity': '0.12',
          'shadow-key-ambient-opacity': '0.04',
          
          // Elevation
          'elevation-1': '0 2px 8px rgba(54, 96, 145, 0.08)',
          'elevation-2': '0 4px 16px rgba(54, 96, 145, 0.12)',
          'elevation-3': '0 8px 24px rgba(54, 96, 145, 0.16)',
          'elevation-4': '0 12px 32px rgba(54, 96, 145, 0.20)',
          
          // Component specific variables
          'card-elevation': '0',
          'btn-elevation': '0',
          'toolbar-elevation': '0',
          
          // Animation
          'transition-primary': '0.3s cubic-bezier(0.4, 0, 0.2, 1)',
          'transition-secondary': '0.2s cubic-bezier(0.4, 0, 0.2, 1)',
          // button
          'theme-primary-hover': '#2f4e73',
        }
      }
    }
  },
  defaults: {
    VCard: {
      elevation: 0,
      rounded: 'lg',
    },
    VBtn: {
      elevation: 0,
      rounded: 'lg',
      style: 'text-transform: none; font-weight: 500;',
    },
    VTextField: {
      variant: 'outlined',
      density: 'comfortable',
      rounded: 'lg',
    },
    VSelect: {
      variant: 'outlined',
      density: 'comfortable',
      rounded: 'lg',
    },
    VTextarea: {
      variant: 'outlined',
      density: 'comfortable',
      rounded: 'lg',
    },
    VAutocomplete: {
      variant: 'outlined',
      density: 'comfortable',
      rounded: 'lg',
    },
    VCombobox: {
      variant: 'outlined',
      density: 'comfortable',
      rounded: 'lg',
    },
    VDialog: {
      rounded: 'xl',
    },
    VSheet: {
      rounded: 'lg',
    },
    VAvatar: {
      rounded: 'lg',
    },
    VChip: {
      rounded: 'lg',
    },
    VBtnToggle: {
      rounded: 'lg',
      elevation: 0,
    },
    VTabs: {
      rounded: 'lg',
    },
    VTab: {
      rounded: 'lg',
    },
    VTooltip: {
      color: 'black'
    }
  },
  locale: {
    rtl: {
      ar: true, // Enable RTL for Arabic
    },
    locale: 'ar', // Default locale
    fallback: 'en',
    messages: { ar: customArabic },
  }
})