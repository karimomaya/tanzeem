<template>
    <div class="file-upload-container">
        <!-- Upload Type Selection (for images) -->
        <div v-if="showUploadTypeSelector" class="form-group">
            <label class="form-label">{{ uploadTypeSelectorLabel }}</label>
            <v-radio-group v-model="uploadType" inline hide-details class="upload-type-selector">
                <v-radio label="رفع ملف" value="file" color="primary"></v-radio>
                <v-radio label="رابط خارجي" value="url" color="primary"></v-radio>
            </v-radio-group>
        </div>

        <!-- URL Input (when upload type is url) -->
        <div v-if="showUrlInput" class="form-group">
            <label class="form-label">{{ urlInputLabel }}</label>
            <v-text-field v-model="urlInput" :rules="urlRules" variant="outlined" density="comfortable"
                :placeholder="urlPlaceholder" hide-details="auto" :loading="urlLoading" class="modern-field"
                @blur="handleUrlChange($event.target.value)" @keyup.enter="handleUrlChange($event.target.value)">

                <template v-slot:prepend-inner>
                    <v-icon :color="urlIconColor" size="20">{{ urlIcon }}</v-icon>
                </template>

                <template v-slot:append-inner>
                    <div class="d-flex align-center">
                        <v-btn v-if="urlInput && !urlLoading && showUrlDownload" icon="mdi-download" size="x-small"
                            variant="text" color="primary" @click="addUrlAsFile" :disabled="!isValidUrl(urlInput)">
                        </v-btn>
                        <v-btn v-if="urlInput && showUrlPreview" icon="mdi-eye" size="x-small" variant="text"
                            @click="$emit('url-preview', urlInput)">
                        </v-btn>
                    </div>
                </template>
            </v-text-field>

            <div v-if="urlInput && !isValidUrl(urlInput) && urlValidationMessage"
                class="text-caption text-warning mt-1">
                {{ urlValidationMessage }}
            </div>
        </div>

        <!-- File Input -->
        <div class="form-group">
            <label v-if="label" class="form-label">{{ label }}</label>
            <div class="file-upload-zone" @dragover.prevent="dragOver = true" @dragleave.prevent="dragOver = false"
                @drop.prevent="handleDrop">

                <v-file-input v-model="vFileInputFiles" :multiple="multiple" :accept="accept" :rules="rules"
                    variant="outlined" density="comfortable" :placeholder="placeholder" :prepend-icon="prependIcon"
                    :prepend-inner-icon="prependInnerIcon" show-size hide-details="auto"
                    :class="['modern-field', { 'drag-over': dragOver }]" @update:model-value="handleFileChange">

                    <!-- Custom selection display for multiple files -->
                    <template v-slot:selection="{ fileNames }">
                        <div class="d-flex flex-wrap ga-2">
                            <template v-for="(fileName, index) in fileNames" :key="fileName">
                                <v-chip v-if="index < maxPreviewChips" color="primary" size="small" closable
                                    @click:close="removeFileFromInput(index)">
                                    <v-icon start size="14">{{ getFileTypeIcon(fileName) }}</v-icon>
                                    {{ truncateFileName(fileName) }}
                                </v-chip>
                                <v-chip v-else-if="index === maxPreviewChips" color="grey" size="small"
                                    variant="outlined">
                                    +{{ fileNames.length - maxPreviewChips }} ملف(ات) أخرى
                                </v-chip>
                            </template>
                        </div>
                    </template>
                </v-file-input>
            </div>
        </div>

        <!-- File Preview List -->
        <div v-if="localFiles && localFiles.length > 0 && showFileList" class="mt-4">
            <div class="attachments-preview">
                <h4 class="text-subtitle-2 mb-3">
                    <v-icon size="16" class="me-1">mdi-file-multiple</v-icon>
                    {{ fileListTitle }} ({{ localFiles.length }})
                    <v-chip v-if="hasUrlFiles" size="x-small" color="info" class="ms-2">
                        {{ urlFileCount }} رابط
                    </v-chip>
                    <v-chip v-if="hasRegularFiles" size="x-small" color="primary" class="ms-1">
                        {{ regularFileCount }} ملف
                    </v-chip>
                </h4>
                <v-card elevation="1" class="attachments-list">
                    <v-list density="compact">
                        <v-list-item v-for="(file, index) in localFiles" :key="index" class="attachment-item">

                            <template v-slot:prepend>
                                <v-avatar size="32" :color="getFileTypeColor(file.name)">
                                    <v-icon color="white" size="16">
                                        {{ getFileTypeIcon(file.name) }}
                                    </v-icon>
                                </v-avatar>
                            </template>

                            <v-list-item-title class="text-body-2">
                                {{ file.name }}
                            </v-list-item-title>

                            <v-list-item-subtitle class="text-caption">
                                {{ formatFileSize(file.size) }}
                            </v-list-item-subtitle>

                            <template v-slot:append>
                                <div class="d-flex align-center ga-1">
                                    <v-btn v-if="showPreview" icon="mdi-eye" size="x-small" variant="text" color="info"
                                        @click="$emit('preview', file, index)" v-tooltip="'معاينة'">
                                    </v-btn>
                                    <v-btn v-if="showDownload" icon="mdi-download" size="x-small" variant="text"
                                        color="success" @click="downloadFile(file, index)" v-tooltip="'تحميل'">
                                    </v-btn>
                                    <v-btn icon="mdi-delete" size="x-small" variant="text" color="error"
                                        @click="removeFile(index)" v-tooltip="'حذف'">
                                    </v-btn>
                                </div>
                            </template>
                        </v-list-item>
                    </v-list>
                </v-card>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: 'FileUploadList',
    props: {
        modelValue: {
            type: Array,
            default: () => []
        },
        label: {
            type: String,
            default: ''
        },
        placeholder: {
            type: String,
            default: 'اختر الملفات...'
        },
        multiple: {
            type: Boolean,
            default: true
        },
        accept: {
            type: String,
            default: '.pdf,.jpg,.jpeg,.png,.doc,.docx,.xlsx,.xls'
        },
        rules: {
            type: Array,
            default: () => []
        },
        maxPreviewChips: {
            type: Number,
            default: 3
        },
        showFileList: {
            type: Boolean,
            default: true
        },
        fileListTitle: {
            type: String,
            default: 'الملفات المرفقة'
        },
        showPreview: {
            type: Boolean,
            default: true
        },
        showDownload: {
            type: Boolean,
            default: false
        },
        prependIcon: {
            type: String,
            default: ''
        },
        prependInnerIcon: {
            type: String,
            default: 'mdi-cloud-upload'
        },
        maxFileSize: {
            type: Number,
            default: 10 * 1024 * 1024 // 10MB
        },
        // URL input props
        showUploadTypeSelector: {
            type: Boolean,
            default: false
        },
        uploadTypeSelectorLabel: {
            type: String,
            default: 'طريقة إضافة الصورة'
        },
        showUrlInput: {
            type: Boolean,
            default: false
        },
        urlInputLabel: {
            type: String,
            default: 'رابط الصورة'
        },
        urlPlaceholder: {
            type: String,
            default: 'https://example.com/image.jpg'
        },
        urlIcon: {
            type: String,
            default: 'mdi-link'
        },
        urlIconColor: {
            type: String,
            default: 'warning'
        },
        urlRules: {
            type: Array,
            default: () => []
        },
        urlLoading: {
            type: Boolean,
            default: false
        },
        showUrlDownload: {
            type: Boolean,
            default: true
        },
        showUrlPreview: {
            type: Boolean,
            default: true
        },
        urlValidationMessage: {
            type: String,
            default: 'اضغط على زر التحميل لإضافة الملف للقائمة'
        },
        urlValidationPattern: {
            type: RegExp,
            default: null
        },
        defaultUploadType: {
            type: String,
            default: 'file'
        }
    },
    emits: ['update:modelValue', 'preview', 'download', 'file-removed', 'file-added', 'url-change', 'url-download', 'url-preview', 'upload-type-change'],
    data() {
        return {
            localFiles: [],
            uploadType: this.defaultUploadType || 'file',
            urlInput: '',
            dragOver: false,
            vFileInputFiles: [], // Separate array for v-file-input
            syncTimeout: null // Add this
        };
    },
    watch: {
        defaultUploadType: {
            immediate: true,
            handler(newValue) {
                if (newValue) {
                    this.uploadType = newValue;
                }
            }
        },
        modelValue: {
            immediate: true,
            handler(newValue) {
                if (JSON.stringify(this.localFiles) !== JSON.stringify(newValue || [])) {
                    this.localFiles = newValue || [];
                    this.updateVFileInputFiles();
                }
            }
        },
        showUploadTypeSelector: {
            immediate: true,
            handler(newValue) {
                if (newValue) {
                    this.uploadType = 'url';
                }
            }
        }
    },
    computed: {
        selectedImageFiles: {
            get() {
                return this.localFiles || [];
            },
            set(value) {
                this.localFiles = value || [];
            }
        },
        hasUrlFiles() {
            return this.localFiles.some(file => file.isUrlFile);
        },
        hasRegularFiles() {
            return this.localFiles.some(file => file instanceof File);
        },
        urlFileCount() {
            return this.localFiles.filter(file => file.isUrlFile).length;
        },
        regularFileCount() {
            return this.localFiles.filter(file => file instanceof File).length;
        }
    },
    beforeUnmount() {
        if (this.syncTimeout) {
            clearTimeout(this.syncTimeout);
        }
    },
    methods: {
        handleDrop(event) {
            this.dragOver = false;
            const files = Array.from(event.dataTransfer.files);

            if (files.length > 0) {
                // Filter out files that already exist
                const newFiles = files.filter(newFile => {
                    return !this.localFiles.some(existingFile =>
                        existingFile instanceof File &&
                        existingFile.name === newFile.name &&
                        existingFile.size === newFile.size &&
                        existingFile.lastModified === newFile.lastModified
                    );
                });

                if (newFiles.length > 0) {
                    if (this.multiple) {
                        this.localFiles = [...this.localFiles, ...newFiles];
                    } else {
                        this.localFiles = [newFiles[0]];
                    }

                    // Update v-file-input
                    this.updateVFileInputFiles();

                    this.$emit('update:modelValue', this.localFiles);
                    this.$emit('file-added', newFiles);

                    console.log('Dropped', newFiles.length, 'new files. Total now:', this.localFiles.length);
                }
            }
        },
        addUrlAsFile() {
            if (this.isValidUrl(this.urlInput)) {
                // Extract filename from URL, fallback to generic name based on detected type
                let fileName = this.urlInput.split('/').pop() || 'file';

                // If no extension in URL, try to add one based on content type detection
                if (!fileName.includes('.')) {
                    const detectedType = this.getFileTypeFromUrl(this.urlInput);
                    if (detectedType.startsWith('image/')) {
                        fileName += '.jpg';
                    } else if (detectedType.startsWith('video/')) {
                        fileName += '.mp4';
                    } else if (detectedType.startsWith('audio/')) {
                        fileName += '.mp3';
                    } else {
                        fileName += '.txt';
                    }
                }

                // Create a virtual file object for the URL
                const urlFile = {
                    name: fileName,
                    type: this.getFileTypeFromUrl(this.urlInput),
                    size: 0,
                    url: this.urlInput,
                    isUrlFile: true
                };

                // Add to existing files
                this.localFiles = [...this.localFiles, urlFile];

                // Emit the changes
                this.$emit('update:modelValue', this.localFiles);
                this.$emit('file-added', [urlFile]);

                // Clear the URL input
                this.urlInput = '';

                console.log('Added URL file. Total files now:', this.localFiles.length);
            }
        },

        getFileTypeFromUrl(url) {
            const extension = url.split('.').pop()?.toLowerCase();
            const typeMap = {
                // Images
                'jpg': 'image/jpeg',
                'jpeg': 'image/jpeg',
                'png': 'image/png',
                'gif': 'image/gif',
                'webp': 'image/webp',
                'bmp': 'image/bmp',
                'svg': 'image/svg+xml',

                // Documents
                'pdf': 'application/pdf',
                'doc': 'application/msword',
                'docx': 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
                'xls': 'application/vnd.ms-excel',
                'xlsx': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
                'ppt': 'application/vnd.ms-powerpoint',
                'pptx': 'application/vnd.openxmlformats-officedocument.presentationml.presentation',

                // Text files
                'txt': 'text/plain',
                'csv': 'text/csv',
                'json': 'application/json',
                'xml': 'text/xml',
                'html': 'text/html',
                'css': 'text/css',
                'js': 'text/javascript',

                // Audio
                'mp3': 'audio/mpeg',
                'wav': 'audio/wav',
                'ogg': 'audio/ogg',
                'aac': 'audio/aac',
                'm4a': 'audio/mp4',

                // Video
                'mp4': 'video/mp4',
                'avi': 'video/x-msvideo',
                'mov': 'video/quicktime',
                'wmv': 'video/x-ms-wmv',
                'webm': 'video/webm',
                'mkv': 'video/x-matroska',

                // Archives
                'zip': 'application/zip',
                'rar': 'application/x-rar-compressed',
                '7z': 'application/x-7z-compressed'
            };
            return typeMap[extension] || 'application/octet-stream';
        },
        updateVFileInputFiles() {
            // Keep vFileInputFiles containing only actual File objects
            this.vFileInputFiles = this.localFiles.filter(file => file instanceof File);
        },
        removeFileFromInput(chipIndex) {
            // Find the actual index in localFiles array
            const realFiles = this.localFiles.filter(file => file instanceof File);
            const fileToRemove = realFiles[chipIndex];

            if (fileToRemove) {
                const actualIndex = this.localFiles.indexOf(fileToRemove);
                if (actualIndex !== -1) {
                    this.removeFile(actualIndex);
                }
            }
        },
        handleFileChange(files) {
            console.log('handleFileChange called with:', files?.length || 0, 'files');
            console.log('Current localFiles:', this.localFiles.length);

            if (!files) {
                return;
            }

            const filesArray = Array.from(files);

            // Check if these are completely new files
            const newFiles = filesArray.filter(newFile => {
                return !this.localFiles.some(existingFile =>
                    existingFile instanceof File &&
                    existingFile.name === newFile.name &&
                    existingFile.size === newFile.size &&
                    existingFile.lastModified === newFile.lastModified
                );
            });

            if (newFiles.length > 0) {
                if (this.multiple) {
                    // Add new files to the main localFiles array
                    this.localFiles = [...this.localFiles, ...newFiles];
                } else {
                    // For single file mode, replace everything
                    this.localFiles = [newFiles[0]];
                    this.vFileInputFiles = [newFiles[0]];
                }

                console.log('Added new files:', newFiles.length, 'Total now:', this.localFiles.length);
                this.$emit('update:modelValue', this.localFiles);
                this.$emit('file-added', newFiles);
            }

            // Keep vFileInputFiles in sync with actual File objects only
            this.updateVFileInputFiles();
        },

        handleUrlChange(url) {
            console.log('FileUploadList handleUrlChange called with:', url, typeof url);
            this.urlInput = url || '';  // Ensure it's always a string
            this.$emit('url-change', this.urlInput);  // Always emit the stored value
        },

        isValidUrl(url) {
            if (!url) return false;

            // If there's a specific validation pattern, use it
            if (this.urlValidationPattern) {
                return this.urlValidationPattern.test(url);
            }

            // Otherwise, just check if it's a valid URL format
            try {
                new URL(url);
                return true;
            } catch {
                return false;
            }
        },
        removeFile(index) {
            if (this.localFiles && this.localFiles.length > index) {
                const removedFile = this.localFiles[index];
                this.localFiles = this.localFiles.filter((_, i) => i !== index);

                // Update the v-file-input files array
                this.updateVFileInputFiles();

                this.$emit('update:modelValue', this.localFiles);
                this.$emit('file-removed', removedFile, index);

                console.log('Removed file at index', index, '. Total files now:', this.localFiles.length);
            }
        },

        downloadFile(file, index) {
            // Handle URL files differently
            if (file.isUrlFile && file.url) {
                // For URL files, open in new tab or direct download
                const link = document.createElement('a');
                link.href = file.url;
                link.target = '_blank';
                link.download = file.name;
                document.body.appendChild(link);
                link.click();
                document.body.removeChild(link);
                return;
            }

            this.$emit('download', file, index);

            // Default download behavior for regular files
            if (file instanceof File) {
                const url = URL.createObjectURL(file);
                const a = document.createElement('a');
                a.href = url;
                a.download = file.name;
                document.body.appendChild(a);
                a.click();
                document.body.removeChild(a);
                URL.revokeObjectURL(url);
            }
        },

        truncateFileName(fileName, maxLength = 20) {
            if (fileName.length <= maxLength) return fileName;
            const extension = fileName.split('.').pop();
            const nameWithoutExt = fileName.substring(0, fileName.lastIndexOf('.'));
            const truncatedName = nameWithoutExt.substring(0, maxLength - extension.length - 4);
            return `${truncatedName}...${extension}`;
        },

        getFileTypeIcon(fileName) {
            const extension = fileName.split('.').pop().toLowerCase();
            const iconMap = {
                pdf: 'mdi-file-pdf-box',
                jpg: 'mdi-file-image',
                jpeg: 'mdi-file-image',
                png: 'mdi-file-image',
                gif: 'mdi-file-image',
                webp: 'mdi-file-image',
                doc: 'mdi-file-word',
                docx: 'mdi-file-word',
                xlsx: 'mdi-file-excel',
                xls: 'mdi-file-excel',
                ppt: 'mdi-file-powerpoint',
                pptx: 'mdi-file-powerpoint',
                txt: 'mdi-file-document-outline',
                zip: 'mdi-folder-zip',
                rar: 'mdi-folder-zip'
            };
            return iconMap[extension] || 'mdi-file-document';
        },

        getFileTypeColor(fileName) {
            const extension = fileName.split('.').pop().toLowerCase();
            const colorMap = {
                pdf: 'red',
                jpg: 'green',
                jpeg: 'green',
                png: 'green',
                gif: 'green',
                webp: 'green',
                doc: 'blue',
                docx: 'blue',
                xlsx: 'green',
                xls: 'green',
                ppt: 'orange',
                pptx: 'orange',
                txt: 'grey',
                zip: 'purple',
                rar: 'purple'
            };
            return colorMap[extension] || 'grey';
        },

        formatFileSize(bytes) {
            if (bytes === 0) return '0 Bytes';
            const k = 1024;
            const sizes = ['Bytes', 'KB', 'MB', 'GB'];
            const i = Math.floor(Math.log(bytes) / Math.log(k));
            return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
        },

        // Public methods for external use
        clearFiles() {
            this.localFiles = [];
            this.vFileInputFiles = [];
            this.urlInput = '';
            this.$emit('update:modelValue', this.localFiles);
        },

        clearUrl() {
            this.urlInput = '';
            this.$emit('url-change', '');
        },

        setUploadType(type) {
            this.uploadType = type;
        },

        setUrlInput(url) {
            this.urlInput = url;
        },

        addFiles(files) {
            const newFiles = [...(this.localFiles || []), ...files];
            this.localFiles = newFiles;
            this.$emit('update:modelValue', this.localFiles);
            this.$emit('file-added', files);
        },

        validateFileSize(file) {
            return file.size <= this.maxFileSize;
        },

        validateFileType(file) {
            if (!this.accept) return true;
            const allowedTypes = this.accept.split(',').map(type => type.trim());
            const fileExtension = '.' + file.name.split('.').pop().toLowerCase();
            return allowedTypes.includes(fileExtension);
        }
    }
};
</script>

