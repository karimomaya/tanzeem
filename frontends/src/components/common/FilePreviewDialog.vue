<template>
    <v-dialog v-model="dialog" max-width="95vw" max-height="95vh" persistent scrollable>
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
                        </div>
                    </div>
                </div>
                <div class="d-flex align-center ga-2">
                    <v-btn
                        v-if="isPdf"
                        icon="mdi-fullscreen"
                        variant="text"
                        @click="openFullscreen"
                        v-tooltip="'ملء الشاشة'"
                    ></v-btn>
                    <v-btn
                        icon="mdi-download"
                        variant="text"
                        @click="downloadFile"
                        v-tooltip="'تحميل الملف'"
                    ></v-btn>
                    <v-btn
                        icon="mdi-close"
                        variant="text"
                        @click="closePreview"
                    ></v-btn>
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
                        <v-img
                            :src="fileUrl"
                            contain
                            class="preview-image"
                            max-height="70vh"
                        >
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
                        <iframe
                            :src="pdfViewerUrl"
                            class="pdf-iframe"
                            frameborder="0"
                            allow="fullscreen"
                        ></iframe>
                    </div>

                    <!-- Text/Code Preview -->
                    <div v-else-if="isTextFile" class="text-preview">
                        <pre class="text-content"><code>{{ textContent }}</code></pre>
                    </div>

                    <!-- CSV Preview -->
                    <div v-else-if="isCsv" class="csv-preview">
                        <v-data-table
                            :headers="csvHeaders"
                            :items="csvData"
                            density="compact"
                            class="csv-table"
                            :items-per-page="50"
                            :show-current-page="true"
                        ></v-data-table>
                    </div>

                    <!-- Excel Preview -->
                    <div v-else-if="isExcel" class="excel-preview">
                        <v-tabs v-model="activeSheet" class="mb-4">
                            <v-tab
                                v-for="(sheet, index) in excelSheets"
                                :key="index"
                                :value="index"
                            >
                                {{ sheet.name }}
                            </v-tab>
                        </v-tabs>
                        <v-data-table
                            :headers="currentSheetHeaders"
                            :items="currentSheetData"
                            density="compact"
                            class="excel-table"
                            :items-per-page="50"
                        ></v-data-table>
                    </div>

                    <!-- Audio Preview -->
                    <div v-else-if="isAudio" class="audio-preview">
                        <div class="audio-container">
                            <v-icon size="80" color="primary" class="mb-4">mdi-music-note</v-icon>
                            <audio controls class="audio-player">
                                <source :src="fileUrl" :type="file.type">
                                متصفحك لا يدعم تشغيل الملفات الصوتية
                            </audio>
                        </div>
                    </div>

                    <!-- Video Preview -->
                    <div v-else-if="isVideo" class="video-preview">
                        <video controls class="video-player">
                            <source :src="fileUrl" :type="file.type">
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
        file: {
            type: File,
            default: null
        }
    },
    emits: ['update:modelValue'],
    data() {
        return {
            loading: false,
            error: null,
            fileUrl: null,
            textContent: '',
            csvHeaders: [],
            csvData: [],
            excelSheets: [],
            activeSheet: 0,
            workbook: null
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
        fileName() {
            return this.file?.name || 'ملف غير معروف'
        },
        fileSize() {
            if (!this.file?.size) return '0 KB'
            const bytes = this.file.size
            const sizes = ['Bytes', 'KB', 'MB', 'GB']
            const i = Math.floor(Math.log(bytes) / Math.log(1024))
            return `${(bytes / Math.pow(1024, i)).toFixed(1)} ${sizes[i]}`
        },
        fileType() {
            return this.file?.name.split('.').pop()?.toLowerCase() || ''
        },
        fileTypeIcon() {
            const iconMap = {
                // Images
                jpg: 'mdi-file-image',
                jpeg: 'mdi-file-image',
                png: 'mdi-file-image',
                gif: 'mdi-file-image',
                webp: 'mdi-file-image',
                svg: 'mdi-file-image',
                // Documents
                pdf: 'mdi-file-pdf-box',
                doc: 'mdi-file-word',
                docx: 'mdi-file-word',
                xls: 'mdi-file-excel',
                xlsx: 'mdi-file-excel',
                ppt: 'mdi-file-powerpoint',
                pptx: 'mdi-file-powerpoint',
                // Text
                txt: 'mdi-file-document',
                csv: 'mdi-file-table',
                json: 'mdi-code-json',
                xml: 'mdi-file-code',
                // Code
                js: 'mdi-language-javascript',
                ts: 'mdi-language-typescript',
                html: 'mdi-language-html5',
                css: 'mdi-language-css3',
                vue: 'mdi-vuejs',
                // Archives
                zip: 'mdi-folder-zip',
                rar: 'mdi-folder-zip',
                '7z': 'mdi-folder-zip',
                // Audio
                mp3: 'mdi-file-music',
                wav: 'mdi-file-music',
                ogg: 'mdi-file-music',
                // Video
                mp4: 'mdi-file-video',
                avi: 'mdi-file-video',
                mov: 'mdi-file-video',
                wmv: 'mdi-file-video'
            }
            return iconMap[this.fileType] || 'mdi-file'
        },
        fileTypeColor() {
            const colorMap = {
                jpg: 'green', jpeg: 'green', png: 'green', gif: 'green', webp: 'green',
                pdf: 'red',
                doc: 'blue', docx: 'blue',
                xls: 'green', xlsx: 'green',
                txt: 'grey', csv: 'orange',
                mp3: 'purple', wav: 'purple',
                mp4: 'indigo', avi: 'indigo',
                zip: 'brown', rar: 'brown'
            }
            return colorMap[this.fileType] || 'grey'
        },
        isImage() {
            return ['jpg', 'jpeg', 'png', 'gif', 'webp', 'svg', 'bmp'].includes(this.fileType)
        },
        isPdf() {
            return this.fileType === 'pdf'
        },
        pdfViewerUrl() {
            // For better PDF viewing, remove the toolbar params to give more space
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
        }
    },
    watch: {
        file: {
            immediate: true,
            handler(newFile) {
                if (newFile) {
                    this.loadFile()
                }
            }
        },
        modelValue(newValue) {
            if (!newValue) {
                this.cleanup()
            }
        }
    },
    methods: {
        async loadFile() {
            if (!this.file) return

            this.loading = true
            this.error = null

            try {
                // Create URL for the file
                this.fileUrl = URL.createObjectURL(this.file)

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
            const text = await this.file.text()
            this.textContent = text
        },

        async loadCsvContent() {
            const text = await this.file.text()
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
        },

        async loadExcelContent() {
            const arrayBuffer = await this.file.arrayBuffer()
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
        },

        downloadFile() {
            if (!this.fileUrl) return

            const link = document.createElement('a')
            link.href = this.fileUrl
            link.download = this.fileName
            document.body.appendChild(link)
            link.click()
            document.body.removeChild(link)
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
        },

        cleanup() {
            if (this.fileUrl) {
                URL.revokeObjectURL(this.fileUrl)
                this.fileUrl = null
            }
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
}
</style>