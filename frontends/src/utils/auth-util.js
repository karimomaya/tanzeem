import { error } from '@/utils/system-util';
import { jwtDecode } from 'jwt-decode';

export function isTokenValid() {
    const token = localStorage.getItem('accessToken');
    if (!token) return false;
  
    try {
      const { exp } = jwtDecode(token);
      return exp * 1000 > Date.now(); // Check if token is expired
    } catch (e) {
      return false;
    }
}

export function getBusinessType() {
    const token = localStorage.getItem('accessToken'); // or however you store your JWT
    if (!token) return null;
    
    try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        return payload.businessType;
    } catch (error) {
        console.error('Error decoding JWT:', error);
        return null;
    }
}

export function getTenantInfo() {
    const token = localStorage.getItem('accessToken');
    if (!token) return {};
    
    try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        return {
            businessType: payload.businessType,
            tenantId: payload.tenantId,
            defaultCurrency: payload.defaultCurrency,
            defaultLanguage: payload.defaultLanguage,
            timezone: payload.timezone
        };
    } catch (error) {
        console.error('Error decoding JWT:', error);
        return {};
    }
}