import { makeRequest } from '@/utils/request-util';

const url = `${import.meta.env.VITE_APP_API_PRODUCT_URL}/api`;

// ===========================================
// API Functions
// ===========================================

export async function saveCategory(data) {
    const options = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    };
    return await makeRequest(`${url}/categories`, options);
}

export async function saveProduct(data) {
    const options = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    };
    return await makeRequest(`${url}/products`, options);
}

export async function updateProduct(data) {
    const options = {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    };
    return await makeRequest(`${url}/products/${data.id}`, options);
}

export async function updateCategory(data) {
    const options = {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    };
    return await makeRequest(`${url}/categories/${data.id}`, options);
}

export async function getProducts(params) {
    return await makeRequest(`${url}/products?${params.toString()}`, { method: 'GET' });
}

export async function getCategories(params) {
    return await makeRequest(`${url}/categories?${params.toString()}`, { method: 'GET' });
}

export async function deleteCategory(id) {
    const options = {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' },
    };
    return await makeRequest(`${url}/categories/${id}`, options);
}

export async function deleteProduct(id) {
    const options = {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' },
    };
    return await makeRequest(`${url}/products/${id}`, options);
}

// ===========================================
// Stock Utility Functions
// ===========================================

export function getStockLevel(stock, minimumStock) {
    const currentStock = stock || 0;
    const minStock = minimumStock || 0;
    
    if (currentStock === 0) return 'empty';
    if (currentStock <= minStock) return 'low';
    if (currentStock <= minStock * 2) return 'medium';
    return 'high';
}

export function getStockText(stock, minimumStock) {
    const level = getStockLevel(stock, minimumStock);
    const texts = {
        'empty': 'نفد المخزون',
        'low': 'مخزون منخفض', 
        'medium': 'مخزون متوسط',
        'high': 'متوفر'
    };
    return texts[level] || 'غير محدد';
}

export function getStockColor(stock, minimumStock) {
    const level = getStockLevel(stock, minimumStock);
    const colors = {
        'empty': 'error',
        'low': 'warning',
        'medium': 'info', 
        'high': 'success'
    };
    return colors[level] || 'grey';
}

export function getStockIcon(stock, minimumStock) {
    const level = getStockLevel(stock, minimumStock);
    const icons = {
        'empty': 'mdi-close-circle',
        'low': 'mdi-alert-circle',
        'medium': 'mdi-minus-circle',
        'high': 'mdi-check-circle'
    };
    return icons[level] || 'mdi-help-circle';
}

