import { makeRequest } from '@/utils/request-util';

const url = `${import.meta.env.VITE_APP_API_PRODUCT_URL}/api/categories`;

export async function saveCateogry(data) {
    
    const options = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    };
    return await makeRequest(url, options);
}

export async function updateCateogry(data) {
    
    const options = {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    };
    return await makeRequest(url+"/"+data.id, options);
}

export async function getCategories(params) {
    return await makeRequest(`${url}?${params.toString()}`, { method: 'GET' });
}
