<template>
    <v-dialog v-model="dialogVisible" max-width="900px" persistent>
        <v-card class="modern-modal" elevation="0">
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
                <v-btn 
                    icon="mdi-close" 
                    variant="text" 
                    color="white"
                    size="small"
                    class="close-btn"
                    @click="closeDialog"
                ></v-btn>
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
                                    <v-text-field
                                        v-model="editedProduct.name"
                                        :rules="[rules.required]"
                                        variant="outlined"
                                        density="comfortable"
                                        placeholder="أدخل اسم المنتج"
                                        hide-details="auto"
                                        class="modern-field"
                                    ></v-text-field>
                                </div>
                            </v-col>

                            <!-- SKU -->
                            <v-col cols="12" md="6">
                                <div class="form-group">
                                    <label class="form-label">
                                        رمز المنتج (SKU) <span class="required">*</span>
                                    </label>
                                    <v-text-field
                                        v-model="editedProduct.sku"
                                        :rules="[rules.required, rules.sku]"
                                        variant="outlined"
                                        density="comfortable"
                                        placeholder="مثال: PRD-001"
                                        hide-details="auto"
                                        class="modern-field"
                                    ></v-text-field>
                                </div>
                            </v-col>

                            <!-- Category -->
                            <v-col cols="12" md="6">
                                <div class="form-group">
                                    <label class="form-label">
                                        التصنيف <span class="required">*</span>
                                    </label>
                                    <v-select
                                        v-model="editedProduct.categoryId"
                                        :items="categories"
                                        item-title="name"
                                        item-value="id"
                                        :rules="[rules.required]"
                                        variant="outlined"
                                        density="comfortable"
                                        placeholder="اختر التصنيف"
                                        hide-details="auto"
                                        class="modern-field"
                                    >
                                        <template v-slot:item="{ props, item }">
                                            <v-list-item v-bind="props" class="category-item">
                                                <template v-slot:prepend>
                                                    <v-avatar size="32" :color="item.raw.color || 'primary'" class="me-3">
                                                        <v-icon color="white" size="16">{{ item.raw.icon || 'mdi-folder' }}</v-icon>
                                                    </v-avatar>
                                                </template>
                                                <!-- <v-list-item-title>{{ item.raw.name }}</v-list-item-title> -->
                                                <!-- <v-list-item-subtitle v-if="item.raw.description">
                                                    {{ item.raw.description }}
                                                </v-list-item-subtitle> -->
                                            </v-list-item>
                                        </template>
                                        <template v-slot:selection="{ item }">
                                            <div class="d-flex align-center">
                                                <v-avatar size="24" :color="item.raw.color || 'primary'" class="me-2">
                                                    <v-icon color="white" size="12">{{ item.raw.icon || 'mdi-folder' }}</v-icon>
                                                </v-avatar>
                                                {{ item.raw.name }}
                                            </div>
                                        </template>
                                    </v-select>
                                </div>
                            </v-col>

                            <!-- Status -->
                            <v-col cols="12" md="6">
                                <div class="form-group">
                                    <label class="form-label">حالة المنتج</label>
                                    <v-select
                                        v-model="editedProduct.status"
                                        :items="statusOptions"
                                        variant="outlined"
                                        density="comfortable"
                                        hide-details="auto"
                                        class="modern-field"
                                    >
                                        <template v-slot:item="{ props, item }">
                                            <v-list-item v-bind="props">
                                                <template v-slot:prepend>
                                                    <v-icon :color="getStatusColor(item.raw.value)" class="me-2">
                                                        {{ getStatusIcon(item.raw.value) }}
                                                    </v-icon>
                                                </template>
                                                <!-- <v-list-item-title>{{ item.raw.title }}</v-list-item-title> -->
                                            </v-list-item>
                                        </template>
                                        <!-- <template v-slot:selection="{ item }">
                                            <div class="d-flex align-center">
                                                <v-icon :color="getStatusColor(item.raw.value)" size="16" class="me-2">
                                                    {{ getStatusIcon(item.raw.value) }}
                                                </v-icon>
                                                {{ item.raw.title }}
                                            </div>
                                        </template> -->
                                    </v-select>
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
                                        السعر (ر.س) <span class="required">*</span>
                                    </label>
                                    <v-text-field
                                        v-model.number="editedProduct.price"
                                        :rules="[rules.required, rules.positive]"
                                        type="number"
                                        variant="outlined"
                                        density="comfortable"
                                        placeholder="0.00"
                                        prefix="ر.س"
                                        hide-details="auto"
                                        class="modern-field"
                                    >
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
                                    <v-text-field
                                        v-model.number="editedProduct.stock"
                                        :rules="[rules.required, rules.nonNegative]"
                                        type="number"
                                        variant="outlined"
                                        density="comfortable"
                                        placeholder="0"
                                        hide-details="auto"
                                        class="modern-field"
                                        @input="updateStatusFromStock"
                                    >
                                        <template v-slot:prepend-inner>
                                            <v-icon color="info" size="20">mdi-package-variant</v-icon>
                                        </template>
                                        <template v-slot:append-inner>
                                            <v-chip 
                                                :color="getStockStatusColor(editedProduct.stock)" 
                                                size="x-small"
                                                class="stock-indicator"
                                            >
                                                {{ getStockStatusText(editedProduct.stock) }}
                                            </v-chip>
                                        </template>
                                    </v-text-field>
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
                                    <v-textarea
                                        v-model="editedProduct.description"
                                        variant="outlined"
                                        density="comfortable"
                                        placeholder="أدخل وصف المنتج..."
                                        rows="3"
                                        hide-details="auto"
                                        class="modern-field"
                                    ></v-textarea>
                                </div>
                            </v-col>

                            <!-- Image URL -->
                            <v-col cols="12">
                                <div class="form-group">
                                    <label class="form-label">رابط الصورة</label>
                                    <v-text-field
                                        v-model="editedProduct.image"
                                        :rules="[rules.url]"
                                        variant="outlined"
                                        density="comfortable"
                                        placeholder="https://example.com/image.jpg"
                                        hide-details="auto"
                                        class="modern-field"
                                    >
                                        <template v-slot:prepend-inner>
                                            <v-icon color="warning" size="20">mdi-image</v-icon>
                                        </template>
                                        <template v-slot:append-inner v-if="editedProduct.image">
                                            <v-btn
                                                icon="mdi-eye"
                                                size="x-small"
                                                variant="text"
                                                @click="previewImage = true"
                                            ></v-btn>
                                        </template>
                                    </v-text-field>
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
                        <v-icon 
                            :color="formValid ? 'success' : 'warning'" 
                            size="16" 
                            class="me-1"
                        >
                            {{ formValid ? 'mdi-check-circle' : 'mdi-alert-circle' }}
                        </v-icon>
                        <span class="text-caption" :class="formValid ? 'text-success' : 'text-warning'">
                            {{ formValid ? 'النموذج صحيح' : 'يرجى ملء الحقول المطلوبة' }}
                        </span>
                    </div>
                    
                    <div class="d-flex ga-3">
                        <v-btn
                            variant="outlined"
                            color="grey-darken-1"
                            size="large"
                            class="cancel-btn"
                            @click="closeDialog"
                        >
                            إلغاء
                        </v-btn>
                        <v-btn
                            color="primary"
                            size="large"
                            :disabled="!formValid"
                            :loading="loading"
                            class="save-btn"
                            @click="saveProduct"
                        >
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
                    <v-img
                        :src="editedProduct.image"
                        max-height="400"
                        contain
                        class="grey-lighten-2"
                    >
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
import { saveProduct, updateProduct } from '@/utils/product-util';
import { success, error } from '@/utils/system-util';
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
        },
        categories: {
            type: Array,
            default: () => []
        }
    },
    emits: ['update:modelValue', 'save'],
    data() {
        return {
            formValid: false,
            loading: false,
            previewImage: false,
            editedProductId: null,
            editedProduct: {
                name: '',
                sku: '',
                categoryId: null,
                price: 0,
                stock: 0,
                status: 'active',
                description: '',
                image: ''
            },
            statusOptions: [
                { title: 'نشط', value: 'active' },
                { title: 'مخزون منخفض', value: 'low-stock' },
                { title: 'نفد المخزون', value: 'out-of-stock' },
                { title: 'غير نشط', value: 'inactive' }
            ],
            rules: {
                required: value => !!value || 'هذا الحقل مطلوب',
                positive: value => value > 0 || 'يجب أن يكون أكبر من صفر',
                nonNegative: value => value >= 0 || 'يجب أن يكون صفر أو أكبر',
                sku: value => {
                    const pattern = /^[A-Z0-9-_]+$/i;
                    return pattern.test(value) || 'رمز المنتج يجب أن يحتوي على أحرف وأرقام فقط';
                },
                url: value => {
                    if (!value) return true;
                    const pattern = /^https?:\/\/.+\.(jpg|jpeg|png|gif|webp)$/i;
                    return pattern.test(value) || 'يرجى إدخال رابط صورة صحيح';
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
        }
    },
    watch: {
        product: {
            immediate: true,
            handler(newProduct) {
                if (newProduct) {
                    this.editedProduct = { ...newProduct };
                    this.editedProductId = newProduct.id;
                } else {
                    this.resetForm();
                }
            }
        },
        modelValue(newValue) {
            if (!newValue) {
                this.resetForm();
            }
        }
    },
    methods: {
        resetForm() {
            this.editedProductId = null;
            this.editedProduct = {
                name: '',
                sku: '',
                categoryId: null,
                price: 0,
                stock: 0,
                status: 'active',
                description: '',
                image: ''
            };
            if (this.$refs.productForm) {
                this.$refs.productForm.resetValidation();
            }
        },

        closeDialog() {
            this.dialogVisible = false;
        },

        updateStatusFromStock() {
            if (this.editedProduct.stock === 0) {
                this.editedProduct.status = 'out-of-stock';
            } else if (this.editedProduct.stock < 10) {
                this.editedProduct.status = 'low-stock';
            } else if (this.editedProduct.status === 'out-of-stock' || this.editedProduct.status === 'low-stock') {
                this.editedProduct.status = 'active';
            }
        },

        getStatusColor(status) {
            const colors = {
                'active': 'success',
                'low-stock': 'warning',
                'out-of-stock': 'error',
                'inactive': 'grey'
            };
            return colors[status] || 'grey';
        },

        getStatusIcon(status) {
            const icons = {
                'active': 'mdi-check-circle',
                'low-stock': 'mdi-alert-circle',
                'out-of-stock': 'mdi-close-circle',
                'inactive': 'mdi-pause-circle'
            };
            return icons[status] || 'mdi-help-circle';
        },

        getStockStatusColor(stock) {
            if (stock === 0) return 'error';
            if (stock < 10) return 'warning';
            return 'success';
        },

        getStockStatusText(stock) {
            if (stock === 0) return 'نفد';
            if (stock < 10) return 'منخفض';
            return 'متوفر';
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

                if (productData.id == null) {
                    let response = await saveProduct(productData);
                    if (response != null && response.id != null) {
                        success('تم حفظ المنتج بنجاح');
                        this.$emit('save', response);
                    } else {
                        error('فشل حفظ المنتج');
                        console.log(response);
                    }
                }else{
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
/* Modern Modal Styling */
.modern-modal {
    border-radius: 20px !important;
    overflow: hidden;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15) !important;
}

/* Header Styling */
.modal-header {
    background: linear-gradient(135deg, #366091 0%, #4299e1 100%);
    padding: 24px 32px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    position: relative;
    overflow: hidden;
}

.modal-header::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.05'%3E%3Ccircle cx='30' cy='30' r='1'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E") repeat;
}

.header-icon {
    width: 48px;
    height: 48px;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    backdrop-filter: blur(10px);
}

.header-title {
    color: white;
    font-size: 20px;
    font-weight: 700;
    margin-bottom: 4px;
}

.header-subtitle {
    color: rgba(255, 255, 255, 0.8);
    font-size: 14px;
    margin: 0;
}

.close-btn {
    background: rgba(255, 255, 255, 0.1) !important;
    backdrop-filter: blur(10px);
}

.close-btn:hover {
    background: rgba(255, 255, 255, 0.2) !important;
}

/* Body Styling */
.modal-body {
    padding: 32px;
    max-height: 70vh;
    overflow-y: auto;
}

/* Form Sections */
.form-section {
    margin-bottom: 32px;
    padding: 24px;
    border: 1px solid #e2e8f0;
    border-radius: 16px;
    background: #f8fafc;
}

.form-section:last-child {
    margin-bottom: 0;
}

.section-header {
    display: flex;
    align-items: center;
    margin-bottom: 20px;
    padding-bottom: 12px;
    border-bottom: 2px solid #e2e8f0;
}

.section-title {
    color: #2d3748;
    font-size: 16px;
    font-weight: 600;
    margin: 0;
}

/* Form Groups */
.form-group {
    margin-bottom: 16px;
}

.form-label {
    display: block;
    color: #4a5568;
    font-size: 14px;
    font-weight: 600;
    margin-bottom: 8px;
}

.required {
    color: #e53e3e;
}

/* Modern Fields */
.modern-field {
    border-radius: 12px;
}

.modern-field .v-field {
    border-radius: 12px !important;
    background: white !important;
}

.modern-field .v-field__outline {
    border-color: #e2e8f0 !important;
    border-width: 1px !important;
}

.modern-field .v-field--focused .v-field__outline {
    border-color: #366091 !important;
    border-width: 2px !important;
    box-shadow: 0 0 0 3px rgba(54, 96, 145, 0.1);
}

.modern-field .v-field--error .v-field__outline {
    border-color: #e53e3e !important;
}

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

/* Modal Actions */
.modal-actions {
    padding: 24px 32px;
    background: #f8fafc;
    border-top: 1px solid #e2e8f0;
}

.form-status {
    display: flex;
    align-items: center;
}

.cancel-btn {
    border-radius: 12px !important;
    font-weight: 500 !important;
    text-transform: none !important;
    border-color: #e2e8f0 !important;
    color: #718096 !important;
}

.cancel-btn:hover {
    border-color: #cbd5e0 !important;
    background: rgba(113, 128, 150, 0.05) !important;
}

.save-btn {
    background: linear-gradient(135deg, #366091 0%, #4299e1 100%) !important;
    border-radius: 12px !important;
    font-weight: 600 !important;
    text-transform: none !important;
    box-shadow: 0 4px 15px rgba(54, 96, 145, 0.3) !important;
}

.save-btn:hover {
    transform: translateY(-1px);
    box-shadow: 0 6px 20px rgba(54, 96, 145, 0.4) !important;
}

.save-btn:disabled {
    background: #e2e8f0 !important;
    color: #a0aec0 !important;
    box-shadow: none !important;
    transform: none !important;
}

/* Scrollbar Styling */
.modal-body::-webkit-scrollbar {
    width: 6px;
}

.modal-body::-webkit-scrollbar-track {
    background: #f1f5f9;
    border-radius: 3px;
}

.modal-body::-webkit-scrollbar-thumb {
    background: linear-gradient(135deg, #366091 0%, #4299e1 100%);
    border-radius: 3px;
}

.modal-body::-webkit-scrollbar-thumb:hover {
    background: linear-gradient(135deg, #2d4f73 0%, #3182ce 100%);
}

/* Responsive Design */
@media (max-width: 768px) {
    .modal-header {
        padding: 20px 24px;
    }
    
    .header-title {
        font-size: 18px;
    }
    
    .modal-body {
        padding: 24px 20px;
    }
    
    .form-section {
        padding: 20px 16px;
        margin-bottom: 24px;
    }
    
    .modal-actions {
        padding: 20px 24px;
        flex-direction: column;
        gap: 16px;
    }
    
    .modal-actions .d-flex {
        flex-direction: column;
        align-items: stretch !important;
        gap: 12px;
    }
    
    .form-status {
        justify-content: center;
    }
}

/* Animation Classes */
.v-dialog {
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.form-section {
    transition: all 0.2s ease;
}

.form-section:hover {
    background: #f1f5f9;
    border-color: #cbd5e0;
}

/* Focus States */
.modern-field:focus-within {
    transform: translateY(-1px);
}

/* Error States */
.v-messages {
    margin-top: 4px !important;
}

.v-messages__message {
    color: #e53e3e !important;
    font-size: 12px !important;
}
</style>