<template>
    <div class="table-container">
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
                        :model-value="itemsPerPage"
                            @update:model-value="$emit('update:items-per-page', $event)"
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
            <v-data-table-server
                :headers="headers"
                :items="products"
                :items-per-page="itemsPerPage"
                :page="page" 
                :items-length="totalItems" 
                :loading="loading"
                :sort-by="sortBy"
                loading-text="جاري التحميل... يرجى الانتظار"
                no-data-text="لا توجد تصنيفات للعرض" 
                @update:options="updateOptions"
                class="modern-table"
                hover
                show-current-page

            >
                <!-- Enhanced Product Column -->
                <template v-slot:item.name="{ item }">
                    <div class="product-list-cell">
                        <div class="product-list-image">
                            <v-img 
                                v-if="item.imageUrl" 
                                :src="item.imageUrl" 
                                :alt="item.name"
                                cover
                                class="product-image"
                            >
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
                               
                        </div>
                    </div>
                </template>

                <!-- Enhanced Category Column -->
                <template v-slot:item.categoryName="{ item }">
                    <div class="product-category-cell">
                        <v-avatar size="24" :color="item.category?.color || 'grey'" class="me-2">
                            <v-icon color="white" size="12">{{ item.category?.icon || 'mdi-tag' }}</v-icon>
                        </v-avatar>
                        <span class="product-category-name">{{ item.category?.name || item.categoryName || 'غير محدد' }}</span>
                    </div>
                </template>

                <!-- Enhanced Price Column -->
                <template v-slot:item.price="{ item }">
                    <div class="product-price-cell">
                        <div class="product-price-main product-price-main-list">{{ formatPrice(item.price) }} ر.س</div>
                        <div class="product-price-badge">
                            <v-icon size="12" class="me-1">mdi-currency-usd</v-icon>
                            السعر
                        </div>
                    </div>
                </template>

                <!-- Enhanced Stock Column -->
                <template v-slot:item.stock="{ item }">
                    <div class="product-stock-cell">
                        <v-chip
                            :color="getStockColor(item.stock, item.minimumStock)"
                            :variant="item.stock === 0 ? 'elevated' : 'tonal'"
                            size="small"
                            class="product-stock-chip"
                        >
                            <v-icon start size="14">{{ getStockIcon(item.stock, item.minimumStock) }}</v-icon>
                            {{ item.stock }}
                        </v-chip>
                        <div class="product-stock-status">{{ getStockStatus(item.stock, item.minimumStock) }}</div>
                    </div>
                </template>

                <!-- Enhanced Status Column
                <template v-slot:item.status="{ item }">
                    <div class="status-cell">

                        <div class="product-status-indicator" :class="`product-status-${item.status}`">
                            <div class="product-status-dot"></div>
                            <span class="status-text">{{ item.status.trim() }}</span>
                        </div>
                    </div>
                </template> -->

                <!-- Enhanced Actions Column -->
                <template v-slot:item.actions="{ item }">
                    <div>
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
                                
                                <v-list-item @click="$emit('delete', item)" class="action-item action-danger">
                                    <template v-slot:prepend>
                                        <v-icon color="error">mdi-delete</v-icon>
                                    </template>
                                    <v-list-item-title class="text-error">حذف المنتج</v-list-item-title>
                                </v-list-item>
                            </v-list>
                        </v-menu>
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
                            <div class="empty-icon">
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
import { 
    getStockStatus, 
    getStockIcon, 
    getStockText, 
    getStockColor, 
    getStockLevel, 
    formatPrice,
    truncateText,
    createDuplicateProduct,
    exportProductsToCSV,
    formatDateText
} from '@/utils/product-util';

export default {
    name: 'ProductList',
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
    mounted() {
        // Debug logging
        console.log('ProductList mounted:', {
            products: this.products,
            totalItems: this.totalItems,
            loading: this.loading,
            itemsPerPage: this.itemsPerPage,
            headers: this.headers
        });
    },
    watch: {
        products: {
            handler(newProducts) {
                console.log('ProductList products changed:', {
                    length: newProducts?.length,
                    firstProduct: newProducts?.[0],
                    data: newProducts
                });
            },
            immediate: true
        },
        totalItems(newTotal) {
            console.log('ProductList totalItems changed:', newTotal);
        },
        loading(newLoading) {
            console.log('ProductList loading changed:', newLoading);
        }
    
        
    },
    emits: ['view', 'edit', 'delete', 'duplicate', 'refresh', 'update:options'],
    data() {
        return {
            itemsPerPageOptions: [10, 25, 50, 100],
            headers: [
                { 
                    title: 'المنتج', 
                    key: 'name', 
                    sortable: true, 
                    width: '35%',
                    align: 'start'
                },
                { 
                    title: 'التصنيف', 
                    key: 'categoryName', 
                    sortable: true, 
                    width: '17%',
                    align: 'start'
                },
                { 
                    title: 'السعر', 
                    key: 'price', 
                    sortable: true, 
                    width: '15%',
                    align: 'start'
                },
                { 
                    title: 'المخزون', 
                    key: 'stock', 
                    sortable: true, 
                    width: '15%',
                    align: 'start'
                },
                // { 
                //     title: 'الحالة', 
                //     key: 'status', 
                //     sortable: true, 
                //     width: '10%',
                //     align: 'center'
                // },
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
        getStockText,
        getStockColor,
        getStockIcon,
        getStockStatus,
        getStockLevel,
        formatPrice,
        truncateText,
        formatDateText,
        // Action methods
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