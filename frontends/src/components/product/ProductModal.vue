<template>
    <v-dialog v-model="dialogVisible" max-width="1000px" persistent>
        <v-card rounded="xl" elevation="8">
            <!-- Header -->
            <ModalHeader :icon="'mdi-clipboard-list'" :title="editedProductId ? 'تعديل المنتج' : 'إضافة منتج جديد'"
                :subtitle="editedProductId ? 'تحديث بيانات المنتج الحالي' : 'أدخل بيانات المنتج الجديد'"
                @close="closeDialog" />

            <!-- Form Content -->
            <div class="modal-body">
                <v-form ref="productForm" v-model="formValid">
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
                                        variant="outlined" density="comfortable" placeholder="مثال: PRD-001"
                                        hide-details="auto" class="modern-field"></v-text-field>
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
                                        :rules="fieldValidations.required('الوحدة')" variant="outlined"
                                        density="comfortable" placeholder="اختر الوحدة" hide-details="auto"
                                        class="modern-field">
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
                                        :current-item="product?.category || null"
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
                                    <v-text-field v-model.number="editedProduct.price"
                                        :rules="fieldValidations.productPrice" type="number" variant="outlined"
                                        density="comfortable" placeholder="0.00" prefix="ج.م." hide-details="auto"
                                        class="modern-field">
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
                                    <v-text-field v-model.number="editedProduct.stock"
                                        :rules="fieldValidations.productStock" type="number" variant="outlined"
                                        density="comfortable" placeholder="0" hide-details="auto" class="modern-field">
                                        <template v-slot:prepend-inner>
                                            <v-icon color="info" size="20">mdi-package-variant</v-icon>
                                        </template>
                                        <template v-slot:append-inner>
                                            <v-chip
                                                :color="getStockMeta(editedProduct.stock, editedProduct.minimumStock).color"
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

                    <!-- Image Section -->
                    <FormSection title="صورة المنتج" icon="mdi-image" :color="SECTION_COLORS.media">
                        <v-row>
                            <!-- Image Upload Type Selection -->
                            <v-col cols="12">
                                <div class="form-group">
                                    <label class="form-label">طريقة إضافة الصورة</label>
                                    <v-radio-group v-model="imageUploadType" inline hide-details
                                        class="image-type-selector">
                                        <v-radio label="رفع ملف" value="file" color="primary"></v-radio>
                                        <v-radio label="رابط خارجي" value="url" color="primary"></v-radio>
                                    </v-radio-group>
                                </div>
                            </v-col>

                            <!-- File Upload -->
                            <v-col cols="12" v-if="imageUploadType === 'file'">
                                <div class="form-group">
                                    <label class="form-label">اختر صورة المنتج</label>
                                    <v-file-input v-model="selectedImageFile" :rules="fieldValidations.productImage"
                                        variant="outlined" density="comfortable" placeholder="اختر ملف الصورة"
                                        prepend-icon="" accept="image/*" hide-details="auto" class="modern-field"
                                        @change="handleFileSelect" :loading="uploadLoading">
                                        <template v-slot:prepend-inner>
                                            <v-icon color="warning" size="20">mdi-cloud-upload</v-icon>
                                        </template>
                                        <template v-slot:append-inner>
                                            <div class="d-flex align-center">
                                                <v-progress-circular v-if="uploadLoading" :model-value="uploadProgress"
                                                    size="20" color="primary" class="me-2">
                                                </v-progress-circular>
                                                <v-btn v-if="selectedImageFile && selectedImageFile.length > 0"
                                                    icon="mdi-eye" size="x-small" variant="text"
                                                    @click="previewUploadedImage">
                                                </v-btn>
                                            </div>
                                        </template>
                                    </v-file-input>
                                </div>
                            </v-col>

                            <!-- URL Input -->
                            <v-col cols="12" v-if="imageUploadType === 'url'">
                                <div class="form-group">
                                    <label class="form-label">رابط الصورة</label>
                                    <v-text-field v-model="imageUrlInput" :rules="fieldValidations.url" variant="outlined"
                                        density="comfortable" placeholder="https://example.com/image.jpg"
                                        hide-details="auto" class="modern-field" :loading="urlDownloadLoading">
                                        <template v-slot:prepend-inner>
                                            <v-icon color="warning" size="20">mdi-link</v-icon>
                                        </template>
                                        <template v-slot:append-inner>
                                            <div class="d-flex align-center">
                                                <v-btn v-if="imageUrlInput && !urlDownloadLoading" icon="mdi-download"
                                                    size="x-small" variant="text" color="primary"
                                                    @click="downloadImageFromUrl"
                                                    :disabled="!isValidImageUrl(imageUrlInput)">
                                                </v-btn>
                                                <v-btn v-if="imageUrlInput" icon="mdi-eye" size="x-small" variant="text"
                                                    @click="previewImageLogic">
                                                </v-btn>
                                            </div>
                                        </template>
                                    </v-text-field>
                                    <div v-if="imageUrlInput && !isValidImageUrl(imageUrlInput)"
                                        class="text-caption text-warning mt-1">
                                        سيتم تحميل الصورة وحفظها محلياً عند الحفظ
                                    </div>
                                </div>
                            </v-col>

                            <!-- Image Preview -->
                            <v-col cols="12" v-if="imagePreviewUrl">
                                <div class="image-preview-container">
                                    <label class="form-label">معاينة الصورة</label>
                                    <div class="image-preview">
                                        <v-img :src="imagePreviewUrl" height="200" width="200" contain
                                            class="preview-image">
                                            <template v-slot:error>
                                                <div class="d-flex align-center justify-center h-100">
                                                    <v-icon size="48" color="grey-lighten-1">mdi-image-broken</v-icon>
                                                </div>
                                            </template>
                                        </v-img>
                                        <v-btn icon="mdi-close" size="x-small" variant="elevated" color="error"
                                            class="remove-image-btn" @click="removeImage"></v-btn>
                                    </div>
                                </div>
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
                                            <div v-if="imagePreviewUrl" class="me-3">
                                                <v-img :src="imagePreviewUrl" width="60" height="60" contain
                                                    class="rounded"></v-img>
                                            </div>
                                            <div v-else class="me-3">
                                                <v-avatar size="60" color="grey-lighten-3">
                                                    <v-icon size="30" color="grey">mdi-package-variant</v-icon>
                                                </v-avatar>
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
                                                    {{ getStockMeta(editedProduct.stock, editedProduct.minimumStock).text }}
                                                </v-chip>
                                                <div class="text-caption mt-1">{{ editedProduct.stock || 0 }} متوفر</div>
                                            </div>
                                        </div>
                                    </v-card>
                                </v-col>
                            </v-row>
                        </FormSection>
                    </v-col>
                </v-form>
            </div>

            <!-- Modern Actions -->
            <ModalActions :form-valid="formValid" :loading="loading"
                :primary-text="editedProductId ? 'تحديث المنتج' : 'حفظ المنتج'"
                :primary-icon="editedProductId ? 'mdi-content-save' : 'mdi-plus'"
                :primary-disabled="!formValid || uploadLoading || urlDownloadLoading" :cancel-disabled="loading"
                @cancel="closeDialog" @primary-action="saveProduct" />

        </v-card>

        <!-- Image Preview Dialog -->
        <FilePreviewDialog v-model="previewDialog" :file="selectedFile" />
    </v-dialog>
