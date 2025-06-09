<template>
  <div class="table-controls">
    <div class="d-flex align-center justify-space-between">
      <div class="table-info">
        <h3 class="table-title">{{ title }}</h3>
        <p class="table-subtitle">{{ totalItems }} {{ itemLabel }} متاح</p>
      </div>

      <div class="d-flex align-center ga-3">
        <!-- Custom controls slot (e.g., items per page, filters) -->
        <slot name="controls" />

        <!-- Export button -->
        <v-btn 
          variant="outlined" 
          color="primary" 
          prepend-icon="mdi-download" 
          class="export-btn"
          :disabled="exportDisabled"
          @click="$emit('export')"
        >
          تصدير
        </v-btn>

        <!-- Add button -->
        <v-btn 
          color="primary" 
          prepend-icon="mdi-plus" 
          class="modern-add-btn" 
          @click="$emit('add')"
        >
          {{ addButtonText }}
        </v-btn>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'BaseTableHeader',
  props: {
    title: {
      type: String,
      required: true
    },
    totalItems: {
      type: Number,
      default: 0
    },
    itemLabel: {
      type: String,
      required: true
    },
    addButtonText: {
      type: String,
      required: true
    },
    exportDisabled: {
      type: Boolean,
      default: false
    }
  },
  emits: ['export', 'add']
};
</script>

<style scoped>
/* Import base styles */
@import '@/styles/product.css';

.table-controls {
  padding: 24px 32px 16px;
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  border-bottom: 1px solid #e2e8f0;
  border-radius: 12px 12px 0 0;
}

.table-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.table-title {
  font-size: 24px;
  font-weight: 700;
  color: #1a202c;
  margin: 0;
  line-height: 1.2;
}

.table-subtitle {
  font-size: 14px;
  color: #718096;
  margin: 0;
  font-weight: 500;
}

.export-btn {
  height: 40px;
  font-weight: 600;
  border-radius: 8px;
  text-transform: none;
  letter-spacing: 0;
}

.modern-add-btn {
  height: 40px;
  font-weight: 600;
  border-radius: 8px;
  text-transform: none;
  letter-spacing: 0;
  box-shadow: 0 4px 12px rgba(54, 96, 145, 0.3);
}

.modern-add-btn:hover {
  box-shadow: 0 6px 16px rgba(54, 96, 145, 0.4);
  transform: translateY(-1px);
}

/* Responsive Design */
@media (max-width: 768px) {
  .table-controls {
    padding: 16px 20px 12px;
  }

  .table-controls .d-flex {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start !important;
  }

  .table-title {
    font-size: 20px;
  }

  .table-subtitle {
    font-size: 13px;
  }
}

@media (max-width: 600px) {
  .table-controls {
    padding: 12px 16px 8px;
  }

  .table-title {
    font-size: 18px;
  }

  .export-btn,
  .modern-add-btn {
    height: 36px;
    font-size: 14px;
  }
}
</style>