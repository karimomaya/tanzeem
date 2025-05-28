<template>
    <div class="product-list-container">
        <!-- Table Header Controls -->
        <div class="table-controls">
            <div class="d-flex align-center justify-space-between">
                <div class="table-info">
                    <h3 class="table-title">قائمة المنتجات</h3>
                    <p class="table-subtitle">{{ products.length }} منتج متاح</p>
                </div>
                
                <div class="d-flex align-center ga-3">
                    <!-- Items per page -->
                    <div class="items-per-page">
                        <span class="text-body-2 text-medium-emphasis me-2">عرض:</span>
                        <v-select
                            v-model="itemsPerPage"
                            :items="[10, 25, 50, 100]"
                            variant="outlined"
                            density="compact"
                            hide-details
                            style="width: 80px;"
                            class="items-select"
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
                </div>
            </div>
        </div>

        <!-- Enhanced Data Table -->
        <v-card class="table-card" elevation="0">
            <v-data-table
                :headers="headers"
                :items="products"
                :items-per-page="itemsPerPage"
                :sort-by="sortBy"
                class="modern-table"
                hover
                show-current-page
            >
                <!-- Enhanced Product Column -->
                <template v-slot:item.product="{ item }">
                    <div class="product-cell">
                        <div class="product-image">
                            <v-img 
                                v-if="item.imageUrl" 
                                :src="item.imageUrl" 
                                :alt="item.name"
                                cover
                                class="product-img"
                            >
                                <template v-slot:error>
                                    <div class="image-fallback">
                                        <v-icon color="grey-lighten-1" size="20">mdi-package-variant</v-icon>
                                    </div>
                                </template>
                            </v-img>
                            <div v-else class="image-fallback">
                                <v-icon color="grey-lighten-1" size="20">mdi-package-variant</v-icon>
                            </div>
                        </div>
                        <div class="product-info">
                            <div class="product-name">{{ item.name }}</div>
                            <div class="product-sku">
                                <v-icon size="12" class="me-1">mdi-barcode</v-icon>
                                {{ item.sku }}
                            </div>
                            <div v-if="item.description" class="product-description">
                                {{ truncateText(item.description, 50) }}
                            </div>
                        </div>
                    </div>
                </template>

                <!-- Enhanced Category Column -->
                <template v-slot:item.category="{ item }">
                    <div class="category-cell">
                        <v-avatar size="24" :color="getCategoryColor(item.categoryId)" class="me-2">
                            <v-icon color="white" size="12">{{ getCategoryIcon(item.categoryId) }}</v-icon>
                        </v-avatar>
                        <span class="category-name">{{ getCategoryName(item.categoryId) }}</span>
                    </div>
                </template>

                <!-- Enhanced Price Column -->
                <template v-slot:item.price="{ item }">
                    <div class="price-cell">
                        <div class="price-main">{{ formatPrice(item.price) }} ر.س</div>
                        <div class="price-badge">
                            <v-icon size="12" class="me-1">mdi-currency-usd</v-icon>
                            السعر
                        </div>
                    </div>
                </template>

                <!-- Enhanced Stock Column -->
                <template v-slot:item.stock="{ item }">
                    <div class="stock-cell">
                        <v-chip
                            :color="getStockColor(item.stock)"
                            :variant="item.stock === 0 ? 'elevated' : 'tonal'"
                            size="small"
                            class="stock-chip"
                        >
                            <v-icon start size="14">{{ getStockIcon(item.stock) }}</v-icon>
                            {{ item.stock }}
                        </v-chip>
                        <div class="stock-status">{{ getStockStatus(item.stock) }}</div>
                    </div>
                </template>

                <!-- Enhanced Status Column -->
                <template v-slot:item.status="{ item }">
                    <div class="status-cell">
                        <div class="status-indicator" :class="`status-${item.status}`">
                            <div class="status-dot"></div>
                            <span class="status-text">{{ getStatusText(item.status) }}</span>
                        </div>
                    </div>
                </template>

                <!-- Enhanced Actions Column -->
                <template v-slot:item.actions="{ item }">
                    <div class="actions-cell">
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
                                <v-list-item @click="$emit('view', item)" class="action-item">
                                    <template v-slot:prepend>
                                        <v-icon color="info">mdi-eye</v-icon>
                                    </template>
                                    <v-list-item-title>عرض التفاصيل</v-list-item-title>
                                </v-list-item>
                                
                                <v-list-item @click="$emit('edit', item)" class="action-item">
                                    <template v-slot:prepend>
                                        <v-icon color="primary">mdi-pencil</v-icon>
                                    </template>
                                    <v-list-item-title>تعديل المنتج</v-list-item-title>
                                </v-list-item>
                                
                                <v-list-item @click="duplicateProduct(item)" class="action-item">
                                    <template v-slot:prepend>
                                        <v-icon color="success">mdi-content-copy</v-icon>
                                    </template>
                                    <v-list-item-title>نسخ المنتج</v-list-item-title>
                                </v-list-item>
                                
                                <v-divider></v-divider>
                                
                                <v-list-item @click="$emit('delete', item)" class="action-item danger">
                                    <template v-slot:prepend>
                                        <v-icon color="error">mdi-delete</v-icon>
                                    </template>
                                    <v-list-item-title class="text-error">حذف المنتج</v-list-item-title>
                                </v-list-item>
                            </v-list>
                        </v-menu>

                        <!-- Quick Actions -->
                        <div class="quick-actions">
                            <v-tooltip text="عرض سريع" location="top">
                                <template v-slot:activator="{ props }">
                                    <v-btn
                                        v-bind="props"
                                        icon="mdi-eye"
                                        size="x-small"
                                        variant="tonal"
                                        color="info"
                                        class="quick-action-btn"
                                        @click="$emit('view', item)"
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
                                        @click="$emit('edit', item)"
                                    ></v-btn>
                                </template>
                            </v-tooltip>
                        </div>
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
                            <h4 class="loading-title">جاري تحميل المنتجات...</h4>
                            <p class="loading-subtitle">يرجى الانتظار قليلاً</p>
                        </div>
                    </div>
                </template>

                <!-- Enhanced No Data -->
                <template v-slot:no-data>
                    <div class="no-data-state">
                        <div class="no-data-content">
                            <div class="no-data-icon">
                                <v-icon size="60" color="grey-lighten-2">mdi-package-variant-closed</v-icon>
                            </div>
                            <h4 class="no-data-title">لا توجد منتجات</h4>
                            <p class="no-data-subtitle">لم يتم العثور على منتجات مطابقة لمعايير البحث الحالية</p>
                            <v-btn
                                color="primary"
                                variant="tonal"
                                prepend-icon="mdi-refresh"
                                class="mt-4"
                                @click="refreshData"
                            >
                                تحديث القائمة
                            </v-btn>
                        </div>
                    </div>
                </template>

                <!-- Enhanced Footer -->
                <template v-slot:bottom>
                    <div class="table-footer">
                        <div class="footer-info">
                            <span class="text-body-2 text-medium-emphasis">
                                عرض {{ Math.min(products.length, itemsPerPage) }} من أصل {{ products.length }} منتج
                            </span>
                        </div>
                        <v-pagination 
                            v-if="Math.ceil(products.length / itemsPerPage) > 1"
                            v-model="currentPage"
                            :length="Math.ceil(products.length / itemsPerPage)"
                            :total-visible="5"
                            color="primary"
                            size="small"
                            class="table-pagination"
                        ></v-pagination>
                    </div>
                </template>
            </v-data-table>
        </v-card>
    </div>