</template>

<script>
import { UNIT_ICON_OPTIONS } from '@/constants/icons';
import { getStockMeta } from '@/utils/product-util';
import { saveProduct, updateProduct, getCategories } from '@/services/product-service';
import { success, error } from '@/utils/system-util';
import { ImageServiceClient } from '@/services/image-service'; // Import the image service client
import SearchableSelect from '@/components/common/SearchableSelect.vue'
import { fieldValidations } from '@/utils/validation-util';
import ModalHeader from '@/components/common/ModalHeader.vue'
import ModalActions from '@/components/common/ModalActions.vue'
import FilePreviewDialog from '@/components/common/FilePreviewDialog.vue'
import { SECTION_COLORS } from  '@/constants/colors'
import { formatCurrency } from '@/utils/currency-util';
import FormSection from '@/components/common/FormSection.vue'

export default {
    name: 'ProductModal',
    components: {
        SearchableSelect,
        ModalHeader,
        ModalActions,
        FilePreviewDialog,
        FormSection
    },
    props: {
        modelValue: {
            type: Boolean,
            default: false
        },
        product: {
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
            formValid: false,
            loading: false,
            uploadLoading: false,
            urlDownloadLoading: false,
            uploadProgress: 0,
            previewImage: false,
            previewImageUrl: null,
            imageUploadType: 'url', // 'file' or 'url'
            selectedImageFile: [],
            imageUrlInput: '',
            imagePreviewUrl: null,
            editedProductId: null,
            previewDialog: false,
            selectedFile: null,
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
                imageUrl: '' // Changed from 'image' to 'imageUrl' to match backend
            },
            imageServiceClient: new ImageServiceClient(), // Initialize image service client
        };
    },
    computed: {
        dialogVisible: {
            get() {
                return this.modelValue;
            },
            set(value) {
                this.$emit('update:modelValue', value);
            }
        },
    },
    watch: {
        product: {
            immediate: true,
            async handler(newProduct) {
                if (newProduct) {
                    this.editedProduct = { ...newProduct };
                    this.editedProductId = newProduct.id;

                    // Set imageUrl in local input for display
                    if (newProduct.imageUrl) {
                        this.imageUrlInput = newProduct.imageUrl;
                        this.updateImagePreview();
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
        imageUploadType() {
            this.clearImageData();
        }
    },
    methods: {
        getCategories,
        getStockMeta,
        formatCurrency,
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
                imageUrl: ''
            };
            this.imageUploadType = 'url';
            this.selectedImageFile = [];
            this.imageUrlInput = '';
            this.imagePreviewUrl = null;
            this.uploadProgress = 0;
            if (this.$refs.productForm) {
                this.$refs.productForm.resetValidation();
            }
        },
        previewFile(file) {
            this.selectedFile = file
            this.previewDialog = true
        },
        closeDialog() {
            this.dialogVisible = false;
        },

        isValidImageUrl(url) {
            if (!url) return false;
            const pattern = /\.(jpg|jpeg|png|gif|webp|bmp|svg)$/i;
            return pattern.test(url);
        },

        async handleFileSelect(files) {
            if (files && files.length > 0) {
                const file = files[0];

                // Show local preview immediately
                const reader = new FileReader();
                reader.onload = (e) => {
                    this.imagePreviewUrl = e.target.result;
                };
                reader.readAsDataURL(file);

                // Clear any URL input
                this.imageUrlInput = '';
                this.editedProduct.imageUrl = '';
            } else {
                this.imagePreviewUrl = null;
            }
        },

        async downloadImageFromUrl() {
            if (!this.imageUrlInput) return;

            this.urlDownloadLoading = true;
            try {
                const response = await this.imageServiceClient.uploadImageFromUrl(this.imageUrlInput, 'product');

                // Set the downloaded image URL
                this.editedProduct.imageUrl = response.url;
                this.imagePreviewUrl = response.url;

                success('تم تحميل الصورة بنجاح');
            } catch (err) {
                error('فشل تحميل الصورة: ' + err.message);
                console.error('Error downloading image:', err);
            } finally {
                this.urlDownloadLoading = false;
            }
        },

        previewUploadedImage() {
            if (this.selectedImageFile) {
                this.previewImageUrl = this.imagePreviewUrl;
                this.previewImage = true;
            }
        },

        previewImageLogic() {
            if (this.imageUrlInput) {
                this.previewImageUrl = this.imageUrlInput;
                this.previewImage = true;
            }
        },

        updateImagePreview() {
            if (this.imageUploadType === 'url' && this.imageUrlInput) {
                this.imagePreviewUrl = this.imageUrlInput;
            } else if (this.imageUploadType === 'file' && this.selectedImageFile) {
                const file = this.selectedImageFile;
                const reader = new FileReader();
                reader.onload = (e) => {
                    this.imagePreviewUrl = e.target.result;
                };
                reader.readAsDataURL(file);
            } else {
                this.imagePreviewUrl = null;
            }
        },

        clearImageData() {
            this.selectedImageFile = [];
            this.imageUrlInput = '';
            this.editedProduct.imageUrl = '';
            this.imagePreviewUrl = null;
        },

        removeImage() {
            this.clearImageData();
        },

        async saveProduct() {
            if (!this.$refs.productForm.validate()) return;

            this.loading = true;
            try {
                const productData = { ...this.editedProduct, id: this.editedProductId };

                // Auto-generate SKU if empty
                if (!productData.sku && productData.name) {
                    productData.sku = productData.name.replace(/\s+/g, '-').toUpperCase().substring(0, 10);
                }
                console.log(this.imageUploadType)
                console.log(this.selectedImageFile)
                if (this.selectedImageFile) console.log(this.selectedImageFile.length)
                // Handle image upload based on type
                if (this.imageUploadType === 'file' && this.selectedImageFile) {
                    // Upload file to image service
                    try {
                        this.uploadLoading = true;
                        const uploadResponse = await this.imageServiceClient.uploadImage(
                            this.selectedImageFile,
                            'product'
                        );
                        productData.imageUrl = uploadResponse.url;
                    } catch (uploadError) {
                        error('فشل رفع الصورة: ' + uploadError.message);
                        return;
                    } finally {
                        this.uploadLoading = false;
                    }
                } else if (this.imageUploadType === 'url' && this.imageUrlInput && !this.editedProduct.imageUrl) {
                    // Download and store image from URL if not already downloaded
                    try {
                        this.urlDownloadLoading = true;
                        const downloadResponse = await this.imageServiceClient.uploadImageFromUrl(
                            this.imageUrlInput,
                            'product'
                        );
                        productData.imageUrl = downloadResponse.url;
                    } catch (downloadError) {
                        error('فشل تحميل الصورة من الرابط: ' + downloadError.message);
                        return;
                    } finally {
                        this.urlDownloadLoading = false;
                    }
                }

                // Save or update product
                if (productData.id == null) {
                    let response = await saveProduct(productData);
                    if (response != null && response.id != null) {
                        success('تم حفظ المنتج بنجاح');
                        this.$emit('save', response);
                    } else {
                        error('فشل حفظ المنتج');
                        console.log(response);
                    }
                } else {
                    let response = await updateProduct(productData);
                    if (response != null && response.id != null) {
                        success('تم تحديث المنتج بنجاح');
                        this.$emit('save', response);
                    } else {
                        error('فشل تحديث المنتج');
                        console.error(response);
                    }
                }

                this.closeDialog();
            } catch (error) {
                console.error('Error saving product:', error);
                error('فشل حفظ المنتج');
            } finally {
                this.loading = false;
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

/* Image Upload Styling */
.image-type-selector {
    margin-top: 8px;
}

.image-type-selector .v-radio {
    margin-right: 24px;
}

.image-preview-container {
    margin-top: 16px;
}

.image-preview {
    position: relative;
    display: inline-block;
    border: 2px dashed #e2e8f0;
    border-radius: 8px;
    padding: 8px;
    background: white;
}

.preview-image {
    border-radius: 8px;
    overflow: hidden;
}

.remove-image-btn {
    position: absolute;
    top: -8px;
    right: -8px;
    z-index: 2;
}
</style>
