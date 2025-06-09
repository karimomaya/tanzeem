<template>
    <div class="meta-data">
      <!-- Created Information -->
      <div class="meta-item" v-if="showCreated && (createdAt || createdBy)">
        <v-icon size="12" class="me-1">mdi-calendar-plus</v-icon>
        <span class="meta-text">{{ formatDate(createdAt, 'created') }}</span>
        <span v-if="createdBy" class="meta-by"> بواسطة {{ getDisplayName(createdBy) }}</span>
      </div>
  
      <!-- Updated Information -->
      <div 
        v-if="showUpdated && updatedAt && isUpdatedRecently(createdAt, updatedAt)" 
        class="meta-item"
      >
        <v-icon size="12" class="me-1">mdi-calendar-edit</v-icon>
        <span class="meta-text">{{ formatDate(updatedAt, 'updated') }}</span>
        <span v-if="updatedBy" class="meta-by"> بواسطة {{ getDisplayName(updatedBy) }}</span>
      </div>
  
      <!-- Custom Additional Information -->
      <div v-if="$slots.additional" class="meta-item">
        <slot name="additional" />
      </div>
    </div>
  </template>
  
  <script>
  import { formatDate } from '@/utils/date-util';
  import { isUpdatedRecently } from '@/utils/date-util';
  
  export default {
    name: 'MetaDataDisplay',
    props: {
      // Date props
      createdAt: {
        type: String,
        default: null
      },
      updatedAt: {
        type: String,
        default: null
      },
      
      // User props - can be string (username) or object (user info)
      createdBy: {
        type: [String, Object],
        default: null
      },
      updatedBy: {
        type: [String, Object],  
        default: null
      },
  
      // Display control props
      showCreated: {
        type: Boolean,
        default: true
      },
      showUpdated: {
        type: Boolean,
        default: true
      },
  
      // Styling props
      compact: {
        type: Boolean,
        default: false
      },
      maxItems: {
        type: Number,
        default: 2
      }
    },
    methods: {
      formatDate,
      isUpdatedRecently,
      
      /**
       * Extract display name from user object or return string as-is
       */
      getDisplayName(user) {
        if (!user) return '';
        
        if (typeof user === 'string') {
          return user;
        }
        
        if (typeof user === 'object') {
          return user.name || user.username || user.displayName || user.email || 'مستخدم غير معروف';
        }
        
        return String(user);
      }
    }
  };
  </script>
  
  <style scoped>
  .meta-data {
    display: flex;
    flex-direction: column;
    gap: 2px;
    margin-top: 4px;
  }
  
  .meta-item {
    /* display: flex; */
    /* align-items: start; */
    font-size: 11px;
    color: #718096;
    line-height: 1.3;
  }
  
  .meta-text {
    font-weight: 500;
    color: #4a5568;
  }
  
  .meta-by {
    margin-right: 4px;
    font-weight: 400;
    color: #718096;
  }
  
  /* Compact variant */
  .meta-data.compact .meta-item {
    font-size: 10px;
  }
  
  .meta-data.compact {
    gap: 1px;
  }
  
  /* Icon styling */
  .meta-item .v-icon {
    color: #a0aec0;
    margin-left: 2px;
  }
  
  /* Responsive design */
  @media (max-width: 768px) {
    .meta-item {
      font-size: 10px;
    }
    
    .meta-by {
      display: none; /* Hide "by user" on mobile to save space */
    }
  }
  
  @media (max-width: 600px) {
    .meta-data {
      margin-top: 2px;
    }
    
    .meta-item {
      font-size: 9px;
    }
  }
  
  /* Print styles */
  @media print {
    .meta-data {
      color: #000 !important;
    }
    
    .meta-item {
      color: #000 !important;
    }
    
    .meta-text {
      color: #000 !important;
    }
    
    .meta-by {
      color: #666 !important;
    }
  }
  </style>