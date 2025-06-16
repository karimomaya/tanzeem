import { makeRequest } from '@/utils/request-util';

export class ContentServiceClient {
    constructor() {
        this.baseURL = `${import.meta.env.VITE_APP_CONTENT_SERVICE_URL}/api` || 'http://localhost:8081/api/contents';
        }
    
    async uploadContent(file, context, entityType, entityId, isPrimary, order, title, description) {
        const formData = new FormData();
        formData.append('file', file);
        formData.append('context', context);
        formData.append('entityType', entityType);
        if (entityId) formData.append('entityId', entityId);
        if (isPrimary !== null) formData.append('isPrimary', isPrimary);
        if (order !== null) formData.append('order', order);
        if (title) formData.append('title', title);
        if (description) formData.append('description', description);

        try {
            const response = await makeRequest(`${this.baseURL}/contents/upload`, {
                method: 'POST',
                body: formData
            }, false, (progress) => {
                console.log(`Upload Progress: ${progress}%`);
                // You can emit events here for progress tracking
            });
            return response.data;
        } catch (error) {
            console.error('Content upload error:', error);
            throw new Error(error.response?.data?.message || 'Failed to upload content');
        }
    }

    async uploadMultipleContents(files, context, entityType, entityId) {
        const formData = new FormData();
        files.forEach((file, index) => {
            formData.append('files', file);
        });
        formData.append('context', context);
        formData.append('entityType', entityType);
        if (entityId) formData.append('entityId', entityId);

        try {
            const response = await makeRequest(`${this.baseURL}/contents/upload-multiple`, {
                method: 'POST',
                body: formData
            }, false, (progress) => {
                console.log(`Upload Progress: ${progress}%`);
                // You can emit events here for progress tracking
            });
            return response;
        } catch (error) {
            console.error('Multiple content upload error:', error);
            throw new Error(error.response?.data?.message || 'Failed to upload contents');
        }
    }

    async downloadAndStoreContent(contentUrl, context, entityType, entityId, isPrimary, order, title, description) {
        const requestData = {
            contentUrl,
            context,
            entityType,
            entityId,
            isPrimary,
            order,
            title,
            description
        };

        try {
            const response = await this.apiClient.post('/download-and-store', requestData, {
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            return response.data;
        } catch (error) {
            console.error('Content download error:', error);
            throw new Error(error.response?.data?.message || 'Failed to download and store content');
        }
    }

    async getContentsByEntity(entityType, entityId) {
        try {
            return await makeRequest(`${this.baseURL}/contents/entity/${entityType}/${entityId}`, { method: 'GET' });
        } catch (error) {
            console.error('Get contents error:', error);
            throw new Error(error.response?.data?.message || 'Failed to get contents');
        }
    }

    async getContentsByEntityAndCategory(entityType, entityId, category) {
        try {
            return await makeRequest(`${this.baseURL}/contents/entity/${entityType}/${entityId}/category/${category}`, { method: 'GET' });
        } catch (error) {
            console.error('Get contents by category error:', error);
            throw new Error(error.response?.data?.message || 'Failed to get contents by category');
        }
    }

    async getPrimaryContent(entityType, entityId) {
        try {
            const response = await this.apiClient.get(`/entity/${entityType}/${entityId}/primary`);
            return response.data;
        } catch (error) {
            console.error('Get primary content error:', error);
            throw new Error(error.response?.data?.message || 'Failed to get primary content');
        }
    }

    async deleteContentsByEntity(entityType, entityId) {
        try {
            const response = await this.apiClient.delete(`/entity/${entityType}/${entityId}`);
            return response.data;
        } catch (error) {
            console.error('Delete contents error:', error);
            throw new Error(error.response?.data?.message || 'Failed to delete contents');
        }
    }

    async updateContentEntityReferences(entityType, entityId, contentUrls) {
        const requestData = {
            entityType,
            entityId,
            contentUrls
        };

        try {
            const response = await this.apiClient.put('/update-entity-references', requestData, {
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            return response.data;
        } catch (error) {
            console.error('Update content entity references error:', error);
            throw new Error(error.response?.data?.message || 'Failed to update content entity references');
        }
    }   

    async downloadAndStoreMultipleContents(contentUrls, context, entityType, entityId) {
        const requestData = {
            contentUrls: contentUrls.map((url, index) => ({
                url: url,
                order: index
            })),
            context,
            entityType,
            entityId
        };

        try {
            const options = {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(requestData)
            };
            return await makeRequest(`${this.baseURL}/contents/download-and-store-multiple`, options);

        } catch (error) {
            console.error('Bulk content download error:', error);
            throw new Error(error.response?.data?.message || 'Failed to download and store contents');
        }
    }

    async getContentsForMultipleEntities(entityType, entityIds) {
        try {
            const response = await this.apiClient.post('/entity/batch', {
                entityType,
                entityIds
            }, {
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            return response.data;
        } catch (error) {
            console.error('Get multiple entities contents error:', error);
            throw new Error(error.response?.data?.message || 'Failed to get contents for entities');
        }
    }
}