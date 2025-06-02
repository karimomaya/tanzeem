<template>
    <v-dialog v-model="dialog" max-width="1200px" persistent scrollable>
        <v-card class="purchase-order-modal">
            <!-- Header -->
            <v-card-title class="modal-header">
                <div class="d-flex align-center justify-space-between w-100">
                    <div class="d-flex align-center">
                        <v-icon size="24" color="primary" class="me-3">mdi-clipboard-list</v-icon>
                        <span class="modal-title">{{ isEdit ? 'تعديل أمر الشراء' : 'إضافة أمر شراء جديد' }}</span>
                    </div>
                    <v-btn icon="mdi-close" size="small" @click="closeDialog"></v-btn>
                </div>
            </v-card-title>

            <v-divider></v-divider>

            <!-- Content -->
            <v-card-text class="modal-content">
                <v-form ref="form" v-model="formValid" lazy-validation>
                    <v-row>
                        <!-- Basic Information Section -->
                        <v-col cols="12">
                            <div class="section-header">
                                <v-icon color="primary" class="me-2">mdi-information</v-icon>
                                <span class="section-title">المعلومات الأساسية</span>
                            </div>
                        </v-col>

                        <!-- Order Number -->
                        <v-col cols="12" md="6">
                            <v-text-field v-model="formData.orderNumber" label="رقم الأمر" variant="outlined"
                                density="comfortable" :rules="orderNumberRules" hide-details="auto"
                                prepend-inner-icon="mdi-pound"></v-text-field>
                        </v-col>

                        <!-- Order Date -->
                        <v-col cols="12" md="6">
                            <v-text-field v-model="formData.orderDate" label="تاريخ الأمر" variant="outlined"
                                density="comfortable" type="date" :rules="orderDateRules" hide-details="auto"
                                prepend-inner-icon="mdi-calendar"></v-text-field>
                        </v-col>

                        <!-- Supplier -->
                        <v-col cols="12" md="6">
                            <v-select v-model="formData.supplierId" :items="supplierOptions" label="المورد"
                                variant="outlined" density="comfortable" :rules="supplierRules" hide-details="auto"
                                prepend-inner-icon="mdi-truck" item-title="name" item-value="id"
                                :loading="loadingSuppliers">
                                <template v-slot:item="{ props, item }">
                                    <v-list-item v-bind="props">
                                        <template v-slot:prepend>
                                            <v-icon>mdi-truck</v-icon>
                                        </template>
                                        <v-list-item-title>{{ item.raw.name }}</v-list-item-title>
                                        <v-list-item-subtitle v-if="item.raw.phone">{{ item.raw.phone }}</v-list-item-subtitle>
                                    </v-list-item>
                                </template>
                            </v-select>
                        </v-col>

                        <!-- Status (only for edit) -->
                        <v-col cols="12" md="6" v-if="isEdit">
                            <v-select v-model="formData.status" :items="statusOptions" label="حالة الأمر"
                                variant="outlined" density="comfortable" hide-details="auto"
                                prepend-inner-icon="mdi-flag" item-title="text" item-value="value"></v-select>
                        </v-col>

                        <!-- Products Section -->
                        <v-col cols="12">
                            <v-divider class="my-4"></v-divider>
                            <div class="section-header">
                                <v-icon color="primary" class="me-2">mdi-package-variant</v-icon>
                                <span class="section-title">المنتجات</span>
                                <v-spacer></v-spacer>
                                <v-btn color="primary" size="small" prepend-icon="mdi-plus" @click="addProductItem">
                                    إضافة منتج
                                </v-btn>
                            </div>
                        </v-col>

                        <!-- Product Items List -->
                        <v-col cols="12">
                            <div v-if="formData.items.length === 0" class="empty-products">
                                <v-icon size="48" color="grey">mdi-package-variant-closed</v-icon>
                                <p class="text-body-2 text-medium-emphasis mt-2">لم يتم إضافة أي منتجات بعد</p>
                                <v-btn color="primary" size="small" prepend-icon="mdi-plus" @click="addProductItem">
                                    إضافة المنتج الأول
                                </v-btn>
                            </div>

                            <div v-else class="products-list">
                                <v-card v-for="(item, index) in formData.items" :key="index" class="product-item mb-3" elevation="2">
                                    <v-card-text class="pa-4">
                                        <v-row align="center">
                                            <!-- Product Selection -->
                                            <v-col cols="12" md="4">
                                                <v-select v-model="item.productId" :items="productOptions" label="المنتج"
                                                    variant="outlined" density="compact" :rules="[v => !!v || 'المنتج مطلوب']"
                                                    hide-details="auto" item-title="name" item-value="id" 
                                                    @update:model-value="updateProductPrice(item, $event)">
                                                    <template v-slot:item="{ props, item: product }">
                                                        <v-list-item v-bind="props">
                                                            <v-list-item-title>{{ product.raw.name }}</v-list-item-title>
                                                            <v-list-item-subtitle>{{ formatCurrency(product.raw.purchasePrice || 0) }}</v-list-item-subtitle>
                                                        </v-list-item>
                                                    </template>
                                                </v-select>
                                            </v-col>

                                            <!-- Quantity -->
                                            <v-col cols="12" md="2">
                                                <v-text-field v-model.number="item.quantity" label="الكمية" variant="outlined"
                                                    density="compact" type="number" min="1" :rules="[v => v > 0 || 'الكمية يجب أن تكون أكبر من صفر']"
                                                    hide-details="auto" @input="calculateItemTotal(item)"></v-text-field>
                                            </v-col>

                                            <!-- Unit Price -->
                                            <v-col cols="12" md="2">
                                                <v-text-field v-model.number="item.unitPrice" label="سعر الوحدة" variant="outlined"
                                                    density="compact" type="number" min="0" step="0.01"
                                                    :rules="[v => v >= 0 || 'السعر يجب أن يكون أكبر من أو يساوي صفر']"
                                                    hide-details="auto" @input="calculateItemTotal(item)"></v-text-field>
                                            </v-col>

                                            <!-- Total Price -->
                                            <v-col cols="12" md="2">
                                                <v-text-field :model-value="formatCurrency(item.totalPrice || 0)" label="الإجمالي"
                                                    variant="outlined" density="compact" readonly hide-details="auto"></v-text-field>
                                            </v-col>

                                            <!-- Remove Button -->
                                            <v-col cols="12" md="2">
                                                <v-btn icon="mdi-delete" size="small" color="error" @click="removeProductItem(index)"
                                                    v-tooltip="'حذف المنتج'"></v-btn>
                                            </v-col>
                                        </v-row>
                                    </v-card-text>
                                </v-card>
                            </div>
                        </v-col>

                        <!-- Total Amount Section -->
                        <v-col cols="12" v-if="formData.items.length > 0">
                            <v-divider class="my-4"></v-divider>
                            <div class="total-section">
                                <v-card class="total-card" elevation="2">
                                    <v-card-text class="pa-4">
                                        <div class="d-flex justify-space-between align-center">
                                            <span class="text-h6">المبلغ الإجمالي:</span>
                                            <span class="text-h4 text-primary font-weight-bold">{{ formatCurrency(totalAmount) }}</span>
                                        </div>
                                    </v-card-text>
                                </v-card>
                            </div>
                        </v-col>

                        <!-- Notes -->
                        <v-col cols="12">
                            <v-divider class="my-4"></v-divider>
                            <v-textarea v-model="formData.notes" label="ملاحظات (اختياري)" variant="outlined"
                                density="comfortable" rows="3" hide-details="auto" prepend-inner-icon="mdi-note-text"></v-textarea>
                        </v-col>

                        <!-- File Upload Section -->
                        <v-col cols="12">
                            <v-divider class="my-4"></v-divider>
                            <div class="section-header">
                                <v-icon color="primary" class="me-2">mdi-paperclip</v-icon>
                                <span class="section-title">المرفقات (اختياري)</span>
                            </div>
                            <v-file-input v-model="formData.attachments" label="رفع الفواتير أو المستندات" variant="outlined"
                                density="comfortable" multiple accept=".pdf,.jpg,.jpeg,.png" hide-details="auto"
                                prepend-icon="mdi-file-document" show-size>
                                <template v-slot:selection="{ fileNames }">
                                    <template v-for="(fileName, index) in fileNames" :key="fileName">
                                        <v-chip v-if="index < 2" color="primary" size="small" class="me-2">
                                            {{ fileName }}
                                        </v-chip>
                                        <span v-else-if="index === 2" class="text-overline text-grey-darken-3 mx-2">
                                            +{{ fileNames.length - 2 }} ملف(ات)
                                        </span>
                                    </template>
                                </template>
                            </v-file-input>
                        </v-col>
                    </v-row>
                </v-form>
            </v-card-text>

            <v-divider></v-divider>

            <!-- Actions -->
            <v-card-actions class="modal-actions">
                <v-spacer></v-spacer>
                <v-btn variant="outlined" @click="closeDialog" :disabled="loading">
                    إلغاء
                </v-btn>
                <v-btn color="primary" @click="saveOrder" :loading="loading" :disabled="!formValid || formData.items.length === 0">
                    {{ isEdit ? 'تحديث' : 'حفظ' }}
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
import { formatCurrency } from '@/utils/system-util';
import { getProducts } from '@/utils/product-util';
import { createPurchaseOrder, updatePurchaseOrder } from '@/utils/purchase-util';
import { success, error } from '@/utils/system-util';