</template>

<script>
export default {
    name: 'ProductList',
    props: {
        products: {
            type: Array,
            default: () => []
        }
    },
    emits: ['view', 'edit', 'delete', 'duplicate', 'refresh'],
    data() {
        return {
            itemsPerPage: 10,
            currentPage: 1,
            sortBy: [{ key: 'name', order: 'asc' }],
            headers: [
                { 
                    title: 'المنتج', 
                    key: 'product', 
                    sortable: true, 
                    width: '35%',
                    align: 'start'
                },
                { 
                    title: 'التصنيف', 
                    key: 'category', 
                    sortable: true, 
                    width: '15%',
                    align: 'center'
                },
                { 
                    title: 'السعر', 
                    key: 'price', 
                    sortable: true, 
                    width: '15%',
                    align: 'center'
                },
                { 
                    title: 'المخزون', 
                    key: 'stock', 
                    sortable: true, 
                    width: '15%',
                    align: 'center'
                },
                { 
                    title: 'الحالة', 
                    key: 'status', 
                    sortable: true, 
                    width: '12%',
                    align: 'center'
                },
                { 
                    title: 'الإجراءات', 
                    key: 'actions', 
                    sortable: false, 
                    width: '8%',
                    align: 'center'
                }
            ],
            categoryMap: {
                1: { name: 'أجهزة كمبيوتر', icon: 'mdi-laptop', color: 'blue' },
                2: { name: 'هواتف ذكية', icon: 'mdi-cellphone', color: 'green' },
                3: { name: 'أجهزة لوحية', icon: 'mdi-tablet', color: 'purple' },
                4: { name: 'إكسسوارات', icon: 'mdi-headphones', color: 'orange' }
            }
        };
    },
    methods: {
        // Status methods
        getStatusText(status) {
            const statusMap = {
                'active': 'نشط',
                'low-stock': 'مخزون منخفض',
                'out-of-stock': 'نفد المخزون',
                'inactive': 'غير نشط'
            };
            return statusMap[status] || 'غير محدد';
        },

        // Stock methods
        getStockColor(stock) {
            if (stock <= 0) return 'error';
            if (stock < 10) return 'warning';
            return 'success';
        },

        getStockIcon(stock) {
            if (stock <= 0) return 'mdi-close-circle';
            if (stock < 10) return 'mdi-alert-circle';
            return 'mdi-check-circle';
        },

        getStockStatus(stock) {
            if (stock <= 0) return 'نفد المخزون';
            if (stock < 10) return 'مخزون منخفض';
            return 'متوفر';
        },

        // Category methods
        getCategoryName(categoryId) {
            return this.categoryMap[categoryId]?.name || 'غير محدد';
        },

        getCategoryIcon(categoryId) {
            return this.categoryMap[categoryId]?.icon || 'mdi-folder';
        },

        getCategoryColor(categoryId) {
            return this.categoryMap[categoryId]?.color || 'grey';
        },

        // Utility methods
        formatPrice(price) {
            return new Intl.NumberFormat('ar-SA', {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2
            }).format(price);
        },

        truncateText(text, maxLength) {
            if (!text) return '';
            return text.length > maxLength ? text.substring(0, maxLength) + '...' : text;
        },

        // Action methods
        duplicateProduct(item) {
            const duplicatedProduct = { 
                ...item, 
                id: null, 
                name: `${item.name} - نسخة`,
                sku: `${item.sku}-COPY`
            };
            this.$emit('duplicate', duplicatedProduct);
        },

        exportData() {
            // Create CSV content
            const headers = ['الاسم', 'SKU', 'التصنيف', 'السعر', 'المخزون', 'الحالة'];
            const csvContent = [
                headers.join(','),
                ...this.products.map(product => [
                    product.name,
                    product.sku,
                    this.getCategoryName(product.categoryId),
                    product.price,
                    product.stock,
                    this.getStatusText(product.status)
                ].join(','))
            ].join('\n');

            // Download CSV
            const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
            const link = document.createElement('a');
            const url = URL.createObjectURL(blob);
            link.setAttribute('href', url);
            link.setAttribute('download', `products_${new Date().getTime()}.csv`);
            link.style.visibility = 'hidden';
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        },

        refreshData() {
            this.$emit('refresh');
        }
    }
};
</script>

