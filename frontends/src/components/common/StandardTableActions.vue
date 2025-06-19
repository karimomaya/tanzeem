<template>
    <v-menu>
      <template v-slot:activator="{ props }">
        <v-btn 
          v-bind="props" 
          icon="mdi-dots-vertical" 
          variant="text" 
          size="small"
          class="actions-trigger"
        />
      </template>
      
      <v-list density="compact" class="actions-menu">
        <template v-for="(action, index) in processedActions" :key="index">
          <!-- Divider -->
          <v-divider v-if="action.divider" />
          
          <!-- Action Item -->
          <v-list-item 
            v-else
            @click="handleAction(action)"
            :class="['action-item', { 'action-danger': action.danger }]"
            :disabled="action.disabled"
          >
            <template v-slot:prepend>
              <v-icon :color="action.color">{{ action.icon }}</v-icon>
            </template>
            <v-list-item-title :class="{ 'text-error': action.danger }">
              {{ action.text }}
            </v-list-item-title>
          </v-list-item>
        </template>
      </v-list>
    </v-menu>
  </template>
  
  <script>
  export default {
    name: 'StandardTableActions',
    props: {
      actions: {
        type: Array,
        required: true,
        validator: (actions) => {
          return actions.every(action => 
            action.divider || (action.key && action.icon && action.text)
          );
        }
      },
      item: {
        type: Object,
        required: true
      }
    },
    emits: ['action'],
    computed: {
      processedActions() {
        return this.actions.filter(action => {
          // Check if action should be shown based on condition
          if (action.condition) {
            return action.condition(this.item);
          }

          // Show divider
          if (action.divider) return true;
          
          return true;
        });
      }
    },
    methods: {
      handleAction(action) {
        if (action.disabled) return;
        
        this.$emit('action', {
          type: action.key,
          item: this.item,
          action: action
        });
      }
    }
  };
  </script>
  
  <style scoped>
  .actions-trigger {
    color: #718096 !important;
    transition: all 0.2s ease;
  }
  
  .actions-trigger:hover {
    color: #366091 !important;
    background: rgba(54, 96, 145, 0.1) !important;
  }
  
  .actions-menu {
    border-radius: 12px !important;
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15) !important;
    border: 1px solid #e2e8f0 !important;
    min-width: 180px;
    overflow: hidden;
  }
  
  .action-item {
    border-radius: 8px !important;
    margin: 4px !important;
    transition: background-color 0.2s ease;
    cursor: pointer;
  }
  
  .action-item:hover {
    background: rgba(54, 96, 145, 0.05) !important;
  }
  
  .action-item.action-danger:hover {
    background: rgba(229, 62, 62, 0.05) !important;
  }
  
  .action-item:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
  
  .action-item:disabled:hover {
    background: transparent !important;
  }
  
  .v-list-item-title {
    font-size: 14px;
    font-weight: 500;
  }
  
  .v-icon {
    font-size: 16px !important;
  }
  
  /* Divider styling */
  .v-divider {
    margin: 4px 8px;
    opacity: 0.3;
  }
  </style>
  