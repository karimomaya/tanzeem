import { error } from '@/utils/system-util';

export async function makeRequest(url, options = {}, excludeAuth = false, onProgress = null) {
    const token = localStorage.getItem('accessToken');

    // Append Authorization header if token exists and excludeAuth is false
    if (token && !excludeAuth) {
        options.headers = {
            ...options.headers,
            Authorization: `Bearer ${token}`
        };
    }

    try {
        let response;
        
        // If we have a progress callback and this is a file upload, use XMLHttpRequest for progress tracking
        if (onProgress && options.body instanceof FormData) {
            response = await makeRequestWithProgress(url, options, onProgress);
        } else {
            response = await fetch(url, options);
        }

        if (!response.ok) {
            // Handle HTTP errors
            let errorData;
            try {
                errorData = await response.json();
            } catch {
                errorData = { error: response.statusText };
            }
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

// Helper function for file uploads with progress tracking using XMLHttpRequest
function makeRequestWithProgress(url, options, onProgress) {
    return new Promise((resolve, reject) => {
        const xhr = new XMLHttpRequest();
        
        // Setup progress tracking
        if (xhr.upload && onProgress) {
            xhr.upload.addEventListener('progress', (event) => {
                if (event.lengthComputable) {
                    const percentCompleted = Math.round((event.loaded * 100) / event.total);
                    onProgress(percentCompleted);
                }
            });
        }
        
        // Setup response handling
        xhr.onload = () => {
            const response = {
                ok: xhr.status >= 200 && xhr.status < 300,
                status: xhr.status,
                statusText: xhr.statusText,
                json: async () => JSON.parse(xhr.responseText),
                text: async () => xhr.responseText
            };
            resolve(response);
        };
        
        xhr.onerror = () => reject(new Error('Network error'));
        
        // Setup request
        xhr.open(options.method || 'GET', url);
        
        // Add headers
        if (options.headers) {
            Object.entries(options.headers).forEach(([key, value]) => {
                // Don't set Content-Type for FormData - browser will set it with boundary
                if (key.toLowerCase() !== 'content-type' || !(options.body instanceof FormData)) {
                    xhr.setRequestHeader(key, value);
                }
            });
        }
        
        // Send request
        xhr.send(options.body);
    });
}