<style scoped>
.file-upload-container {
    width: 100%;
}

.form-group {
    margin-bottom: 16px;
}

.form-label {
    display: block;
    font-weight: 500;
    font-size: 0.875rem;
    color: #374151;
    margin-bottom: 8px;
}

.modern-field {
    border-radius: 8px;
}

.attachments-preview {
    margin-top: 16px;
}

.attachments-list {
    border: 1px solid #e2e8f0;
    border-radius: 8px;
    max-height: 300px;
    overflow-y: auto;
}

.attachment-item {
    padding: 8px 16px;
    transition: background-color 0.2s ease;
}

.attachment-item:hover {
    background-color: #f8fafc;
}

/* Scrollbar styling */
.attachments-list::-webkit-scrollbar {
    width: 6px;
}

.attachments-list::-webkit-scrollbar-track {
    background: #f1f5f9;
    border-radius: 3px;
}

.attachments-list::-webkit-scrollbar-thumb {
    background: #cbd5e0;
    border-radius: 3px;
}

.attachments-list::-webkit-scrollbar-thumb:hover {
    background: #94a3b8;
}

/* Upload type selector styling */
.upload-type-selector {
    margin-top: 8px;
}

.upload-type-selector .v-radio {
    margin-right: 24px;
}

@media (max-width: 600px) {
    .attachments-list {
        max-height: 200px;
    }

    .attachment-item {
        padding: 12px 16px;
    }
}

.file-upload-zone {
    transition: all 0.3s ease;
}

.drag-over .v-field {
    border-color: #1976d2 !important;
    background-color: rgba(25, 118, 210, 0.04) !important;
}
</style>