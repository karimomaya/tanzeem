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
export function getStockText(stock, minimumStock) {
    const minStock = minimumStock || 0;
    if (stock === 0) return 'نفد';
    if (stock <= minStock) return 'منخفض';
    return 'متوفر';
}

export function getStockColor(stock, minimumStock) {
    const minStock = minimumStock || 0;
    if (stock === 0) return 'error';
    if (stock <= minStock) return 'warning';
    return 'success';
}

export function getStockLevel(stock, minimumStock) {
    const status = getStockStatus(stock, minimumStock);
    if (status == 'out-of-stock') return 'empty';
    if (status == 'low-stock') return 'low';
    return 'medium';
    //  'high';
}

export function getStockStatus(stock, minimumStock) {
    if (stock === 0) {
        return 'out-of-stock';
    } else if (stock <= minimumStock) {
        return 'low-stock';
    }
    return 'active';   
}
export function getStockPercentage(currentStock, minimumStock) {
    if (minimumStock === 0) {
      return currentStock === 0 ? 100 : Infinity; // or return 0 if you prefer
    }
  
    const percentage = (currentStock / minimumStock) * 100;
    return Math.round(percentage * 100) / 100; // rounded to 2 decimal places
}

export function getStockIcon(stock, minimumStock) {
    if (stock <= 0) return 'mdi-close-circle';
    if (stock <= minimumStock) return 'mdi-alert-circle';
    return 'mdi-check-circle';
}