import { makeRequest } from '@/utils/request-util';

const url = `${import.meta.env.VITE_APP_API_PRODUCT_URL}/api`;

export async function saveCateogry(data) {
    
    const options = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    };
    return await makeRequest(`${url}/categories`, options);
}


export async function saveProduct(data) {
    
    const options = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    };
    return await makeRequest(`${url}/products`, options);
}


export async function updateProduct(data) {
    const options = {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    };
    return await makeRequest(`${url}/products`+"/"+data.id, options);
}

export async function updateCateogry(data) {
    const options = {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    };
    return await makeRequest(`${url}/categories`+"/"+data.id, options);
}

export async function getProducts(params) {
    return await makeRequest(`${url}/products?${params.toString()}`, { method: 'GET' });
}

export async function getCategories(params) {
    return await makeRequest(`${url}/categories?${params.toString()}`, { method: 'GET' });
}
export async function deleteCategory(id) {
    const options = {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' },
    };
    return await makeRequest(`${url}/categories` + "/" + id, options);
}