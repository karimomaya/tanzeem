import { makeRequest } from '@/utils/request-util';
const LOOKUP_BASE_URL = `${import.meta.env.VITE_APP_API_LOOKUP_URL}/api`;


export async function getCountries() {
    return await makeRequest(`${LOOKUP_BASE_URL}/countries`, { method: 'GET' });
}

export async function getGovernorates(countryCode) {
    return await makeRequest(`${LOOKUP_BASE_URL}/governorates?countryCode=${countryCode}`, { method: 'GET' });
}