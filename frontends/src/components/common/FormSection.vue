<!-- @/components/common/FormSection.vue -->
<template>
    <div class="form-section">
      <div class="section-header">
        <v-icon :color="iconColor" class="me-2">{{ icon }}</v-icon>
        <h3 class="section-title">{{ title }}</h3>
        
        <!-- Flexible action area -->
        <template v-if="$slots.actions || actionButton">
          <v-spacer></v-spacer>
          
          <!-- Custom actions slot -->
          <slot name="actions"></slot>
          
          <!-- Quick action button prop -->
          <v-btn v-if="actionButton" 
                 :color="actionButton.color || 'primary'" 
                 :size="actionButton.size || 'small'" 
                 :prepend-icon="actionButton.icon"
                 @click="$emit('action', actionButton.action)">
            {{ actionButton.text }}
          </v-btn>
        </template>
        
        <!-- Info text (like file size limits) -->
        <template v-if="infoText">
          <v-spacer></v-spacer>
          <span class="text-caption text-grey">{{ infoText }}</span>
        </template>
      </div>
      
      <slot></slot>
    </div>
  </template>
  
  <script>
  export default {
    name: 'FormSection',
    props: {
      title: { type: String, required: true },
      icon: { type: String, required: true },
      iconColor: { type: String, default: 'primary' },
      infoText: { type: String, default: null },
      actionButton: { type: Object, default: null }
      // actionButton: { text, icon, color, size, action }
    },
    emits: ['action']
  }
  </script>
  <style>
  @import '@/styles/modal.css';
  </style>