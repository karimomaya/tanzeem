// Enhanced formatDateTime function inspired by formatDate
export function formatDateTime(dateString, options = {}) {
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

    // Default options
    const {
        type = null, // 'created', 'updated', or null for no prefix
        includeTime = false, // Whether to include exact time for recent dates
        showSeconds = false, // Whether to show seconds for very recent times
        format = 'relative' // 'relative', 'absolute', or 'mixed'
    } = options;

    // Get prefix based on type
    const getPrefix = (type) => {
        switch (type) {
            case 'created': return 'تم الإنشاء';
            case 'updated': return 'تم التحديث';
            case 'ordered': return 'تم الطلب';
            case 'received': return 'تم الاستلام';
            case 'cancelled': return 'تم الإلغاء';
            default: return '';
        }
    };

    const prefix = getPrefix(type);
    const prefixText = prefix ? `${prefix} ` : '';

    // For absolute format, return formatted date
    if (format === 'absolute') {
        const arabicDate = new Intl.DateTimeFormat('ar-SA', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
            hour12: true
        }).format(date);
        return `${prefixText}${arabicDate}`;
    }

    // Calculate seconds for very recent times
    const diffInSeconds = Math.floor(diffInMs / 1000);

    // Less than 30 seconds
    if (diffInSeconds < 30) {
        return `${prefixText}الآن`;
    }
    // Less than a minute but more than 30 seconds
    else if (diffInMinutes < 1 && showSeconds) {
        return `${prefixText}منذ ${diffInSeconds} ثانية`;
    }
    // Less than a minute
    else if (diffInMinutes < 1) {
        return `${prefixText}منذ أقل من دقيقة`;
    }
    // Less than an hour
    else if (diffInMinutes < 60) {
        const timeText = includeTime && diffInMinutes < 30 ? 
            ` (${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')})` : '';
        return `${prefixText}منذ ${diffInMinutes} دقيقة${timeText}`;
    }
    // Less than a day
    else if (diffInHours < 24) {
        const timeText = includeTime ? 
            ` (${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')})` : '';
        return `${prefixText}منذ ${diffInHours} ساعة${timeText}`;
    }
    // Less than a week
    else if (diffInDays < 7) {
        const timeText = includeTime && diffInDays < 3 ? 
            ` (${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')})` : '';
        
        if (format === 'mixed' && diffInDays <= 2) {
            // Show day name for recent days
            const dayNames = ['الأحد', 'الاثنين', 'الثلاثاء', 'الأربعاء', 'الخميس', 'الجمعة', 'السبت'];
            const dayName = dayNames[date.getDay()];
            return `${prefixText}${dayName}${timeText}`;
        }
        
        return `${prefixText}منذ ${diffInDays} يوم${timeText}`;
    }
    // Less than a month
    else if (diffInWeeks < 4) {
        const weekText = diffInWeeks === 1 ? 'أسبوع' : `${diffInWeeks} أسبوع`;
        
        if (format === 'mixed') {
            // Show exact date for weeks
            const arabicDate = new Intl.DateTimeFormat('ar-SA', {
                month: 'long',
                day: 'numeric'
            }).format(date);
            return `${prefixText}${arabicDate}`;
        }
        
        return `${prefixText}منذ ${weekText}`;
    }
    // Less than a year
    else if (diffInMonths < 12) {
        const monthText = diffInMonths === 1 ? 'شهر' : `${diffInMonths} شهر`;
        
        if (format === 'mixed') {
            // Show month and day for months
            const arabicDate = new Intl.DateTimeFormat('ar-SA', {
                month: 'long',
                day: 'numeric'
            }).format(date);
            return `${prefixText}${arabicDate}`;
        }
        
        return `${prefixText}منذ ${monthText}`;
    }
    // More than a year
    else {
        const yearText = diffInYears === 1 ? 'سنة' : `${diffInYears} سنة`;
        
        if (format === 'mixed') {
            // Show full date for years
            const arabicDate = new Intl.DateTimeFormat('ar-SA', {
                year: 'numeric',
                month: 'long',
                day: 'numeric'
            }).format(date);
            return `${prefixText}${arabicDate}`;
        }
        
        return `${prefixText}منذ ${yearText}`;
    }
}

