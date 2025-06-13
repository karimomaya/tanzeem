<template>
    <div v-if="contentFiles.length > 0" class="content-preview-container">
        <div class="content-gallery-preview" @click="openContentGallery">
            <!-- Dynamic Content Thumbnail -->
            <div class="content-thumbnail" :style="{ backgroundColor: primaryContentColor }">
                <!-- Image Thumbnail -->
                <v-img 
                    v-if="primaryContentType === 'IMAGE'" 
                    :src="primaryContentUrl" 
                    :width="size" 
                    :height="size" 
                    cover
                    class="main-preview-content">
                    <template v-slot:error>
                        <div class="image-error">
                            <v-icon :color="primaryContentIconColor" :size="size * 0.5">
                                {{ primaryContentIcon }}
                            </v-icon>
                        </div>
                    </template>
                </v-img>
                
                <!-- File Icon for Non-Images -->
                <div v-else class="file-icon-container">
                    <v-icon 
                        :color="primaryContentIconColor" 
                        :size="size * 0.5">
                        {{ primaryContentIcon }}
                    </v-icon>
                </div>
            </div>

            <!-- Content Counter Badge -->
            <div v-if="contentFiles.length > 1 && showCounter" class="content-counter-badge">
                <v-icon :size="counterIconSize" class="me-1">{{ contentTypesIcon }}</v-icon>
                {{ contentFiles.length }}
            </div>

            <!-- Content Type Indicator -->
            <div v-if="showTypeIndicator" class="content-type-indicator">
                <v-chip :size="chipSize" :color="primaryContentColor" variant="flat">
                    {{ primaryContentExtension }}
                </v-chip>
            </div>
        </div>
    </div>
    <div v-else class="content-preview-container" @click="openContentGallery">
        <v-avatar :size="size" :color="placeholderColor" >
            <v-icon :size="size * 0.5" :color="placeholderIconColor">{{ placeholderIcon }}</v-icon>
        </v-avatar>
    </div>
</template>

