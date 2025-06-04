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