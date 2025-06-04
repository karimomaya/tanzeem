<template>
    <div class="table-container">
        <!-- Table Controls -->
        <div class="table-controls">
            <div class="d-flex justify-space-between align-center">
                <div>
                    <h3 class="table-title">أوامر الشراء</h3>
                    <p class="table-subtitle">إدارة ومتابعة جميع أوامر الشراء</p>
                </div>
                <div class="d-flex align-center ga-3">
                    <div class="items-per-page">
                        <span class="text-body-2 text-medium-emphasis me-2">عناصر لكل صفحة:</span>
                        <v-select v-model="currentItemsPerPage" :items="itemsPerPageOptions" variant="outlined"
                            density="compact" hide-details class="items-select" style="width: 80px;"
                            @update:model-value="updateItemsPerPage"></v-select>
                    </div>
                </div>
            </div>
        </div>

        <!-- Data Table -->
        <v-data-table :headers="headers" :items="purchaseOrders" :loading="loading" :items-per-page="currentItemsPerPage"
            :page="currentPage" :server-items-length="totalItems" class="modern-table table-card"
            @update:options="handleOptionsUpdate" hover>

            <!-- Order Number Column -->
            <template v-slot:item.orderNumber="{ item }">
                <div class="d-flex align-center">
                    <div>
                        <div class="font-weight-bold text-body-2">{{ item.orderNumber }}</div>
                        <div class="text-caption text-medium-emphasis">{{ formatDate(item.orderDate) }}</div>
                    </div>
                </div>
            </template>

            <!-- Supplier Column -->
            <template v-slot:item.supplier="{ item }">
                <div class="supplier-cell">
                    <div class="d-flex align-center">
                        <v-icon size="20" color="primary" class="me-2">mdi-truck</v-icon>
                        <div>
                            <div class="font-weight-medium text-body-2">{{ item.supplier?.name || 'غير محدد' }}</div>
                            <div class="text-caption text-medium-emphasis" v-if="item.supplier?.phone">
                                {{ item.supplier.phone }}
                            </div>
                        </div>
                    </div>
                </div>
            </template>

            <!-- Items Summary Column -->
            <template v-slot:item.items="{ item }">
                <div class="text-center">
                    <div class="font-weight-bold text-body-2">{{ item.items?.length || 0 }}</div>
                    <div class="text-caption text-medium-emphasis">{{ getTotalQuantity(item.items) }} قطعة</div>
                </div>
            </template>

            <!-- Status Column -->
            <template v-slot:item.status="{ item }">
                <div class="status-cell">
                    <v-chip :color="getStatusColor(item.status)" size="small" class="font-weight-bold">
                        {{ getStatusText(item.status) }}
                    </v-chip>
                </div>
            </template>

            <!-- Total Amount Column -->
            <template v-slot:item.totalAmount="{ item }">
                <div class="text-end">
                    <div class="font-weight-bold text-h6 text-primary">{{ formatCurrency(item.totalAmount) }}</div>
                </div>
            </template>

            <!-- Notes Column -->
            <template v-slot:item.notes="{ item }">
                <div class="notes-cell">
                    <v-tooltip v-if="item.notes" :text="item.notes">
                        <template v-slot:activator="{ props }">
                            <v-icon v-bind="props" size="20" color="info">mdi-note-text</v-icon>
                        </template>
                    </v-tooltip>
                    <span v-else class="text-medium-emphasis">-</span>
                </div>
            </template>

            <!-- Created Info Column -->
            <template v-slot:item.created="{ item }">
                <div class="meta-data">
                    <div class="text-caption">
                        <span class="meta-by">بواسطة</span>
                        <span class="font-weight-medium">{{ item.createdBy || 'النظام' }}</span>
                    </div>
                    <div class="text-caption text-medium-emphasis">
                        {{ formatDate(item.createdAt) }}
                    </div>
                </div>
            </template>

            <!-- Actions Column -->
            <template v-slot:item.actions="{ item }">
                <div class="d-flex align-center justify-end ga-1">
                    <!-- Quick Action Buttons -->
                    <v-btn v-if="item.status === 'PENDING'" icon="mdi-check" size="small" color="success"
                        @click="$emit('mark-received', item)" v-tooltip="'تسليم الأمر'"></v-btn>
                    
                    <v-btn icon="mdi-eye" size="small" color="info"
                        @click="$emit('view', item)" v-tooltip="'عرض التفاصيل'"></v-btn>

                    <!-- More Actions Menu -->
                    <v-menu>
                        <template v-slot:activator="{ props }">
                            <v-btn icon="mdi-dots-vertical" size="small" class="actions-trigger" v-bind="props"></v-btn>
                        </template>
                        <v-list class="actions-menu">
                            <v-list-item @click="$emit('view', item)" class="action-item">
                                <template v-slot:prepend>
                                    <v-icon>mdi-eye</v-icon>
                                </template>
                                <v-list-item-title>عرض التفاصيل</v-list-item-title>
                            </v-list-item>
                            <v-list-item v-if="item.status === 'PENDING'" @click="$emit('edit', item)" class="action-item">
                                <template v-slot:prepend>
                                    <v-icon>mdi-pencil</v-icon>
                                </template>
                                <v-list-item-title>تعديل</v-list-item-title>
                            </v-list-item>
                            <v-list-item v-if="item.status === 'PENDING'" @click="$emit('mark-received', item)" class="action-item">
                                <template v-slot:prepend>
                                    <v-icon>mdi-check</v-icon>
                                </template>
                                <v-list-item-title>تسليم الأمر</v-list-item-title>
                            </v-list-item>
                            <v-divider v-if="item.status === 'PENDING'"></v-divider>
                            <v-list-item v-if="item.status === 'PENDING'" @click="$emit('delete', item)" 
                                class="action-item action-danger">
                                <template v-slot:prepend>
                                    <v-icon color="error">mdi-delete</v-icon>
                                </template>
                                <v-list-item-title>حذف</v-list-item-title>
                            </v-list-item>
                        </v-list>
                    </v-menu>
                </div>
            </template>

            <!-- No Data Slot -->
            <template v-slot:no-data>
                <div class="no-data-state">
                    <div class="no-data-content">
                        <v-icon size="80" color="grey-lighten-2" class="empty-icon">mdi-clipboard-list</v-icon>
                        <h3 class="no-data-title">لا توجد أوامر شراء</h3>
                        <p class="no-data-subtitle">ابدأ بإنشاء أمر الشراء الأول</p>
                    </div>
                </div>
            </template>

            <!-- Loading Slot -->
            <template v-slot:loading>
                <div class="loading-state">
                    <div class="loading-content">
                        <v-progress-circular indeterminate size="64" color="primary"></v-progress-circular>
                        <h3 class="loading-title">جاري تحميل أوامر الشراء...</h3>
                        <p class="loading-subtitle">يرجى الانتظار</p>
                    </div>
                </div>
            </template>

        </v-data-table>

        <!-- Table Footer -->
        <div class="table-footer">
            <div class="product-footer-info">
                عرض {{ startItem }} - {{ endItem }} من أصل {{ totalItems }} أمر شراء
            </div>
            <div>
                <v-pagination v-model="currentPage" :length="totalPages" :total-visible="7" class="table-pagination"
                    @update:model-value="updatePage"></v-pagination>
            </div>
        </div>
    </div>
