<template>
    <div class="table-container">
        <!-- Table Header Controls -->
        <BaseTableHeader title="قائمة المنتجات" :total-items="totalItems" item-label="منتج" add-button-text="إضافة منتج"
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
            <v-data-table-server :headers="headers" :items="products" :items-per-page="itemsPerPage" :page="page"
                :items-length="totalItems" :loading="loading" :sort-by="sortBy" loading-text="جاري التحميل... يرجى الانتظار"
                no-data-text="لا توجد تصنيفات للعرض" @update:options="updateOptions" class="modern-table" hover
                show-current-page>
                <!-- Enhanced Product Column -->
                <template v-slot:item.name="{ item }">
                    <div class="product-list-cell">
                        <div class="product-list-image">
                            <v-img v-if="item.imageUrl" :src="item.imageUrl" :alt="item.name" cover class="product-image">
                                <template v-slot:error>
                                    <div class="product-image-fallback">
                                        <v-icon color="grey-lighten-1" size="20">mdi-package-variant</v-icon>
                                    </div>
                                </template>
                            </v-img>
                            <div v-else class="product-image-fallback">
                                <v-icon color="grey-lighten-1" size="20">mdi-package-variant</v-icon>
                            </div>
                        </div>
                        <div class="product-list-info">
                            <div class="product-list-name">{{ item.name }}</div>
                            <div class="product-sku">
                                <v-icon size="12" class="me-1">mdi-barcode</v-icon>
                                {{ item.sku }}
                            </div>
                            <div v-if="item.description" class="product-list-description">
                                {{ truncateText(item.description, 50) }}
                            </div>
                            <MetaDataDisplay 
                                :created-at="item.createdAt"
                                :created-by="item.createdBy"
                                :updated-at="item.updatedAt"
                                :updated-by="item.updatedBy"
                            />

                        </div>
                    </div>
                </template>

                <!-- Enhanced Category Column -->
                <template v-slot:item.categoryName="{ item }">
                    <div class="product-category-cell">
                        <v-avatar size="24" :color="item.category?.color || 'grey'" class="me-2">
                            <v-icon color="white" size="12">{{ item.category?.icon || 'mdi-tag' }}</v-icon>
                        </v-avatar>
                        <span class="product-category-name">{{ item.category?.name || item.categoryName || 'غير محدد'
                        }}</span>
                    </div>
                </template>

                <!-- Enhanced Price Column -->
                <template v-slot:item.price="{ item }">
                    <div class="product-price-cell">
                        <div class="product-price-main product-price-main-list">{{ formatCurrency(item.price) }} </div>
                        <div class="product-price-badge">
                            <v-icon size="12" class="me-1">mdi-currency-usd</v-icon>
                            السعر
                        </div>
                    </div>
                </template>

                <!-- Enhanced Stock Column -->
                <template v-slot:item.stock="{ item }">
                    <div class="product-stock-cell">
                        <v-chip :color="getStockMeta(item.stock, item.minimumStock).color"
                            :variant="item.stock === 0 ? 'elevated' : 'tonal'" size="small" class="product-stock-chip">
                            <v-icon start size="14">{{ getStockMeta(item.stock, item.minimumStock).icon }}</v-icon>
                            {{ item.stock }}
                        </v-chip>
                        <div class="product-stock-status">{{ getStockMeta(item.stock, item.minimumStock).text }}</div>
                    </div>
                </template>

                <!-- Enhanced Status Column -->
                <template v-slot:item.status="{ item }">
                    <div class="status-cell">
                        <v-chip
                            :color="getProductStatusColor(getProductStatus(item.stock, item.minimumStock, item.active, item.category?.active))"
                            size="small" variant="tonal" class="product-status-chip">
                            <v-icon start size="12">{{ getProductStatusIcon(getProductStatus(item.stock, item.minimumStock,
                                item.active, item.category?.active)) }}</v-icon>
                            {{ getProductStatusText(getProductStatus(item.stock, item.minimumStock, item.active,
                                item.category?.active)) }}
                        </v-chip>
                    </div>
                </template>
                <!-- Enhanced Actions Column -->
                <template v-slot:item.actions="{ item }">
                    <StandardTableActions :actions="productActions" :item="item" @action="handleTableAction" />
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
                            <h4 class="loading-title">جاري تحميل المنتجات...</h4>
                            <p class="loading-subtitle">يرجى الانتظار قليلاً</p>
                        </div>
                    </div>
                </template>

                <!-- Enhanced No Data -->
                <template v-slot:no-data>
                    <NoDataState icon="mdi-package-variant-closed" title="لا توجد منتجات"
                        subtitle="لم يتم العثور على منتجات مطابقة لمعايير البحث الحالية" add-button-text="تحديث القائمة"
                        @add-item="refreshData" />
                </template>
            </v-data-table-server>
            <TablePagination :page="page" :items-per-page="itemsPerPage" :total-items="totalItems" item-label="منتج"
                :items-per-page-options="itemsPerPageOptions" @update:page="$emit('update:page', $event)"
                @update:items-per-page="$emit('update:items-per-page', $event)" />
        </v-card>
    </div>
