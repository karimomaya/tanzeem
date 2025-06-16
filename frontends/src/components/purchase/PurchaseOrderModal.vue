<template>
    <BaseModal :model-value="modelValue" @update:model-value="$emit('update:modelValue', $event)"
        icon="mdi-clipboard-list"
        :title="editedPurchaseOrderId ? 'تعديل أمر الشراء' : 'إضافة أمر شراء جديد'"
        :subtitle="editedPurchaseOrderId ? 'تحديث بيانات أمر الشراء' : 'إنشاء أمر شراء جديد'"
        :primary-text="editedPurchaseOrderId ? 'تحديث الأمر' : 'حفظ الأمر'"
        :primary-icon="editedPurchaseOrderId ? 'mdi-content-save' : 'mdi-plus'" :loading="loading"
        :primary-disabled="loading || formData.items.length === 0" @close="closeDialog" @save="saveOrder">
        <template #content="{ formValid }">
            <FormSection title="المعلومات الأساسية" icon="mdi-information" :color="SECTION_COLORS.basic">

                <v-row>
                    <!-- Order Number -->
                    <v-col cols="12" md="6">
                        <div class="form-group">
                            <label class="form-label">
                                رقم الفاتورة <span class="required">*</span>
                            </label>
                            <v-text-field v-model="formData.invoiceNumber" :rules="fieldValidations.invoiceNumber"
                                variant="outlined" density="comfortable" placeholder="أدخل رقم الفاتورة"
                                hide-details="auto" prepend-inner-icon="mdi-pound" class="modern-field"></v-text-field>
                        </div>
                    </v-col>

                    <!-- Order Date -->
                    <v-col cols="12" md="6">
                        <div class="form-group">
                            <label class="form-label">
                                تاريخ الأمر <span class="required">*</span>
                            </label>
                            <v-text-field v-model="formData.purchaseDate" :rules="fieldValidations.purchaseDate"
                                variant="outlined" density="comfortable" type="date" hide-details="auto"
                                prepend-inner-icon="mdi-calendar" class="modern-field"></v-text-field>
                        </div>
                    </v-col>

                    <v-col cols="12" md="6">
                        <div class="form-group">
                            <label class="form-label">تاريخ التسليم المتوقع</label>
                            <v-text-field v-model="formData.expectedDeliveryAt" variant="outlined" density="comfortable"
                                type="datetime-local" hide-details="auto" prepend-inner-icon="mdi-truck-delivery"
                                class="modern-field"></v-text-field>
                        </div>
                    </v-col>

                    <!-- Delivered At (only for edit and when status is RECEIVED) -->
                    <v-col cols="12" md="6" v-if="editedPurchaseOrderId && formData.status === 'RECEIVED'">
                        <div class="form-group">
                            <label class="form-label">تاريخ التسليم الفعلي</label>
                            <v-text-field v-model="formData.deliveredAt" variant="outlined" density="comfortable"
                                type="datetime-local" hide-details="auto" prepend-inner-icon="mdi-check-circle"
                                class="modern-field"></v-text-field>
                        </div>
                    </v-col>

                    <!-- Confirmed At (only for edit) -->
                    <v-col cols="12" md="6" v-if="editedPurchaseOrderId">
                        <div class="form-group">
                            <label class="form-label">تاريخ التأكيد</label>
                            <v-text-field v-model="formData.confirmedAt" variant="outlined" density="comfortable"
                                type="date" hide-details="auto" prepend-inner-icon="mdi-calendar-check"
                                class="modern-field"></v-text-field>
                        </div>
                    </v-col>

                    <!-- Supplier -->
                    <v-col cols="12" md="6">
                        <div class="form-group">
                            <label class="form-label">
                                المورد <span class="required">*</span>
                            </label>

                            <SearchableSelect v-model="formData.supplierId" :api-service="getSuppliers"
                                :current-item="itemToEdit?.supplier || null" :rules="fieldValidations.supplier"
                                placeholder="ابحث عن المورد..." class="modern-field">

                                <template v-slot:item="{ props, item }">
                                    <v-list-item v-bind="props">
                                        <template v-slot:prepend>
                                            <v-icon>mdi-truck</v-icon>
                                        </template>
                                        <v-list-item-title>{{ item.raw.name }}</v-list-item-title>
                                        <v-list-item-subtitle v-if="item.raw.phone">{{ item.raw.phone
                                        }}</v-list-item-subtitle>
                                    </v-list-item>
                                </template>

                                <template v-slot:prepend-inner>
                                    <v-icon color="primary" size="20">mdi-truck</v-icon>
                                </template>
                            </SearchableSelect>
                        </div>
                    </v-col>

                    <!-- Status (only for edit) -->
                    <v-col cols="12" md="6" v-if="editedPurchaseOrderId">
                        <div class="form-group">
                            <label class="form-label">حالة الأمر</label>
                            <v-select v-model="formData.status" :items="statusOptions" variant="outlined"
                                density="comfortable" hide-details="auto" prepend-inner-icon="mdi-flag"
                                item-title="text" item-value="value" class="modern-field"></v-select>
                        </div>
                    </v-col>
                </v-row>
            </FormSection>
            <FormSection title="المنتجات" icon="mdi-package-variant" :icon-color="SECTION_COLORS.products"
                :action-button="{ text: 'إضافة منتج', icon: 'mdi-plus', action: 'add' }" @action="addProductItem">
                <v-row>
                    <!-- Product Items List -->
                    <v-col cols="12">
                        <div v-if="formData.items.length === 0" class="empty-products">
                            <v-icon size="48" color="grey">mdi-package-variant-closed</v-icon>
                            <p class="text-body-2 text-medium-emphasis mt-2">لم يتم إضافة أي منتجات بعد
                            </p>
                            <v-btn color="primary" size="small" prepend-icon="mdi-plus" @click="addProductItem">
                                إضافة المنتج الأول
                            </v-btn>
                        </div>

                        <div v-else class="products-list">
                            <v-card v-for="(item, index) in formData.items" :key="index" class="product-item mb-3"
                                elevation="2">
                                <v-card-text class="pa-4">
                                    <v-row align="center">
                                        <!-- Product Selection -->
                                        <v-col cols="12" md="4">
                                            <v-select v-model="item.productId" :items="productOptions" label="المنتج"
                                                variant="outlined" density="compact"
                                                :rules="fieldValidations.orderItems" hide-details="auto"
                                                item-title="name" item-value="id"
                                                @update:model-value="updateProductPrice(item, $event)">
                                                <template v-slot:item="{ props, item: product }">
                                                    <v-list-item v-bind="props">
                                                        <v-list-item-title>{{ product.raw.name
                                                        }}</v-list-item-title>
                                                        <v-list-item-subtitle>{{
                                                            formatCurrency(product.raw.purchasePrice ||
                                                                0)
                                                        }}</v-list-item-subtitle>
                                                    </v-list-item>
                                                </template>
                                            </v-select>
                                        </v-col>

                                        <!-- Quantity -->
                                        <v-col cols="12" md="2">
                                            <v-text-field v-model.number="item.quantity" label="الكمية"
                                                variant="outlined" density="compact" type="number" min="1"
                                                :rules="fieldValidations.nonNegativeNumber" hide-details="auto"
                                                @input="calculateItemTotal(item)"></v-text-field>
                                        </v-col>

                                        <!-- Unit Price -->
                                        <v-col cols="12" md="2">
                                            <v-text-field v-model.number="item.unitPrice" label="سعر الوحدة"
                                                variant="outlined" density="compact" type="number" min="0" step="0.01"
                                                :rules="fieldValidations.nonNegativeNumber" hide-details="auto"
                                                @input="calculateItemTotal(item)"></v-text-field>
                                        </v-col>

                                        <!-- Total Price -->
                                        <v-col cols="12" md="2">
                                            <v-text-field :model-value="formatCurrency(item.totalPrice || 0)"
                                                label="الإجمالي" variant="outlined" density="compact" readonly
                                                hide-details="auto"></v-text-field>
                                        </v-col>

                                        <!-- Remove Button -->
                                        <v-col cols="12" md="2">
                                            <v-btn icon="mdi-delete" size="small" color="error"
                                                @click="removeProductItem(index)" v-tooltip="'حذف المنتج'"></v-btn>
                                        </v-col>
                                    </v-row>
                                </v-card-text>
                            </v-card>
                        </div>
                    </v-col>

                    <!-- Total Amount Section -->
                    <v-col cols="12" v-if="formData.items.length > 0">
                        <div class="total-section">
                            <v-card class="total-card" elevation="2">
                                <v-card-text class="pa-4">
                                    <div class="d-flex justify-space-between align-center">
                                        <span class="text-h6">المبلغ الإجمالي:</span>
                                        <span class="text-h4 text-primary font-weight-bold">{{
                                            formatCurrency(totalAmount) }}</span>
                                    </div>
                                </v-card-text>
                            </v-card>
                        </div>
                    </v-col>
                </v-row>
            </FormSection>
            <FormSection title="المرفقات" icon="mdi-image" :icon-color="SECTION_COLORS.media"
                info-text="يمكن رفع ملفات متعددة (PDF, JPG, PNG) - حد أقصى 10 ميجابايت لكل ملف">
                <v-row>
                    <v-col cols="12">
                        <FileUploadList ref="contentUpload" v-model="formData.attachments" label="رفع الفواتير أو المستندات" :multiple="true"
                            :rules="fieldValidations.attachments" placeholder="اختر الملفات..."
                            file-list-title="الملفات المرفقة" :show-download="true" :show-url-input="true"
                            url-input-label="رابط الملف" url-placeholder="https://example.com/file.pdf" url-icon="mdi-link"
                            url-icon-color="warning" :url-rules="fieldValidations.url" :show-url-download="true"
                            :show-url-preview="true" url-validation-message="اضغط على زر التحميل لإضافة الملف للقائمة"
                            :entity-type="'Purchase'" :entity-id="editedPurchaseOrderId" content-context="purchase"
                            :auto-load="!!editedPurchaseOrderId"
                            accept=".pdf,.jpg,.jpeg,.png,.doc,.docx,.xlsx,.xls,image/*" 
                            :url-validation-pattern="/\.(pdf|jpg|jpeg|png|gif|webp|bmp|svg|doc|docx|xlsx|xls)$/i" />
                    </v-col>
                </v-row>
            </FormSection>
            <FormSection title="معلومات إضافية" icon="mdi-text" :icon-color="SECTION_COLORS.additional">

                <v-row>
                    <v-col cols="12">
                        <div class="form-group">
                            <label class="form-label">ملاحظات (اختياري)</label>
                            <v-textarea v-model="formData.notes" variant="outlined" density="comfortable"
                                placeholder="أدخل أي ملاحظات..." rows="3" hide-details="auto"
                                prepend-inner-icon="mdi-note-text" class="modern-field"></v-textarea>
                        </div>
                    </v-col>
                </v-row>
            </FormSection>
            <FormSection title="معاينة الأمر" icon="mdi-eye" :icon-color="SECTION_COLORS.preview">
                <v-row>
                    <v-col cols="12">
                        <v-card elevation="2" rounded="lg" class="pa-4 bg-grey-lighten-5">
                            <div class="d-flex align-center justify-space-between mb-3">
                                <div>
                                    <div class="text-h6 font-weight-bold">{{ formData.invoiceNumber || 'رقم الأمر' }}
                                    </div>
                                    <div class="text-body-2 text-grey-darken-1">{{ formData.purchaseDate ||
                                        'تاريخ الأمر' }}</div>
                                </div>
                                <v-chip :color="getStatusColor(formData.status)" size="small">
                                    {{ getStatusText(formData.status) }}
                                </v-chip>
                            </div>
                            <div v-if="formData.items.length > 0" class="mt-3">
                                <div class="text-subtitle-2 mb-2">المنتجات: {{ formData.items.length }}
                                </div>
                                <div class="text-h6 text-primary">المجموع: {{ formatCurrency(totalAmount) }}
                                </div>
                            </div>
                        </v-card>
                    </v-col>
                </v-row>
            </FormSection>
        </template>
    </BaseModal>
    <!-- Image Preview Dialog -->
