import { makeRequest } from '@/utils/request-util';
const PURCHASE_BASE_URL = `${import.meta.env.VITE_APP_API_PURCHASE_URL}/api`;


export async function getBusinessType() {
    return await makeRequest(`${PURCHASE_BASE_URL}/business-types`, { method: 'GET' });
}

export async function getPaymentTerms() {
    return await makeRequest(`${PURCHASE_BASE_URL}/payment-terms`, { method: 'GET' });
}

// Purchase Order API functions
export async function getPurchaseOrders(params = null) {
    try {
        let url = `${PURCHASE_BASE_URL}/orders`;
        if (params) {
            url += `?${params.toString()}`;
        }
        // return await httpService.get(url);
    } catch (error) {
        console.error('Error fetching purchase orders:', error);
        throw error;
    }
}

export async function getPurchaseOrderById(id) {
    try {
        // return await httpService.get(`${PURCHASE_BASE_URL}/orders/${id}`);
    } catch (error) {
        console.error('Error fetching purchase order:', error);
        throw error;
    }
}

export async function createPurchaseOrder(orderData) {
    try {
        // return await httpService.post(`${PURCHASE_BASE_URL}/orders`, orderData);
    } catch (error) {
        console.error('Error creating purchase order:', error);
        throw error;
    }
}

export async function updatePurchaseOrder(orderData) {
    try {
        // return await httpService.put(`${PURCHASE_BASE_URL}/orders/${orderData.id}`, orderData);
    } catch (error) {
        console.error('Error updating purchase order:', error);
        throw error;
    }
}

export async function deletePurchaseOrder(id) {
    try {
        // return await httpService.delete(`${PURCHASE_BASE_URL}/orders/${id}`);
    } catch (error) {
        console.error('Error deleting purchase order:', error);
        throw error;
    }
}

export async function markPurchaseOrderAsReceived(id) {
    try {
        // return await httpService.patch(`${PURCHASE_BASE_URL}/orders/${id}/receive`);
    } catch (error) {
        console.error('Error marking purchase order as received:', error);
        throw error;
    }
}

export async function cancelPurchaseOrder(id) {
    try {
        // return await httpService.patch(`${PURCHASE_BASE_URL}/orders/${id}/cancel`);
    } catch (error) {
        console.error('Error cancelling purchase order:', error);
        throw error;
    }
}

// Supplier API functions
export async function getSuppliers(params = null) {
    let url = `${PURCHASE_BASE_URL}/suppliers`;
    if (params) url += `?${params.toString()}`;
    return await makeRequest(`${url}`, { method: 'GET' });
}

export async function getSupplierById(id) {
    try {
        // return await httpService.get(`${PURCHASE_BASE_URL}/suppliers/${id}`);
    } catch (error) {
        console.error('Error fetching supplier:', error);
        throw error;
    }
}

export async function createSupplier(supplierData) {
    try {
        // return await httpService.post(`${PURCHASE_BASE_URL}/suppliers`, supplierData);
    } catch (error) {
        console.error('Error creating supplier:', error);
        throw error;
    }
}

export async function updateSupplier(supplierData) {
    try {
        const options = {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(supplierData)
        };
        let url = `${PURCHASE_BASE_URL}/suppliers`;
        return await makeRequest(`${url}/${supplierData.id}`, options);
    } catch (error) {
        console.error('Error updating supplier:', error);
        throw error;
    }
}

export async function deleteSupplier(id) {
    const options = {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' },
    };
    return await makeRequest(`${PURCHASE_BASE_URL}/suppliers/${id}`, options);
}

// Statistics API functions
export async function getPurchaseStats() {
    try {
        // return await httpService.get(`${PURCHASE_BASE_URL}/stats`);
    } catch (error) {
        console.error('Error fetching purchase statistics:', error);
        throw error;
    }
}

export async function getSupplierStats() {
    try {
        // return await httpService.get(`${PURCHASE_BASE_URL}/suppliers/stats`);
    } catch (error) {
        console.error('Error fetching supplier statistics:', error);
        throw error;
    }
}

