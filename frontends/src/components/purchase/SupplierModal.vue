<template>
    <v-dialog v-model="dialogVisible" max-width="900px" persistent>
        <v-card rounded="xl" elevation="8">
            <!-- Modern Header -->
            <div class="modal-header">
                <div class="d-flex align-center">
                    <div class="header-icon">
                        <v-icon color="white" size="24">mdi-truck</v-icon>
                    </div>
                    <div class="ms-4">
                        <h2 class="header-title">
                            {{ editedSupplierId ? 'تعديل المورد' : 'إضافة مورد جديد' }}
                        </h2>
                        <p class="header-subtitle">
                            {{ editedSupplierId ? 'تحديث بيانات المورد الحالي' : 'أدخل بيانات المورد الجديد' }}
                        </p>
                    </div>
                </div>
                <v-btn icon="mdi-close" variant="text" color="white" size="small" class="close-btn"
                    @click="closeDialog"></v-btn>
            </div>

            <!-- Form Content -->
            <div class="modal-body">
                <v-form ref="supplierForm" v-model="formValid">
                    <!-- Basic Information Section -->
                    <div class="form-section">
                        <div class="section-header">
                            <v-icon color="primary" class="me-2">mdi-information</v-icon>
                            <h3 class="section-title">المعلومات الأساسية</h3>
                        </div>

                        <v-row>
                            <!-- Supplier Name -->
                            <v-col cols="12" md="6">
                                <div class="form-group">
                                    <label class="form-label">
                                        اسم المورد <span class="required">*</span>
                                    </label>
                                    <v-text-field v-model="editedSupplier.name" :rules="[rules.required]" variant="outlined"
                                        density="comfortable" placeholder="أدخل اسم المورد" hide-details="auto"
                                        class="modern-field"></v-text-field>
                                </div>
                            </v-col>

                            <!-- Supplier Code -->
                            <v-col cols="12" md="6">
                                <div class="form-group">
                                    <label class="form-label">
                                        كود المورد <span class="required">*</span>
                                    </label>
                                    <v-text-field v-model="editedSupplier.code" :rules="[rules.required, rules.code]"
                                        variant="outlined" density="comfortable" placeholder="مثال: SUP-001"
                                        hide-details="auto" class="modern-field"></v-text-field>
                                </div>
                            </v-col>

                            <!-- Contact Person -->
                            <v-col cols="12" md="6">
                                <div class="form-group">
                                    <label class="form-label">جهة الاتصال</label>
                                    <v-text-field v-model="editedSupplier.contactPerson" variant="outlined"
                                        density="comfortable" placeholder="اسم المسؤول" hide-details="auto"
                                        class="modern-field">
                                        <template v-slot:prepend-inner>
                                            <v-icon color="primary" size="20">mdi-account</v-icon>
                                        </template>
                                    </v-text-field>
                                </div>
                            </v-col>

                            <!-- Phone -->
                            <v-col cols="12" md="6">
                                <div class="form-group">
                                    <label class="form-label">
                                        رقم الهاتف <span class="required">*</span>
                                    </label>
                                    <v-text-field v-model="editedSupplier.phone" :rules="[rules.required, rules.phone]"
                                        variant="outlined" density="comfortable" placeholder="01xxxxxxxxx"
                                        hide-details="auto" class="modern-field">
                                        <template v-slot:prepend-inner>
                                            <v-icon color="success" size="20">mdi-phone</v-icon>
                                        </template>
                                    </v-text-field>
                                </div>
                            </v-col>

                            <!-- Email -->
                            <v-col cols="12" md="6">
                                <div class="form-group">
                                    <label class="form-label">البريد الإلكتروني</label>
                                    <v-text-field v-model="editedSupplier.email" :rules="[rules.email]" variant="outlined"
                                        density="comfortable" placeholder="example@domain.com" hide-details="auto"
                                        class="modern-field">
                                        <template v-slot:prepend-inner>
                                            <v-icon color="info" size="20">mdi-email</v-icon>
                                        </template>
                                    </v-text-field>
                                </div>
                            </v-col>

                            <!-- Status Switch -->
                            <v-col cols="12" md="6">
                                <div class="form-group">
                                    <label class="form-label">حالة المورد</label>
                                    <div class="d-flex align-center mt-2">
                                        <v-switch v-model="editedSupplier.isActive" color="primary" hide-details
                                            class="me-3"></v-switch>
                                        <span class="text-body-1">
                                            {{ editedSupplier.isActive ? 'نشط' : 'غير نشط' }}
                                        </span>
                                    </div>
                                </div>
                            </v-col>
                        </v-row>
                    </div>

                    <!-- Address Information Section -->
                    <div class="form-section">
                        <div class="section-header">
                            <v-icon color="orange" class="me-2">mdi-map-marker</v-icon>
                            <h3 class="section-title">معلومات العنوان</h3>
                        </div>

                        <v-row>
                            <!-- Full Address -->
                            <v-col cols="12">
                                <div class="form-group">
                                    <label class="form-label">العنوان الكامل</label>
                                    <v-textarea v-model="editedSupplier.address" variant="outlined" density="comfortable"
                                        placeholder="أدخل العنوان الكامل للمورد..." rows="3" hide-details="auto"
                                        class="modern-field">
                                        <template v-slot:prepend-inner>
                                            <v-icon color="orange" size="20">mdi-map-marker</v-icon>
                                        </template>
                                    </v-textarea>
                                </div>
                            </v-col>
                            <!-- Country -->
                            <v-col cols="12" md="4">
                                <div class="form-group">
                                    <label class="form-label">البلد</label>
                                    <v-select v-model="editedSupplier.country" :items="countryOptions" variant="outlined"
                                        item-value="code"
                                        item-title="name"
                                        density="comfortable" placeholder="اختر البلد" hide-details="auto"
                                        class="modern-field">
                                        <template v-slot:prepend-inner>
                                            <v-icon color="orange" size="20">mdi-earth</v-icon>
                                        </template>
                                    </v-select>
                                </div>
                            </v-col>
                            <!-- City -->
                            <v-col cols="12" md="4">
                                <div class="form-group">
                                    <label class="form-label">المدينة</label>
                                    <v-select v-model="editedSupplier.city" :items="cityOptions" variant="outlined"
                                        item-value="code"
                                        item-title="name"
                                        density="comfortable" placeholder="اختر المدينة" hide-details="auto"
                                        class="modern-field">
                                        <template v-slot:prepend-inner>
                                            <v-icon color="orange" size="20">mdi-city</v-icon>
                                        </template>
                                    </v-select>
                                </div>
                            </v-col>

                            <!-- Postal Code -->
                            <v-col cols="12" md="4">
                                <div class="form-group">
                                    <label class="form-label">الرمز البريدي</label>
                                    <v-text-field v-model="editedSupplier.postalCode" :rules="[rules.postalCode]"
                                        variant="outlined" density="comfortable" placeholder="12345" hide-details="auto"
                                        class="modern-field">
                                        <template v-slot:prepend-inner>
                                            <v-icon color="orange" size="20">mdi-mailbox</v-icon>
                                        </template>
                                    </v-text-field>
                                </div>
                            </v-col>

                            
                        </v-row>
                    </div>

                    <!-- Business Information Section -->
                    <div class="form-section">
                        <div class="section-header">
                            <v-icon color="success" class="me-2">mdi-briefcase</v-icon>
                            <h3 class="section-title">المعلومات التجارية</h3>
                        </div>

                        <v-row>
                            <!-- Business Type -->
                            <v-col cols="12" md="6">
                                <div class="form-group">
                                    <label class="form-label">نوع النشاط التجاري</label>
                                    <v-select v-model="editedSupplier.businessType" :items="businessTypeOptions"
                                        item-value="code"
                                        item-title="name"
                                        variant="outlined" density="comfortable" placeholder="اختر نوع النشاط"
                                        hide-details="auto" class="modern-field">
                                        <template v-slot:prepend-inner>
                                            <v-icon color="success" size="20">mdi-briefcase</v-icon>
                                        </template>
                                    </v-select>
                                </div>
                            </v-col>

                            <!-- Tax Number -->
                            <v-col cols="12" md="6">
                                <div class="form-group">
                                    <label class="form-label">الرقم الضريبي</label>
                                    <v-text-field v-model="editedSupplier.taxNumber" :rules="[rules.taxNumber]"
                                        variant="outlined" density="comfortable" placeholder="رقم التسجيل الضريبي"
                                        hide-details="auto" class="modern-field">
                                        <template v-slot:prepend-inner>
                                            <v-icon color="success" size="20">mdi-receipt</v-icon>
                                        </template>
                                    </v-text-field>
                                </div>
                            </v-col>

                            <!-- Payment Terms -->
                            <v-col cols="12" md="6">
                                <div class="form-group">
                                    <label class="form-label">شروط الدفع</label>
                                    <v-select v-model="editedSupplier.paymentTerms" :items="paymentTermsOptions"
                                        item-value="code"
                                        item-title="name"
                                        variant="outlined" density="comfortable" placeholder="اختر شروط الدفع"
                                        hide-details="auto" class="modern-field">
                                        <template v-slot:prepend-inner>
                                            <v-icon color="success" size="20">mdi-credit-card</v-icon>
                                        </template>
                                    </v-select>
                                </div>
                            </v-col>

                            <!-- Credit Limit -->
                            <v-col cols="12" md="6">
                                <div class="form-group">
                                    <label class="form-label">الحد الائتماني</label>
                                    <v-text-field v-model.number="editedSupplier.creditLimit"
                                        :rules="[rules.nonNegative]" type="number" variant="outlined"
                                        density="comfortable" placeholder="0.00" prefix="ج.م." hide-details="auto"
                                        class="modern-field">
                                        <template v-slot:prepend-inner>
                                            <v-icon color="success" size="20">mdi-currency-usd</v-icon>
                                        </template>
                                    </v-text-field>
                                </div>
                            </v-col>
                        </v-row>
                    </div>

                    <!-- Supplier Icon & Color Section -->
                    <div class="form-section">
                        <div class="section-header">
                            <v-icon color="warning" class="me-2">mdi-palette</v-icon>
                            <h3 class="section-title">الأيقونة واللون</h3>
                        </div>

                        <v-row>
                            <!-- Icon Selection -->
                            <v-col cols="12" md="6">
                                <div class="form-group">
                                    <label class="form-label">أيقونة المورد</label>
                                    <v-select v-model="editedSupplier.icon" :items="iconOptions" variant="outlined"
                                        density="comfortable" placeholder="اختر الأيقونة" hide-details="auto"
                                        class="modern-field">
                                        <template v-slot:item="{ props, item }">
                                            <v-list-item v-bind="props">
                                                <template v-slot:prepend>
                                                    <v-icon color="primary" size="20" class="me-2">
                                                        {{ item.raw.value }}
                                                    </v-icon>
                                                </template>
                                            </v-list-item>
                                        </template>
                                        <template v-slot:selection="{ item }">
                                            <div class="d-flex align-center">
                                                <v-icon color="primary" size="20" class="me-2">
                                                    {{ item.raw.value }}
                                                </v-icon>
                                                {{ item.raw.title }}
                                            </div>
                                        </template>
                                    </v-select>
                                </div>
                            </v-col>

                            <!-- Color Selection -->
                            <v-col cols="12" md="6">
                                <div class="form-group">
                                    <label class="form-label">لون المورد</label>
                                    <v-select v-model="editedSupplier.color" :items="colorOptions" variant="outlined"
                                        density="comfortable" placeholder="اختر اللون" hide-details="auto"
                                        class="modern-field">
                                        <template v-slot:item="{ props, item }">
                                            <v-list-item v-bind="props">
                                                <template v-slot:prepend>
                                                    <div class="color-dot me-3" :style="{ background: item.raw.value }"></div>
                                                </template>
                                            </v-list-item>
                                        </template>
                                        <template v-slot:selection="{ item }">
                                            <div class="d-flex align-center">
                                                <div class="color-dot me-2" :style="{ background: item.raw.value }"></div>
                                                {{ item.raw.title }}
                                            </div>
                                        </template>
                                    </v-select>
                                </div>
                            </v-col>

                            <!-- Icon Preview -->
                            <v-col cols="12">
                                <div class="form-group">
                                    <label class="form-label">معاينة</label>
                                    <div class="icon-preview">
                                        <v-avatar size="64" :color="editedSupplier.color || '#366091'" class="preview-avatar">
                                            <v-icon color="white" size="32">
                                                {{ editedSupplier.icon || 'mdi-truck' }}
                                            </v-icon>
                                        </v-avatar>
                                        <div class="preview-text ms-4">
                                            <h4>{{ editedSupplier.name || 'اسم المورد' }}</h4>
                                            <p class="text-grey">{{ editedSupplier.code || 'كود المورد' }}</p>
                                        </div>
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
                            <!-- Notes -->
                            <v-col cols="12">
                                <div class="form-group">
                                    <label class="form-label">ملاحظات</label>
                                    <v-textarea v-model="editedSupplier.notes" variant="outlined" density="comfortable"
                                        placeholder="أدخل أي ملاحظات عن المورد..." rows="3" hide-details="auto"
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
                        <v-btn color="primary" size="large" :disabled="!formValid" :loading="loading" class="save-btn"
                            @click="saveSupplier">
                            <v-icon start>{{ editedSupplierId ? 'mdi-content-save' : 'mdi-plus' }}</v-icon>
                            {{ editedSupplierId ? 'تحديث المورد' : 'حفظ المورد' }}
                        </v-btn>
                    </div>
                </div>
            </div>
        </v-card>
    </v-dialog>