export default {
    name: 'PurchaseOrderModal',
    props: {
        modelValue: {
            type: Boolean,
            default: false
        },
        purchaseOrder: {
            type: Object,
            default: null
        },
        suppliers: {
            type: Array,
            default: () => []
        }
    },

    data() {
        return {
            dialog: false,
            formValid: false,
            loading: false,
            loadingSuppliers: false,
            loadingProducts: false,
            productOptions: [],
            
            statusOptions: [
                { text: 'في الانتظار', value: 'PENDING' },
                { text: 'تم الاستلام', value: 'RECEIVED' },
                { text: 'ملغي', value: 'CANCELLED' }
            ],

            formData: {
                orderNumber: '',
                orderDate: new Date().toISOString().split('T')[0],
                supplierId: null,
                status: 'PENDING',
                items: [],
                notes: '',
                attachments: []
            },

            // Validation rules
            orderNumberRules: [
                v => !!v || 'رقم الأمر مطلوب',
                v => v.length >= 3 || 'رقم الأمر يجب أن يكون 3 أحرف على الأقل'
            ],
            orderDateRules: [
                v => !!v || 'تاريخ الأمر مطلوب'
            ],
            supplierRules: [
                v => !!v || 'المورد مطلوب'
            ]
        };
    },

    computed: {
        isEdit() {
            return this.purchaseOrder && this.purchaseOrder.id;
        },

        supplierOptions() {
            return this.suppliers.filter(supplier => supplier.isActive);
        },

        totalAmount() {
            return this.formData.items.reduce((total, item) => {
                return total + (item.totalPrice || 0);
            }, 0);
        }
    },

    watch: {
        modelValue(newVal) {
            this.dialog = newVal;
            if (newVal) {
                this.initializeForm();
                this.loadProducts();
            }
        },

        dialog(newVal) {
            if (!newVal) {
                this.$emit('update:modelValue', false);
            }
        },

        totalAmount() {
            this.formData.totalAmount = this.totalAmount;
        }
    },

    methods: {
        formatCurrency,

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

        initializeForm() {
            if (this.isEdit) {
                // Edit mode - populate form with existing data
                this.formData = {
                    id: this.purchaseOrder.id,
                    orderNumber: this.purchaseOrder.orderNumber,
                    orderDate: this.purchaseOrder.orderDate,
                    supplierId: this.purchaseOrder.supplier?.id,
                    status: this.purchaseOrder.status,
                    items: this.purchaseOrder.items ? [...this.purchaseOrder.items] : [],
                    notes: this.purchaseOrder.notes || '',
                    attachments: []
                };
            } else {
                // Add mode - reset form
                this.formData = {
                    orderNumber: this.generateOrderNumber(),
                    orderDate: new Date().toISOString().split('T')[0],
                    supplierId: null,
                    status: 'PENDING',
                    items: [],
                    notes: '',
                    attachments: []
                };
            }
        },

        generateOrderNumber() {
            const now = new Date();
            const year = now.getFullYear();
            const month = String(now.getMonth() + 1).padStart(2, '0');
            const day = String(now.getDate()).padStart(2, '0');
            const time = String(now.getHours()).padStart(2, '0') + String(now.getMinutes()).padStart(2, '0');
            return `PO-${year}${month}${day}-${time}`;
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
            if (!this.$refs.form.validate()) {
                return;
            }

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
                if (this.isEdit) {
                    response = await updatePurchaseOrder(orderData);
                } else {
                    response = await createPurchaseOrder(orderData);
                }

                if (response && response.id) {
                    success(this.isEdit ? 'تم تحديث أمر الشراء بنجاح' : 'تم إنشاء أمر الشراء بنجاح');
                    this.$emit('save');
                    this.closeDialog();
                } else {
                    error(this.isEdit ? 'فشل تحديث أمر الشراء' : 'فشل إنشاء أمر الشراء');
                }
            } catch (err) {
                console.error('Error saving purchase order:', err);
                error(this.isEdit ? 'فشل تحديث أمر الشراء' : 'فشل إنشاء أمر الشراء');
            } finally {
                this.loading = false;
            }
        },

        closeDialog() {
            this.dialog = false;
            this.$refs.form?.reset();
            this.formData.items = [];
        }
    }
};
</script>

