<template>
    <v-dialog v-model="dialogVisible" max-width="900px" persistent>
        <v-card rounded="xl" elevation="8">
            <!-- Modern Header -->
            <div class="modal-header">
                <div class="d-flex align-center">
                    <div class="header-icon">
                        <v-icon color="white" size="24">mdi-package-variant</v-icon>
                    </div>
                    <div class="ms-4">
                        <h2 class="header-title">
                            {{ editedProductId ? 'تعديل المنتج' : 'إضافة منتج جديد' }}
                        </h2>
                        <p class="header-subtitle">
                            {{ editedProductId ? 'تحديث بيانات المنتج الحالي' : 'أدخل بيانات المنتج الجديد' }}
                        </p>
                    </div>
                </div>
                <v-btn icon="mdi-close" variant="text" color="white" size="small" class="close-btn"
                    @click="closeDialog"></v-btn>
            </div>

            <!-- Form Content -->
            <div class="modal-body">
                <v-form ref="productForm" v-model="formValid">
                    <!-- Basic Information Section -->
                    <div class="form-section">
                        <div class="section-header">
                            <v-icon color="primary" class="me-2">mdi-information</v-icon>
                            <h3 class="section-title">المعلومات الأساسية</h3>
                        </div>

                        <v-row>
                            <!-- Product Name -->
                            <v-col cols="12" md="6">
                                <div class="form-group">
                                    <label class="form-label">
                                        اسم المنتج <span class="required">*</span>
                                    </label>
                                    <v-text-field v-model="editedProduct.name" :rules="[rules.required]" variant="outlined"
                                        density="comfortable" placeholder="أدخل اسم المنتج" hide-details="auto"
                                        class="modern-field"></v-text-field>
                                </div>
                            </v-col>

                            <!-- SKU -->
                            <v-col cols="12" md="6">
                                <div class="form-group">
                                    <label class="form-label">
                                        رمز المنتج (SKU) <span class="required">*</span>
                                    </label>
                                    <v-text-field v-model="editedProduct.sku" :rules="[rules.required, rules.sku]"
                                        variant="outlined" density="comfortable" placeholder="مثال: PRD-001"
                                        hide-details="auto" class="modern-field"></v-text-field>
                                </div>
                            </v-col>

                            <!-- Barcode -->
                            <v-col cols="12" md="6">
                                <div class="form-group">
                                    <label class="form-label">الباركود</label>
                                    <v-text-field v-model="editedProduct.barcode" :rules="[rules.barcode]"
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
                                    <v-select v-model="editedProduct.unit" :items="UNIT_ICON_OPTIONS" :rules="[rules.required]"
                                        variant="outlined" density="comfortable" placeholder="اختر الوحدة"
                                        hide-details="auto" class="modern-field">
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
                                    <v-autocomplete v-model="selectedCategoryId" :items="searchableCategories"
                                        :search="categorySearch" @update:search="onCategorySearch" item-title="name"
                                        item-value="id" :rules="[rules.required]" variant="outlined" density="comfortable"
                                        placeholder="ابحث عن التصنيف..." hide-details="auto" class="modern-field"
                                        :loading="categoryLoading" no-data-text="لا توجد نتائج"
                                        :menu-props="{ maxHeight: 300 }" clearable>
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
                                        <template v-slot:no-data>
                                            <v-list-item>
                                                <v-list-item-title class="text-center text-grey">
                                                    {{ categorySearch ? 'لا توجد نتائج للبحث' : 'ابدأ بكتابة اسم التصنيف' }}
                                                </v-list-item-title>
                                            </v-list-item>
                                        </template>
                                    </v-autocomplete>
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
                    </div>

                    <!-- Pricing & Inventory Section -->
                    <div class="form-section">
                        <div class="section-header">
                            <v-icon color="success" class="me-2">mdi-currency-usd</v-icon>
                            <h3 class="section-title">التسعير والمخزون</h3>
                        </div>

                        <v-row>
                            <!-- Price -->
                            <v-col cols="12" md="6">
                                <div class="form-group">
                                    <label class="form-label">
                                        السعر <span class="required">*</span>
                                    </label>
                                    <v-text-field v-model.number="editedProduct.price"
                                        :rules="[rules.required, rules.positive]" type="number" variant="outlined"
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
                                        :rules="[rules.required, rules.nonNegative]" type="number" variant="outlined"
                                        density="comfortable" placeholder="0" hide-details="auto" class="modern-field">
                                        <template v-slot:prepend-inner>
                                            <v-icon color="info" size="20">mdi-package-variant</v-icon>
                                        </template>
                                        <template v-slot:append-inner>
                                            <v-chip :color="getStockMeta(editedProduct.stock, editedProduct.minimumStock).color" size="x-small"
                                                class="stock-indicator">
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
                                        :rules="[rules.required, rules.nonNegative]" type="number" variant="outlined"
                                        density="comfortable" placeholder="0" hide-details="auto" class="modern-field">
                                        <template v-slot:prepend-inner>
                                            <v-icon color="warning" size="20">mdi-alert-circle</v-icon>
                                        </template>
                                    </v-text-field>
                                </div>
                            </v-col>
                        </v-row>
                    </div>

                    <!-- Image Section -->
                    <div class="form-section">
                        <div class="section-header">
                            <v-icon color="warning" class="me-2">mdi-image</v-icon>
                            <h3 class="section-title">صورة المنتج</h3>
                        </div>

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
                                    <v-file-input v-model="selectedImageFile" :rules="[rules.imageFile]" variant="outlined"
                                        density="comfortable" placeholder="اختر ملف الصورة" prepend-icon="" accept="image/*"
                                        hide-details="auto" class="modern-field" @change="handleFileSelect"
                                        :loading="uploadLoading">
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
                                    <v-text-field v-model="imageUrlInput" :rules="[rules.url]" variant="outlined"
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
                    </div>

                    <!-- Additional Information Section -->
                    <div class="form-section">
                        <div class="section-header">
                            <v-icon color="info" class="me-2">mdi-text</v-icon>
                            <h3 class="section-title">معلومات إضافية</h3>
                        </div>

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
                        
                    </div>
                </v-form>
            </div>

            <!-- Modern Actions -->
            <div class="modal-actions">
                <div class="d-flex align-center justify-space-between">
                    <div class="form-status">
                        <v-icon :color="formValid ? 'success' : 'warning'" size="16" class="me-1">
                            {{ formValid ? 'mdi-check-circle' : 'mdi-alert-circle' }}
                        </v-icon>
                        <span class="text-caption" :class="formValid ? 'text-success' : 'text-warning'">
                            {{ formValid ? 'النموذج صحيح' : 'يرجى ملء الحقول المطلوبة' }}
                        </span>
                    </div>

                    <div class="d-flex ga-3">
                        <v-btn variant="outlined" color="grey-darken-1" size="large" class="cancel-btn"
                            @click="closeDialog">
                            إلغاء
                        </v-btn>
                        <v-btn color="primary" size="large" :disabled="!formValid || uploadLoading || urlDownloadLoading"
                            :loading="loading" class="save-btn" @click="saveProduct">
                            <v-icon start>{{ editedProductId ? 'mdi-content-save' : 'mdi-plus' }}</v-icon>
                            {{ editedProductId ? 'تحديث المنتج' : 'حفظ المنتج' }}
                        </v-btn>
                    </div>
                </div>
            </div>
        </v-card>

        <!-- Image Preview Dialog -->
        <v-dialog v-model="previewImage" max-width="500px">
            <v-card>
                <v-card-title class="d-flex align-center justify-space-between">
                    <span>معاينة الصورة</span>
                    <v-btn icon="mdi-close" variant="text" @click="previewImage = false"></v-btn>
                </v-card-title>
                <v-card-text class="pa-0">
                    <v-img :src="previewImageUrl" max-height="400" contain class="grey-lighten-2">
                        <template v-slot:error>
                            <div class="d-flex align-center justify-center h-100">
                                <div class="text-center">
                                    <v-icon size="48" color="grey-lighten-1">mdi-image-broken</v-icon>
                                    <p class="text-grey-lighten-1 mt-2">لا يمكن تحميل الصورة</p>
                                </div>
                            </div>
                        </template>
                    </v-img>
                </v-card-text>
            </v-card>
        </v-dialog>
    </v-dialog>
