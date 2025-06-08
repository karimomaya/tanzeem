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

export function formatNumber(number) {
    const tenantInfo = getTenantInfo()
    const currency = tenantInfo.defaultCurrency || 'EGP';
    if (currency === 'AED' || currency === 'SAR' || currency === 'OMR') {
        return new Intl.NumberFormat('ar-AE').format(number);
    }
    return new Intl.NumberFormat('ar-EG').format(number);
}

