<template>
    <v-dialog v-model="dialog" max-width="95vw" max-height="95vh" persistent scrollable @keydown="handleKeydown">
        <v-card rounded="xl" elevation="8" class="file-preview-card">
            <!-- Header -->
            <v-card-title class="preview-header">
                <div class="d-flex align-center">
                    <v-avatar :color="fileTypeColor" size="40" class="me-3">
                        <v-icon color="white" size="20">{{ fileTypeIcon }}</v-icon>
                    </v-avatar>
                    <div class="flex-grow-1">
                        <div class="text-h6 font-weight-bold">{{ fileName }}</div>
                        <div class="text-caption text-medium-emphasis">
                            {{ fileSize }} • {{ fileType.toUpperCase() }}
                            <span v-if="isMultipleFiles"> • {{ currentFileIndex + 1 }} من {{ files.length }}</span>
                        </div>
                    </div>
                </div>
                <div class="d-flex align-center ga-2">
                    <!-- Navigation buttons for multiple files -->
                    <template v-if="isMultipleFiles">
                        <v-btn 
                            icon="mdi-chevron-right" 
                            variant="text" 
                            @click="previousFile"
                            :disabled="currentFileIndex === 0 && !infiniteLoop"
                            v-tooltip="'الملف السابق'">
                        </v-btn>
                        <v-btn 
                            icon="mdi-chevron-left" 
                            variant="text" 
                            @click="nextFile"
                            :disabled="currentFileIndex === files.length - 1 && !infiniteLoop"
                            v-tooltip="'الملف التالي'">
                        </v-btn>
                        <v-divider vertical class="mx-2"></v-divider>
                    </template>
                    
                    <v-btn v-if="isPdf" icon="mdi-fullscreen" variant="text" @click="openFullscreen"
                        v-tooltip="'ملء الشاشة'"></v-btn>
                    <v-btn icon="mdi-download" variant="text" @click="downloadFile" v-tooltip="'تحميل الملف'"></v-btn>
                    <v-btn v-if="allowShare" icon="mdi-share" variant="text" @click="shareFile" v-tooltip="'مشاركة'"></v-btn>
                    <v-btn icon="mdi-close" variant="text" @click="closePreview"></v-btn>
                </div>
            </v-card-title>

            <v-divider></v-divider>

            <!-- Content -->
            <v-card-text class="preview-content pa-0">
                <div class="preview-container">
                    <!-- Loading State -->
                    <div v-if="loading" class="loading-container">
                        <v-progress-circular indeterminate color="primary" size="48"></v-progress-circular>
                        <p class="text-body-1 mt-4">جاري تحميل الملف...</p>
                    </div>

                    <!-- Error State -->
                    <div v-else-if="error" class="error-container">
                        <v-icon size="64" color="error">mdi-alert-circle</v-icon>
                        <h3 class="text-h6 mt-4 mb-2">فشل في تحميل الملف</h3>
                        <p class="text-body-2 text-medium-emphasis mb-4">{{ error }}</p>
                        <v-btn color="primary" @click="retryLoad">إعادة المحاولة</v-btn>
                    </div>

                    <!-- Image Preview -->
                    <div v-else-if="isImage" class="image-preview">
                        <v-img :src="fileUrl" contain class="preview-image" max-height="70vh">
                            <!-- Navigation arrows overlay for images -->
                            <template v-if="isMultipleFiles">
                                <v-btn 
                                    icon="mdi-chevron-left" 
                                    class="nav-btn nav-btn-left"
                                    @click="previousFile"
                                    :disabled="currentFileIndex === 0 && !infiniteLoop">
                                </v-btn>
                                <v-btn 
                                    icon="mdi-chevron-right" 
                                    class="nav-btn nav-btn-right"
                                    @click="nextFile"
                                    :disabled="currentFileIndex === files.length - 1 && !infiniteLoop">
                                </v-btn>
                            </template>

                            <!-- File counter overlay -->
                            <div v-if="isMultipleFiles && showCounter" class="file-counter">
                                {{ currentFileIndex + 1 }} / {{ files.length }}
                            </div>

                            <template v-slot:error>
                                <div class="image-error">
                                    <v-icon size="48" color="error">mdi-image-broken</v-icon>
                                    <p class="mt-2">لا يمكن عرض الصورة</p>
                                </div>
                            </template>
                        </v-img>
                    </div>

                    <!-- PDF Preview -->
                    <div v-else-if="isPdf" class="pdf-preview">
                        <iframe :src="pdfViewerUrl" class="pdf-iframe" frameborder="0" allow="fullscreen"></iframe>
                    </div>

                    <!-- Text/Code Preview -->
                    <div v-else-if="isTextFile" class="text-preview">
                        <pre class="text-content"><code>{{ textContent }}</code></pre>
                    </div>

                    <!-- CSV Preview -->
                    <div v-else-if="isCsv" class="csv-preview">
                        <v-data-table :headers="csvHeaders" :items="csvData" density="compact" class="csv-table"
                            :items-per-page="50" :show-current-page="true"></v-data-table>
                    </div>

                    <!-- Excel Preview -->
                    <div v-else-if="isExcel" class="excel-preview">
                        <v-tabs v-model="activeSheet" class="mb-4">
                            <v-tab v-for="(sheet, index) in excelSheets" :key="index" :value="index">
                                {{ sheet.name }}
                            </v-tab>
                        </v-tabs>
                        <v-data-table :headers="currentSheetHeaders" :items="currentSheetData" density="compact"
                            class="excel-table" :items-per-page="50"></v-data-table>
                    </div>

                    <!-- Audio Preview -->
                    <div v-else-if="isAudio" class="audio-preview">
                        <div class="audio-container">
                            <v-icon size="80" color="primary" class="mb-4">mdi-music-note</v-icon>
                            <audio controls class="audio-player">
                                <source :src="fileUrl" :type="currentFile.type">
                                متصفحك لا يدعم تشغيل الملفات الصوتية
                            </audio>
                        </div>
                    </div>

                    <!-- Video Preview -->
                    <div v-else-if="isVideo" class="video-preview">
                        <video controls class="video-player">
                            <source :src="fileUrl" :type="currentFile.type">
                            متصفحك لا يدعم تشغيل الفيديو
                        </video>
                    </div>

                    <!-- Unsupported File Type -->
                    <div v-else class="unsupported-preview">
                        <v-icon size="80" color="grey" class="mb-4">{{ fileTypeIcon }}</v-icon>
                        <h3 class="text-h6 mb-2">{{ fileName }}</h3>
                        <p class="text-body-2 text-medium-emphasis mb-4">
                            لا يمكن معاينة هذا النوع من الملفات
                        </p>
                        <div class="file-info">
                            <v-chip class="ma-1">{{ fileType.toUpperCase() }}</v-chip>
                            <v-chip class="ma-1">{{ fileSize }}</v-chip>
                        </div>
                        <v-btn color="primary" size="large" class="mt-4" @click="downloadFile">
                            <v-icon start>mdi-download</v-icon>
                            تحميل الملف
                        </v-btn>
                    </div>
                </div>
            </v-card-text>

            <!-- File thumbnails strip for multiple files -->
            <div v-if="isMultipleFiles && showThumbnails" class="thumbnail-strip">
                <div class="thumbnails-container">
                    <div 
                        v-for="(file, index) in files" 
                        :key="index"
                        class="thumbnail-item"
                        :class="{ active: index === currentFileIndex }"
                        @click="goToFile(index)">
                        
                        <!-- Image thumbnail -->
                        <template v-if="getFileType(file).isImage">
                            <v-img 
                                :src="getFileUrl(file)" 
                                width="60" 
                                height="60" 
                                cover
                                class="thumbnail-preview">
                                <template v-slot:error>
                                    <div class="thumbnail-error">
                                        <v-icon size="16" color="grey">mdi-image-broken</v-icon>
                                    </div>
                                </template>
                            </v-img>
                        </template>
                        
                        <!-- File type icon for non-images -->
                        <template v-else>
                            <div class="file-thumbnail" :style="{ backgroundColor: getFileType(file).color }">
                                <v-icon color="white" size="24">{{ getFileType(file).icon }}</v-icon>
                            </div>
                        </template>
                        
                        <!-- File name -->
                        <div class="thumbnail-name">{{ getFileName(file) }}</div>
                    </div>
                </div>
            </div>
        </v-card>
    </v-dialog>