</template>

<script>
import { UNIT_ICON_OPTIONS } from '@/constants/icons';
import { getStockMeta } from '@/utils/product-util';
import { saveProduct, updateProduct, getCategories } from '@/services/product-service';
import { success, error } from '@/utils/system-util';
import { ImageServiceClient } from '@/services/image-service'; // Import the image service client

export default {
    name: 'ProductModal',
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
            searchableCategories: [],
            categorySearch: '',
            categoryLoading: false,
            categorySearchTimeout: null,
            imageServiceClient: new ImageServiceClient(), // Initialize image service client
            rules: {
                required: value => !!value || 'هذا الحقل مطلوب',
                positive: value => value > 0 || 'يجب أن يكون أكبر من صفر',
                nonNegative: value => value >= 0 || 'يجب أن يكون صفر أو أكبر',
                sku: value => {
                    const pattern = /^[A-Z0-9-_]+$/i;
                    return pattern.test(value) || 'رمز المنتج يجب أن يحتوي على أحرف وأرقام فقط';
                },
                barcode: value => {
                    if (!value) return true;
                    const pattern = /^[0-9]+$/;
                    return pattern.test(value) || 'الباركود يجب أن يحتوي على أرقام فقط';
                },
                url: value => {
                    if (!value) return true;
                    const pattern = /^https?:\/\/.+/i;
                    return pattern.test(value) || 'يرجى إدخال رابط صحيح';
                },
                imageFile: value => {
                    if (!value || value.length === 0) return true;
                    let file = value;
                    if (value instanceof Array) file = value[0];
                    const maxSize = 10 * 1024 * 1024; // 10MB (matching backend)
                    const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];

                    if (file.size > maxSize) {
                        return 'حجم الملف يجب أن يكون أقل من 10 ميجابايت';
                    }
                    if (!allowedTypes.includes(file.type)) {
                        return 'نوع الملف غير مدعوم. يرجى اختيار صورة بصيغة JPG, PNG, GIF, أو WebP';
                    }
                    return true;
                }
            }
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
        // Add this new computed property
        selectedCategoryId: {
            get() {
                return this.editedProduct.category?.id || null;
            },
            set(value) {
                if (value) {
                    // Find the selected category from the list
                    const selectedCategory = this.searchableCategories.find(cat => cat.id === value);
                    if (selectedCategory) {
                        this.editedProduct.category = {
                            id: selectedCategory.id,
                            name: selectedCategory.name
                        };
                    }
                } else {
                    this.editedProduct.category = { id: null, name: '' };
                }
            }
        }
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

                    // Handle category for editing - ensure the current category is in the searchable list
                    if (newProduct.category.id && newProduct.category.name) {
                        await this.ensureCategoryInList(newProduct.category.id, newProduct.category.name);
                    }
                } else {
                    this.resetForm();
                }
            }
        },
        modelValue(newValue) {
            if (!newValue) {
                this.resetForm();
            } else {
                // Load initial categories when modal opens
                this.loadInitialCategories();
            }
        },
        imageUploadType() {
            this.clearImageData();
        }
    },
    mounted() {
        this.loadInitialCategories();
    },
    methods: {
        getStockMeta,
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
                active: true ,
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
        async ensureCategoryInList(categoryId, categoryName) {
            // Check if the category is already in the list
            const existingCategory = this.searchableCategories.find(cat => cat.id === categoryId);

            if (!existingCategory) {
                // Add the current category to the list so it can be displayed
                const currentCategory = {
                    id: categoryId,
                    name: categoryName,
                    // Add any other properties that might be needed
                };

                // Add to the beginning of the array so it's easily visible
                this.searchableCategories.unshift(currentCategory);
            }
        },
        async loadInitialCategories() {
            this.categoryLoading = true;
            try {
                const params = new URLSearchParams({
                    page: 0, // Convert to 0-based index
                    size: 5
                });
                const response = await getCategories(params); // Load first 5 categories
                this.searchableCategories = response && response.content ? response.content : [];

                // If we're editing a product and its category isn't in the loaded list, add it
                if (this.editedProduct.category && this.editedProduct.category.id && this.editedProduct.category.name) {
                    await this.ensureCategoryInList(this.editedProduct.category.id, this.editedProduct.category.name);
                }
            } catch (error) {
                console.error('Error loading initial categories:', error);
                this.searchableCategories = [];
            } finally {
                this.categoryLoading = false;
            }
        },
        async searchCategoriesFromBackend(searchTerm) {
            this.categoryLoading = true;
            try {
                const params = new URLSearchParams({
                    page: 0, // Convert to 0-based index
                    size: 5
                });
                if (searchTerm) {
                    params.append('search', searchTerm);
                }
                const response = await getCategories(params); // Search with max 10 results
                this.searchableCategories = response && response.content ? response.content : [];
            } catch (error) {
                console.error('Error searching categories:', error);
                this.searchableCategories = [];
            } finally {
                this.categoryLoading = false;
            }
        },

        onCategorySearch(searchValue) {
            this.categorySearch = searchValue;

            // Clear previous timeout
            if (this.categorySearchTimeout) {
                clearTimeout(this.categorySearchTimeout);
            }

            // If search is empty, load initial categories
            if (!searchValue || searchValue.trim() === '') {
                this.loadInitialCategories();
                return;
            }

            // Debounce search to avoid too many API calls
            this.categorySearchTimeout = setTimeout(() => {
                this.searchCategoriesFromBackend(searchValue.trim());
            }, 300); // 300ms delay
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
