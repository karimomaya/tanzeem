import { error } from '@/utils/system-util';

export async function makeRequest(url, options = {}, excludeAuth = false) {
    const token = localStorage.getItem('accessToken');

    // Append Authorization header if token exists and excludeAuth is false
    if (token && !excludeAuth) {
        options.headers = {
            ...options.headers,
            Authorization: `Bearer ${token}`
        };
    }

    try {
        const response = await fetch(url, options);

        if (!response.ok) {
            // Handle HTTP errors
            const errorData = await response.json();
            error(`Error: ${errorData.error || response.statusText}`);
            return null;
        }
        if (response.status === 204) {
            // No content to return
            return null;
        }

        // Handle successful response
        return await response.json();
    } catch (e) {
        error(`Error: ${e.message}`);
        return null;
    }
}