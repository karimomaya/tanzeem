import {  CURRENCY_SYMBOL, CURRENCY_ICON, CURRENCY_FORMAT } from '@/constants/currency';
import { getTenantInfo } from '@/utils/auth-util';

export function getCurrencyIcon() {
    const tenantInfo = getTenantInfo()
    return tenantInfo && tenantInfo.defaultCurrency ? CURRENCY_ICON[tenantInfo.defaultCurrency] || 'mdi-currency-usd' : 'mdi-currency-usd';
}


export function getCurrencySymbol() {
    const tenantInfo = getTenantInfo()
    return tenantInfo && tenantInfo.defaultCurrency ? CURRENCY_SYMBOL[tenantInfo.defaultCurrency] || 'ج.م.' : 'ج.م.';
}

export function formatCurrency(amount) {
    const tenantInfo = getTenantInfo()
    const currency = tenantInfo.defaultCurrency || 'EGP';
    return new Intl.NumberFormat(CURRENCY_FORMAT[currency], {
        style: 'currency',
        currency: currency
    }).format(amount);
}