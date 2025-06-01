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

export async function registration(data) {
    const requestOptions = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    };
    try {
        const response = await fetch(`${import.meta.env.VITE_APP_API_AUTH_URL}/api/auth/register`, requestOptions);
        if (!response.ok) {
            // Handle HTTP errors
            const errorData = await response.json();
            error('خطأ أثناء التسجيل: ' + errorData.error);
            return null;
        } 
        // Handle successful response
        const data = await response.json();
        return data;
        
    } catch (e) {
        error('خطأ أثناء التسجيل' + e.message);
    }
}

export async function login(data) {
    const requestOptions = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    };
    try {
        const response = await fetch(`${import.meta.env.VITE_APP_API_AUTH_URL}/api/auth/login`, requestOptions);
        if (!response.ok) {
            // Handle HTTP errors
            const errorData = await response.json();
            error('خطأ أثناء تسجيل الدخول: ' + errorData.error);
            return null;
        } 
        // Handle successful response
        const data = await response.json();
        return data;
        
    } catch (e) {
        error('خطأ أثناء تسجيل الدخول' + e.message);
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