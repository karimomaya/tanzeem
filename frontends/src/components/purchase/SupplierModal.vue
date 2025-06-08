<template>
    <v-dialog v-model="dialogVisible" max-width="1000px" persistent>
        <v-card rounded="xl" elevation="8">
            <!-- Modern Header -->
            <ModalHeader :icon="'mdi-truck'" :title="editedSupplierId ? 'تعديل المورد' : 'إضافة مورد جديد'"
                :subtitle="editedSupplierId ? 'تحديث بيانات المورد الحالي' : 'أدخل بيانات المورد الجديد'"
                @close="closeDialog" />

            <!-- Form Content -->
            <div class="modal-body">
                <v-form ref="supplierForm" v-model="formValid">
                    <!-- Basic Information Section -->
                    <FormSection title="المعلومات الأساسية" icon="mdi-information" :color="SECTION_COLORS.basic">

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
                    </FormSection>
                    <!-- Address Information Section -->
                    <FormSection title="معلومات العنوان" icon="mdi-map-marker" :color="SECTION_COLORS.address">
                        <v-row>
                            <!-- Full Address -->
                            <v-col cols="12">
                                <div class="form-group">
                                    <label class="form-label">العنوان الكامل</label>
                                    <v-textarea v-model="editedSupplier.address" variant="outlined" density="comfortable"
                                        placeholder="أدخل العنوان الكامل للمورد..." rows="3" hide-details="auto"
                                        class="modern-field">
                                        <template v-slot:prepend-inner>
                                            <v-icon :color="SECTION_COLORS.address" size="20">mdi-map-marker</v-icon>
                                        </template>
                                    </v-textarea>
                                </div>
                            </v-col>
                            <!-- Country -->
                            <v-col cols="12" md="4">
                                <div class="form-group">
                                    <label class="form-label">البلد</label>
                                    <v-select v-model="editedSupplier.country" :items="countryOptions" variant="outlined"
                                        item-value="code" item-title="name" density="comfortable" placeholder="اختر البلد"
                                        hide-details="auto" class="modern-field">
                                        <template v-slot:prepend-inner>
                                            <v-icon :color="SECTION_COLORS.address" size="20">mdi-earth</v-icon>
                                        </template>
                                    </v-select>
                                </div>
                            </v-col>
                            <!-- governorate -->
                            <v-col cols="12" md="4">
                                <div class="form-group">
                                    <label class="form-label">المدينة</label>
                                    <v-select v-model="editedSupplier.governorate" :items="governorateOptions"
                                        variant="outlined" item-value="code" item-title="name" density="comfortable"
                                        placeholder="اختر المدينة" hide-details="auto" class="modern-field">
                                        <template v-slot:prepend-inner>
                                            <v-icon :color="SECTION_COLORS.address" size="20">mdi-city</v-icon>
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
                                            <v-icon :color="SECTION_COLORS.address" size="20">mdi-mailbox</v-icon>
                                        </template>
                                    </v-text-field>
                                </div>
                            </v-col>
                        </v-row>
                    </FormSection>
                    <!-- Business Information Section -->
                    <FormSection title="المعلومات التجارية" icon="mdi-briefcase" :color="SECTION_COLORS.business">
                        <v-row>
                            <!-- Business Type -->
                            <v-col cols="12" md="6">
                                <div class="form-group">
                                    <label class="form-label">نوع النشاط التجاري</label>
                                    <v-select v-model="editedSupplier.businessType" :items="businessTypeOptions"
                                        item-value="code" item-title="name" variant="outlined" density="comfortable"
                                        placeholder="اختر نوع النشاط" hide-details="auto" class="modern-field">
                                        <template v-slot:prepend-inner>
                                            <v-icon :color="SECTION_COLORS.business" size="20">mdi-briefcase</v-icon>
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
                                            <v-icon :color="SECTION_COLORS.business" size="20">mdi-receipt</v-icon>
                                        </template>
                                    </v-text-field>
                                </div>
                            </v-col>
                            <!-- Payment Terms -->
                            <v-col cols="12" md="6">
                                <div class="form-group">
                                    <label class="form-label">شروط الدفع</label>
                                    <v-select v-model="editedSupplier.paymentTerm" :items="paymentTermsOptions"
                                        item-value="code" item-title="name" variant="outlined" density="comfortable"
                                        placeholder="اختر شروط الدفع" hide-details="auto" class="modern-field">
                                        <template v-slot:prepend-inner>
                                            <v-icon :color="SECTION_COLORS.business" size="20">mdi-credit-card</v-icon>
                                        </template>
                                    </v-select>
                                </div>
                            </v-col>
                            <!-- Credit Limit -->
                            <v-col cols="12" md="6">
                                <div class="form-group">
                                    <label class="form-label">الحد الائتماني</label>
                                    <v-text-field v-model.number="editedSupplier.creditLimit" :rules="[rules.nonNegative]"
                                        type="number" variant="outlined" density="comfortable" placeholder="0.00"
                                        :prefix="getCurrencySymbol()" hide-details="auto" class="modern-field">
                                        <template v-slot:prepend-inner>
                                            <v-icon :color="SECTION_COLORS.business" size="20">{{ getCurrencyIcon()
                                            }}</v-icon>
                                        </template>
                                    </v-text-field>
                                </div>
                            </v-col>
                        </v-row>
                    </FormSection>
                    <!-- Supplier Icon & Color Section -->
                    <FormSection title="الأيقونة واللون" icon="mdi-palette" :color="SECTION_COLORS.appearance">
                        <v-row>
                            <!-- Icon Selection -->
                            <v-col cols="12" md="6">
                                <div class="form-group">
                                    <label class="form-label">أيقونة المورد</label>
                                    <v-select v-model="editedSupplier.icon" :items="LOCATION_ICON_OPTIONS"
                                        variant="outlined" density="comfortable" placeholder="اختر الأيقونة"
                                        hide-details="auto" class="modern-field">
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
                                    <v-select v-model="editedSupplier.color" :items="COLOR_OPTIONS" variant="outlined"
                                        density="comfortable" placeholder="اختر اللون" hide-details="auto"
                                        class="modern-field">
                                        <template v-slot:item="{ props, item }">
                                            <v-list-item v-bind="props">
                                                <template v-slot:prepend>
                                                    <div class="color-dot me-3" :style="{ background: item.raw.value }">
                                                    </div>
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

                        </v-row>
                    </FormSection>

                    <!-- Additional Information Section -->
                    <FormSection title="معلومات إضافية" icon="mdi-text" :color="SECTION_COLORS.additional">
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
                    </FormSection>
                    <!-- Icon Preview -->
                    <FormSection title="معاينة" icon="mdi-eye" :color="SECTION_COLORS.preview">
                        <v-col cols="12">
                            <div class="form-group">
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
                    </FormSection>
                </v-form>
            </div>
            <!-- Actions -->
            <ModalActions :form-valid="formValid" :loading="loading"
                :primary-text="editedSupplierId ? 'تحديث المورد' : 'حفظ المورد'"
                :primary-icon="editedSupplierId ? 'mdi-content-save' : 'mdi-plus'" :primary-disabled="!formValid"
                :cancel-disabled="loading" @cancel="closeDialog" @primary-action="saveSupplier" />
        </v-card>
    </v-dialog>
