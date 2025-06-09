<template>
    <div class="table-container">
        <!-- Table Header Controls -->
        <BaseTableHeader title="قائمة الموردين" :total-items="totalItems" item-label="مورد" add-button-text="إضافة مورد"
            @export="exportData" @add="$emit('add')">
            <template #controls>
                <div class="items-per-page">
                    <span class="text-body-2 text-medium-emphasis me-2">عرض:</span>
                    <v-select :model-value="itemsPerPage" :items="itemsPerPageOptions" variant="outlined" density="compact"
                        hide-details style="width: 80px;" class="items-select"
                        @update:model-value="$emit('update:items-per-page', $event)" />
                </div>
            </template>
        </BaseTableHeader>

        <!-- Enhanced Data Table -->
        <v-card class="table-card" elevation="0">
            <v-data-table-server :headers="supplierHeaders" :items="suppliers" :items-per-page="itemsPerPage" :page="page"
                :items-length="totalItems" :loading="loading" loading-text="جاري التحميل... يرجى الانتظار"
                @update:options="updateOptions" no-data-text="لا توجد موردين للعرض" class="modern-table" hover
                show-current-page hide-default-footer>

                <!-- Enhanced Icon Display -->
                <template v-slot:item.icon="{ item }">
                    <div class="icon-cell">
                        <div class="supplier-icon-wrapper">
                            <v-avatar size="48" :style="{ background: item.color || '#366091' }" class="supplier-avatar">
                                <v-icon color="white" size="20">{{ item.icon || 'mdi-truck' }}</v-icon>
                            </v-avatar>
                        </div>
                    </div>
                </template>

                <!-- Enhanced Name Display -->
                <template v-slot:item.name="{ item }">
                    <div class="name-cell">
                        <div class="supplier-name">{{ item.name }}</div>
                        <div v-if="item.code" class="supplier-code">
                            كود المورد: {{ item.code }}
                        </div>
                        <MetaDataDisplay :created-at="item.createdAt" :created-by="item.createdBy"
                            :updated-at="item.updatedAt" :updated-by="item.updatedBy" />
                    </div>
                </template>

                <!-- Contact Information Display -->
                <template v-slot:item.contact="{ item }">
                    <div class="contact-cell">
                        <div v-if="item.contactPerson" class="contact-item">
                            <v-icon size="14" color="primary" class="me-1">mdi-account</v-icon>
                            <span class="contact-text">{{ item.contactPerson }}</span>
                        </div>
                        <div v-if="item.phone" class="contact-item">
                            <v-icon size="14" color="success" class="me-1">mdi-phone</v-icon>
                            <span class="contact-text">{{ item.phone }}</span>
                        </div>
                        <div v-if="item.email" class="contact-item">
                            <v-icon size="14" color="info" class="me-1">mdi-email</v-icon>
                            <span class="contact-text">{{ truncateText(item.email, 25) }}</span>
                        </div>
                        <div v-if="item.address" class="contact-item">
                            <v-icon size="14" color="orange" class="me-1">mdi-map-marker</v-icon>
                            <span class="contact-text">{{ truncateText(item.address, 30) }}</span>
                        </div>
                    </div>
                </template>

                <!-- المعلومات التجارية -->
                <template v-slot:item.business="{ item }">
                    <div v-if="item.businessType" class="contact-item">
                        <v-icon size="14" color="success" class="me-1">mdi-briefcase</v-icon>
                        <span class="contact-text">{{ item.businessType.name }}</span>
                    </div>
                    <div v-if="item.paymentTerm" class="contact-item">
                        <v-icon size="14" color="info" class="me-1">mdi-credit-card</v-icon>
                        <span class="contact-text">{{ item.paymentTerm.name }}</span>
                    </div>
                </template>

                <!-- Enhanced Statistics Display -->
                <template v-slot:item.statistics="{ item }">
                    <div class="statistics-cell">
                        <div class="stat-row">
                            <v-chip :color="getOrderCountColor(item.totalOrders)" variant="tonal" size="small"
                                class="stat-chip">
                                <v-icon start size="14">mdi-clipboard-list</v-icon>
                                {{ item.totalOrders || 0 }} أوامر
                            </v-chip>
                        </div>
                        <div class="stat-row">
                            <v-chip color="success" variant="tonal" size="small" class="stat-chip">
                                <v-icon start size="14">{{ getCurrencyIcon() }}</v-icon>
                                {{ formatCurrency(item.totalAmount || 0) }}
                            </v-chip>
                        </div>
                    </div>
                </template>

                <!-- Enhanced Rating Display -->
                <template v-slot:item.rating="{ item }">
                    <div class="rating-cell">
                        <div class="rating-progress-container">
                            <div class="rating-info">
                                <span class="rating-percentage">{{ item.rating || 85 }}%</span>
                                <span class="rating-label">تقييم المورد</span>
                            </div>
                            <v-progress-linear :model-value="item.rating || 85"
                                :color="getSupplierRatingColor(item.rating || 85)" height="6" rounded
                                class="rating-progress-bar"></v-progress-linear>
                        </div>
                    </div>
                </template>

                <!-- Enhanced Status Display -->
                <template v-slot:item.isActive="{ item }">
                    <div class="status-cell">
                        <div class="status-indicator" :class="`status-${item.isActive ? 'active' : 'inactive'}`">
                            <div class="status-dot"></div>
                            <span class="status-text">{{ item.isActive ? 'نشط' : 'غير نشط' }}</span>
                        </div>
                        <div class="status-badge">
                            <v-chip :color="item.isActive ? 'success' : 'error'" variant="tonal" size="x-small"
                                class="mini-status-chip">
                                {{ item.isActive ? 'مفعل' : 'معطل' }}
                            </v-chip>
                        </div>
                    </div>
                </template>

                <!-- Enhanced Actions -->
                <template v-slot:item.actions="{ item }">
                    <StandardTableActions :actions="supplierActions" :item="item" @action="handleTableAction" />
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

                <!-- Enhanced Loading -->
                <template v-slot:loading>
                    <div class="loading-state">
                        <div class="loading-content">
                            <v-progress-circular indeterminate color="primary" size="40" class="mb-4"></v-progress-circular>
                            <h4 class="loading-title">جاري تحميل الموردين...</h4>
                            <p class="loading-subtitle">يرجى الانتظار قليلاً</p>
                        </div>
                    </div>
                </template>

                <!-- Enhanced No Data -->
                <template v-slot:no-data>
                    <NoDataState icon="mdi-truck" title="لا توجد موردين"
                        subtitle="لم يتم العثور على موردين مطابقين لمعايير البحث الحالية" add-button-text="إضافة مورد جديد"
                        @add-item="$emit('add')" />
                </template>
            </v-data-table-server>
            <TablePagination :page="page" :items-per-page="itemsPerPage" :total-items="totalItems" item-label="مورد"
                :items-per-page-options="itemsPerPageOptions" @update:page="$emit('update:page', $event)"
                @update:items-per-page="$emit('update:items-per-page', $event)" />
        </v-card>
    </div>
