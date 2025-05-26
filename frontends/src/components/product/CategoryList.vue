<template>
    <div class="category-list-container">
        <!-- Table Header Controls -->
        <div class="table-controls">
            <div class="d-flex align-center justify-space-between">
                <div class="table-info">
                    <h3 class="table-title">قائمة التصنيفات</h3>
                    <p class="table-subtitle">{{ totalItems }} تصنيف متاح</p>
                </div>
                
                <div class="d-flex align-center ga-3">
                    <!-- Items per page -->
                    <div class="items-per-page">
                        <span class="text-body-2 text-medium-emphasis me-2">عرض:</span>
                        <v-select
                            :model-value="itemsPerPage"
                            :items="itemsPerPageOptions"
                            variant="outlined"
                            density="compact"
                            hide-details
                            style="width: 80px;"
                            class="items-select"
                            @update:model-value="$emit('update:items-per-page', $event)"
                        ></v-select>
                    </div>
                    
                    <!-- Export button -->
                    <v-btn
                        variant="outlined"
                        color="primary"
                        prepend-icon="mdi-download"
                        class="export-btn"
                        @click="exportData"
                    >
                        تصدير
                    </v-btn>
                    
                    <!-- Add Category button -->
                    <v-btn
                        color="primary"
                        prepend-icon="mdi-plus"
                        class="add-btn"
                        @click="$emit('add-category')"
                    >
                        إضافة تصنيف
                    </v-btn>
                </div>
            </div>
        </div>

        <!-- Enhanced Data Table -->
        <v-card class="table-card" elevation="0">
            <v-data-table-server 
                :headers="categoryHeaders" 
                :items="categories" 
                :items-per-page="itemsPerPage"
                :page="page" 
                :items-length="totalItems" 
                :loading="loading"
                loading-text="جاري التحميل... يرجى الانتظار"
                no-data-text="لا توجد تصنيفات للعرض" 
                @update:options="updateTableOptions"
                class="modern-table" 
                show-current-page 
                hover
            >
                <!-- Enhanced Icon Display -->
                <template v-slot:item.icon="{ item }">
                    <div class="icon-cell">
                        <div class="category-icon-wrapper">
                            <v-avatar 
                                size="48" 
                                :style="{ background: getCategoryGradient(item.color) }"
                                class="category-avatar"
                            >
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
                        <div class="category-meta">
                            <v-icon size="12" class="me-1">mdi-calendar</v-icon>
                            <span class="meta-text">تم الإنشاء مؤخراً</span>
                        </div>
                    </div>
                </template>

                <!-- Enhanced Description Display -->
                <template v-slot:item.description="{ item }">
                    <div class="description-cell">
                        <div v-if="item.description" class="description-text">
                            {{ truncateText(item.description, 80) }}
                        </div>
                        <div v-else class="no-description">
                            <v-icon size="16" color="grey-lighten-1" class="me-1">mdi-text-box-remove</v-icon>
                            <span class="text-grey-lighten-1">لا يوجد وصف</span>
                        </div>
                    </div>
                </template>

                <!-- Enhanced Product Count -->
                <template v-slot:item.products="{ item }">
                    <div class="products-cell">
                        <v-chip 
                            :color="getProductCountColor(item.productCount)" 
                            variant="tonal" 
                            size="small"
                            class="product-count-chip"
                        >
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
                            <v-chip 
                                :color="item.active ? 'success' : 'error'" 
                                variant="tonal" 
                                size="x-small"
                                class="mini-status-chip"
                            >
                                {{ item.active ? 'مفعل' : 'معطل' }}
                            </v-chip>
                        </div>
                    </div>
                </template>

                <!-- Enhanced Actions -->
                <template v-slot:item.actions="{ item }">
                    <div >
                        <v-menu>
                            <template v-slot:activator="{ props }">
                                <v-btn
                                    v-bind="props"
                                    icon="mdi-dots-vertical"
                                    variant="text"
                                    size="small"
                                    class="actions-trigger"
                                ></v-btn>
                            </template>
                            <v-list density="compact" class="actions-menu">
                                <v-list-item @click="viewCategory(item)" class="action-item">
                                    <template v-slot:prepend>
                                        <v-icon color="info">mdi-eye</v-icon>
                                    </template>
                                    <v-list-item-title>عرض التفاصيل</v-list-item-title>
                                </v-list-item>
                                
                                <v-list-item @click="$emit('edit-category', item)" class="action-item">
                                    <template v-slot:prepend>
                                        <v-icon color="primary">mdi-pencil</v-icon>
                                    </template>
                                    <v-list-item-title>تعديل التصنيف</v-list-item-title>
                                </v-list-item>
                                
                                <v-list-item @click="duplicateCategory(item)" class="action-item">
                                    <template v-slot:prepend>
                                        <v-icon color="success">mdi-content-copy</v-icon>
                                    </template>
                                    <v-list-item-title>نسخ التصنيف</v-list-item-title>
                                </v-list-item>
                                
                                <v-list-item @click="toggleCategoryStatus(item)" class="action-item">
                                    <template v-slot:prepend>
                                        <v-icon :color="item.active ? 'warning' : 'success'">
                                            {{ item.active ? 'mdi-pause' : 'mdi-play' }}
                                        </v-icon>
                                    </template>
                                    <v-list-item-title>
                                        {{ item.active ? 'إلغاء تفعيل' : 'تفعيل' }}
                                    </v-list-item-title>
                                </v-list-item>
                                
                                <v-divider></v-divider>
                                
                                <v-list-item @click="$emit('delete-confirmation', item)" class="action-item danger">
                                    <template v-slot:prepend>
                                        <v-icon color="error">mdi-delete</v-icon>
                                    </template>
                                    <v-list-item-title class="text-error">حذف التصنيف</v-list-item-title>
                                </v-list-item>
                            </v-list>
                        </v-menu>

                        <!-- Quick Actions -->
                        <!-- <div class="quick-actions">
                            <v-tooltip text="عرض سريع" location="top">
                                <template v-slot:activator="{ props }">
                                    <v-btn
                                        v-bind="props"
                                        icon="mdi-eye"
                                        size="x-small"
                                        variant="tonal"
                                        color="info"
                                        class="quick-action-btn"
                                        @click="viewCategory(item)"
                                    ></v-btn>
                                </template>
                            </v-tooltip>

                            <v-tooltip text="تعديل سريع" location="top">
                                <template v-slot:activator="{ props }">
                                    <v-btn
                                        v-bind="props"
                                        icon="mdi-pencil"
                                        size="x-small"
                                        variant="tonal"
                                        color="primary"
                                        class="quick-action-btn"
                                        @click="$emit('edit-category', item)"
                                    ></v-btn>
                                </template>
                            </v-tooltip>
                        </div> -->
                    </div>
                </template>

                <!-- Enhanced Headers -->
                <template v-slot:headers="{ columns, isSorted, getSortIcon, toggleSort }">
                    <tr class="table-header">
                        <template v-for="column in columns" :key="column.key">
                            <th 
                                :class="[
                                    'header-cell',
                                    column.align ? `text-${column.align}` : '',
                                    column.sortable ? 'sortable' : ''
                                ]"
                                :style="{ width: column.width }"
                                @click="column.sortable ? toggleSort(column) : null"
                            >
                                <div class="header-content">
                                    <span class="header-title">{{ column.title }}</span>
                                    <v-icon 
                                        v-if="column.sortable && isSorted(column)" 
                                        :icon="getSortIcon(column)" 
                                        color="primary"
                                        size="16"
                                        class="sort-icon"
                                    ></v-icon>
                                    <v-icon 
                                        v-else-if="column.sortable" 
                                        icon="mdi-sort" 
                                        color="grey-lighten-1"
                                        size="16"
                                        class="sort-icon"
                                    ></v-icon>
                                </div>
                            </th>
                        </template>
                    </tr>
                </template>

                <!-- Enhanced Loading -->
                <template v-slot:loading>
                    <div class="loading-state">
                        <div class="loading-content">
                            <v-progress-circular 
                                indeterminate 
                                color="primary" 
                                size="40"
                                class="mb-4"
                            ></v-progress-circular>
                            <h4 class="loading-title">جاري تحميل التصنيفات...</h4>
                            <p class="loading-subtitle">يرجى الانتظار قليلاً</p>
                        </div>
                    </div>
                </template>

                <!-- Enhanced No Data -->
                <template v-slot:no-data>
                    <div class="no-data-state">
                        <div class="no-data-content">
                            <div class="no-data-icon">
                                <v-icon size="60" color="grey-lighten-2">mdi-tag-multiple</v-icon>
                            </div>
                            <h4 class="no-data-title">لا توجد تصنيفات</h4>
                            <p class="no-data-subtitle">لم يتم العثور على تصنيفات مطابقة لمعايير البحث الحالية</p>
                            <v-btn
                                color="primary"
                                variant="tonal"
                                prepend-icon="mdi-plus"
                                class="mt-4"
                                @click="$emit('add-category')"
                            >
                                إضافة تصنيف جديد
                            </v-btn>
                        </div>
                    </div>
                </template>

                <!-- Enhanced Footer -->
                <template v-slot:bottom>
                    <div class="table-footer">
                        <div class="footer-info">
                            <span class="text-body-2 text-medium-emphasis">
                                عرض {{ (page - 1) * itemsPerPage + 1 }} - {{ Math.min(page * itemsPerPage, totalItems) }} من أصل {{ totalItems }} تصنيف
                            </span>
                        </div>
                        <v-pagination 
                            v-if="Math.ceil(totalItems / itemsPerPage) > 1"
                            :model-value="page" 
                            :length="Math.ceil(totalItems / itemsPerPage)" 
                            :total-visible="5"
                            @update:model-value="$emit('update:page', $event)" 
                            color="primary" 
                            size="small"
                            class="table-pagination"
                        ></v-pagination>
                        <v-select 
                            :model-value="itemsPerPage" 
                            :items="itemsPerPageOptions" 
                            label="عدد العناصر" 
                            variant="outlined"
                            density="compact" 
                            hide-details 
                            style="max-width: 120px;"
                            class="items-per-page-select"
                            @update:model-value="$emit('update:items-per-page', $event)"
                        ></v-select>
                    </div>
                </template>
            </v-data-table-server>
        </v-card>
    </div>
