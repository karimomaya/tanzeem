<template>
    <BaseModal :model-value="modelValue" @update:model-value="$emit('update:modelValue', $event)"
        icon="mdi-clipboard-list" :title="editedProductId ? 'تعديل المنتج' : 'إضافة منتج جديد'"
        :subtitle="editedProductId ? 'تحديث بيانات المنتج الحالي' : 'أدخل بيانات المنتج الجديد'"
        :primary-text="editedProductId ? 'تحديث المنتج' : 'حفظ المنتج'"
        :primary-icon="editedProductId ? 'mdi-content-save' : 'mdi-plus'" :loading="loading"
        :primary-disabled="loading || uploadLoading" @close="closeDialog" @save="saveProduct">
        <template #content="{ formValid }">
            <!-- Basic Information Section -->
            <FormSection title="المعلومات الأساسية" icon="mdi-information" :color="SECTION_COLORS.basic">
                <v-row>
                    <!-- Product Name -->
                    <v-col cols="12" md="6">
                        <div class="form-group">
                            <label class="form-label">
                                اسم المنتج <span class="required">*</span>
                            </label>
                            <v-text-field v-model="editedProduct.name" :rules="fieldValidations.productName"
                                variant="outlined" density="comfortable" placeholder="أدخل اسم المنتج"
                                hide-details="auto" class="modern-field"></v-text-field>
                        </div>
                    </v-col>

                    <!-- SKU -->
                    <v-col cols="12" md="6">
                        <div class="form-group">
                            <label class="form-label">
                                رمز المنتج (SKU) <span class="required">*</span>
                            </label>
                            <v-text-field v-model="editedProduct.sku" :rules="fieldValidations.productSku"
                                variant="outlined" density="comfortable" placeholder="مثال: PRD-001" hide-details="auto"
                                class="modern-field"></v-text-field>
                        </div>
                    </v-col>

                    <!-- Barcode -->
                    <v-col cols="12" md="6">
                        <div class="form-group">
                            <label class="form-label">الباركود</label>
                            <v-text-field v-model="editedProduct.barcode" :rules="fieldValidations.productBarcode"
                                variant="outlined" density="comfortable" placeholder="أدخل رقم الباركود"
                                hide-details="auto" class="modern-field">
                                <template v-slot:prepend-inner>
                                    <v-icon color="info" size="20">mdi-barcode</v-icon>
                                </template>
                            </v-text-field>
                        </div>
                    </v-col>

                    <!-- Unit -->
                    <v-col cols="12" md="6">
                        <div class="form-group">
                            <label class="form-label">
                                الوحدة <span class="required">*</span>
                            </label>
                            <v-select v-model="editedProduct.unit" :items="UNIT_ICON_OPTIONS"
                                :rules="fieldValidations.required('الوحدة')" variant="outlined" density="comfortable"
                                placeholder="اختر الوحدة" hide-details="auto" class="modern-field">
                                <template v-slot:item="{ props, item }">
                                    <v-list-item v-bind="props">
                                        <template v-slot:prepend>
                                            <v-icon color="primary" size="16" class="me-2">
                                                {{ item.raw.value }}
                                            </v-icon>
                                        </template>
                                    </v-list-item>
                                </template>
                                <template v-slot:selection="{ item }">
                                    <div class="d-flex align-center">
                                        <v-icon color="primary" size="16" class="me-2">
                                            {{ item.raw.value }}
                                        </v-icon>
                                        {{ item.raw.title }}
                                    </div>
                                </template>
                            </v-select>
                        </div>
                    </v-col>

                    <!-- Category -->
                    <v-col cols="12" md="6">
                        <div class="form-group">
                            <label class="form-label">
                                التصنيف <span class="required">*</span>
                            </label>
                            <SearchableSelect v-model="editedProduct.category.id" :api-service="getCategories"
                                :current-item="itemToEdit?.category || null"
                                :rules="fieldValidations.required('التصنيف')" placeholder="ابحث عن التصنيف..."
                                class="modern-field">

                                <template v-slot:item="{ props, item }">
                                    <v-list-item v-bind="props" class="category-item">
                                        <template v-slot:prepend>
                                            <v-avatar size="32" :color="item.raw.color || 'primary'" class="me-3">
                                                <v-icon color="white" size="16">{{ item.raw.icon || 'mdi-folder'
                                                    }}</v-icon>
                                            </v-avatar>
                                        </template>
                                        <v-list-item-subtitle v-if="item.raw.description">
                                            {{ item.raw.description }}
                                        </v-list-item-subtitle>
                                    </v-list-item>
                                </template>

                                <template v-slot:prepend-inner>
                                    <v-icon color="primary" size="20">mdi-magnify</v-icon>
                                </template>
                            </SearchableSelect>
                        </div>
                    </v-col>

                    <!-- Status Switch -->
                    <v-col cols="12" md="6">
                        <div class="form-group">
                            <label class="form-label">
                                حالة المنتج
                            </label>
                            <div class="d-flex align-center mt-2">
                                <v-switch v-model="editedProduct.active" color="primary" hide-details
                                    class="me-3"></v-switch>
                                <span class="text-body-1">
                                    {{ editedProduct.active ? 'نشط' : 'غير نشط' }}
                                </span>
                            </div>
                        </div>
                    </v-col>
                </v-row>
            </FormSection>

            <!-- Pricing & Inventory Section -->
            <FormSection title="التسعير والمخزون" icon="mdi-currency-usd" :color="SECTION_COLORS.pricing">
                <v-row>
                    <!-- Price -->
                    <v-col cols="12" md="6">
                        <div class="form-group">
                            <label class="form-label">
                                السعر <span class="required">*</span>
                            </label>
                            <v-text-field v-model.number="editedProduct.price" :rules="fieldValidations.productPrice"
                                type="number" variant="outlined" density="comfortable" placeholder="0.00" prefix="ج.م."
                                hide-details="auto" class="modern-field">
                                <template v-slot:prepend-inner>
                                    <v-icon color="success" size="20">mdi-currency-usd</v-icon>
                                </template>
                            </v-text-field>
                        </div>
                    </v-col>

                    <!-- Stock -->
                    <v-col cols="12" md="6">
                        <div class="form-group">
                            <label class="form-label">
                                الكمية في المخزون <span class="required">*</span>
                            </label>
                            <v-text-field v-model.number="editedProduct.stock" :rules="fieldValidations.productStock"
                                type="number" variant="outlined" density="comfortable" placeholder="0"
                                hide-details="auto" class="modern-field">
                                <template v-slot:prepend-inner>
                                    <v-icon color="info" size="20">mdi-package-variant</v-icon>
                                </template>
                                <template v-slot:append-inner>
                                    <v-chip :color="getStockMeta(editedProduct.stock, editedProduct.minimumStock).color"
                                        size="x-small" class="stock-indicator">
                                        {{ getStockMeta(editedProduct.stock, editedProduct.minimumStock).text }}
                                    </v-chip>
                                </template>
                            </v-text-field>
                        </div>
                    </v-col>

                    <!-- Minimum Stock -->
                    <v-col cols="12" md="6">
                        <div class="form-group">
                            <label class="form-label">
                                الحد الأدنى للمخزون <span class="required">*</span>
                            </label>
                            <v-text-field v-model.number="editedProduct.minimumStock"
                                :rules="fieldValidations.nonNegativeAndRequired" type="number" variant="outlined"
                                density="comfortable" placeholder="0" hide-details="auto" class="modern-field">
                                <template v-slot:prepend-inner>
                                    <v-icon color="warning" size="20">mdi-alert-circle</v-icon>
                                </template>
                            </v-text-field>
                        </div>
                    </v-col>
                </v-row>
            </FormSection>

            <FormSection title="صورة المنتج" icon="mdi-image" :color="SECTION_COLORS.media">
                <v-row>
                    <v-col cols="12">
                        <!-- accept="image/*" -->
                        <FileUploadList ref="imageUpload" v-model="selectedImageFiles" label="صور المنتج"
                            :multiple="true" 
                            :rules="fieldValidations.productImage"
                            placeholder="اختر صور المنتج..." file-list-title="الصور المختارة" :show-download="false"
                            :show-url-input="true" url-input-label="رابط الملف"
                            url-placeholder="https://example.com/file.jpg" url-icon="mdi-link" url-icon-color="warning"
                            :url-rules="fieldValidations.url" :show-url-download="true" :show-url-preview="true"
                            url-validation-message="اضغط على زر التحميل لإضافة الملف للقائمة"
                            @file-added="handleFileSelect" @url-change="handleUrlChange"
                            @url-preview="previewImageFromUrl" @preview="handleFilePreview" />
                    </v-col>
                </v-row>
            </FormSection>

            <!-- Additional Information Section -->
            <FormSection title="معلومات إضافية" icon="mdi-text" :color="SECTION_COLORS.additional">
                <v-row>
                    <!-- Description -->
                    <v-col cols="12">
                        <div class="form-group">
                            <label class="form-label">الوصف</label>
                            <v-textarea v-model="editedProduct.description" variant="outlined" density="comfortable"
                                placeholder="أدخل وصف المنتج..." rows="3" hide-details="auto"
                                class="modern-field"></v-textarea>
                        </div>
                    </v-col>
                </v-row>
            </FormSection>
            <v-col cols="12" v-if="editedProduct.name || editedProduct.sku">
                <FormSection title="معاينة المنتج" icon="mdi-eye" :color="SECTION_COLORS.preview">
                    <v-row>
                        <v-col cols="12">
                            <v-card elevation="2" rounded="lg" class="pa-4 bg-grey-lighten-5">
                                <div class="d-flex align-center">
                                    <div class="me-3">
                                        <ContentThumbnailPreview 
                                            :files="selectedImageFiles"
                                            :size="60"
                                            :show-counter="true"
                                            :show-type-indicator="true"
                                            :clickable="true"
                                            @open-gallery="handleOpenGallery"
                                            placeholder-icon="mdi-package-variant"
                                            placeholder-color="grey-lighten-3" />
                                    </div>
                                    <div class="flex-grow-1">
                                        <div class="text-subtitle-1 font-weight-medium">
                                            {{ editedProduct.name || 'اسم المنتج' }}
                                        </div>
                                        <div class="text-body-2 text-grey-darken-1">
                                            {{ editedProduct.sku || 'رمز المنتج' }}
                                        </div>
                                        <div class="text-body-2 text-success font-weight-bold">
                                            {{ formatCurrency(editedProduct.price || 0) }}
                                        </div>
                                    </div>
                                    <div class="text-center">
                                        <v-chip
                                            :color="getStockMeta(editedProduct.stock, editedProduct.minimumStock).color"
                                            size="small">
                                            {{ getStockMeta(editedProduct.stock,
                                                editedProduct.minimumStock).text }}
                                        </v-chip>
                                        <div class="text-caption mt-1">{{ editedProduct.stock || 0 }} متوفر
                                        </div>
                                    </div>
                                </div>
                            </v-card>
                        </v-col>
                    </v-row>
                </FormSection>
            </v-col>

        </template>
    </BaseModal>

    <!-- Image Preview Dialog -->
    <FilePreviewDialog v-model="previewDialog" :file="selectedFile"
        :files="previewFiles && previewFiles.length > 0 ? previewFiles : previewableFiles" :initial-index="previewIndex"
        :show-thumbnails="true" :show-counter="true" :allow-download="true" :allow-share="false"
        @file-change="onImageChange" @download="onImageDownload" @close="onGalleryClose" />