</template>

<script>
import { formatCurrency } from '@/utils/currency-util'
import TablePagination from '@/components/common/TablePagination.vue';
import NoDataState from '@/components/common/NoDataState.vue';
import BaseTableHeader from '@/components/common/BaseTableHeader.vue';
import StandardTableActions from '@/components/common/StandardTableActions.vue';
import MetaDataDisplay from '@/components/common/MetaDataDisplay.vue';
import {
    getStockMeta,
    getStockLevel,
    truncateText,
    createDuplicateProduct,
    exportProductsToCSV,
    getProductStatus,
    getProductStatusText,
    getProductStatusColor,
    getProductStatusIcon
} from '@/utils/product-util';

export default {
    name: 'ProductList',
    components: {
        TablePagination,
        NoDataState,
        BaseTableHeader,
        StandardTableActions,
        MetaDataDisplay
    },
    props: {
        products: {
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
    watch: {
        products: {
            handler(newProducts) {

            },
            immediate: true
        },
    },
    emits: ['view', 'edit', 'delete', 'duplicate', 'refresh', 'update:options', 'add', 'update:page', 'update:items-per-page'],
    data() {
        return {
            itemsPerPageOptions: [10, 25, 50, 100],
            productActions: [
                { key: 'view', icon: 'mdi-eye', color: 'info', text: 'عرض التفاصيل' },
                { key: 'edit', icon: 'mdi-pencil', color: 'primary', text: 'تعديل المنتج' },
                { key: 'duplicate', icon: 'mdi-content-copy', color: 'success', text: 'نسخ المنتج' },
                { divider: true },
                { key: 'delete', icon: 'mdi-delete', color: 'error', text: 'حذف المنتج', danger: true }
            ],
            headers: [
                {
                    title: 'المنتج',
                    key: 'name',
                    sortable: true,
                    width: '30%',  // Reduce width slightly
                    align: 'start'
                },
                {
                    title: 'التصنيف',
                    key: 'categoryName',
                    sortable: true,
                    width: '15%',  // Reduce width slightly
                    align: 'start'
                },
                {
                    title: 'السعر',
                    key: 'price',
                    sortable: true,
                    width: '12%',  // Reduce width slightly
                    align: 'start'
                },
                {
                    title: 'المخزون',
                    key: 'stock',
                    sortable: true,
                    width: '12%',  // Reduce width slightly
                    align: 'start'
                },
                {
                    title: 'الحالة',
                    key: 'status',
                    sortable: true,
                    width: '13%',  // Add status column
                    align: 'center'
                },
                {
                    title: 'الإجراءات',
                    key: 'actions',
                    sortable: false,
                    width: '8%',
                    align: 'start'
                }
            ]
        };
    },
    methods: {
        // Import utility functions
        getStockMeta,
        getStockLevel,
        formatCurrency,
        truncateText,
        getProductStatus,
        getProductStatusText,
        getProductStatusColor,
        getProductStatusIcon,
        // Action methods
        handleTableAction(payload) {
            const { type, item } = payload;

            switch (type) {
                case 'view':
                    this.$emit('view', item);
                    break;
                case 'edit':
                    this.$emit('edit', item);
                    break;
                case 'duplicate':
                    this.duplicateProduct(item);
                    break;
                case 'delete':
                    this.$emit('delete', item);
                    break;
            }
        },
        duplicateProduct(item) {
            const duplicatedProduct = createDuplicateProduct(item);
            this.$emit('duplicate', duplicatedProduct);
        },

        exportData() {
            exportProductsToCSV(this.products);
        },

        refreshData() {
            this.$emit('refresh');
        },

        // Server-side data table options handler
        updateOptions(options) {
            this.$emit('update:options', options);
        },

        // Handle items per page change
        updateItemsPerPage(newItemsPerPage) {
            this.$emit('update:options', {
                page: 1, // Reset to first page
                itemsPerPage: newItemsPerPage,
                sortBy: this.sortBy
            });
        }
    }
};
</script>

<style scoped>
@import '@/styles/product.css';
</style>