</template>

<script>
import { success, error } from '@/utils/system-util';
import { getBusinessType, getPaymentTerms } from '@/utils/purchase-util';
import { getCountries, getGovernorates } from '@/utils/lookup-core-util';

export default {
    name: 'SupplierModal',
    props: {
        modelValue: {
            type: Boolean,
            default: false
        },
        supplier: {
            type: Object,
            default: null
        }
    },
    emits: ['update:modelValue', 'save'],
    data() {
        return {
            formValid: false,
            loading: false,
            editedSupplierId: null,
            editedSupplier: {
                name: '',
                code: '',
                contactPerson: '',
                phone: '',
                email: '',
                address: '',
                city: '',
                postalCode: '',
                country: '',
                businessType: '',
                taxNumber: '',
                paymentTerms: '',
                creditLimit: 0,
                icon: 'mdi-truck',
                color: '#366091',
                notes: '',
                isActive: true,
                rating: 85,
                totalOrders: 0,
                totalAmount: 0
            },
            cityOptions: [],
            countryOptions: [],
            businessTypeOptions: [],
            paymentTermsOptions: [],
            iconOptions: [
                { title: 'شاحنة', value: 'mdi-truck' },
                { title: 'مصنع', value: 'mdi-factory' },
                { title: 'متجر', value: 'mdi-store' },
                { title: 'مكتب', value: 'mdi-office-building' },
                { title: 'شركة', value: 'mdi-domain' },
                { title: 'مستودع', value: 'mdi-warehouse' },
                { title: 'طائرة', value: 'mdi-airplane' },
                { title: 'سفينة', value: 'mdi-ship-wheel' },
                { title: 'حقيبة عمل', value: 'mdi-briefcase' },
                { title: 'أدوات', value: 'mdi-tools' }
            ],
            colorOptions: [
                { title: 'أزرق', value: '#366091' },
                { title: 'أخضر', value: '#4CAF50' },
                { title: 'برتقالي', value: '#FF9800' },
                { title: 'أحمر', value: '#F44336' },
                { title: 'بنفسجي', value: '#9C27B0' },
                { title: 'سماوي', value: '#00BCD4' },
                { title: 'وردي', value: '#E91E63' },
                { title: 'أصفر', value: '#FFC107' },
                { title: 'رمادي', value: '#607D8B' },
                { title: 'بني', value: '#795548' }
            ],
            rules: {
                required: value => !!value || 'هذا الحقل مطلوب',
                nonNegative: value => value >= 0 || 'يجب أن يكون صفر أو أكبر',
                code: value => {
                    const pattern = /^[A-Z0-9-_]+$/i;
                    return pattern.test(value) || 'كود المورد يجب أن يحتوي على أحرف وأرقام فقط';
                },
                phone: value => {
                    if (!value) return true;
                    const pattern = /^[0-9+\-\s()]+$/;
                    return pattern.test(value) || 'رقم الهاتف غير صحيح';
                },
                email: value => {
                    if (!value) return true;
                    const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                    return pattern.test(value) || 'البريد الإلكتروني غير صحيح';
                },
                taxNumber: value => {
                    if (!value) return true;
                    const pattern = /^[A-Z0-9]+$/i;
                    return pattern.test(value) || 'الرقم الضريبي يجب أن يحتوي على أحرف وأرقام فقط';
                },
                postalCode: value => {
                    if (!value) return true;
                    const pattern = /^[0-9]{5}$/;
                    return pattern.test(value) || 'الرمز البريدي يجب أن يكون 5 أرقام';
                }
            }
        };
    },

    async mounted() {
        this.businessTypeOptions = await getBusinessType();
        this.paymentTermsOptions = await getPaymentTerms();
        this.countryOptions = await getCountries();
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
        async 'editedSupplier.country'(newCountryCode)  {
            if(!newCountryCode) return
            this.cityOptions = await this.getGovernorates(newCountryCode);
            this.cityOptions = this.cityOptions || []
            this.editedSupplier.city = null; // reset city when country changes
        },
        supplier: {
            immediate: true,
            handler(newSupplier) {
                if (newSupplier) {
                    this.editedSupplier = { ...newSupplier };
                    this.editedSupplierId = newSupplier.id;
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
        getGovernorates,
        resetForm() {
            this.editedSupplierId = null;
            this.editedSupplier = {
                name: '',
                code: '',
                contactPerson: '',
                phone: '',
                email: '',
                address: '',
                city: '',
                postalCode: '',
                country: '',
                businessType: '',
                taxNumber: '',
                paymentTerms: '',
                creditLimit: 0,
                icon: 'mdi-truck',
                color: '#366091',
                notes: '',
                isActive: true,
                rating: 85,
                totalOrders: 0,
                totalAmount: 0
            };
            if (this.$refs.supplierForm) {
                this.$refs.supplierForm.resetValidation();
            }
        },

        closeDialog() {
            this.dialogVisible = false;
        },

        async saveSupplier() {
            if (!this.$refs.supplierForm.validate()) return;

            this.loading = true;
            try {
                const supplierData = { ...this.editedSupplier, id: this.editedSupplierId };

                // Auto-generate code if empty
                if (!supplierData.code && supplierData.name) {
                    supplierData.code = 'SUP-' + supplierData.name.replace(/\s+/g, '-').toUpperCase().substring(0, 10);
                }

                // Here you would typically call your API
                // For now, we'll simulate saving
                if (supplierData.id == null) {
                    // Simulate creating new supplier
                    supplierData.id = Date.now(); // Temporary ID
                    success('تم حفظ المورد بنجاح');
                    this.$emit('save', supplierData);
                } else {
                    // Simulate updating existing supplier
                    success('تم تحديث المورد بنجاح');
                    this.$emit('save', supplierData);
                }

                this.closeDialog();
            } catch (error) {
                console.error('Error saving supplier:', error);
                error('فشل حفظ المورد');
            } finally {
                this.loading = false;
            }
        }
    }
};
</script>

<style scoped>
@import '@/styles/modal.css';

/* Color Dot for color picker */
.color-dot {
    width: 20px;
    height: 20px;
    border-radius: 50%;
    border: 2px solid white;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

/* Icon Preview Section */
.icon-preview {
    display: flex;
    align-items: center;
    padding: 16px;
    background: #f8f9fa;
    border-radius: 8px;
    border: 1px solid #e9ecef;
}

.preview-avatar {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15) !important;
    border: 2px solid white !important;
}

.preview-text h4 {
    margin: 0;
    color: #2d3748;
    font-weight: 600;
}

.preview-text p {
    margin: 4px 0 0 0;
    font-size: 14px;
}

/* Form Section Styling */
.form-section {
    margin-bottom: 32px;
}

.form-section:last-child {
    margin-bottom: 0;
}

/* Business Type and Payment Terms Styling */
.modern-field .v-field__input {
    padding: 12px 16px;
}

/* Status Switch Styling */
.v-switch {
    transform: scale(1.1);
}

/* Required Field Indicator */
.required {
    color: #e53e3e;
    font-weight: bold;
}

/* Responsive Design */
@media (max-width: 768px) {
    .icon-preview {
        flex-direction: column;
        text-align: center;
    }
    
    .preview-text {
        margin-left: 0 !important;
        margin-top: 16px;
    }
    
    .color-dot {
        width: 16px;
        height: 16px;
    }
}

@media (max-width: 600px) {
    .modal-body {
        padding: 16px;
    }
    
    .form-section {
        margin-bottom: 24px;
    }
    
    .section-header {
        margin-bottom: 16px;
    }
    
    .preview-avatar {
        width: 48px !important;
        height: 48px !important;
    }
    
    .preview-avatar .v-icon {
        font-size: 24px !important;
    }
}

/* Animation and Transitions */
.preview-avatar {
    transition: all 0.3s ease;
}

.color-dot {
    transition: all 0.2s ease;
}

.color-dot:hover {
    transform: scale(1.1);
}

/* Print Styles */
@media print {
    .modal-header,
    .modal-actions {
        display: none !important;
    }
    
    .modal-body {
        padding: 0 !important;
    }
    
    .form-section {
        break-inside: avoid;
        margin-bottom: 20px;
    }
}
</style>