<style scoped>
/* Container Styling */
.product-list-container {
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
.product-cell {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 8px 0;
}

.product-image {
    width: 48px;
    height: 48px;
    border-radius: 8px;
    overflow: hidden;
    position: relative;
    background: #f7fafc;
    border: 1px solid #e2e8f0;
}

.product-img {
    width: 100%;
    height: 100%;
}

.image-fallback {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f7fafc;
}

.product-info {
    flex: 1;
    min-width: 0;
}

.product-name {
    font-weight: 600;
    color: #2d3748;
    font-size: 14px;
    margin-bottom: 2px;
}

.product-sku {
    display: flex;
    align-items: center;
    color: #718096;
    font-size: 12px;
    margin-bottom: 2px;
}

.product-description {
    color: #a0aec0;
    font-size: 11px;
    line-height: 1.3;
}

/* Category Cell */
.category-cell {
    display: flex;
    align-items: center;
    justify-content: center;
}

.category-name {
    font-size: 13px;
    font-weight: 500;
    color: #4a5568;
}

/* Price Cell */
.price-cell {
    text-align: center;
}

.price-main {
    font-weight: 700;
    color: #2d3748;
    font-size: 16px;
    margin-bottom: 2px;
}

.price-badge {
    display: flex;
    align-items: center;
    justify-content: center;
    color: #718096;
    font-size: 11px;
}

/* Stock Cell */
.stock-cell {
    text-align: center;
}

.stock-chip {
    margin-bottom: 4px;
    font-weight: 600 !important;
}

.stock-status {
    font-size: 11px;
    color: #718096;
}

/* Status Cell */
.status-cell {
    display: flex;
    justify-content: center;
}

.status-indicator {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 4px 8px;
    border-radius: 16px;
    font-size: 12px;
    font-weight: 500;
}

.status-dot {
    width: 6px;
    height: 6px;
    border-radius: 50%;
}

.status-active {
    background: rgba(72, 187, 120, 0.1);
    color: #2f855a;
}

.status-active .status-dot {
    background: #48bb78;
}

.status-low-stock {
    background: rgba(237, 137, 54, 0.1);
    color: #c05621;
}

.status-low-stock .status-dot {
    background: #ed8936;
}

.status-out-of-stock {
    background: rgba(229, 62, 62, 0.1);
    color: #c53030;
}

.status-out-of-stock .status-dot {
    background: #e53e3e;
}

.status-inactive {
    background: rgba(160, 174, 192, 0.1);
    color: #718096;
}

.status-inactive .status-dot {
    background: #a0aec0;
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
    
    .product-cell {
        gap: 8px;
    }
    
    .product-image {
        width: 40px;
        height: 40px;
    }
    
    .product-name {
        font-size: 13px;
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
}

@media (max-width: 600px) {
    .header-cell {
        padding: 12px 8px !important;
        font-size: 12px !important;
    }
    
    .product-cell {
        gap: 6px;
    }
    
    .product-image {
        width: 36px;
        height: 36px;
    }
    
    .category-name {
        font-size: 12px;
    }
    
    .price-main {
        font-size: 14px;
    }
}

/* Animation */
.v-data-table :deep(.v-data-table__tr) {
    transition: background-color 0.2s ease;
}

.actions-cell {
    transition: all 0.2s ease;
}

.status-indicator {
    transition: all 0.2s ease;
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
}
</style>