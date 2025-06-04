const stockMeta = {
    empty: { text: 'نفد المخزون', color: 'error', icon: 'mdi-close-circle' },
    low: { text: 'مخزون منخفض', color: 'warning', icon: 'mdi-alert-circle' },
    medium: { text: 'مخزون متوسط', color: 'info', icon: 'mdi-minus-circle' },
    high: { text: 'متوفر', color: 'success', icon: 'mdi-check-circle' }
};

export function getStockMeta(stock, minimumStock) {
    const level = getStockLevel(stock, minimumStock);
    return stockMeta[level] || { text: 'غير محدد', color: 'grey', icon: 'mdi-help-circle' };
}
// ===========================================
// Stock Utility Functions
// ===========================================

export function getStockLevel(stock, minimumStock, thresholds = { low: 1, medium: 2 }) {
    const currentStock = stock || 0;
    const minStock = minimumStock || 0;

    if (minStock === 0) return currentStock > 0 ? 'high' : 'empty';
    if (currentStock <= minStock * thresholds.low) return 'low';
    if (currentStock <= minStock * thresholds.medium) return 'medium';
    return 'high';
}

export function getStockPercentage(stock, minimumStock) {
    const currentStock = stock || 0;
    const minStock = minimumStock || 0;
    
    if (minStock === 0) return currentStock > 0 ? 100 : 0;
    
    const maxStock = minStock * 3; // Assume max stock is 3x minimum
    return Math.min((currentStock / maxStock) * 100, 100);
}

// ===========================================
// Product Action Functions
// ===========================================

export function createDuplicateProduct(product) {
    return { 
        ...product, 
        id: null, 
        name: `${product.name} - نسخة`,
        sku: `${product.sku}-COPY`
    };
}

// ===========================================
// Export Utility Functions
// ===========================================

export function exportProductsToCSV(products, filename = null) {
    const headers = ['الاسم', 'SKU', 'التصنيف', 'السعر', 'المخزون', 'الحالة'];
    const csvContent = [
        headers.join(','),
        ...products.map(product => [
            `"${product.name}"`,
            `"${product.sku}"`,
            `"${product.category?.name || ''}"`,
            product.price || 0,
            product.stock || 0,
            `"${getStockMeta(product.stock, product.minimumStock).text}"`
        ].join(','))
    ].join('\n');

    // Add UTF-8 BOM for Arabic text support
    const bom = '\uFEFF';
    const blob = new Blob([bom + csvContent], { type: 'text/csv;charset=utf-8;' });
    
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    link.setAttribute('href', url);
    link.setAttribute('download', filename || `products_${new Date().getTime()}.csv`);
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
}

// ===========================================
// Pagination Utility Functions
// ===========================================

export function calculatePaginationInfo(page, itemsPerPage, totalItems) {
    const totalPages = Math.ceil(totalItems / itemsPerPage);
    const startItem = totalItems === 0 ? 0 : (page - 1) * itemsPerPage + 1;
    const endItem = Math.min(page * itemsPerPage, totalItems);
    
    return {
        totalPages,
        startItem,
        endItem,
        currentPage: page,
        hasNextPage: page < totalPages,
        hasPreviousPage: page > 1
    };
}

export function getPaginationVisible(screenSize) {
    // Responsive pagination visibility based on screen size
    const breakpoints = {
        xs: 3,
        sm: 5,
        md: 7,
        lg: 9,
        xl: 11
    };
    return breakpoints[screenSize] || 7;
}
// ===========================================
// Image Utility Functions
// ===========================================

export function validateImageFile(file) {
    const allowedTypes = ['image/jpeg', 'image/png', 'image/webp'];
    const maxSize = 5 * 1024 * 1024; // 5MB
    
    const errors = [];
    
    if (!allowedTypes.includes(file.type)) {
        errors.push('نوع الملف غير مدعوم. يُسمح فقط بـ JPEG، PNG، وWebP');
    }
    
    if (file.size > maxSize) {
        errors.push('حجم الملف كبير جداً. الحد الأقصى 5 ميجابايت');
    }
    
    return {
        isValid: errors.length === 0,
        errors
    };
}

// ===========================================
// System Functions
// ===========================================


export function isUpdatedRecently(createdAt, updatedAt) {
    if (!createdAt || !updatedAt) return false;

    const created = new Date(createdAt);
    const updated = new Date(updatedAt);

    // Show update info if updated more than 1 minute after creation
    return (updated - created) > (1000 * 60);
}

export function truncateText(text, maxLength) {
    if (!text) return '';
    return text.length > maxLength ? text.substring(0, maxLength) + '...' : text;
}

export function getProductStatus(stock, minimumStock, isActive, categoryActive) {
    if (!isActive || !categoryActive) return 'DISABLED';
    if (stock === 0) return 'OUT_OF_STOCK';
    if (stock <= minimumStock) return 'LOW_STOCK';
    return 'ACTIVE';
}

export function getProductStatusText(status) {
    const statusMap = {
        'ACTIVE': 'نشط',
        'LOW_STOCK': 'مخزون منخفض',
        'OUT_OF_STOCK': 'نفد المخزون',
        'DISABLED': 'معطل'
    };
    return statusMap[status] || 'غير محدد';
}

export function getProductStatusColor(status) {
    const colorMap = {
        'ACTIVE': 'success',
        'LOW_STOCK': 'warning',
        'OUT_OF_STOCK': 'error',
        'DISABLED': 'grey'
    };
    return colorMap[status] || 'grey';
}

export function getProductStatusIcon(status) {
    const iconMap = {
        'ACTIVE': 'mdi-check-circle',
        'LOW_STOCK': 'mdi-alert-circle',
        'OUT_OF_STOCK': 'mdi-close-circle',
        'DISABLED': 'mdi-minus-circle'
    };
    return iconMap[status] || 'mdi-help-circle';
}