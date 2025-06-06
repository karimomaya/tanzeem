import { makeRequest } from '@/utils/request-util';
const PURCHASE_BASE_URL = `${import.meta.env.VITE_APP_API_PURCHASE_URL}/api`;


// Utility functions for purchase order validation
export function validatePurchaseOrder(orderData) {
    const errors = [];
    
    if (!orderData.orderNumber || orderData.orderNumber.trim().length < 3) {
        errors.push('رقم الأمر مطلوب ويجب أن يكون 3 أحرف على الأقل');
    }
    
    if (!orderData.orderDate) {
        errors.push('تاريخ الأمر مطلوب');
    }
    
    if (!orderData.supplierId) {
        errors.push('المورد مطلوب');
    }
    
    if (!orderData.items || orderData.items.length === 0) {
        errors.push('يجب إضافة منتج واحد على الأقل');
    } else {
        orderData.items.forEach((item, index) => {
            if (!item.productId) {
                errors.push(`المنتج مطلوب في العنصر رقم ${index + 1}`);
            }
            if (!item.quantity || item.quantity <= 0) {
                errors.push(`الكمية يجب أن تكون أكبر من صفر في العنصر رقم ${index + 1}`);
            }
            if (item.unitPrice === null || item.unitPrice === undefined || item.unitPrice < 0) {
                errors.push(`سعر الوحدة يجب أن يكون أكبر من أو يساوي صفر في العنصر رقم ${index + 1}`);
            }
        });
    }
    
    return errors;
}

export function validateSupplier(supplierData) {
    const errors = [];
    
    if (!supplierData.name || supplierData.name.trim().length < 2) {
        errors.push('اسم المورد مطلوب ويجب أن يكون حرفين على الأقل');
    }
    
    if (supplierData.email && !isValidEmail(supplierData.email)) {
        errors.push('البريد الإلكتروني غير صحيح');
    }
    
    if (supplierData.phone && !isValidPhone(supplierData.phone)) {
        errors.push('رقم الهاتف غير صحيح');
    }
    
    return errors;
}

function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

function isValidPhone(phone) {
    const phoneRegex = /^[\+]?[1-9][\d]{0,15}$/;
    return phoneRegex.test(phone.replace(/[\s\-\(\)]/g, ''));
}

// Calculate purchase order totals
export function calculatePurchaseOrderTotals(items) {
    if (!items || !Array.isArray(items)) {
        return {
            totalQuantity: 0,
            totalAmount: 0,
            itemCount: 0
        };
    }
    
    const totalQuantity = items.reduce((sum, item) => sum + (item.quantity || 0), 0);
    const totalAmount = items.reduce((sum, item) => {
        const itemTotal = (item.quantity || 0) * (item.unitPrice || 0);
        return sum + itemTotal;
    }, 0);
    
    return {
        totalQuantity,
        totalAmount,
        itemCount: items.length
    };
}

// Format purchase order status
export function formatPurchaseOrderStatus(status) {
    const statusMap = {
        'PENDING': 'في الانتظار',
        'RECEIVED': 'تم الاستلام',
        'CANCELLED': 'ملغي'
    };
    return statusMap[status] || status;
}

// Get status color for UI
export function getPurchaseOrderStatusColor(status) {
    const colorMap = {
        'PENDING': 'warning',
        'RECEIVED': 'success',
        'CANCELLED': 'error'
    };
    return colorMap[status] || 'grey';
}

// Check if purchase order can be edited
export function canEditPurchaseOrder(status) {
    return status === 'PENDING';
}

// Check if purchase order can be received
export function canReceivePurchaseOrder(status) {
    return status === 'PENDING';
}

// Check if purchase order can be cancelled
export function canCancelPurchaseOrder(status) {
    return status === 'PENDING';
}

// Generate unique order number
export function generateOrderNumber(prefix = 'PO') {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    const seconds = String(now.getSeconds()).padStart(2, '0');
    
    return `${prefix}-${year}${month}${day}-${hours}${minutes}${seconds}`;
}

// Format supplier data for display
export function formatSupplierForDisplay(supplier) {
    return {
        ...supplier,
        displayName: supplier.name,
        contactInfo: [
            supplier.contactPerson,
            supplier.phone,
            supplier.email
        ].filter(Boolean).join(' • '),
        statusText: supplier.isActive ? 'نشط' : 'غير نشط',
        statusColor: supplier.isActive ? 'success' : 'error'
    };
}

export default {
    validatePurchaseOrder,
    validateSupplier,
    calculatePurchaseOrderTotals,
    formatPurchaseOrderStatus,
    getPurchaseOrderStatusColor,
    canEditPurchaseOrder,
    canReceivePurchaseOrder,
    canCancelPurchaseOrder,
    generateOrderNumber,
    formatSupplierForDisplay
};