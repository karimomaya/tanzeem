// /utils/status-util.js
// Centralized status management for all components

/**
 * Product Status Configuration
 */
export const PRODUCT_STATUS = {
  ACTIVE: {
    value: 'ACTIVE',
    text: 'نشط',
    color: 'success',
    icon: 'mdi-check-circle'
  },
  LOW_STOCK: {
    value: 'LOW_STOCK',
    text: 'مخزون منخفض',
    color: 'warning',
    icon: 'mdi-alert-circle'
  },
  OUT_OF_STOCK: {
    value: 'OUT_OF_STOCK',
    text: 'نفد المخزون',
    color: 'error',
    icon: 'mdi-close-circle'
  },
  DISABLED: {
    value: 'DISABLED',
    text: 'معطل',
    color: 'grey',
    icon: 'mdi-pause-circle'
  }
};

/**
 * Category/Supplier Status Configuration  
 */
export const ENTITY_STATUS = {
  ACTIVE: {
    value: true,
    text: 'نشط',
    color: 'success',
    icon: 'mdi-check-circle'
  },
  INACTIVE: {
    value: false,
    text: 'غير نشط',
    color: 'error',
    icon: 'mdi-close-circle'
  }
};

/**
 * Purchase Order Status Configuration
 */
export const ORDER_STATUS = {
  PENDING: {
    value: 'PENDING',
    text: 'في الانتظار',
    color: 'warning',
    icon: 'mdi-clock-outline'
  },
  CONFIRMED: {
    value: 'CONFIRMED',
    text: 'مؤكد',
    color: 'info',
    icon: 'mdi-calendar-check'
  },
  RECEIVED: {
    value: 'RECEIVED',
    text: 'تم الاستلام',
    color: 'success',
    icon: 'mdi-check-circle'
  },
  DELIVERED: {
    value: 'DELIVERED',
    text: 'تم التسليم',
    color: 'success',
    icon: 'mdi-truck-check'
  },
  CANCELLED: {
    value: 'CANCELLED',
    text: 'ملغي',
    color: 'error',
    icon: 'mdi-close-circle'
  }
};

/**
 * Status Filter Options for dropdowns
 */
export const STATUS_FILTER_OPTIONS = {
  product: [
    { title: 'الكل', value: 'all' },
    { title: PRODUCT_STATUS.ACTIVE.text, value: PRODUCT_STATUS.ACTIVE.value },
    { title: PRODUCT_STATUS.LOW_STOCK.text, value: PRODUCT_STATUS.LOW_STOCK.value },
    { title: PRODUCT_STATUS.OUT_OF_STOCK.text, value: PRODUCT_STATUS.OUT_OF_STOCK.value },
    { title: PRODUCT_STATUS.DISABLED.text, value: PRODUCT_STATUS.DISABLED.value }
  ],
  entity: [
    { title: 'الكل', value: 'all' },
    { title: ENTITY_STATUS.ACTIVE.text, value: 'true' },
    { title: ENTITY_STATUS.INACTIVE.text, value: 'false' }
  ],
  order: [
    { title: 'الكل', value: 'all' },
    { title: ORDER_STATUS.PENDING.text, value: ORDER_STATUS.PENDING.value },
    { title: ORDER_STATUS.CONFIRMED.text, value: ORDER_STATUS.CONFIRMED.value },
    { title: ORDER_STATUS.RECEIVED.text, value: ORDER_STATUS.RECEIVED.value },
    { title: ORDER_STATUS.CANCELLED.text, value: ORDER_STATUS.CANCELLED.value }
  ]
};

/**
 * Get product status based on business logic
 */
export function getProductStatus(stock, minimumStock, isActive, categoryActive) {
  if (!isActive || !categoryActive) return PRODUCT_STATUS.DISABLED.value;
  if (stock === 0) return PRODUCT_STATUS.OUT_OF_STOCK.value;
  if (stock <= minimumStock) return PRODUCT_STATUS.LOW_STOCK.value;
  return PRODUCT_STATUS.ACTIVE.value;
}

/**
 * Get status configuration by type and value
 */
export function getStatusConfig(type, statusValue) {
  const configs = {
    product: PRODUCT_STATUS,
    entity: ENTITY_STATUS,
    order: ORDER_STATUS
  };

  const config = configs[type];
  if (!config) return null;

  // For entity status (boolean values)
  if (type === 'entity') {
    return statusValue ? ENTITY_STATUS.ACTIVE : ENTITY_STATUS.INACTIVE;
  }

  // For other status types (string values)
  return Object.values(config).find(status => status.value === statusValue) || null;
}

/**
 * Get status text
 */
export function getStatusText(type, statusValue) {
  const config = getStatusConfig(type, statusValue);
  return config?.text || statusValue;
}

/**
 * Get status color
 */
export function getStatusColor(type, statusValue) {
  const config = getStatusConfig(type, statusValue);
  return config?.color || 'grey';
}

/**
 * Get status icon
 */
export function getStatusIcon(type, statusValue) {
  const config = getStatusConfig(type, statusValue);
  return config?.icon || 'mdi-help-circle';
}

/**
 * Get product-specific status helpers (backward compatibility)
 */
export function getProductStatusText(statusValue) {
  return getStatusText('product', statusValue);
}

export function getProductStatusColor(statusValue) {
  return getStatusColor('product', statusValue);
}

export function getProductStatusIcon(statusValue) {
  return getStatusIcon('product', statusValue);
}

/**
 * Get entity status helpers (for categories/suppliers)
 */
export function getEntityStatusText(isActive) {
  return getStatusText('entity', isActive);
}

export function getEntityStatusColor(isActive) {
  return getStatusColor('entity', isActive);
}

export function getEntityStatusIcon(isActive) {
  return getStatusIcon('entity', isActive);
}

/**
 * Get order status helpers
 */
export function getOrderStatusText(statusValue) {
  return getStatusText('order', statusValue);
}

export function getOrderStatusColor(statusValue) {
  return getStatusColor('order', statusValue);
}

export function getOrderStatusIcon(statusValue) {
  return getStatusIcon('order', statusValue);
}