</template>

<script>
import * as XLSX from 'xlsx'

export default {
    name: 'FilePreviewDialog',
    props: {
        modelValue: {
            type: Boolean,
            default: false
        },
        // Support both single file and multiple files
        file: {
            type: [File, Object],
            default: null
        },
        files: {
            type: Array,
            default: () => []
        },
        initialIndex: {
            type: Number,
            default: 0
        },
        showThumbnails: {
            type: Boolean,
            default: true
        },
        showCounter: {
            type: Boolean,
            default: true
        },
        allowShare: {
            type: Boolean,
            default: false
        },
        infiniteLoop: {
            type: Boolean,
            default: true
        }
    },
    emits: ['update:modelValue', 'file-change', 'download', 'share', 'close'],
    data() {
        return {
            loading: false,
            error: null,
            textContent: '',
            csvHeaders: [],
            csvData: [],
            excelSheets: [],
            activeSheet: 0,
            workbook: null,
            currentFileIndex: 0,
            createdFileUrls: new Map() // Track created URLs for cleanup
        }
    },
    computed: {
        dialog: {
            get() {
                return this.modelValue
            },
            set(value) {
                this.$emit('update:modelValue', value)
            }
        },
        
        // Determine if we're working with multiple files
        isMultipleFiles() {
            return this.normalizedFiles.length > 1
        },
        
        // Normalize files prop to always work with an array
        normalizedFiles() {
            if (this.files && this.files.length > 0) {
                return this.files
            } else if (this.file) {
                return [this.file]
            }
            return []
        },
        
        // Current file being previewed
        currentFile() {
            return this.normalizedFiles[this.currentFileIndex] || null
        },
        
        fileName() {
            return this.getFileName(this.currentFile)
        },
        
        fileSize() {
            return this.getFileSize(this.currentFile)
        },
        
        fileType() {
            return this.getFileExtension(this.currentFile)
        },
        
        fileTypeIcon() {
            return this.getFileType(this.currentFile).icon
        },
        
        fileTypeColor() {
            return this.getFileType(this.currentFile).color
        },
        
        isImage() {
            return this.getFileType(this.currentFile).isImage
        },
        
        isPdf() {
            return this.fileType === 'pdf'
        },
        
        pdfViewerUrl() {
            return this.fileUrl || ''
        },
        
        isTextFile() {
            return ['txt', 'json', 'xml', 'js', 'ts', 'html', 'css', 'vue', 'md'].includes(this.fileType)
        },
        
        isCsv() {
            return this.fileType === 'csv'
        },
        
        isExcel() {
            return ['xls', 'xlsx'].includes(this.fileType)
        },
        
        isAudio() {
            return ['mp3', 'wav', 'ogg', 'aac', 'm4a'].includes(this.fileType)
        },
        
        isVideo() {
            return ['mp4', 'avi', 'mov', 'wmv', 'webm', 'mkv'].includes(this.fileType)
        },
        
        currentSheetHeaders() {
            if (!this.excelSheets[this.activeSheet]?.headers) return []
            return this.excelSheets[this.activeSheet].headers.map(header => ({
                title: header,
                value: header,
                sortable: true
            }))
        },
        
        currentSheetData() {
            return this.excelSheets[this.activeSheet]?.data || []
        },
        
        fileUrl() {
            return this.getFileUrl(this.currentFile)
        }
    },
    watch: {
        normalizedFiles: {
            immediate: true,
            handler(newFiles) {
                if (newFiles.length > 0) {
                    this.currentFileIndex = Math.max(0, Math.min(this.initialIndex, newFiles.length - 1))
                    this.loadFile()
                }
            }
        },
        currentFileIndex(newIndex, oldIndex) {
            if (newIndex !== oldIndex) {
                this.loadFile()
                this.$emit('file-change', {
                    index: newIndex,
                    file: this.currentFile,
                    url: this.fileUrl
                })
            }
        },
        modelValue(newValue) {
            if (!newValue) {
                this.cleanup()
            } else if (newValue && this.normalizedFiles.length > 0) {
                this.currentFileIndex = Math.max(0, Math.min(this.initialIndex, this.normalizedFiles.length - 1))
                this.loadFile()
            }
        }
    },
    methods: {
        getFileName(file) {
            return file?.name || 'ملف غير معروف'
        },
        
        getFileSize(file) {
            if (file?.size && file.size > 0) {
                const bytes = file.size;
                const sizes = ['Bytes', 'KB', 'MB', 'GB'];
                const i = Math.floor(Math.log(bytes) / Math.log(1024));
                return `${(bytes / Math.pow(1024, i)).toFixed(1)} ${sizes[i]}`;
            }
            return 'حجم غير معروف';
        },
        
        getFileExtension(file) {
            return file?.name.split('.').pop()?.toLowerCase() || ''
        },
        
        getFileType(file) {
            const ext = this.getFileExtension(file)
            
            const typeMap = {
                // Images
                jpg: { icon: 'mdi-file-image', color: '#4CAF50', isImage: true },
                jpeg: { icon: 'mdi-file-image', color: '#4CAF50', isImage: true },
                png: { icon: 'mdi-file-image', color: '#4CAF50', isImage: true },
                gif: { icon: 'mdi-file-image', color: '#4CAF50', isImage: true },
                webp: { icon: 'mdi-file-image', color: '#4CAF50', isImage: true },
                svg: { icon: 'mdi-file-image', color: '#4CAF50', isImage: true },
                bmp: { icon: 'mdi-file-image', color: '#4CAF50', isImage: true },
                
                // Documents
                pdf: { icon: 'mdi-file-pdf-box', color: '#F44336', isImage: false },
                doc: { icon: 'mdi-file-word', color: '#2196F3', isImage: false },
                docx: { icon: 'mdi-file-word', color: '#2196F3', isImage: false },
                xls: { icon: 'mdi-file-excel', color: '#4CAF50', isImage: false },
                xlsx: { icon: 'mdi-file-excel', color: '#4CAF50', isImage: false },
                ppt: { icon: 'mdi-file-powerpoint', color: '#FF9800', isImage: false },
                pptx: { icon: 'mdi-file-powerpoint', color: '#FF9800', isImage: false },
                
                // Text
                txt: { icon: 'mdi-file-document', color: '#9E9E9E', isImage: false },
                csv: { icon: 'mdi-file-table', color: '#FF9800', isImage: false },
                json: { icon: 'mdi-code-json', color: '#9C27B0', isImage: false },
                xml: { icon: 'mdi-file-code', color: '#9C27B0', isImage: false },
                
                // Code
                js: { icon: 'mdi-language-javascript', color: '#FFC107', isImage: false },
                ts: { icon: 'mdi-language-typescript', color: '#2196F3', isImage: false },
                html: { icon: 'mdi-language-html5', color: '#FF5722', isImage: false },
                css: { icon: 'mdi-language-css3', color: '#2196F3', isImage: false },
                vue: { icon: 'mdi-vuejs', color: '#4CAF50', isImage: false },
                md: { icon: 'mdi-language-markdown', color: '#9E9E9E', isImage: false },
                
                // Archives
                zip: { icon: 'mdi-folder-zip', color: '#795548', isImage: false },
                rar: { icon: 'mdi-folder-zip', color: '#795548', isImage: false },
                '7z': { icon: 'mdi-folder-zip', color: '#795548', isImage: false },
                
                // Audio
                mp3: { icon: 'mdi-file-music', color: '#9C27B0', isImage: false },
                wav: { icon: 'mdi-file-music', color: '#9C27B0', isImage: false },
                ogg: { icon: 'mdi-file-music', color: '#9C27B0', isImage: false },
                aac: { icon: 'mdi-file-music', color: '#9C27B0', isImage: false },
                m4a: { icon: 'mdi-file-music', color: '#9C27B0', isImage: false },
                
                // Video
                mp4: { icon: 'mdi-file-video', color: '#3F51B5', isImage: false },
                avi: { icon: 'mdi-file-video', color: '#3F51B5', isImage: false },
                mov: { icon: 'mdi-file-video', color: '#3F51B5', isImage: false },
                wmv: { icon: 'mdi-file-video', color: '#3F51B5', isImage: false },
                webm: { icon: 'mdi-file-video', color: '#3F51B5', isImage: false },
                mkv: { icon: 'mdi-file-video', color: '#3F51B5', isImage: false }
            }
            
            return typeMap[ext] || { icon: 'mdi-file', color: '#9E9E9E', isImage: false }
        },
        
        getFileUrl(file) {
            if (!file) return null
            
            // Support URL strings
            if (file.url) {
                return file.url
            }
            
            // Handle regular File objects
            if (file instanceof File) {
                if (!this.createdFileUrls.has(file)) {
                    this.createdFileUrls.set(file, URL.createObjectURL(file))
                }
                return this.createdFileUrls.get(file)
            }
            
            return null
        },
        
        async loadFile() {
            if (!this.currentFile) return

            this.loading = true
            this.error = null

            try {
                // Load content based on file type
                if (this.isTextFile) {
                    await this.loadTextContent()
                } else if (this.isCsv) {
                    await this.loadCsvContent()
                } else if (this.isExcel) {
                    await this.loadExcelContent()
                }
            } catch (err) {
                console.error('Error loading file:', err)
                this.error = 'فشل في تحميل محتوى الملف'
            } finally {
                this.loading = false
            }
        },

        async loadTextContent() {
            if (this.currentFile instanceof File) {
                const text = await this.currentFile.text()
                this.textContent = text
            }
        },

        async loadCsvContent() {
            if (this.currentFile instanceof File) {
                const text = await this.currentFile.text()
                const lines = text.split('\n').filter(line => line.trim())

                if (lines.length === 0) return

                const headers = lines[0].split(',').map(h => h.trim().replace(/"/g, ''))
                this.csvHeaders = headers.map(header => ({
                    title: header,
                    value: header,
                    sortable: true
                }))

                this.csvData = lines.slice(1).map(line => {
                    const values = line.split(',').map(v => v.trim().replace(/"/g, ''))
                    const row = {}
                    headers.forEach((header, index) => {
                        row[header] = values[index] || ''
                    })
                    return row
                })
            }
        },

        async loadExcelContent() {
            if (this.currentFile instanceof File) {
                const arrayBuffer = await this.currentFile.arrayBuffer()
                this.workbook = XLSX.read(arrayBuffer, { type: 'array' })

                this.excelSheets = this.workbook.SheetNames.map(sheetName => {
                    const worksheet = this.workbook.Sheets[sheetName]
                    const jsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 })

                    if (jsonData.length === 0) {
                        return { name: sheetName, headers: [], data: [] }
                    }

                    const headers = jsonData[0] || []
                    const data = jsonData.slice(1).map(row => {
                        const rowObj = {}
                        headers.forEach((header, index) => {
                            rowObj[header] = row[index] || ''
                        })
                        return rowObj
                    })

                    return {
                        name: sheetName,
                        headers,
                        data
                    }
                })
            }
        },
        
        // Navigation methods
        nextFile() {
            if (this.currentFileIndex < this.normalizedFiles.length - 1) {
                this.currentFileIndex++
            } else if (this.infiniteLoop) {
                this.currentFileIndex = 0
            }
        },

        previousFile() {
            if (this.currentFileIndex > 0) {
                this.currentFileIndex--
            } else if (this.infiniteLoop) {
                this.currentFileIndex = this.normalizedFiles.length - 1
            }
        },

        goToFile(index) {
            if (index >= 0 && index < this.normalizedFiles.length) {
                this.currentFileIndex = index
            }
        },
        
        // Keyboard navigation
        handleKeydown(event) {
            if (!this.isMultipleFiles) return
            
            switch (event.key) {
                case 'ArrowRight':
                    event.preventDefault()
                    this.nextFile()
                    break
                case 'ArrowLeft':
                    event.preventDefault()
                    this.previousFile()
                    break
                case 'Escape':
                    event.preventDefault()
                    this.closePreview()
                    break
                case 'Home':
                    event.preventDefault()
                    this.goToFile(0)
                    break
                case 'End':
                    event.preventDefault()
                    this.goToFile(this.normalizedFiles.length - 1)
                    break
            }
        },

        downloadFile() {
            const file = this.currentFile
            const url = this.fileUrl
            
            this.$emit('download', {
                file: file,
                url: url,
                filename: this.fileName,
                index: this.currentFileIndex
            })

            if (file?.url && file?.isUrlFile) {
                // For URL files, open in new tab or download directly from URL
                window.open(file.url, '_blank')
                return
            }

            if (!url) return

            const link = document.createElement('a')
            link.href = url
            link.download = this.fileName
            document.body.appendChild(link)
            link.click()
            document.body.removeChild(link)
        },
        
        shareFile() {
            const fileData = {
                file: this.currentFile,
                url: this.fileUrl,
                index: this.currentFileIndex,
                filename: this.fileName
            }

            this.$emit('share', fileData)

            // Default share behavior (if Web Share API is available)
            if (navigator.share) {
                navigator.share({
                    title: this.fileName,
                    text: `ملف ${this.currentFileIndex + 1} من ${this.normalizedFiles.length}`,
                    url: this.fileUrl
                }).catch(console.error)
            } else {
                // Fallback: copy to clipboard
                navigator.clipboard.writeText(this.fileUrl).then(() => {
                    console.log('File URL copied to clipboard')
                })
            }
        },

        openFullscreen() {
            if (this.fileUrl && this.isPdf) {
                window.open(this.fileUrl, '_blank')
            }
        },

        retryLoad() {
            this.loadFile()
        },

        closePreview() {
            this.dialog = false
            this.$emit('close')
        },

        cleanup() {
            // Clean up created URLs
            this.createdFileUrls.forEach(url => {
                URL.revokeObjectURL(url)
            })
            this.createdFileUrls.clear()
            
            this.textContent = ''
            this.csvHeaders = []
            this.csvData = []
            this.excelSheets = []
            this.activeSheet = 0
            this.workbook = null
            this.error = null
            this.loading = false
        }
    },

    beforeUnmount() {
        this.cleanup()
    }
}
</script>

<style scoped>
.file-preview-card {
    height: 95vh;
    display: flex;
    flex-direction: column;
}

.preview-header {
    padding: 20px 24px;
    background: #f8fafc;
    border-bottom: 1px solid #e2e8f0;
    flex-shrink: 0;
}

.preview-content {
    flex: 1;
    overflow: hidden;
}

.preview-container {
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
}

/* Loading & Error States */
.loading-container,
.error-container,
.unsupported-preview {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    text-align: center;
    padding: 40px;
    height: 100%;
}

/* Image Preview */
.image-preview {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 20px;
    position: relative;
}

.preview-image {
    max-width: 100%;
    border-radius: 8px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.image-error {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 200px;
    color: #64748b;
}

/* Navigation buttons for images */
.nav-btn {
    position: absolute;
    top: 50%;
    transform: translateY(-50%);
    background: rgba(0, 0, 0, 0.6) !important;
    color: white !important;
    z-index: 2;
    transition: all 0.3s ease;
}

.nav-btn:hover:not(:disabled) {
    background: rgba(0, 0, 0, 0.8) !important;
    transform: translateY(-50%) scale(1.1);
}

.nav-btn:disabled {
    opacity: 0.3;
}

.nav-btn-left {
    left: 16px;
}

.nav-btn-right {
    right: 16px;
}

.file-counter {
    position: absolute;
    top: 16px;
    right: 16px;
    background: rgba(0, 0, 0, 0.7);
    color: white!important;;
    padding: 8px 12px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 600;
    z-index: 2;
}

/* PDF Preview */
.pdf-preview {
    width: 100%;
    height: 100%;
    padding: 0;
    display: flex;
    flex-direction: column;
}

.pdf-iframe {
    width: 100%;
    height: 100%;
    min-height: 600px;
    border: none;
    flex: 1;
}

/* Text Preview */
.text-preview {
    width: 100%;
    height: 100%;
    padding: 20px;
    overflow: auto;
}

.text-content {
    background: #f8fafc;
    border: 1px solid #e2e8f0;
    border-radius: 8px;
    padding: 20px;
    font-family: 'Courier New', monospace;
    font-size: 14px;
    line-height: 1.5;
    white-space: pre-wrap;
    word-wrap: break-word;
    margin: 0;
    max-height: 100%;
    overflow: auto;
}

/* CSV & Excel Preview */
.csv-preview,
.excel-preview {
    width: 100%;
    height: 100%;
    padding: 20px;
    overflow: auto;
}

.csv-table,
.excel-table {
    background: white;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

/* Audio Preview */
.audio-preview {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
}

.audio-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 40px;
}

.audio-player {
    width: 300px;
    margin-top: 20px;
}

/* Video Preview */
.video-preview {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 20px;
}

.video-player {
    max-width: 100%;
    max-height: 100%;
    border-radius: 8px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

/* Thumbnail Strip */
.thumbnail-strip {
    flex-shrink: 0;
    background: #fafafa;
    border-top: 1px solid #e0e0e0;
    padding: 16px 24px;
    max-height: 120px;
    overflow: hidden;
}

.thumbnails-container {
    display: flex;
    gap: 12px;
    overflow-x: auto;
    padding-bottom: 8px;
}

.thumbnails-container::-webkit-scrollbar {
    height: 6px;
}

.thumbnails-container::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 3px;
}

.thumbnails-container::-webkit-scrollbar-thumb {
    background: #c1c1c1;
    border-radius: 3px;
}

.thumbnails-container::-webkit-scrollbar-thumb:hover {
    background: #a8a8a8;
}

.thumbnail-item {
    flex-shrink: 0;
    cursor: pointer;
    border-radius: 8px;
    overflow: hidden;
    border: 2px solid transparent;
    transition: all 0.2s ease;
    display: flex;
    flex-direction: column;
    align-items: center;
    width: 80px;
}

.thumbnail-item:hover {
    border-color: #1976d2;
    transform: scale(1.05);
}

.thumbnail-item.active {
    border-color: #1976d2;
    box-shadow: 0 0 0 2px rgba(25, 118, 210, 0.2);
}

.thumbnail-preview {
    border-radius: 6px;
    margin-bottom: 4px;
}

.file-thumbnail {
    width: 60px;
    height: 60px;
    border-radius: 6px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 4px;
}

.thumbnail-error {
    width: 60px;
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f5f5f5;
    border-radius: 6px;
    margin-bottom: 4px;
}

.thumbnail-name {
    font-size: 10px;
    text-align: center;
    color: #666;
    max-width: 76px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    line-height: 1.2;
}

.file-info {
    margin-top: 16px;
}

/* Responsive Design */
@media (max-width: 768px) {
    .file-preview-card {
        height: 98vh;
        margin: 10px;
    }

    .preview-header {
        padding: 16px;
    }

    .preview-container {
        padding: 16px;
    }

    .text-content {
        font-size: 12px;
        padding: 16px;
    }

    .audio-player {
        width: 250px;
    }

    .pdf-iframe {
        min-height: 500px;
    }

    .thumbnail-strip {
        padding: 12px 16px;
        max-height: 100px;
    }

    .thumbnail-item {
        width: 70px;
    }

    .file-thumbnail {
        width: 50px;
        height: 50px;
    }

    .thumbnail-preview {
        width: 50px;
        height: 50px;
    }

    .thumbnail-error {
        width: 50px;
        height: 50px;
    }

    .thumbnail-name {
        font-size: 9px;
        max-width: 66px;
    }

    .nav-btn {
        width: 40px;
        height: 40px;
    }

    .nav-btn-left {
        left: 8px;
    }

    .nav-btn-right {
        right: 8px;
    }

    .file-counter {
        font-size: 10px;
        padding: 6px 8px;
    }
}

@media (max-width: 480px) {
    .preview-header {
        padding: 12px;
    }

    .thumbnail-strip {
        padding: 8px 12px;
        max-height: 90px;
    }

    .thumbnail-item {
        width: 60px;
        gap: 8px;
    }

    .file-thumbnail {
        width: 40px;
        height: 40px;
    }

    .thumbnail-preview {
        width: 40px;
        height: 40px;
    }

    .thumbnail-error {
        width: 40px;
        height: 40px;
    }

    .thumbnail-name {
        font-size: 8px;
        max-width: 56px;
    }
}

/* Animation */
.preview-image,
.text-content,
.csv-table,
.excel-table {
    transition: opacity 0.3s ease;
}
</style>