export function getStockStatus(stock, minimumStock) {
    return getStockText(stock, minimumStock);
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
            `"${getStockText(product.stock, product.minimumStock)}"`
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
// Validation Functions
// ===========================================

export function validateProductData(product) {
    const errors = [];
    
    if (!product.name || product.name.trim() === '') {
        errors.push('اسم المنتج مطلوب');
    }
    
    if (!product.sku || product.sku.trim() === '') {
        errors.push('رمز المنتج (SKU) مطلوب');
    }
    
    if (!product.price || product.price <= 0) {
        errors.push('سعر المنتج يجب أن يكون أكبر من صفر');
    }
    
    if (product.stock < 0) {
        errors.push('كمية المخزون لا يمكن أن تكون سالبة');
    }
    
    if (!product.category || !product.category.id) {
        errors.push('تصنيف المنتج مطلوب');
    }
    
    return {
        isValid: errors.length === 0,
        errors
    };
}

// ===========================================
// Search and Filter Functions
// ===========================================

export function filterProducts(products, filters) {
    let filtered = [...products];
    
    // Search by name, SKU, or description
    if (filters.searchTerm) {
        const term = filters.searchTerm.toLowerCase();
        filtered = filtered.filter(product => 
            product.name.toLowerCase().includes(term) ||
            product.sku.toLowerCase().includes(term) ||
            (product.description && product.description.toLowerCase().includes(term))
        );
    }
    
    // Filter by status
    if (filters.statusFilter && filters.statusFilter !== 'all') {
        filtered = filtered.filter(product => {
            const level = getStockLevel(product.stock, product.minimumStock);
            return level === filters.statusFilter;
        });
    }
    
    // Filter by category
    if (filters.categoryFilter && filters.categoryFilter !== 'all') {
        filtered = filtered.filter(product => 
            product.category && product.category.id === filters.categoryFilter
        );
    }
    
    // Filter by price range
    if (filters.priceRange) {
        const { min, max } = filters.priceRange;
        if (min !== null && min !== undefined) {
            filtered = filtered.filter(product => product.price >= min);
        }
        if (max !== null && max !== undefined) {
            filtered = filtered.filter(product => product.price <= max);
        }
    }
    
    return filtered;
}

export function sortProducts(products, sortBy) {
    if (!sortBy || sortBy.length === 0) return products;
    
    return [...products].sort((a, b) => {
        for (const sort of sortBy) {
            const { key, order } = sort;
            let aValue = a[key];
            let bValue = b[key];
            
            // Handle nested properties (like category.name)
            if (key.includes('.')) {
                const keys = key.split('.');
                aValue = keys.reduce((obj, k) => obj?.[k], a);
                bValue = keys.reduce((obj, k) => obj?.[k], b);
            }
            
            // Handle different data types
            if (typeof aValue === 'string' && typeof bValue === 'string') {
                aValue = aValue.toLowerCase();
                bValue = bValue.toLowerCase();
            }
            
            if (aValue < bValue) return order === 'asc' ? -1 : 1;
            if (aValue > bValue) return order === 'asc' ? 1 : -1;
        }
        return 0;
    });
}

// ===========================================
// Image Utility Functions
// ===========================================

export function getImageUrl(product, size = 'medium') {
    if (!product.imageUrl) return null;
    
    // If using a CDN or image service, you can modify URLs for different sizes
    const sizeParams = {
        small: '?w=150&h=150',
        medium: '?w=300&h=300',
        large: '?w=600&h=600'
    };
    
    return product.imageUrl + (sizeParams[size] || '');
}

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
// Analytics Functions
// ===========================================

export function calculateStockAnalytics(products) {
    const analytics = {
        total: products.length,
        inStock: 0,
        lowStock: 0,
        outOfStock: 0,
        totalValue: 0,
        averagePrice: 0,
        categories: {}
    };
    
    products.forEach(product => {
        const level = getStockLevel(product.stock, product.minimumStock);
        
        switch (level) {
            case 'empty':
                analytics.outOfStock++;
                break;
            case 'low':
                analytics.lowStock++;
                break;
            default:
                analytics.inStock++;
        }
        
        analytics.totalValue += (product.price || 0) * (product.stock || 0);
        
        // Category analytics
        const categoryName = product.category?.name || 'غير مصنف';
        if (!analytics.categories[categoryName]) {
            analytics.categories[categoryName] = {
                count: 0,
                totalValue: 0,
                totalStock: 0
            };
        }
        analytics.categories[categoryName].count++;
        analytics.categories[categoryName].totalValue += (product.price || 0) * (product.stock || 0);
        analytics.categories[categoryName].totalStock += product.stock || 0;
    });
    
    analytics.averagePrice = analytics.total > 0 
        ? products.reduce((sum, p) => sum + (p.price || 0), 0) / analytics.total 
        : 0;
    
    return analytics;
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


export function formatPrice(price) {
    return new Intl.NumberFormat('ar-SA', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    }).format(price);
}

export function truncateText(text, maxLength) {
    if (!text) return '';
    return text.length > maxLength ? text.substring(0, maxLength) + '...' : text;
}

export function formatDateText(dateString, type) {
    if (!dateString) return '';

    const date = new Date(dateString);
    const now = new Date();
    const diffInMs = now - date;
    const diffInMinutes = Math.floor(diffInMs / (1000 * 60));
    const diffInHours = Math.floor(diffInMs / (1000 * 60 * 60));
    const diffInDays = Math.floor(diffInMs / (1000 * 60 * 60 * 24));
    const diffInWeeks = Math.floor(diffInDays / 7);
    const diffInMonths = Math.floor(diffInDays / 30);
    const diffInYears = Math.floor(diffInDays / 365);

    const prefix = type === 'created' ? 'تم الإنشاء' : 'تم التحديث';

    // Less than a minute
    if (diffInMinutes < 1) {
        return `${prefix} الآن`;
    }
    // Less than an hour
    else if (diffInMinutes < 60) {
        return `${prefix} منذ ${diffInMinutes} دقيقة`;
    }
    // Less than a day
    else if (diffInHours < 24) {
        return `${prefix} منذ ${diffInHours} ساعة`;
    }
    // Less than a week
    else if (diffInDays < 7) {
        return `${prefix} منذ ${diffInDays} يوم`;
    }
    // Less than a month
    else if (diffInWeeks < 4) {
        return `${prefix} منذ ${diffInWeeks} أسبوع`;
    }
    // Less than a year
    else if (diffInMonths < 12) {
        return `${prefix} منذ ${diffInMonths} شهر`;
    }
    // More than a year
    else {
        return `${prefix} منذ ${diffInYears} سنة`;
    }
}