import {  CURRENCY_SYMBOL, CURRENCY_ICON } from '@/constants/currency';

export function getCurrencyIcon() {
    const token = localStorage.getItem('accessToken'); // or however you store your JWT
    if (!token) return null;
    
    try {
        const payload = JSON.parse(atob(token.split('.')[1]));

        return CURRENCY_ICON[payload.defaultCurrency] || 'mdi-currency-usd' ;
    } catch (error) {
        console.error('Error decoding JWT:', error);
        return null;
    }
}


export function getCurrencySymbol() {
    const token = localStorage.getItem('accessToken'); // or however you store your JWT
    if (!token) return null;
    
    try {
        const payload = JSON.parse(atob(token.split('.')[1]));

        return CURRENCY_SYMBOL[payload.defaultCurrency] || 'mdi-currency-usd' ;
    } catch (error) {
        console.error('Error decoding JWT:', error);
        return null;
    }
}