</template>

<script>
import { formatCurrency, getCurrencyIcon } from '@/utils/currency-util';
import { truncateText } from '@/utils/product-util';
import TablePagination from '@/components/common/TablePagination.vue';
import NoDataState from '@/components/common/NoDataState.vue';
import BaseTableHeader from '@/components/common/BaseTableHeader.vue';
import StandardTableActions from '@/components/common/StandardTableActions.vue';
import MetaDataDisplay from '@/components/common/MetaDataDisplay.vue';

export default {
    name: 'SupplierList',
    components: {
        TablePagination,
        NoDataState,
        BaseTableHeader,
        StandardTableActions,
        MetaDataDisplay
    },
    props: {
        suppliers: {
            type: Array,
            default: () => []
        },
        loading: {
            type: Boolean,
            default: false
        },
        page: {
            type: Number,
            default: 1
        },
        itemsPerPage: {
            type: Number,
            default: 10
        },
        totalItems: {
            type: Number,
            default: 0
        },
        searchTerm: {
            type: String,
            default: ''
        },
        statusFilter: {
            type: String,
            default: 'all'
        },
        sortOption: {
            type: String,
            default: 'name-asc'
        }
    },
    emits: ['add', 'edit', 'delete', 'update:page', 'update:items-per-page', 'update:search-term', 'update:status-filter', 'update:sort-option', 'refresh', 'view', 'duplicate', 'toggle-status', 'update:options'],
    data() {
        return {
            supplierActions: [
                { key: 'view', icon: 'mdi-eye', color: 'info', text: 'عرض التفاصيل' },
                { key: 'edit', icon: 'mdi-pencil', color: 'primary', text: 'تعديل المورد' },
                { key: 'duplicate', icon: 'mdi-content-copy', color: 'success', text: 'نسخ المورد' },
                { key: 'toggle-status', icon: 'mdi-pause', color: 'warning', text: 'تغيير الحالة' },
                { divider: true },
                { key: 'delete', icon: 'mdi-delete', color: 'error', text: 'حذف المورد', danger: true }
            ],
            supplierHeaders: [
                { title: 'الأيقونة', key: 'icon', sortable: false, align: 'start', width: '8%' },
                { title: 'الاسم', key: 'name', sortable: true, width: '20%' },
                { title: 'معلومات الاتصال', key: 'contact', sortable: false, width: '22%' },
                { title: 'المعلومات التجارية', key: 'business', sortable: false, width: '15%' }, // New column
                { title: 'الإحصائيات', key: 'statistics', sortable: true, align: 'start', width: '15%' },
                { title: 'التقييم', key: 'rating', sortable: true, align: 'start', width: '12%' },
                { title: 'الحالة', key: 'isActive', sortable: true, align: 'start', width: '8%' },
                { title: 'الإجراءات', key: 'actions', sortable: false, align: 'start', width: '8%' }
            ],
            statusOptions: [
                { title: 'جميع الحالات', value: 'all' },
                { title: 'نشط', value: 'active' },
                { title: 'غير نشط', value: 'inactive' }
            ],
            sortOptions: [
                { title: 'الاسم (أ-ي)', value: 'name-asc' },
                { title: 'الاسم (ي-أ)', value: 'name-desc' },
                { title: 'التقييم (الأعلى أولاً)', value: 'rating-desc' },
                { title: 'التقييم (الأقل أولاً)', value: 'rating-asc' },
                { title: 'الحالة (نشط أولاً)', value: 'active-desc' },
                { title: 'الحالة (غير نشط أولاً)', value: 'active-asc' }
            ],
            itemsPerPageOptions: [10, 25, 50, 100]
        };
    },
    methods: {
        formatCurrency,
        getCurrencyIcon,
        truncateText,
        handleTableAction(payload) {
            const { type, item } = payload;

            switch (type) {
                case 'view':
                    this.viewSupplier(item);
                    break;
                case 'edit':
                    this.$emit('edit', item);
                    break;
                case 'duplicate':
                    this.duplicateSupplier(item);
                    break;
                case 'toggle-status':
                    this.toggleSupplierStatus(item);
                    break;
                case 'delete':
                    this.$emit('delete', item);
                    break;
            }
        },
        updateOptions(options) {
            this.$emit('update:options', options);
        },
        getOrderCountColor(count) {
            if (count === 0) return 'grey';
            if (count < 5) return 'warning';
            if (count < 15) return 'info';
            return 'success';
        },

        getSupplierRatingColor(rating) {
            if (rating >= 90) return 'success';
            if (rating >= 75) return 'info';
            if (rating >= 60) return 'warning';
            return 'error';
        },

        viewSupplier(supplier) {
            this.$emit('view', supplier);
        },

        duplicateSupplier(supplier) {
            const duplicatedSupplier = {
                ...supplier,
                id: null,
                name: `${supplier.name} - نسخة`,
                isActive: false
            };
            this.$emit('duplicate', duplicatedSupplier);
        },

        toggleSupplierStatus(supplier) {
            this.$emit('toggle-status', { ...supplier, isActive: !supplier.isActive });
        },

        exportData() {
            // Create CSV content
            const headers = [
                'الاسم',
                'كود المورد',
                'جهة الاتصال',
                'الهاتف',
                'البريد الإلكتروني',
                'العنوان',
                'إجمالي الأوامر',
                'إجمالي المبلغ',
                'التقييم',
                'الحالة',
                'تاريخ الإنشاء'
            ];

            const csvContent = [
                headers.join(','),
                ...this.suppliers.map(supplier => [
                    supplier.name || '',
                    supplier.code || '',
                    supplier.contactPerson || '',
                    supplier.phone || '',
                    supplier.email || '',
                    supplier.address || '',
                    supplier.totalOrders || 0,
                    supplier.totalAmount || 0,
                    supplier.rating || 0,
                    supplier.isActive ? 'نشط' : 'غير نشط',
                    supplier.createdAt ? new Date(supplier.createdAt).toLocaleDateString('ar-EG') : ''
                ].map(field => `"${field}"`).join(','))
            ].join('\n');

            // Download CSV
            const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
            const link = document.createElement('a');
            const url = URL.createObjectURL(blob);
            link.setAttribute('href', url);
            link.setAttribute('download', `suppliers_${new Date().getTime()}.csv`);
            link.style.visibility = 'hidden';
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        }
    }
};
</script>

