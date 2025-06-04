// Import your existing makeRequest function
import { makeRequest } from '@/utils/request-util'; // Adjust the path as needed

// Configuration
const IMAGE_SERVICE_URL = `${import.meta.env.VITE_APP_IMAGE_SERVICE_URL}` || 'http://localhost:8085';

export class ImageServiceClient {
    
    /**
     * Upload an image file to the image service
     * @param {File} file - The image file to upload
     * @param {string} context - Context for organizing images (e.g., 'product', 'category')
     * @returns {Promise<Object>} - Upload response with URL and metadata
     */
    async uploadImage(file, context = 'product') {
        try {
            this.validateFile(file);
            
            const formData = new FormData();
            formData.append('image', file);
            formData.append('context', context);
            
            const response = await makeRequest(`${IMAGE_SERVICE_URL}/api/images/upload`, {
                method: 'POST',
                body: formData
            }, false, (progress) => {
                console.log(`Upload Progress: ${progress}%`);
                // You can emit events here for progress tracking
            });
            
            if (!response) {
                throw new Error('فشل رفع الصورة');
            }
            
            return response;
        } catch (error) {
            console.error('Error uploading image:', error);
            throw new Error(error.message || 'فشل رفع الصورة');
        }
    }
    
    /**
     * Download an image from URL and store it in the image service
     * @param {string} imageUrl - The URL of the image to download
     * @param {string} context - Context for organizing images
     * @returns {Promise<Object>} - Upload response with URL and metadata
     */
    async uploadImageFromUrl(imageUrl, context = 'product') {
        try {
            if (!this.isValidUrl(imageUrl)) {
                throw new Error('رابط الصورة غير صحيح');
            }
            
            const response = await makeRequest(`${IMAGE_SERVICE_URL}/api/images/upload-from-url`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ 
                    imageUrl: imageUrl,
                    context: context 
                })
            });
            
            if (!response) {
                throw new Error('فشل تحميل الصورة من الرابط');
            }
            
            return response;
        } catch (error) {
            console.error('Error downloading image from URL:', error);
            throw new Error(error.message || 'فشل تحميل الصورة من الرابط');
        }
    }
    
    /**
     * Delete an image from the image service
     * @param {string} filename - The filename or relative path of the image
     * @returns {Promise<void>}
     */
    async deleteImage(filename) {
        try {
            await makeRequest(`${IMAGE_SERVICE_URL}/api/images/${filename}`, {
                method: 'DELETE'
            });
        } catch (error) {
            console.error('Error deleting image:', error);
            throw new Error('فشل حذف الصورة');
        }
    }
    
    /**
     * Get the full URL for an image
     * @param {string} filename - The filename or relative path
     * @returns {string} - Full image URL
     */
    getImageUrl(filename) {
        if (!filename) return null;
        
        // If it's already a full URL, return as is
        if (filename.startsWith('http')) {
            return filename;
        }
        
        // Otherwise, construct the URL
        return `${IMAGE_SERVICE_URL}/api/images/${filename}`;
    }
    
    /**
     * Check if the image service is healthy
     * @returns {Promise<boolean>}
     */
    async checkHealth() {
        try {
            const response = await makeRequest(`${IMAGE_SERVICE_URL}/api/images/health`, {
                method: 'GET'
            });
            return response !== null;
        } catch (error) {
            console.error('Image service health check failed:', error);
            return false;
        }
    }
    
    /**
     * Validate image file before upload
     * @param {File} file - The file to validate
     * @throws {Error} - If validation fails
     */
    validateFile(file) {
        if (!file) {
            throw new Error('لم يتم اختيار ملف');
        }
        
        if (!file.type.startsWith('image/')) {
            throw new Error('يجب أن يكون الملف صورة');
        }
        
        const maxSize = 10 * 1024 * 1024; // 10MB (matching backend)
        if (file.size > maxSize) {
            throw new Error('حجم الصورة كبير جداً (الحد الأقصى 10 ميجابايت)');
        }
        
        const allowedTypes = [
            'image/jpeg', 
            'image/jpg', 
            'image/png', 
            'image/gif', 
            'image/webp', 
            'image/bmp'
        ];
        
        if (!allowedTypes.includes(file.type.toLowerCase())) {
            throw new Error('نوع الملف غير مدعوم. الأنواع المدعومة: JPG, PNG, GIF, WebP, BMP');
        }
    }
    
    /**
     * Validate URL format
     * @param {string} url - The URL to validate
     * @returns {boolean}
     */
    isValidUrl(url) {
        if (!url) return false;
        
        try {
            const urlObj = new URL(url);
            return urlObj.protocol === 'http:' || urlObj.protocol === 'https:';
        } catch {
            return false;
        }
    }
    
    /**
     * Check if URL points to an image
     * @param {string} url - The URL to check
     * @returns {boolean}
     */
    isImageUrl(url) {
        if (!this.isValidUrl(url)) return false;
        
        const imageExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.webp', '.bmp', '.svg'];
        const urlLower = url.toLowerCase();
        
        return imageExtensions.some(ext => urlLower.includes(ext)) || 
               this.isKnownImageHost(url);
    }
    
    /**
     * Check if URL is from a known image hosting service
     * @param {string} url - The URL to check
     * @returns {boolean}
     */
    isKnownImageHost(url) {
        const imageHosts = [
            'imgur.com',
            'cloudinary.com',
            'amazonaws.com',
            'googleusercontent.com',
            'fbcdn.net',
            'unsplash.com',
            'pexels.com',
            'pixabay.com',
            'freepik.com'
        ];
        
        return imageHosts.some(host => url.includes(host));
    }
    
    /**
     * Extract filename from a full image URL
     * @param {string} url - The full image URL
     * @returns {string|null} - The filename or null
     */
    extractFilename(url) {
        if (!url) return null;
        
        try {
            // If it's our service URL, extract the relative path
            if (url.startsWith(IMAGE_SERVICE_URL)) {
                return url.replace(`${IMAGE_SERVICE_URL}/api/images/`, '');
            }
            
            // Otherwise, extract just the filename
            const urlObj = new URL(url);
            return urlObj.pathname.split('/').pop();
        } catch {
            return null;
        }
    }
}

// Create a singleton instance for convenience
export const imageService = new ImageServiceClient();

// Export utility functions for standalone use
export const uploadImage = (file, context) => imageService.uploadImage(file, context);
export const uploadImageFromUrl = (url, context) => imageService.uploadImageFromUrl(url, context);
export const deleteImage = (filename) => imageService.deleteImage(filename);
export const getImageUrl = (filename) => imageService.getImageUrl(filename);

// Default export
export default ImageServiceClient;