<script>
export default {
    name: 'ContentThumbnailPreview',
    props: {
        // Content files array
        files: {
            type: Array,
            default: () => []
        },
        // Thumbnail size
        size: {
            type: Number,
            default: 60
        },
        // Show counter badge
        showCounter: {
            type: Boolean,
            default: true
        },
        // Show type indicator chip
        showTypeIndicator: {
            type: Boolean,
            default: true
        },
        // Placeholder when no content
        placeholderIcon: {
            type: String,
            default: 'mdi-package-variant'
        },
        placeholderColor: {
            type: String,
            default: 'grey-lighten-3'
        },
        placeholderIconColor: {
            type: String,
            default: 'grey'
        },
        // Enable click to open gallery
        clickable: {
            type: Boolean,
            default: true
        }
    },
    emits: ['click', 'open-gallery'],
    computed: {
        contentFiles() {
            return this.files.map((file, index) => {
                const fileExtension = this.getFileExtension(file.name || file.url);
                const contentType = this.getContentTypeFromFile(file);
                
                return {
                    name: file.name || `content-${index + 1}`,
                    type: file.type || this.getMimeTypeFromExtension(fileExtension),
                    size: file.size || 0,
                    url: file.url || (file instanceof File ? URL.createObjectURL(file) : null),
                    isUrlFile: file.isUrlFile || false,
                    extension: fileExtension,
                    contentType: contentType,
                    icon: this.getFileTypeIcon(fileExtension),
                    color: this.getFileTypeColor(fileExtension),
                    iconColor: this.getFileIconColor(fileExtension),
                    contentId: file.contentId || null,
                    isPrimary: file.isPrimary || false,
                    ...file // Preserve any additional properties
                };
            });
        },

        primaryContent() {
            // Try to find primary content first
            const primary = this.contentFiles.find(file => file.isPrimary);
            return primary || (this.contentFiles.length > 0 ? this.contentFiles[0] : null);
        },

        primaryContentType() {
            return this.primaryContent?.contentType || 'OTHER';
        },

        primaryContentUrl() {
            return this.primaryContent?.url || null;
        },

        primaryContentIcon() {
            return this.primaryContent?.icon || 'mdi-file';
        },

        primaryContentColor() {
            return this.primaryContent?.color || 'grey';
        },

        primaryContentIconColor() {
            return this.primaryContent?.iconColor || 'white';
        },

        primaryContentExtension() {
            return this.primaryContent?.extension?.toUpperCase() || 'FILE';
        },

        contentTypesIcon() {
            // Show different icon based on content mix
            const types = [...new Set(this.contentFiles.map(f => f.contentType))];
            if (types.length === 1) {
                switch (types[0]) {
                    case 'IMAGE': return 'mdi-image-multiple';
                    case 'DOCUMENT': return 'mdi-file-multiple';
                    case 'VIDEO': return 'mdi-video-multiple';
                    case 'AUDIO': return 'mdi-music-note-multiple';
                    case 'ARCHIVE': return 'mdi-folder-zip-outline';
                    default: return 'mdi-file-multiple';
                }
            }
            return 'mdi-file-multiple-outline'; // Mixed content types
        },

        // Dynamic sizing
        counterIconSize() {
            return Math.max(10, this.size * 0.2);
        },

        chipSize() {
            if (this.size <= 40) return 'x-small';
            if (this.size <= 60) return 'small';
            return 'small';
        }
    },
    methods: {
        getFileExtension(fileName) {
            if (!fileName) return '';
            const parts = fileName.split('.');
            return parts.length > 1 ? parts.pop().toLowerCase() : '';
        },

        getContentTypeFromFile(file) {
            const extension = this.getFileExtension(file.name || file.url);
            const mimeType = file.type || this.getMimeTypeFromExtension(extension);
            
            if (mimeType.startsWith('image/')) return 'IMAGE';
            if (mimeType.startsWith('video/')) return 'VIDEO';
            if (mimeType.startsWith('audio/')) return 'AUDIO';
            if (mimeType.startsWith('text/')) return 'TEXT';
            
            // Document types
            if (mimeType.includes('pdf') || 
                mimeType.includes('word') || 
                mimeType.includes('excel') || 
                mimeType.includes('powerpoint') ||
                mimeType.includes('officedocument')) {
                return 'DOCUMENT';
            }
            
            // Archive types
            if (mimeType.includes('zip') || 
                mimeType.includes('rar') || 
                mimeType.includes('7z')) {
                return 'ARCHIVE';
            }
            
            return 'OTHER';
        },

        getMimeTypeFromExtension(extension) {
            const mimeMap = {
                // Images
                'jpg': 'image/jpeg', 'jpeg': 'image/jpeg', 'png': 'image/png',
                'gif': 'image/gif', 'webp': 'image/webp', 'bmp': 'image/bmp',
                'svg': 'image/svg+xml',
                
                // Documents
                'pdf': 'application/pdf',
                'doc': 'application/msword',
                'docx': 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
                'xls': 'application/vnd.ms-excel',
                'xlsx': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
                'ppt': 'application/vnd.ms-powerpoint',
                'pptx': 'application/vnd.openxmlformats-officedocument.presentationml.presentation',
                
                // Text
                'txt': 'text/plain', 'csv': 'text/csv', 'json': 'application/json',
                'xml': 'text/xml', 'html': 'text/html', 'css': 'text/css',
                'js': 'text/javascript',
                
                // Video
                'mp4': 'video/mp4', 'avi': 'video/x-msvideo', 'mov': 'video/quicktime',
                'wmv': 'video/x-ms-wmv', 'webm': 'video/webm', 'mkv': 'video/x-matroska',
                
                // Audio
                'mp3': 'audio/mpeg', 'wav': 'audio/wav', 'ogg': 'audio/ogg',
                'aac': 'audio/aac', 'm4a': 'audio/mp4',
                
                // Archives
                'zip': 'application/zip',
                'rar': 'application/x-rar-compressed',
                '7z': 'application/x-7z-compressed'
            };
            
            return mimeMap[extension] || 'application/octet-stream';
        },

        getFileTypeIcon(extension) {
            const iconMap = {
                // Images
                'jpg': 'mdi-file-image', 'jpeg': 'mdi-file-image', 'png': 'mdi-file-image',
                'gif': 'mdi-file-image', 'webp': 'mdi-file-image', 'bmp': 'mdi-file-image',
                'svg': 'mdi-file-image',
                
                // Documents
                'pdf': 'mdi-file-pdf-box',
                'doc': 'mdi-file-word', 'docx': 'mdi-file-word',
                'xls': 'mdi-file-excel', 'xlsx': 'mdi-file-excel',
                'ppt': 'mdi-file-powerpoint', 'pptx': 'mdi-file-powerpoint',
                
                // Text
                'txt': 'mdi-file-document', 'csv': 'mdi-file-table',
                'json': 'mdi-code-json', 'xml': 'mdi-file-code',
                'html': 'mdi-language-html5', 'css': 'mdi-language-css3',
                'js': 'mdi-language-javascript',
                
                // Video
                'mp4': 'mdi-file-video', 'avi': 'mdi-file-video', 'mov': 'mdi-file-video',
                'wmv': 'mdi-file-video', 'webm': 'mdi-file-video', 'mkv': 'mdi-file-video',
                
                // Audio
                'mp3': 'mdi-file-music', 'wav': 'mdi-file-music', 'ogg': 'mdi-file-music',
                'aac': 'mdi-file-music', 'm4a': 'mdi-file-music',
                
                // Archives
                'zip': 'mdi-folder-zip', 'rar': 'mdi-folder-zip', '7z': 'mdi-folder-zip'
            };
            
            return iconMap[extension] || 'mdi-file';
        },

        getFileTypeColor(extension) {
            const colorMap = {
                // Images
                'jpg': 'green', 'jpeg': 'green', 'png': 'green', 'gif': 'green',
                'webp': 'green', 'bmp': 'green', 'svg': 'green',
                
                // Documents
                'pdf': 'red',
                'doc': 'blue', 'docx': 'blue',
                'xls': 'green', 'xlsx': 'green',
                'ppt': 'orange', 'pptx': 'orange',
                
                // Text
                'txt': 'grey', 'csv': 'orange', 'json': 'purple', 'xml': 'purple',
                'html': 'deep-orange', 'css': 'blue', 'js': 'yellow',
                
                // Video
                'mp4': 'indigo', 'avi': 'indigo', 'mov': 'indigo',
                'wmv': 'indigo', 'webm': 'indigo', 'mkv': 'indigo',
                
                // Audio
                'mp3': 'purple', 'wav': 'purple', 'ogg': 'purple',
                'aac': 'purple', 'm4a': 'purple',
                
                // Archives
                'zip': 'brown', 'rar': 'brown', '7z': 'brown'
            };
            
            return colorMap[extension] || 'grey';
        },

        getFileIconColor(extension) {
            return 'white';
        },

        openContentGallery() {
            if (!this.clickable) return;
            
            this.$emit('click', this.contentFiles);
            this.$emit('open-gallery', {
                files: this.contentFiles,
                primaryIndex: this.contentFiles.findIndex(f => f.isPrimary) || 0
            });
        }
    }
};
</script>