<style scoped>
@import '@/styles/product.css';

/* Table Controls */
.items-per-page {
    display: flex;
    align-items: center;
}

/* Cell Styling */
.icon-cell {
    display: flex;
    padding: 8px 0;
}

.supplier-icon-wrapper {
    display: flex;
}

.supplier-avatar {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15) !important;
    border: 2px solid white !important;
}

.name-cell {
    padding: 12px 0;
}

.supplier-name {
    font-weight: 600;
    color: #2d3748;
    font-size: 15px;
    margin-bottom: 4px;
}

.supplier-code {
    color: #718096;
    font-size: 12px;
    margin-bottom: 4px;
    line-height: 1.3;
}

.meta-text {
    font-weight: 500;
}

/* Contact Information Styling */
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

/* Statistics Styling */
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

/* Rating Styling */
.rating-cell {
    padding: 8px 0;
}

.rating-progress-container {
    display: flex;
    flex-direction: column;
    gap: 6px;
    min-width: 100px;
}

.rating-info {
    display: flex;
    justify-content: space-between;
}

.rating-percentage {
    font-size: 14px;
    font-weight: 600;
    color: #2d3748;
}

.rating-label {
    font-size: 10px;
    color: #718096;
    font-weight: 500;
}

.rating-progress-bar {
    border-radius: 4px;
    overflow: hidden;
}

