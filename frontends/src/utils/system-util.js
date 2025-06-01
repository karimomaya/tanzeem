import { emitter } from '@/main.js';
import { getTenantInfo } from '@/utils/auth-util.js';

export function error(message) {
    emitter.emit('global-error', message);
    console.error(message);
}

export function success(message) {
    emitter.emit('global-success', message);
    console.info(message);
}

export function warn(message) {
    emitter.emit('global-warning', message);
    console.warn(message);
}

export function formatCurrency(amount) {
    const tenantInfo = getTenantInfo()
    const currency = tenantInfo.defaultCurrency || 'EGP';
    if (currency === 'AED' || currency === 'SAR' || currency === 'OMR') {
        return new Intl.NumberFormat('ar-AE', {
            style: 'currency',
            currency: currency
        }).format(amount);
    }
    return new Intl.NumberFormat('ar-EG', {
        style: 'currency',
        currency: currency
    }).format(amount);
}

export function formatNumber(number) {
    const tenantInfo = getTenantInfo()
    const currency = tenantInfo.defaultCurrency || 'EGP';
    if (currency === 'AED' || currency === 'SAR' || currency === 'OMR') {
        return new Intl.NumberFormat('ar-AE').format(number);
    }
    return new Intl.NumberFormat('ar-EG').format(number);
}

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