<style scoped>
.purchase-order-modal {
    border-radius: 16px !important;
}

.modal-header {
    background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
    border-bottom: 1px solid #e2e8f0;
    padding: 20px 24px;
}

.modal-title {
    font-size: 20px;
    font-weight: 600;
    color: #2d3748;
}

.modal-content {
    padding: 24px !important;
    max-height: 70vh;
    overflow-y: auto;
}

.section-header {
    display: flex;
    align-items: center;
    margin-bottom: 16px;
    padding-bottom: 8px;
    border-bottom: 1px solid #e2e8f0;
}

.section-title {
    font-size: 16px;
    font-weight: 600;
    color: #2d3748;
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

.total-section {
    margin-top: 16px;
}

.total-card {
    background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
    border: 1px solid #bae6fd;
    border-radius: 12px !important;
}

.modal-actions {
    padding: 16px 24px;
    background: #f8fafc;
}

/* Scrollbar styling */
.modal-content::-webkit-scrollbar,
.products-list::-webkit-scrollbar {
    width: 6px;
}

.modal-content::-webkit-scrollbar-track,
.products-list::-webkit-scrollbar-track {
    background: #f1f5f9;
    border-radius: 3px;
}

.modal-content::-webkit-scrollbar-thumb,
.products-list::-webkit-scrollbar-thumb {
    background: #cbd5e0;
    border-radius: 3px;
}

.modal-content::-webkit-scrollbar-thumb:hover,
.products-list::-webkit-scrollbar-thumb:hover {
    background: #94a3b8;
}

@media (max-width: 768px) {
    .modal-content {
        padding: 16px !important;
    }
    
    .section-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 8px;
    }
    
    .products-list {
        max-height: 300px;
    }
    
    .product-item .v-row {
        flex-direction: column;
    }
    
    .product-item .v-col {
        padding: 4px 12px;
    }
}
</style>