<template>
    <div class="table-container">
        <!-- Table Controls -->
        <BaseTableHeader title="أوامر الشراء" :total-items="totalItems" item-label="أمر شراء"
            add-button-text="إضافة أمر شراء" @export="exportData" @add="$emit('add')" />
        <v-card class="table-card" elevation="0">

            <!-- Data Table -->
            <v-data-table-server :headers="headers" :items="purchaseOrders" :items-per-page="itemsPerPage" :page="page"
                :items-length="totalItems" :loading="loading" @update:options="updateOptions"
                loading-text="جاري التحميل... يرجى الانتظار" no-data-text="لا توجد أوامر شراء للعرض" class="modern-table"
                hover show-current-page hide-default-footer>

                <!-- Icon Column -->
                <template v-slot:item.icon="{ item }">
                    <div class="icon-cell">
                        <div class="purchase-icon-wrapper">
                            <v-avatar size="48" color="primary" class="purchase-avatar">
                                <v-icon color="white" size="20">mdi-clipboard-list</v-icon>
                            </v-avatar>
                        </div>
                    </div>
                </template>

                <!-- Order Info Column -->
                <template v-slot:item.orderInfo="{ item }">
                    <div class="name-cell">
                        <div class="order-number">{{ item.invoiceNumber || `أمر-${item.id}` }}</div>
                        <div class="order-date">
                            تاريخ الأمر: {{ formatDate(item.purchaseDate) }}
                        </div>
                        <MetaDataDisplay :created-at="item.createdAt" :created-by="item.createdBy" :show-updated="false" />
                    </div>
                </template>

                <!-- Supplier Column -->
                <template v-slot:item.supplier="{ item }">
                    <div class="contact-cell">
                        <div v-if="item.supplier" class="contact-item">
                            <v-icon size="14" color="primary" class="me-1">mdi-truck</v-icon>
                            <span class="contact-text">{{ item.supplier.name }}</span>
                        </div>
                        <div v-if="item.supplier?.phone" class="contact-item">
                            <v-icon size="14" color="success" class="me-1">mdi-phone</v-icon>
                            <span class="contact-text">{{ item.supplier.phone }}</span>
                        </div>
                        <div v-if="!item.supplier" class="contact-item">
                            <span class="text-medium-emphasis">غير محدد</span>
                        </div>
                    </div>
                </template>

                <!-- Amounts Column -->
                <template v-slot:item.amounts="{ item }">
                    <div class="statistics-cell">
                        <div class="stat-row">
                            <v-chip color="success" variant="tonal" size="small" class="stat-chip">
                                <v-icon start size="14">{{ getCurrencyIcon() }}</v-icon>
                                {{ formatCurrency(item.totalAmount || 0) }}
                            </v-chip>
                        </div>
                        <div v-if="item.notes" class="stat-row">
                            <v-tooltip :text="item.notes">
                                <template v-slot:activator="{ props }">
                                    <v-chip v-bind="props" color="info" variant="tonal" size="small" class="stat-chip">
                                        <v-icon start size="14">mdi-note-text</v-icon>
                                        ملاحظات
                                    </v-chip>
                                </template>
                            </v-tooltip>
                        </div>
                    </div>
                </template>

                <!-- Dates Column -->
                <template v-slot:item.dates="{ item }">
                    <div class="contact-cell">
                        <div v-if="item.purchaseDate" class="contact-item">
                            <v-icon size="14" color="warning" class="me-1">mdi-calendar-clock</v-icon>
                            <span class="contact-text">تم الشراء: {{ formatDate(item.purchaseDate) }}</span>
                        </div>
                        <div v-if="item.expectedDeliveryAt" class="contact-item">
                            <v-icon size="14" color="warning" class="me-1">mdi-calendar-clock</v-icon>
                            <span class="contact-text">متوقع: {{ formatDate(item.expectedDeliveryAt) }}</span>
                        </div>
                        <div v-if="item.deliveredAt" class="contact-item">
                            <v-icon size="14" color="success" class="me-1">mdi-calendar-check</v-icon>
                            <span class="contact-text">تم: {{ formatDate(item.deliveredAt) }}</span>
                        </div>
                        <div v-if="item.confirmedAt" class="contact-item">
                            <v-icon size="14" color="info" class="me-1">mdi-calendar-star</v-icon>
                            <span class="contact-text">تأكيد: {{ formatDate(item.confirmedAt) }}</span>
                        </div>
                    </div>
                </template>

                <!-- Items Summary Column -->
                <template v-slot:item.items="{ item }">
                    <div>
                        <div class="font-weight-bold text-body-2">{{ getTotalQuantity(item.items) }} قطعة</div>
                    </div>
                </template>

                <!-- Status Column -->
                <template v-slot:item.status="{ item }">
                    <div class="status-cell">

                        <div class="status-badge">
                            <v-chip :color="getStatusColor(item.status)" variant="tonal" size="x-small"
                                class="mini-status-chip">
                                {{ getStatusText(item.status) }}
                            </v-chip>
                        </div>
                    </div>
                </template>

                <!-- Actions Column -->
                <template v-slot:item.actions="{ item }">
                    <StandardTableActions :actions="purchaseOrderActions" :item="item" @action="handleTableAction" />
                </template>

                <!-- Enhanced Headers -->
                <template v-slot:headers="{ columns, isSorted, getSortIcon, toggleSort }">
                    <tr class="table-header">
                        <template v-for="column in columns" :key="column.key">
                            <th :class="[
                                'header-cell',
                                column.align ? `text-${column.align}` : '',
                                column.sortable ? 'sortable' : ''
                            ]" :style="{ width: column.width }" @click="column.sortable ? toggleSort(column) : null">
                                <div class="header-content">
                                    <span class="header-title">{{ column.title }}</span>
                                    <v-icon v-if="column.sortable && isSorted(column)" :icon="getSortIcon(column)"
                                        color="primary" size="16" class="sort-icon"></v-icon>
                                    <v-icon v-else-if="column.sortable" icon="mdi-sort" color="grey-lighten-1" size="16"
                                        class="sort-icon"></v-icon>
                                </div>
                            </th>
                        </template>
                    </tr>
                </template>

                <!-- No Data Slot -->
                <template v-slot:no-data>
                    <NoDataState icon="mdi-clipboard-list" title="لا توجد أوامر شراء"
                        subtitle="لم يتم العثور على أوامر شراء مطابقين لمعايير البحث الحالية"
                        add-button-text="إضافة أمر شراء جديد" @add-item="$emit('add-purchase')" />
                </template>

                <!-- Enhanced Loading -->
                <template v-slot:loading>
                    <div class="loading-state">
                        <div class="loading-content">
                            <v-progress-circular indeterminate color="primary" size="40" class="mb-4"></v-progress-circular>
                            <h4 class="loading-title">جاري تحميل أوامر الشراء...</h4>
                            <p class="loading-subtitle">يرجى الانتظار قليلاً</p>
                        </div>
                    </div>
                </template>

            </v-data-table-server>

            <!-- Table Footer -->
            <TablePagination :page="page" :items-per-page="itemsPerPage" :total-items="totalItems" item-label="أمر شراء"
                :items-per-page-options="itemsPerPageOptions" @update:page="updatePage"
                @update:items-per-page="updateItemsPerPage" />
        </v-card>
    </div>
