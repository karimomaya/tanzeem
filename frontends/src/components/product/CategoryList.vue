<template>
    <div class="table-container">
        <!-- Table Header Controls -->
        <BaseTableHeader title="قائمة التصنيفات" :total-items="totalItems" item-label="تصنيف" add-button-text="إضافة تصنيف"
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
            <v-data-table-server :headers="categoryHeaders" :items="categories" :items-per-page="itemsPerPage" :page="page"
                :items-length="totalItems" :loading="loading" loading-text="جاري التحميل... يرجى الانتظار"
                no-data-text="لا توجد تصنيفات للعرض" @update:options="updateOptions" class="modern-table" hover
                show-current-page>
                <!-- Enhanced Icon Display -->
                <template v-slot:item.icon="{ item }">
                    <div class="icon-cell">
                        <div class="category-icon-wrapper">
                            <v-avatar size="48" :style="{ background: item.color }" class="category-avatar">
                                <v-icon color="white" size="20">{{ item.icon || 'mdi-folder' }}</v-icon>
                            </v-avatar>
                        </div>
                    </div>
                </template>

                <!-- Enhanced Name Display -->
                <template v-slot:item.name="{ item }">
                    <div class="name-cell">
                        <div class="category-name">{{ item.name }}</div>
                        <div v-if="item.description" class="category-preview">
                            {{ truncateText(item.description, 40) }}
                        </div>
                        <MetaDataDisplay 
                            :created-at="item.createdAt"
                            :created-by="item.createdBy"
                            :updated-at="item.updatedAt"
                            :updated-by="item.updatedBy"
                        />
                    </div>
                </template>

                <!-- Enhanced Activity Display -->
                <template v-slot:item.activity="{ item }">
                    <div class="activity-cell">
                        <div class="activity-progress-container">
                            <div class="activity-info">
                                <span class="activity-percentage">{{ item.activity }}%</span>
                                <span class="activity-label">نشاط التصنيف</span>
                            </div>
                            <v-progress-linear :model-value="item.activity" :color="getCategoryActivityColor(item.activity)"
                                height="6" rounded class="activity-progress-bar"></v-progress-linear>
                        </div>
                    </div>
                </template>

                <!-- Enhanced Product Count -->
                <template v-slot:item.productCount="{ item }">
                    <div class="products-cell">
                        <v-chip :color="getProductCountColor(item.productCount)" variant="tonal" size="small"
                            class="product-count-chip">
                            <v-icon start size="14">mdi-package-variant</v-icon>
                            {{ item.productCount || 0 }}
                        </v-chip>
                        <div class="product-status">{{ getProductStatusText(item.productCount) }}</div>
                    </div>
                </template>

                <!-- Enhanced Status Display -->
                <template v-slot:item.isActive="{ item }">
                    <div class="status-cell">
                        <div class="status-indicator" :class="`status-${item.active ? 'active' : 'inactive'}`">
                            <div class="status-dot"></div>
                            <span class="status-text">{{ item.active ? 'نشط' : 'غير نشط' }}</span>
                        </div>
                        <div class="status-badge">
                            <v-chip :color="item.active ? 'success' : 'error'" variant="tonal" size="x-small"
                                class="mini-status-chip">
                                {{ item.active ? 'مفعل' : 'معطل' }}
                            </v-chip>
                        </div>
                    </div>
                </template>

                <!-- Enhanced Actions -->
                <template v-slot:item.actions="{ item }">
                    <StandardTableActions :actions="categoryActions" :item="item" @action="handleTableAction" />
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
                            <h4 class="loading-title">جاري تحميل التصنيفات...</h4>
                            <p class="loading-subtitle">يرجى الانتظار قليلاً</p>
                        </div>
                    </div>
                </template>

                <!-- Enhanced No Data -->
                <template v-slot:no-data>
                    <NoDataState icon="mdi-tag-multiple" title="لا توجد تصنيفات"
                        subtitle="لم يتم العثور على تصنيفات مطابقة لمعايير البحث الحالية" add-button-text="إضافة تصنيف جديد"
                        @add-item="$emit('add')" />
                </template>
            </v-data-table-server>
            <TablePagination :page="page" :items-per-page="itemsPerPage" :total-items="totalItems" item-label="تصنيف"
                :items-per-page-options="itemsPerPageOptions" @update:page="$emit('update:page', $event)"
                @update:items-per-page="$emit('update:items-per-page', $event)" />
        </v-card>
    </div>
</template>

<script>