</template>

<script>
import { UNIT_ICON_OPTIONS } from '@/constants/icons';
import { getStockMeta } from '@/utils/product-util';
import { saveProduct, updateProduct, getCategories } from '@/services/product-service';
import { success, error } from '@/utils/system-util';
import { ContentServiceClient } from '@/services/content-service'; // Import the image service client
import SearchableSelect from '@/components/common/SearchableSelect.vue'
import { fieldValidations } from '@/utils/validation-util';
import ModalHeader from '@/components/common/ModalHeader.vue'
import ModalActions from '@/components/common/ModalActions.vue'
import FilePreviewDialog from '@/components/common/FilePreviewDialog.vue'
import { SECTION_COLORS } from '@/constants/colors'
import { formatCurrency } from '@/utils/currency-util';
import FormSection from '@/components/common/FormSection.vue'
import FileUploadList from '@/components/common/FileUploadList.vue'
import BaseModal from '@/components/common/BaseModal.vue'
import ContentThumbnailPreview from '@/components/common/ContentThumbnailPreview.vue';

export default {
    name: 'ProductModal',
    components: {
        SearchableSelect,
        ModalHeader,
        ModalActions,
        FilePreviewDialog,
        FormSection,
        FileUploadList,
        BaseModal,
        ContentThumbnailPreview
    },
    props: {
        modelValue: {
            type: Boolean,
            default: false
        },
        itemToEdit: {
            type: Object,
            default: null
        }
    },
    emits: ['update:modelValue', 'save'],
    data() {
        return {
            SECTION_COLORS,
            UNIT_ICON_OPTIONS,
            fieldValidations,
            loading: false,
            previewDialog: false,
            selectedFile: null,
            previewFiles: [],
            previewIndex: 0,
            uploadLoading: false,
            selectedImageFiles: [],
            editedProductId: null,
            editedProduct: {
                name: '',
                sku: '',
                barcode: '',
                category: { id: null, name: '' },
                price: 0,
                stock: 0,
                minimumStock: 0,
                unit: 'piece',
                description: '',
                active: true,
                imageUrls: []
            },
            contentServiceClient: new ContentServiceClient()
        };
    },
    computed: {
        previewableFiles() {
            return this.selectedImageFiles.map((file, index) => {
                if (file.url) {
                    return {
                        name: file.name || `image-${index + 1}.jpg`,
                        type: file.type || 'image/jpeg',
                        size: file.size || 0,
                        url: file.url,
                        isUrlFile: file.isUrlFile || false
                    };
                } else if (file instanceof File) {
                    return {
                        name: file.name,
                        type: file.type,
                        size: file.size,
                        url: URL.createObjectURL(file),
                        isUrlFile: false
                    };
                }
                return file;
            });
        },
        handleOpenGallery(data) {
            this.previewFiles = data.files;
            this.previewIndex = data.primaryIndex;
            this.selectedFile = null;
            this.previewDialog = true;
        },
        imagePreviewUrl() {
            // Show the first image for preview
            if (this.selectedImageFiles && this.selectedImageFiles.length > 0) {
                const firstFile = this.selectedImageFiles[0];
                if (firstFile.url) {
                    return firstFile.url; // URL file
                } else if (firstFile instanceof File) {
                    return URL.createObjectURL(firstFile); // Regular file
                }
            }

            // Check for existing product images (handle both formats)
            if (this.editedProduct.imageUrl) {
                return this.editedProduct.imageUrl; // Single image
            }
            if (this.editedProduct.imageUrls && this.editedProduct.imageUrls.length > 0) {
                return this.editedProduct.imageUrls[0]; // Multiple images
            }
            return null;
        },
        allImageUrls() {
            const urls = [];

            // Add from selected files
            if (this.selectedImageFiles && this.selectedImageFiles.length > 0) {
                this.selectedImageFiles.forEach(file => {
                    if (file.url) {
                        urls.push(file.url);
                    } else if (file instanceof File) {
                        urls.push(URL.createObjectURL(file));
                    }
                });
            }

            // Add from existing product images if no selected files
            if (urls.length === 0) {
                if (this.editedProduct.imageUrl) {
                    urls.push(this.editedProduct.imageUrl);
                }
                if (this.editedProduct.imageUrls && this.editedProduct.imageUrls.length > 0) {
                    urls.push(...this.editedProduct.imageUrls);
                }
            }

            return urls;
        }
    },
    watch: {
        itemToEdit: {
            immediate: true,
            async handler(newProduct) {
                if (newProduct) {
                    this.editedProduct = { ...newProduct };
                    this.editedProductId = newProduct.id;

                    let imageUrls = [];

                    if (this.editedProductId) {
                        await this.loadProductImages();
                    }


                    if (newProduct.imageUrl) {
                        imageUrls = [newProduct.imageUrl]; // Convert single to array
                    } else if (newProduct.imageUrls && newProduct.imageUrls.length > 0) {
                        imageUrls = newProduct.imageUrls; // Use existing array
                    }

                    if (imageUrls.length > 0) {
                        this.selectedImageFiles = imageUrls.map((url, index) => ({
                            name: `existing-image-${index + 1}.jpg`,
                            type: 'image/jpeg',
                            size: 0,
                            url: url,
                            isUrlFile: true
                        }));

                        // Update the FileUploadList component
                        this.$nextTick(() => {
                            if (this.$refs.imageUpload) {
                                this.$refs.imageUpload.localFiles = [...this.selectedImageFiles];
                            }
                        });
                    }

                } else {
                    this.resetForm();
                }
            }
        },
        modelValue(newValue) {
            if (!newValue) {
                this.resetForm();
            }
        },
    },
    methods: {
        getCategories,
        getStockMeta,
        formatCurrency,
        handleFilePreview(file, index) {
            if (this.selectedImageFiles.length > 1) {
                // Multiple files - show gallery
                this.previewFiles = this.selectedImageFiles.map((f, i) => ({
                    name: f.name || `image-${i + 1}.jpg`,
                    type: f.type || 'image/jpeg',
                    size: f.size || 0,
                    url: f.url || (f instanceof File ? URL.createObjectURL(f) : null),
                    isUrlFile: f.isUrlFile || false
                }));
                this.previewIndex = index;
                this.selectedFile = null; // Use files array for gallery
            } else {
                // Single file - show single preview
                this.selectedFile = file;
                this.previewFiles = [];
                this.previewIndex = 0;
            }
            this.previewDialog = true;
        },
        resetForm() {
            this.editedProductId = null;
            this.editedProduct = {
                name: '',
                sku: '',
                barcode: '',
                category: { id: null, name: '' },
                price: 0,
                stock: 0,
                minimumStock: 0,
                unit: 'piece',
                description: '',
                active: true,
                imageUrls: []
            };
            this.selectedImageFiles = [];

            if (this.$refs.imageUpload) {
                this.$refs.imageUpload.clearFiles();
                this.$refs.imageUpload.clearUrl();
            }
        },
        previewFile(file) {
            this.selectedFile = file
            this.previewDialog = true
        },
        closeDialog() {
            this.$emit('update:modelValue', false);
        },

        isValidImageUrl(url) {
            if (!url) return false;
            const pattern = /\.(jpg|jpeg|png|gif|webp|bmp|svg)$/i;
            return pattern.test(url);
        },

        openImageGallery() {
            // Convert image URLs to file objects for the enhanced dialog
            if (this.allImageUrls.length > 0) {
                // Convert image URLs to file objects for the enhanced dialog
                this.previewFiles = this.allImageUrls.map((url, index) => ({
                    name: `product-image-${index + 1}.jpg`,
                    type: 'image/jpeg',
                    size: 0,
                    url: url,
                    isUrlFile: true
                }));
                this.previewIndex = 0;
                this.selectedFile = null; // Use files array instead
                this.previewDialog = true;
            }
        },

        onImageChange(data) {
            console.log('Image changed:', data);
            this.previewIndex = data.index;
        },

        onImageDownload(data) {
            console.log('Download image:', data);
        },

        onGalleryClose() {
            console.log('Gallery closed');
            this.previewFiles = [];
            this.previewIndex = 0;
        },

        previewImageFile(file) {
            // For single file preview
            this.selectedFile = file;
            this.previewFiles = [];
            this.previewIndex = 0;
            this.previewDialog = true;
        },

        previewImageFromUrl(url) {
            if (url) {
                const urlFile = {
                    name: url.split('/').pop() || 'image.jpg',
                    type: 'image/jpeg',
                    size: 0,
                    url: url,
                    isUrlFile: true
                };
                this.selectedFile = urlFile;
                this.previewFiles = [];
                this.previewIndex = 0;
                this.previewDialog = true;
            }
        },

        handleFileSelect(files) {
            console.log('Files selected:', files.length, 'Total files:', this.selectedImageFiles.length);
            // Files are automatically handled by v-model
        },

        handleUrlChange(url) {
            console.log('ProductModal received URL change:', url);
            // URL changes are handled by the FileUploadList component
        },
        async saveProduct() {
            this.loading = true;
            try {
                const productData = { ...this.editedProduct, id: this.editedProductId };

                if (!productData.sku && productData.name) {
                    productData.sku = productData.name.replace(/\s+/g, '-').toUpperCase().substring(0, 10);
                }

                // Handle content uploads using bulk operations
                const uploadedContentUrls = [];
                this.uploadLoading = true;

                // Separate files and URLs for different handling
                const filesToUpload = [];
                const urlsToDownload = [];
                const existingUrls = [];

                this.selectedImageFiles.forEach((file, index) => {
                    if (file.isUrlFile) {
                        if (file.contentId) {
                            // Existing content - keep the URL
                            existingUrls.push(file.url);
                        } else {
                            // New URL to download and store
                            urlsToDownload.push({ url: file.url, order: index });
                        }
                    } else if (file instanceof File) {
                        // New file to upload
                        filesToUpload.push({ file: file, order: index });
                    }
                });

                // 1. Bulk upload new files
                if (filesToUpload.length > 0) {
                    try {
                        const files = filesToUpload.map(item => item.file);
                        const uploadResponses = await this.contentServiceClient.uploadMultipleContents(
                            files,
                            'product',           // context
                            'Product',           // entityType
                            this.editedProductId // entityId
                        );

                        // Add uploaded URLs to the result
                        console.log(uploadResponses)
                        uploadedContentUrls.push(...uploadResponses.map(response => response.url));
                    } catch (uploadError) {
                        console.error('Bulk file upload failed:', uploadError);
                        error('فشل رفع الملفات: ' + uploadError.message);
                        throw uploadError;
                    }
                }

                // 2. Bulk download URLs
                if (urlsToDownload.length > 0) {
                    try {
                        const urls = urlsToDownload.map(item => item.url);
                        const downloadResponses = await this.contentServiceClient.downloadAndStoreMultipleContents(
                            urls,
                            'product',           // context
                            'Product',           // entityType
                            this.editedProductId // entityId
                        );

                        // Add downloaded URLs to the result
                        uploadedContentUrls.push(...downloadResponses.map(response => response.url));
                    } catch (downloadError) {
                        console.error('Bulk URL download failed:', downloadError);
                        error('فشل تحميل الروابط: ' + downloadError.message);
                        // For URLs that can't be downloaded, keep original URLs as fallback
                        uploadedContentUrls.push(...urlsToDownload.map(item => item.url));
                    }
                }

                // 3. Add existing URLs
                uploadedContentUrls.push(...existingUrls);

                // Set the content URLs to product data
                productData.imageUrls = uploadedContentUrls;
                if (uploadedContentUrls.length > 0) {
                    productData.imageUrl = uploadedContentUrls[0]; // Set first image as primary
                }

                // Save the product
                const response = this.editedProductId
                    ? await updateProduct(productData)
                    : await saveProduct(productData);

                if (response?.id) {
                    success(this.editedProductId ? 'تم تحديث المنتج بنجاح' : 'تم حفظ المنتج بنجاح');
                    this.$emit('save', response);
                    this.closeDialog();
                } else {
                    error(this.editedProductId ? 'فشل تحديث المنتج' : 'فشل حفظ المنتج');
                }
            } catch (err) {
                console.error('Error saving product:', err);
                error('فشل حفظ المنتج');
            } finally {
                this.loading = false;
                this.uploadLoading = false;
            }
        }
    }
};
</script>

<style scoped>
@import '@/styles/modal.css';


/* Category Item Styling */
.category-item {
    border-radius: 8px !important;
    margin: 2px !important;
}

.category-item:hover {
    background: rgba(54, 96, 145, 0.05) !important;
}

/* Stock Indicator */
.stock-indicator {
    font-size: 10px !important;
    height: 20px !important;
    font-weight: 600 !important;
}
</style>