// Reports API functions
export async function getPurchaseReport(params) {
    try {
        let url = `${PURCHASE_BASE_URL}/reports`;
        if (params) {
            url += `?${params.toString()}`;
        }
        // return await httpService.get(url);
    } catch (error) {
        console.error('Error fetching purchase report:', error);
        throw error;
    }
}

export async function getSupplierPerformanceReport(supplierId, params = null) {
    try {
        let url = `${PURCHASE_BASE_URL}/suppliers/${supplierId}/performance`;
        if (params) {
            url += `?${params.toString()}`;
        }
        // return await httpService.get(url);
    } catch (error) {
        console.error('Error fetching supplier performance report:', error);
        throw error;
    }
}

export async function getTopSuppliersReport(params = null) {
    try {
        let url = `${PURCHASE_BASE_URL}/reports/top-suppliers`;
        if (params) {
            url += `?${params.toString()}`;
        }
        // return await httpService.get(url);
    } catch (error) {
        console.error('Error fetching top suppliers report:', error);
        throw error;
    }
}

export async function getMostPurchasedProductsReport(params = null) {
    try {
        let url = `${PURCHASE_BASE_URL}/reports/most-purchased-products`;
        if (params) {
            url += `?${params.toString()}`;
        }
        // return await httpService.get(url);
    } catch (error) {
        console.error('Error fetching most purchased products report:', error);
        throw error;
    }
}

// File upload functions
export async function uploadPurchaseOrderAttachment(orderId, file) {
    try {
        const formData = new FormData();
        formData.append('file', file);
        // return await httpService.post(`${PURCHASE_BASE_URL}/orders/${orderId}/attachments`, formData, {
        //     headers: {
        //         'Content-Type': 'multipart/form-data'
        //     }
        // });
    } catch (error) {
        console.error('Error uploading purchase order attachment:', error);
        throw error;
    }
}

export async function deletePurchaseOrderAttachment(orderId, attachmentId) {
    try {
        // return await httpService.delete(`${PURCHASE_BASE_URL}/orders/${orderId}/attachments/${attachmentId}`);
    } catch (error) {
        console.error('Error deleting purchase order attachment:', error);
        throw error;
    }
}

export async function downloadPurchaseOrderAttachment(orderId, attachmentId) {
    try {
        // return await httpService.get(`${PURCHASE_BASE_URL}/orders/${orderId}/attachments/${attachmentId}/download`, {
        //     responseType: 'blob'
        // });
    } catch (error) {
        console.error('Error downloading purchase order attachment:', error);
        throw error;
    }
}

// Export functions
export async function exportPurchaseOrders(params = null) {
    try {
        let url = `${PURCHASE_BASE_URL}/orders/export`;
        if (params) {
            url += `?${params.toString()}`;
        }
        // return await httpService.get(url, {responseType: 'blob' });
    } catch (error) {
        console.error('Error exporting purchase orders:', error);
        throw error;
    }
}

export async function exportSuppliers(params = null) {
    try {
        let url = `${PURCHASE_BASE_URL}/suppliers/export`;
        if (params) {
            url += `?${params.toString()}`;
        }
        // return await httpService.get(url, {
        //     responseType: 'blob'
        // });
    } catch (error) {
        console.error('Error exporting suppliers:', error);
        throw error;
    }
}

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
    // Purchase Orders
    getPurchaseOrders,
    getPurchaseOrderById,
    createPurchaseOrder,
    updatePurchaseOrder,
    deletePurchaseOrder,
    markPurchaseOrderAsReceived,
    cancelPurchaseOrder,
    
    // Suppliers
    getSuppliers,
    getSupplierById,
    createSupplier,
    updateSupplier,
    deleteSupplier,
    
    // Statistics
    getPurchaseStats,
    getSupplierStats,
    
    // Reports
    getPurchaseReport,
    getSupplierPerformanceReport,
    getTopSuppliersReport,
    getMostPurchasedProductsReport,
    
    // File operations
    uploadPurchaseOrderAttachment,
    deletePurchaseOrderAttachment,
    downloadPurchaseOrderAttachment,
    
    // Export
    exportPurchaseOrders,
    exportSuppliers,
    
    // Utilities
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