<style scoped>
.content-preview-container {
    display: inline-block;
}

.content-gallery-preview {
    position: relative;
    cursor: pointer;
    transition: transform 0.2s ease;
    display: inline-block;
}

.content-gallery-preview:hover {
    transform: scale(1.05);
}

.content-thumbnail {
    border-radius: 8px;
    overflow: hidden;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
}

.file-icon-container {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
}

.main-preview-content {
    border-radius: 8px;
}

.image-error {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(0, 0, 0, 0.1);
}

.content-counter-badge {
    position: absolute;
    top: -8px;
    right: -8px;
    background: rgba(0, 0, 0, 0.8);
    color: white;
    border-radius: 12px;
    padding: 2px 6px;
    font-size: 10px;
    font-weight: 600;
    min-width: 20px;
    text-align: center;
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 2;
}

.content-type-indicator {
    position: absolute;
    bottom: -6px;
    left: 50%;
    transform: translateX(-50%);
    z-index: 2;
}

.content-type-indicator .v-chip {
    height: 16px;
    font-size: 8px;
    font-weight: 600;
}

/* Responsive adjustments */
@media (max-width: 768px) {
    .content-counter-badge {
        font-size: 9px;
        padding: 1px 4px;
    }
    
    .content-type-indicator .v-chip {
        height: 14px;
        font-size: 7px;
    }
}
</style>