</template>

<script>
export default {
    name: 'CategoryList',
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
    emits: [
        'add-category', 
        'edit-category', 
        'delete-confirmation', 
        'update:page', 
        'update:items-per-page',
        'update:search-term',
        'update:status-filter',
        'update:sort-option',
        'refresh',
        'update:options',
        'view',
        'duplicate',
        'toggle-status'
    ],
    data() {
        return {
            categoryHeaders: [
                { title: 'الأيقونة', key: 'icon', sortable: false, align: 'center', width: '10%' },
                { title: 'الاسم', key: 'name', sortable: true, width: '25%' },
                { title: 'الوصف', key: 'description', sortable: false, width: '30%' },
                { title: 'المنتجات', key: 'products', sortable: false, align: 'center', width: '15%' },
                { title: 'الحالة', key: 'isActive', sortable: true, align: 'center', width: '12%' },
                { title: 'الإجراءات', key: 'actions', sortable: false, align: 'center', width: '8%' }
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
            itemsPerPageOptions: [5, 10, 25, 50, 100]
        };
    },
    methods: {
        updateTableOptions(options) {
            // Handle table updates (pagination, sorting, etc.)
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

        getCategoryGradient(color) {
            const gradients = {
                'blue': 'linear-gradient(135deg, #4299e1 0%, #3182ce 100%)',
                'green': 'linear-gradient(135deg, #48bb78 0%, #38a169 100%)',
                'purple': 'linear-gradient(135deg, #9f7aea 0%, #805ad5 100%)',
                'orange': 'linear-gradient(135deg, #ed8936 0%, #dd6b20 100%)',
                'red': 'linear-gradient(135deg, #f56565 0%, #e53e3e 100%)',
                'teal': 'linear-gradient(135deg, #4fd1c7 0%, #38b2ac 100%)',
                'pink': 'linear-gradient(135deg, #f687b3 0%, #ed64a6 100%)',
                'yellow': 'linear-gradient(135deg, #f6e05e 0%, #ecc94b 100%)'
            };
            return gradients[color] || gradients['blue'];
        },

        truncateText(text, maxLength) {
            if (!text) return '';
            return text.length > maxLength ? text.substring(0, maxLength) + '...' : text;
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
            const headers = ['الاسم', 'الوصف', 'عدد المنتجات', 'الحالة'];
            const csvContent = [
                headers.join(','),
                ...this.categories.map(category => [
                    category.name,
                    category.description || 'لا يوجد وصف',
                    category.productCount || 0,
                    category.active ? 'نشط' : 'غير نشط'
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
        }
    }
};
</script>

<style scoped>
/* Container Styling */
.category-list-container {
    background: white;
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    border: 1px solid #e2e8f0;
}

/* Table Controls */
.table-controls {
    padding: 24px 32px 16px;
    border-bottom: 1px solid #e2e8f0;
    background: #f8fafc;
}

.table-title {
    font-size: 18px;
    font-weight: 600;
    color: #2d3748;
    margin: 0;
}

.table-subtitle {
    font-size: 14px;
    color: #718096;
    margin: 4px 0 0 0;
}

.items-per-page {
    display: flex;
    align-items: center;
}

.items-select {
    border-radius: 8px;
}

.items-select .v-field {
    border-radius: 8px !important;
}

.export-btn {
    border-radius: 8px !important;
    font-weight: 500 !important;
    text-transform: none !important;
}

.add-btn {
    border-radius: 8px !important;
    font-weight: 500 !important;
    text-transform: none !important;
    background: linear-gradient(135deg, #366091 0%, #4299e1 100%) !important;
}

/* Table Card */
.table-card {
    border-radius: 0 !important;
    box-shadow: none !important;
}

/* Modern Table */
.modern-table {
    background: white;
}

/* Header Styling */
.table-header {
    background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
    border-bottom: 2px solid #cbd5e0;
}

.header-cell {
    padding: 16px 12px !important;
    font-weight: 600 !important;
    font-size: 14px !important;
    color: #2d3748 !important;
    border-bottom: none !important;
    position: relative;
}

.header-cell.sortable {
    cursor: pointer;
    transition: background-color 0.2s ease;
}

.header-cell.sortable:hover {
    background: rgba(54, 96, 145, 0.05);
}

.header-content {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
}

.header-title {
    font-weight: 600;
    color: #2d3748;
}

.sort-icon {
    transition: all 0.2s ease;
}

/* Cell Styling */
.icon-cell {
    display: flex;
    justify-content: center;
    padding: 8px 0;
}

.category-icon-wrapper {
    display: flex;
    align-items: center;
    justify-content: center;
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

.category-meta {
    display: flex;
    align-items: center;
    color: #a0aec0;
    font-size: 11px;
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
    text-align: center;
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

.status-cell {
    text-align: center;
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
    justify-content: center;
}

.mini-status-chip {
    font-size: 10px !important;
    height: 18px !important;
    font-weight: 600 !important;
}

/* Actions Cell */
.actions-cell {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 4px;
    position: relative;
}

.actions-trigger {
    color: #718096 !important;
}

.actions-trigger:hover {
    color: #366091 !important;
    background: rgba(54, 96, 145, 0.1) !important;
}

.quick-actions {
    display: none;
    gap: 4px;
}

.actions-cell:hover .quick-actions {
    display: flex;
}

.actions-cell:hover .actions-trigger {
    display: none;
}

.quick-action-btn {
    border-radius: 6px !important;
}

/* Actions Menu */
.actions-menu {
    border-radius: 12px !important;
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15) !important;
    border: 1px solid #e2e8f0 !important;
}

.action-item {
    border-radius: 8px !important;
    margin: 4px !important;
}

.action-item:hover {
    background: rgba(54, 96, 145, 0.05) !important;
}

.action-item.danger:hover {
    background: rgba(229, 62, 62, 0.05) !important;
}

/* Row Hover Effect */
.modern-table :deep(.v-data-table__tr:hover) {
    background: rgba(54, 96, 145, 0.02) !important;
}

/* Loading State */
.loading-state {
    padding: 60px 20px;
    text-align: center;
}

.loading-content {
    max-width: 300px;
    margin: 0 auto;
}

.loading-title {
    color: #2d3748;
    font-weight: 600;
    margin-bottom: 8px;
}

.loading-subtitle {
    color: #718096;
    font-size: 14px;
    margin: 0;
}

/* No Data State */
.no-data-state {
    padding: 60px 20px;
    text-align: center;
}

.no-data-content {
    max-width: 400px;
    margin: 0 auto;
}

.no-data-icon {
    margin-bottom: 24px;
}

.no-data-title {
    color: #2d3748;
    font-weight: 600;
    margin-bottom: 8px;
    font-size: 18px;
}

.no-data-subtitle {
    color: #718096;
    font-size: 14px;
    line-height: 1.5;
    margin: 0;
}

/* Table Footer */
.table-footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px 24px;
    background: #f8fafc;
    border-top: 1px solid #e2e8f0;
}

.footer-info {
    color: #718096;
}

.table-pagination {
    margin: 0;
}

.items-per-page-select .v-field {
    border-radius: 8px !important;
}

/* Responsive Design */
@media (max-width: 768px) {
    .table-controls {
        padding: 16px 20px 12px;
    }
    
    .table-controls .d-flex {
        flex-direction: column;
        gap: 16px;
        align-items: flex-start !important;
    }
    
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
    
    .actions-cell:hover .quick-actions {
        display: none;
    }
    
    .actions-cell:hover .actions-trigger {
        display: inline-flex;
    }
    
    .table-footer {
        flex-direction: column;
        gap: 12px;
        padding: 16px 20px;
    }
    
    .category-avatar {
        width: 40px !important;
        height: 40px !important;
    }
}

@media (max-width: 600px) {
    .header-cell {
        padding: 12px 8px !important;
        font-size: 12px !important;
    }
    
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

/* Animation */
.modern-table :deep(.v-data-table__tr) {
    transition: background-color 0.2s ease;
}

.actions-cell {
    transition: all 0.2s ease;
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

/* Focus States */
.actions-trigger:focus-visible,
.quick-action-btn:focus-visible {
    outline: 2px solid rgba(54, 96, 145, 0.5);
    outline-offset: 2px;
}

/* Print Styles */
@media print {
    .table-controls,
    .actions-cell,
    .table-footer {
        display: none !important;
    }
    
    .table-card {
        box-shadow: none !important;
        border: 1px solid #000 !important;
    }
    
    .category-avatar {
        background: #f0f0f0 !important;
        color: #000 !important;
    }
}

/* Custom Scrollbar */
.category-list-container::-webkit-scrollbar {
    width: 6px;
}

.category-list-container::-webkit-scrollbar-track {
    background: #f1f5f9;
    border-radius: 3px;
}

.category-list-container::-webkit-scrollbar-thumb {
    background: linear-gradient(135deg, #366091 0%, #4299e1 100%);
    border-radius: 3px;
}

.category-list-container::-webkit-scrollbar-thumb:hover {
    background: linear-gradient(135deg, #2d4f73 0%, #3182ce 100%);
}

/* Loading Animation */
.loading-content {
    animation: pulse 2s ease-in-out infinite alternate;
}

@keyframes pulse {
    0% {
        opacity: 0.8;
    }
    100% {
        opacity: 1;
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

/* Table Row Animation */
.modern-table :deep(.v-data-table__tr) {
    animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
    from {
        opacity: 0;
        transform: translateY(10px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

/* Enhanced Chip Styling */
.v-chip {
    border-radius: 8px !important;
    font-weight: 500 !important;
    transition: all 0.2s ease;
}

.v-chip:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

/* Button Enhancements */
.v-btn {
    transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.v-btn:hover {
    transform: translateY(-1px);
}

/* Text Selection */
::selection {
    background: rgba(54, 96, 145, 0.2);
    color: #2d3748;
}

/* Enhanced Tooltips */
.v-tooltip .v-overlay__content {
    background: #2d3748 !important;
    /* color: white !important; */
    border-radius: 8px !important;
    font-size: 12px !important;
    padding: 8px 12px !important;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15) !important;
}
</style>