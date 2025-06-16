import { makeRequest } from '@/utils/request-util';

const url = `${import.meta.env.VITE_APP_API_PRODUCT_URL}/api`;

export async function saveCategory(data) {
    const options = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    };
    return await makeRequest(`${url}/categories`, options);
}

export const getProductById = async (productId) => {
    try {
        const response = await makeRequest(`${url}/products/${productId}`, 'GET');
        return response.data;
    } catch (error) {
        console.error('Error fetching product:', error);
        throw error;
    }
};

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
    return await makeRequest(`${url}/products/${data.id}`, options);
}

export async function updateCategory(data) {
    const options = {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    };
    return await makeRequest(`${url}/categories/${data.id}`, options);
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
    return await makeRequest(`${url}/categories/${id}`, options);
}

export async function deleteProduct(id) {
    const options = {
        method: 'DELETE',
        headers: { 'Content-Type': 'application/json' },
    };
    return await makeRequest(`${url}/products/${id}`, options);
}

export async function getProductsStats(){
    return await makeRequest(`${url}/products/stats`, { method: 'GET' });
}

export async function getCategoriesStats(){
    return await makeRequest(`${url}/categories/stats`, { method: 'GET' });
}