/* Status Styling */
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

.status-active .status-dot {
    background: #48bb78;
}

.status-active .status-text {
    color: #2f855a;
}

.status-inactive .status-dot {
    background: #e53e3e;
}

.status-inactive .status-text {
    color: #c53030;
}

.status-badge {
    display: flex;
    align-items: center;
    justify-content: center;
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

/* Table Footer */
.items-per-page-select .v-field {
    border-radius: 8px !important;
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

.supplier-avatar {
    transition: all 0.3s ease;
}

.supplier-avatar:hover {
    transform: scale(1.1);
}

/* Responsive Design */
@media (max-width: 768px) {
    .items-per-page {
        width: 100%;
        justify-content: space-between;
    }

    .name-cell {
        padding: 8px 0;
    }

    .supplier-name {
        font-size: 14px;
    }

    .supplier-code {
        font-size: 11px;
    }

    .contact-cell {
        max-width: 200px;
    }

    .contact-text {
        font-size: 11px;
    }

    .supplier-avatar {
        width: 40px !important;
        height: 40px !important;
    }

    .rating-info {
        flex-direction: column;
        gap: 2px;
    }

    .rating-percentage {
        font-size: 12px;
    }

    .rating-label {
        font-size: 9px;
    }
}

@media (max-width: 600px) {
    .supplier-name {
        font-size: 13px;
    }

    .contact-text {
        font-size: 10px;
    }

    .stat-chip {
        font-size: 10px !important;
    }

    .status-indicator {
        font-size: 11px;
    }

    .supplier-avatar {
        width: 36px !important;
        height: 36px !important;
    }

    .rating-progress-container {
        min-width: 80px;
    }

    .rating-percentage {
        font-size: 11px;
    }
}

/* Print Styles */
@media print {

    .actions-cell,
    .table-card {
        box-shadow: none !important;
        border: 1px solid #000 !important;
    }

    .supplier-avatar {
        background: #f0f0f0 !important;
        color: #000 !important;
    }
}

/* Text Selection */
::selection {
    background: rgba(54, 96, 145, 0.2);
    color: #2d3748;
}
</style>