</template>

<script>

import { formatDate } from '@/utils/date-util';
import TablePagination from '@/components/common/TablePagination.vue';
import NoDataState from '@/components/common/NoDataState.vue';
import { formatCurrency, getCurrencyIcon } from '@/utils/currency-util';
import { truncateText } from '@/utils/product-util';
import BaseTableHeader from '@/components/common/BaseTableHeader.vue';
import StandardTableActions from '@/components/common/StandardTableActions.vue';
import MetaDataDisplay from '@/components/common/MetaDataDisplay.vue';

export default {
    name: 'PurchaseOrderList',
    components: {
        TablePagination,
        NoDataState,
        BaseTableHeader,
        StandardTableActions,
        MetaDataDisplay
    },
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
    emits: ['add', 'edit', 'delete', 'view', 'duplicate', 'mark-received', 'update:page', 'update:items-per-page', 'update:options', 'refresh'],
    data() {
        return {
            purchaseOrderActions: [
                { key: 'view', icon: 'mdi-eye', color: 'info', text: 'عرض التفاصيل' },
                { key: 'edit', icon: 'mdi-pencil', color: 'primary', text: 'تعديل الأمر', condition: (item) => item.status === 'PENDING' },
                { key: 'duplicate', icon: 'mdi-content-copy', color: 'success', text: 'نسخ الأمر', condition: (item) => item.status === 'PENDING' },
                { key: 'mark-received', icon: 'mdi-check', color: 'success', text: 'تسليم الأمر', condition: (item) => item.status === 'PENDING' },
                { divider: true, condition: (item) => item.status === 'PENDING' },
                { key: 'delete', icon: 'mdi-delete', color: 'error', text: 'حذف الأمر', danger: true, condition: (item) => item.status === 'PENDING' }
            ],
            headers: [
                { title: 'الأيقونة', key: 'icon', sortable: false, align: 'start', width: '8%' },
                { title: 'معلومات الأمر', key: 'orderInfo', sortable: true, width: '20%' },
                { title: 'المورد', key: 'supplier', sortable: false, width: '18%' },
                { title: 'المنتجات', key: 'items', sortable: false, align: 'center', width: '12%' },
                { title: 'المبالغ', key: 'amounts', sortable: true, align: 'start', width: '15%' },
                { title: 'التواريخ', key: 'dates', sortable: false, width: '15%' },
                { title: 'الحالة', key: 'status', sortable: true, align: 'start', width: '8%' },
                { title: 'الإجراءات', key: 'actions', sortable: false, align: 'end', width: '8%' }
            ],
            itemsPerPageOptions: [10, 25, 50, 100]
        };
    },

    methods: {
        getCurrencyIcon,
        truncateText,
        formatCurrency,
        formatDate,
        handleTableAction(payload) {
            const { type, item } = payload;
            switch (type) {
                case 'view':
                    this.viewPurchaseOrder(item);
                    break;
                case 'edit':
                    this.$emit('edit', item);
                    break;
                case 'duplicate':
                    this.duplicatePurchaseOrder(item);
                    break;
                case 'mark-received':
                    this.$emit('mark-received', item);
                    break;
                case 'delete':
                    this.$emit('delete', item);
                    break;
            }
        },
        updateOptions(options) {
            this.$emit('update:options', options);
        },
        viewPurchaseOrder(purchaseOrder) {
            this.$emit('view', purchaseOrder);
        },

        duplicatePurchaseOrder(purchaseOrder) {
            const duplicatedOrder = {
                ...purchaseOrder,
                id: null,
                invoiceNumber: `${purchaseOrder.invoiceNumber} - نسخة`,
                status: 'PENDING'
            };
            this.$emit('duplicate', duplicatedOrder);
        },

        exportData() {
            // Create CSV content
            const headers = [
                'رقم الفاتورة',
                'تاريخ الشراء',
                'المورد',
                'المبلغ الإجمالي',
                'عدد المنتجات',
                'الحالة',
                'تاريخ التسليم المتوقع',
                'تاريخ الإنشاء'
            ];

            const csvContent = [
                headers.join(','),
                ...this.purchaseOrders.map(order => [
                    order.invoiceNumber || `أمر-${order.id}`,
                    order.purchaseDate || '',
                    order.supplier?.name || 'غير محدد',
                    order.totalAmount || 0,
                    order.items?.length || 0,
                    this.getStatusText(order.status || 'PENDING'), // Fix here
                    order.expectedDeliveryAt || '',
                    order.createdAt ? new Date(order.createdAt).toLocaleDateString('ar-EG') : ''
                ].map(field => `"${field}"`).join(','))
            ].join('\n');

            // Download CSV
            const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
            const link = document.createElement('a');
            const url = URL.createObjectURL(blob);
            link.setAttribute('href', url);
            link.setAttribute('download', `purchase_orders_${new Date().getTime()}.csv`);
            link.style.visibility = 'hidden';
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        },
        getStatusClass(status) {
            return status?.toLowerCase() || 'pending';
        },
        getStatusColor(status) {
            const statusColors = {
                'PENDING': 'warning',
                'CONFIRMED': 'info',
                'DELIVERED': 'success',
                'COMPLETED': 'success',
                'CANCELLED': 'error'
            };
            return statusColors[status] || 'grey';
        },

        getStatusText(status) {
            const statusTexts = {
                'PENDING': 'في الانتظار',
                'CONFIRMED': 'مؤكد',
                'DELIVERED': 'تم التسليم',
                'COMPLETED': 'تم الاستلام',
                'CANCELLED': 'ملغي'
            };
            return statusTexts[status] || status;
        },

        getTotalQuantity(items) {
            if (!items || !Array.isArray(items)) return 0;
            return items.reduce((total, item) => total + (item.quantity || 0), 0);
        },

        updatePage(newPage) {
            this.$emit('update:page', newPage);
        },

        updateItemsPerPage(newItemsPerPage) {
            this.$emit('update:items-per-page', newItemsPerPage);
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


.icon-cell {
    display: flex;
    padding: 8px 0;
}

.purchase-icon-wrapper {
    display: flex;
}

.purchase-avatar {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15) !important;
    border: 2px solid white !important;
}

.name-cell {
    padding: 12px 0;
}

.order-number {
    font-weight: 600;
    color: #2d3748;
    font-size: 15px;
    margin-bottom: 4px;
}

.order-date {
    color: #718096;
    font-size: 12px;
    margin-bottom: 4px;
    line-height: 1.3;
}

.contact-cell {
    padding: 8px 0;
    max-width: 250px;
}

.contact-item {
    display: flex;
    align-items: center;
    margin-bottom: 6px;
}

.contact-item:last-child {
    margin-bottom: 0;
}

.contact-text {
    font-size: 12px;
    color: #4a5568;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    flex: 1;
}

.statistics-cell {
    padding: 8px 0;
}

.stat-row {
    margin-bottom: 6px;
}

.stat-row:last-child {
    margin-bottom: 0;
}

.stat-chip {
    font-weight: 600 !important;
    font-size: 11px !important;
}

.status-cell {
    display: flex;
    padding: 8px 0;
}

.status-indicator {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
    margin-bottom: 4px;
    font-size: 12px;
    font-weight: 500;
}

.status-dot {
    width: 6px;
    height: 6px;
    border-radius: 50%;
}

.status-pending .status-dot {
    background: #ed8936;
}

.status-pending .status-text {
    color: #c05621;
}

.status-confirmed .status-dot {
    background: #4299e1;
}

.status-confirmed .status-text {
    color: #2b6cb0;
}

.status-delivered .status-dot,
.status-received .status-dot {
    background: #48bb78;
}

.status-delivered .status-text,
.status-received .status-text {
    color: #2f855a;
}

.status-cancelled .status-dot {
    background: #e53e3e;
}

.status-cancelled .status-text {
    color: #c53030;
}

.status-badge {
    display: flex;
    align-items: start !important;
    ;
    justify-content: start !important;
    ;
}

.mini-status-chip {
    font-size: 10px !important;
    height: 18px !important;
    font-weight: 600 !important;
}

/* Row Hover Effect */
.modern-table :deep(.v-data-table__tr:hover) {
    background: rgba(54, 96, 145, 0.02) !important;
}

.modern-table :deep(.v-data-table__tr) {
    transition: background-color 0.2s ease;
}

/* Hover Effects */
.stat-chip {
    transition: all 0.2s ease;
}

.stat-chip:hover {
    transform: scale(1.05);
}

.status-indicator:hover .status-dot {
    transform: scale(1.3);
}

.purchase-avatar {
    transition: all 0.3s ease;
}

.purchase-avatar:hover {
    transform: scale(1.1);
}
</style>