</template>

<script>

import { formatCurrency } from '@/utils/currency-util';
import { formatDate } from '@/utils/system-util';

export default {
    name: 'PurchaseOrderList',
    props: {
        purchaseOrders: {
            type: Array,
            default: () => []
        },
        loading: {
            type: Boolean,
            default: false
        },
        totalItems: {
            type: Number,
            default: 0
        },
        page: {
            type: Number,
            default: 1
        },
        itemsPerPage: {
            type: Number,
            default: 10
        },
        sortBy: {
            type: Array,
            default: () => []
        }
    },

    data() {
        return {
            currentPage: this.page,
            currentItemsPerPage: this.itemsPerPage,
            itemsPerPageOptions: [5, 10, 25, 50],
            headers: [
                {
                    title: 'رقم الأمر',
                    key: 'orderNumber',
                    align: 'start',
                    sortable: true,
                    width: '150px'
                },
                {
                    title: 'المورد',
                    key: 'supplier',
                    align: 'start',
                    sortable: false,
                    width: '200px'
                },
                {
                    title: 'المنتجات',
                    key: 'items',
                    align: 'center',
                    sortable: false,
                    width: '120px'
                },
                {
                    title: 'الحالة',
                    key: 'status',
                    align: 'center',
                    sortable: true,
                    width: '120px'
                },
                {
                    title: 'المبلغ الإجمالي',
                    key: 'totalAmount',
                    align: 'end',
                    sortable: true,
                    width: '150px'
                },
                {
                    title: 'ملاحظات',
                    key: 'notes',
                    align: 'center',
                    sortable: false,
                    width: '80px'
                },
                {
                    title: 'معلومات الإنشاء',
                    key: 'created',
                    align: 'start',
                    sortable: true,
                    width: '180px'
                },
                {
                    title: 'الإجراءات',
                    key: 'actions',
                    align: 'end',
                    sortable: false,
                    width: '120px'
                }
            ]
        };
    },

    computed: {
        totalPages() {
            return Math.ceil(this.totalItems / this.currentItemsPerPage);
        },

        startItem() {
            return (this.currentPage - 1) * this.currentItemsPerPage + 1;
        },

        endItem() {
            const end = this.currentPage * this.currentItemsPerPage;
            return Math.min(end, this.totalItems);
        }
    },

    watch: {
        page(newPage) {
            this.currentPage = newPage;
        },

        itemsPerPage(newItemsPerPage) {
            this.currentItemsPerPage = newItemsPerPage;
        }
    },

    methods: {
        formatCurrency,
        formatDate,

        getStatusColor(status) {
            const statusColors = {
                'PENDING': 'warning',
                'RECEIVED': 'success',
                'CANCELLED': 'error'
            };
            return statusColors[status] || 'grey';
        },

        getStatusText(status) {
            const statusTexts = {
                'PENDING': 'في الانتظار',
                'RECEIVED': 'تم الاستلام',
                'CANCELLED': 'ملغي'
            };
            return statusTexts[status] || status;
        },

        getTotalQuantity(items) {
            if (!items || !Array.isArray(items)) return 0;
            return items.reduce((total, item) => total + (item.quantity || 0), 0);
        },

        updatePage(newPage) {
            this.currentPage = newPage;
            this.$emit('update:options', {
                page: newPage,
                itemsPerPage: this.currentItemsPerPage,
                sortBy: this.sortBy
            });
        },

        updateItemsPerPage(newItemsPerPage) {
            this.currentItemsPerPage = newItemsPerPage;
            this.currentPage = 1;
            this.$emit('update:options', {
                page: 1,
                itemsPerPage: newItemsPerPage,
                sortBy: this.sortBy
            });
        },

        handleOptionsUpdate(options) {
            this.currentPage = options.page;
            this.currentItemsPerPage = options.itemsPerPage;
            this.$emit('update:options', options);
        }
    }
};
</script>

<style scoped>
@import '@/styles/product.css';

.supplier-cell {
    min-width: 180px;
}

.notes-cell {
    text-align: center;
}

.meta-data {
    min-width: 160px;
}

/* Mobile responsive adjustments */
@media (max-width: 768px) {
    .table-controls {
        padding: 16px 20px 12px;
    }
    
    .table-controls .d-flex {
        flex-direction: column;
        gap: 12px;
        align-items: flex-start !important;
    }
    
    .items-per-page {
        width: 100%;
        justify-content: space-between;
    }
}
</style>