</template>

<script>
import SearchableSelect from '@/components/common/SearchableSelect.vue'
import { formatCurrency } from '@/utils/currency-util';
import { getProducts } from '@/services/product-service';
import { createPurchaseOrder, updatePurchaseOrder, getSuppliers } from '@/services/purchase-service';
import { success, error } from '@/utils/system-util';
import { fieldValidations } from '@/utils/validation-util';
import ModalHeader from '@/components/common/ModalHeader.vue'
import ModalActions from '@/components/common/ModalActions.vue'
import { SECTION_COLORS } from '@/constants/colors'
import FormSection from '@/components/common/FormSection.vue'
import FileUploadList from '@/components/common/FileUploadList.vue'
import BaseModal from '@/components/common/BaseModal.vue'

export default {
    name: 'PurchaseOrderModal',
    components: {
        SearchableSelect,
        ModalHeader,
        ModalActions,
        FormSection,
        FileUploadList,
        BaseModal
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
    data() {
        return {
            SECTION_COLORS,
            fieldValidations,
            loadingProducts: false,
            productOptions: [],
            loading: false,
            editedPurchaseOrderId: null,
            statusOptions: [
                { text: 'في الانتظار', value: 'PENDING' },
                { text: 'تم الاستلام', value: 'RECEIVED' },
                { text: 'ملغي', value: 'CANCELLED' }
            ],
            formData: {
                invoiceNumber: '',
                purchaseDate: new Date().toISOString().split('T')[0],
                supplierId: null,
                status: 'PENDING',
                items: [],
                notes: '',
                attachments: [],
                expectedDeliveryAt: '',
                deliveredAt: '',
                confirmedAt: ''
            }
        };
    },
    computed: {
        totalAmount() {
            return this.formData.items.reduce((total, item) => {
                return total + (item.totalPrice || 0);
            }, 0);
        }
    },
    emits: ['update:modelValue', 'save'],
    watch: {
        itemToEdit: {
            immediate: true,
            handler(newPurchaseOrder) {
                if (newPurchaseOrder) {
                    this.editedPurchaseOrderId = newPurchaseOrder.id;
                    this.formData = {
                        id: newPurchaseOrder.id,
                        invoiceNumber: newPurchaseOrder.invoiceNumber,
                        purchaseDate: newPurchaseOrder.purchaseDate,
                        supplierId: newPurchaseOrder.supplier?.id,
                        status: newPurchaseOrder.status,
                        items: newPurchaseOrder.items ? [...newPurchaseOrder.items] : [],
                        notes: newPurchaseOrder.notes || '',
                        attachments: [],
                        expectedDeliveryAt: this.formatDateTime(newPurchaseOrder.expectedDeliveryAt),
                        deliveredAt: this.formatDateTime(newPurchaseOrder.deliveredAt),
                        confirmedAt: this.formatDate(newPurchaseOrder.confirmedAt)
                    };
                } else {
                    this.resetForm();
                }
            }
        },

        modelValue(newValue) {
            if (newValue) {
                this.loadProducts();
            } else {
                this.resetForm();
            }
        }
    },

    methods: {
        getSuppliers,
        formatCurrency,
        getStatusColor(status) {
            const colors = {
                'PENDING': 'warning',
                'RECEIVED': 'success',
                'CANCELLED': 'error'
            };
            return colors[status] || 'grey';
        },
        getStatusText(status) {
            const texts = {
                'PENDING': 'في الانتظار',
                'RECEIVED': 'تم الاستلام',
                'CANCELLED': 'ملغي'
            };
            return texts[status] || status;
        },
        resetForm() {
            this.editedPurchaseOrderId = null;
            this.formData = {
                invoiceNumber: this.generateInvoiceNumber(),
                purchaseDate: new Date().toISOString().split('T')[0],
                supplierId: null,
                status: 'PENDING',
                items: [],
                notes: '',
                attachments: [],
                expectedDeliveryAt: '',
                deliveredAt: '',
                confirmedAt: ''
            };

        },
        async loadProducts() {
            try {
                this.loadingProducts = true;
                const params = new URLSearchParams({
                    page: 0,
                    size: 1000,
                    isActive: 'true'
                });
                const response = await getProducts(params);
                if (response && response.content) {
                    this.productOptions = response.content;
                }
            } catch (err) {
                console.error('Error loading products:', err);
                error('فشل تحميل المنتجات');
            } finally {
                this.loadingProducts = false;
            }
        },
        generateInvoiceNumber() {
            const now = new Date();
            const year = now.getFullYear();
            const month = String(now.getMonth() + 1).padStart(2, '0');
            const day = String(now.getDate()).padStart(2, '0');
            const time = String(now.getHours()).padStart(2, '0') + String(now.getMinutes()).padStart(2, '0');
            return `PO-${year}${month}${day}-${time}`;
        },
        formatDateTime(dateTime) {
            if (!dateTime) return '';
            return new Date(dateTime).toISOString().slice(0, 16); // for datetime-local input
        },

        formatDate(date) {
            if (!date) return '';
            return new Date(date).toISOString().split('T')[0]; // for date input
        },

        addProductItem() {
            this.formData.items.push({
                productId: null,
                quantity: 1,
                unitPrice: 0,
                totalPrice: 0
            });
        },

        removeProductItem(index) {
            this.formData.items.splice(index, 1);
        },

        updateProductPrice(item, productId) {
            const product = this.productOptions.find(p => p.id === productId);
            if (product) {
                item.unitPrice = product.purchasePrice || 0;
                this.calculateItemTotal(item);
            }
        },

        calculateItemTotal(item) {
            item.totalPrice = (item.quantity || 0) * (item.unitPrice || 0);
        },

        async saveOrder() {
            if (this.formData.items.length === 0) {
                error('يجب إضافة منتج واحد على الأقل');
                return;
            }

            try {
                this.loading = true;

                // Prepare the data
                const orderData = {
                    ...this.formData,
                    totalAmount: this.totalAmount
                };

                let response;
                if (this.editedPurchaseOrderId) {
                    response = await updatePurchaseOrder(orderData);
                } else {
                    response = await createPurchaseOrder(orderData);
                }

                if (response?.id) {
                    await this.$refs.contentUpload.save(response?.id);
                    success(this.editedPurchaseOrderId ? 'تم تحديث أمر الشراء بنجاح' : 'تم إنشاء أمر الشراء بنجاح');
                    this.$emit('save');
                    this.closeDialog();
                } else {
                    error(this.editedPurchaseOrderId ? 'فشل تحديث أمر الشراء' : 'فشل إنشاء أمر الشراء');
                }
            } catch (err) {
                console.error('Error saving purchase order:', err);
                error(this.editedPurchaseOrderId ? 'فشل تحديث أمر الشراء' : 'فشل إنشاء أمر الشراء');
            } finally {
                this.loading = false;
            }
        },

        closeDialog() {
            this.$emit('update:modelValue', false);
        }
    }
};
</script>
<style scoped>
@import '@/styles/modal.css';

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
}

.attachment-item:hover {
    background-color: #f8fafc;
}

/* Scrollbar for attachments list */
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

.empty-products {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 40px 20px;
    text-align: center;
    background: #f8fafc;
    border: 2px dashed #cbd5e0;
    border-radius: 12px;
}

.products-list {
    max-height: 400px;
    overflow-y: auto;
}

.product-item {
    border: 1px solid #e2e8f0;
    border-radius: 12px !important;
}

.total-card {
    background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
    border: 1px solid #bae6fd;
    border-radius: 12px !important;
}
</style>