import TablePagination from '@/components/common/TablePagination.vue';
import NoDataState from '@/components/common/NoDataState.vue';
import BaseTableHeader from '@/components/common/BaseTableHeader.vue';
import StandardTableActions from '@/components/common/StandardTableActions.vue';
import MetaDataDisplay from '@/components/common/MetaDataDisplay.vue';
import {
    truncateText,
} from '@/utils/product-util';
import { formatDate, isUpdatedRecently } from '@/utils/date-util';
export default {
    name: 'CategoryList',
    components: {
        TablePagination,
        NoDataState,
        BaseTableHeader,
        StandardTableActions,
        MetaDataDisplay
    },
    props: {
        categories: {
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
    emits: [ 'add', 'edit', 'delete', 'update:page', 'update:items-per-page', 'update:search-term', 'update:status-filter', 'update:sort-option', 'refresh', 'update:options', 'view', 'duplicate', 'toggle-status' ],
    data() {
        return {
            categoryActions: [
                {
                    key: 'view',
                    icon: 'mdi-eye',
                    color: 'info',
                    text: 'عرض التفاصيل'
                },
                {
                    key: 'edit',
                    icon: 'mdi-pencil',
                    color: 'primary',
                    text: 'تعديل التصنيف'
                },
                {
                    key: 'duplicate',
                    icon: 'mdi-content-copy',
                    color: 'success',
                    text: 'نسخ التصنيف'
                },
                {
                    key: 'toggle-status',
                    icon: 'mdi-pause',
                    color: 'warning',
                    text: 'تغيير الحالة',
                    condition: (item) => item.active // Only show for active items
                },
                { divider: true },
                {
                    key: 'delete',
                    icon: 'mdi-delete',
                    color: 'error',
                    text: 'حذف التصنيف',
                    danger: true
                }
            ],
            categoryHeaders: [
                { title: 'الأيقونة', key: 'icon', sortable: false, align: 'start', width: '10%' },
                { title: 'الاسم', key: 'name', sortable: true, width: '25%' },
                // { title: 'الوصف', key: 'description', sortable: true, width: '30%' },
                { title: 'المنتجات', key: 'productCount', sortable: true, align: 'start', width: '15%' },
                { title: 'النشاط', key: 'activity', sortable: true, align: 'start', width: '15%' }, // New column
                { title: 'الحالة', key: 'isActive', sortable: true, align: 'start', width: '12%' },
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
                { title: 'الحالة (نشط أولاً)', value: 'active-desc' },
                { title: 'الحالة (غير نشط أولاً)', value: 'active-asc' }
            ],
            itemsPerPageOptions: [10, 25, 50, 100]
        };
    },
    methods: {
        truncateText,
        isUpdatedRecently,
        formatDate,
        handleTableAction(payload) {
            const { type, item } = payload;

            switch (type) {
                case 'view':
                    this.$emit('view', item);
                    break;
                case 'edit':
                    this.$emit('edit', item);  // ✅ Changed from 'edit-category'
                    break;
                case 'duplicate':
                    this.duplicateCategory(item);
                    break;
                case 'toggle-status':
                    this.toggleCategoryStatus(item);
                    break;
                case 'delete':
                    this.$emit('delete', item);  // ✅ Changed from 'delete-confirmation'
                    break;
            }
        },
        updateOptions(options) {
            this.$emit('update:options', options);
        },

        getProductCountColor(count) {
            if (count === 0) return 'grey';
            if (count < 5) return 'warning';
            if (count < 20) return 'info';
            return 'success';
        },

        getProductStatusText(count) {
            if (count === 0) return 'لا توجد منتجات';
            if (count < 5) return 'منتجات قليلة';
            if (count < 20) return 'منتجات متوسطة';
            return 'منتجات كثيرة';
        },


        viewCategory(category) {
            this.$emit('view', category);
        },

        duplicateCategory(category) {
            const duplicatedCategory = {
                ...category,
                id: null,
                name: `${category.name} - نسخة`,
                active: false
            };
            this.$emit('duplicate', duplicatedCategory);
        },

        toggleCategoryStatus(category) {
            this.$emit('toggle-status', { ...category, active: !category.active });
        },

        exportData() {
            // Create CSV content
            const headers = ['الاسم', 'الوصف', 'عدد المنتجات', 'الحالة', 'تاريخ الإنشاء', 'تاريخ التحديث'];
            const csvContent = [
                headers.join(','),
                ...this.categories.map(category => [
                    category.name,
                    category.description || 'لا يوجد وصف',
                    category.productCount || 0,
                    category.active ? 'نشط' : 'غير نشط',
                    category.createdDate ? new Date(category.createdDate).toLocaleDateString('ar-EG') : '',
                    category.updatedDate ? new Date(category.updatedDate).toLocaleDateString('ar-EG') : ''
                ].join(','))
            ].join('\n');

            // Download CSV
            const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
            const link = document.createElement('a');
            const url = URL.createObjectURL(blob);
            link.setAttribute('href', url);
            link.setAttribute('download', `categories_${new Date().getTime()}.csv`);
            link.style.visibility = 'hidden';
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        },

        getCategoryActivityColor(activity) {
            if (activity >= 80) return 'success';
            if (activity >= 50) return 'info';
            if (activity >= 30) return 'warning';
            return 'error';
        },
    }
};
</script>

<style scoped>
@import '@/styles/product.css';
@import '@/styles/table.css';

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

.category-icon-wrapper {
    display: flex;
}

.category-avatar {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15) !important;
    border: 2px solid white !important;
}

.name-cell {
    padding: 12px 0;
}

.category-name {
    font-weight: 600;
    color: #2d3748;
    font-size: 15px;
    margin-bottom: 4px;
}

.category-preview {
    color: #718096;
    font-size: 12px;
    margin-bottom: 4px;
    line-height: 1.3;
}



.meta-text {
    font-weight: 500;
}

.description-cell {
    padding: 8px 0;
    max-width: 250px;
}

.description-text {
    color: #4a5568;
    font-size: 13px;
    line-height: 1.4;
}

.no-description {
    display: flex;
    align-items: center;
    color: #a0aec0;
    font-size: 12px;
    font-style: italic;
}

.products-cell {
    padding: 8px 0;
}

.product-count-chip {
    margin-bottom: 4px;
    font-weight: 600 !important;
}

.product-status {
    font-size: 11px;
    color: #718096;
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

/* Table Footer */

.items-per-page-select .v-field {
    border-radius: 8px !important;
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

    .category-name {
        font-size: 14px;
    }

    .category-preview {
        font-size: 11px;
    }

    .description-cell {
        max-width: 200px;
    }

    .category-avatar {
        width: 40px !important;
        height: 40px !important;
    }
}

@media (max-width: 600px) {

    .category-name {
        font-size: 13px;
    }

    .description-text {
        font-size: 12px;
    }

    .product-count-chip {
        font-size: 10px !important;
    }

    .status-indicator {
        font-size: 11px;
    }

    .category-avatar {
        width: 36px !important;
        height: 36px !important;
    }
}

.modern-table :deep(.v-data-table__tr) {
    transition: background-color 0.2s ease;
}


.status-indicator {
    transition: all 0.2s ease;
}

.category-avatar {
    transition: all 0.3s ease;
}

.category-avatar:hover {
    transform: scale(1.1);
}

/* Print Styles */
@media print {

    .actions-cell,
    .table-card {
        box-shadow: none !important;
        border: 1px solid #000 !important;
    }

    .category-avatar {
        background: #f0f0f0 !important;
        color: #000 !important;
    }
}


/* Hover Effects */
.product-count-chip {
    transition: all 0.2s ease;
}

.product-count-chip:hover {
    transform: scale(1.05);
}

.status-indicator:hover .status-dot {
    transform: scale(1.3);
}


/* Text Selection */
::selection {
    background: rgba(54, 96, 145, 0.2);
    color: #2d3748;
}

.activity-cell {
    padding: 8px 0;
}

.activity-progress-container {
    display: flex;
    flex-direction: column;
    gap: 6px;
    min-width: 100px;
}

.activity-info {
    display: flex;
    justify-content: space-between;
}

.activity-percentage {
    font-size: 14px;
    font-weight: 600;
    color: #2d3748;
}

.activity-label {
    font-size: 10px;
    color: #718096;
    font-weight: 500;
}

.activity-progress-bar {
    border-radius: 4px;
    overflow: hidden;
}

/* Responsive adjustments */
@media (max-width: 768px) {
    .activity-info {
        flex-direction: column;
        gap: 2px;
    }

    .activity-percentage {
        font-size: 12px;
    }

    .activity-label {
        font-size: 9px;
    }
}

@media (max-width: 600px) {
    .activity-progress-container {
        min-width: 80px;
    }

    .activity-percentage {
        font-size: 11px;
    }
}
</style>