// Enhanced formatDate function (backwards compatible)
export function formatDate(dateString, type) {
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

// Utility function for formatting time only
export function formatTimeOnly(dateString) {
    if (!dateString) return '';
    
    const date = new Date(dateString);
    return new Intl.DateTimeFormat('ar-SA', {
        hour: '2-digit',
        minute: '2-digit',
        hour12: true
    }).format(date);
}

// Utility function for formatting date only
export function formatDateOnly(dateString, includeYear = true) {
    if (!dateString) return '';
    
    const date = new Date(dateString);
    const options = {
        month: 'long',
        day: 'numeric'
    };
    
    if (includeYear) {
        options.year = 'numeric';
    }
    
    return new Intl.DateTimeFormat('ar-SA', options).format(date);
}

// Utility function for short date format
export function formatShortDate(dateString) {
    if (!dateString) return '';
    
    const date = new Date(dateString);
    return new Intl.DateTimeFormat('ar-SA', {
        year: '2-digit',
        month: '2-digit',
        day: '2-digit'
    }).format(date);
}

// Function to check if date is today
export function isToday(dateString) {
    if (!dateString) return false;
    
    const date = new Date(dateString);
    const today = new Date();
    
    return date.getDate() === today.getDate() &&
           date.getMonth() === today.getMonth() &&
           date.getFullYear() === today.getFullYear();
}

// Function to check if date is yesterday
export function isYesterday(dateString) {
    if (!dateString) return false;
    
    const date = new Date(dateString);
    const yesterday = new Date();
    yesterday.setDate(yesterday.getDate() - 1);
    
    return date.getDate() === yesterday.getDate() &&
           date.getMonth() === yesterday.getMonth() &&
           date.getFullYear() === yesterday.getFullYear();
}

// Function to check if date is this week
export function isThisWeek(dateString) {
    if (!dateString) return false;
    
    const date = new Date(dateString);
    const now = new Date();
    const diffInDays = Math.floor((now - date) / (1000 * 60 * 60 * 24));
    
    return diffInDays >= 0 && diffInDays < 7;
}

// Usage examples and demonstrations
export const formatDateTimeExamples = {
    // Basic usage (backwards compatible)
    basic: (dateString) => formatDateTime(dateString),
    
    // With type prefix
    created: (dateString) => formatDateTime(dateString, { type: 'created' }),
    updated: (dateString) => formatDateTime(dateString, { type: 'updated' }),
    ordered: (dateString) => formatDateTime(dateString, { type: 'ordered' }),
    
    // With time information for recent dates
    withTime: (dateString) => formatDateTime(dateString, { 
        type: 'created', 
        includeTime: true 
    }),
    
    // With seconds for very recent times
    withSeconds: (dateString) => formatDateTime(dateString, { 
        type: 'updated', 
        showSeconds: true 
    }),
    
    // Absolute format
    absolute: (dateString) => formatDateTime(dateString, { 
        format: 'absolute' 
    }),
    
    // Mixed format (relative for recent, absolute for older)
    mixed: (dateString) => formatDateTime(dateString, { 
        type: 'created',
        format: 'mixed',
        includeTime: true 
    }),
    
    // For purchase orders
    purchaseOrder: (dateString) => formatDateTime(dateString, {
        type: 'ordered',
        format: 'mixed',
        includeTime: false
    }),
    
    // For supplier last contact
    supplierContact: (dateString) => formatDateTime(dateString, {
        type: 'updated',
        format: 'relative',
        includeTime: true
    })
};

// Export default configuration for common use cases
export default {
    formatDateTime,
    formatDate,
    formatTimeOnly,
    formatDateOnly,
    formatShortDate,
    isToday,
    isYesterday,
    isThisWeek,
    examples: formatDateTimeExamples
};