</template>

<script>
import { success, error } from '@/utils/system-util';
import { getCurrencyIcon, getCurrencySymbol } from '@/utils/currency-util';
import { getCountries, getGovernorates } from '@/services/lookup-service';
import { updateSupplier, createSupplier, getBusinessType, getPaymentTerms } from '@/services/purchase-service';

import { LOCATION_ICON_OPTIONS } from '@/constants/icons.js';
import { COLOR_OPTIONS } from '@/constants/colors.js';
import ModalHeader from '@/components/common/ModalHeader.vue'
import ModalActions from '@/components/common/ModalActions.vue'
import { SECTION_COLORS } from '@/constants/colors'
import FormSection from '@/components/common/FormSection.vue'

export default {
    name: 'SupplierModal',
    components: {
        ModalHeader,
        ModalActions,
        FormSection
    },
    props: {
        modelValue: {
            type: Boolean,
            default: false
        },

        supplierToEdit: {
            type: Object,
            default: null
        }
    },
    emits: ['update:modelValue', 'save'],
    data() {
        return {
            SECTION_COLORS,
            LOCATION_ICON_OPTIONS, COLOR_OPTIONS,
            formValid: false,
            loading: false,
            editedSupplierId: null,
            isInitialLoad: false,
            editedSupplier: {
                name: '',
                code: '',
                contactPerson: '',
                phone: '',
                email: '',
                address: '',
                governorate: '',
                postalCode: '',
                country: '',
                businessType: '',
                taxNumber: '',
                paymentTerm: '',
                creditLimit: 0,
                icon: 'mdi-truck',
                color: '#366091',
                notes: '',
                isActive: true,
                rating: 85,
                totalOrders: 0,
                totalAmount: 0
            },
            governorateOptions: [],
            countryOptions: [],
            businessTypeOptions: [],
            paymentTermsOptions: [],

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
        async 'editedSupplier.country'(newCountryCode) {
            if (!newCountryCode || this.isInitialLoad) return
            this.governorateOptions = await this.getGovernorates(newCountryCode);

            this.editedSupplier.governorate = null;
        },
        supplierToEdit: {
            immediate: true,
            async handler(newSupplier) {
                if (newSupplier) {
                    this.isInitialLoad = true; // Set flag before setting country

                    // First, load governorate options if country exists
                    if (newSupplier.country?.code || newSupplier.country) {
                        const countryCode = newSupplier.country?.code || newSupplier.country;
                        this.governorateOptions = await this.getGovernorates(countryCode);
                    }

                    this.editedSupplier = {
                        ...newSupplier,
                        country: newSupplier.country.code || newSupplier.country,
                        governorate: newSupplier.governorate?.code || newSupplier.governorate,
                        businessType: newSupplier.businessType?.code || newSupplier.businessType,
                        paymentTerm: newSupplier.paymentTerm?.code || newSupplier.paymentTerm,
                    };
                    this.editedSupplierId = newSupplier.id;

                    setTimeout(() => {
                        this.isInitialLoad = false;
                    }, 100);

                } else {
                    this.isInitialLoad = false;
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
        getCurrencySymbol,
        getCurrencyIcon,
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
                governorate: '',
                postalCode: '',
                country: '',
                businessType: '',
                taxNumber: '',
                paymentTerm: '',
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

                if (supplierData.id == null) {
                    let response = await createSupplier(supplierData);
                    if (response != null && response.id != null) {
                        success('تم حفظ المورد بنجاح');
                        this.$emit('save', supplierData);
                    } else {
                        error('فشل حفظ المورد');
                        console.error(response);
                    }
                } else {
                    let response = await updateSupplier(supplierData);
                    if (response != null && response.id != null) {
                        success('تم تحديث المورد بنجاح');
                        this.$emit('save', supplierData);
                    } else {
                        error('فشل تحديث المورد');
                        console.error(response);
                    }

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


/* Business Type and Payment Terms Styling */
.modern-field .v-field__input {
    padding: 12px 16px;
}

/* Status Switch Styling */
.v-switch {
    transform: scale(1.1);
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
</style>