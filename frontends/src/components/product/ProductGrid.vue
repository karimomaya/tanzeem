<template>
    <div class="grid-container">
        <!-- Loading State -->
        <div v-if="loading" class="loading-state">
            <div class="loading-content">
                <v-progress-circular indeterminate color="primary" size="60" class="mb-4"></v-progress-circular>
                <h4 class="loading-title">جاري تحميل المنتجات...</h4>
                <p class="loading-subtitle">يرجى الانتظار قليلاً</p>
            </div>
        </div>

        <!-- Products Grid -->
        <v-row v-else>
            <v-col v-for="product in paginatedItems" :key="product.id" cols="12" sm="6" md="4" lg="3" xl="2">
                <v-card class="card-base grid-card product-card-hover" elevation="0" @click="$emit('view', product)">
                    <!-- Product Image Section -->
                    <div class="image-section">
                        <div class="image-container">
                            <v-img v-if="product.imageUrl" :src="product.imageUrl" :alt="product.name" cover
                                class="product-image" gradient="to bottom, rgba(0,0,0,0) 0%, rgba(0,0,0,0.1) 100%">
                                <template v-slot:error>
                                    <div class="product-image-fallback">
                                        <v-icon color="grey-lighten-1" size="32">mdi-package-variant</v-icon>
                                    </div>
                                </template>
                            </v-img>
                            <div v-else class="product-image-fallback">
                                <v-icon color="grey-lighten-1" size="32">mdi-package-variant</v-icon>
                            </div>

                            <!-- Product Overlay -->
                            <div class="overlay">
                                <!-- Status Badge -->
                                <div class="status-badge">
                                    <v-chip
                                        :color="getProductStatusColor(getProductStatus(product.stock, product.minimumStock, product.active, product.category?.active))"
                                        size="small" class="status-chip">
                                        <v-icon start size="12">{{ getProductStatusIcon(getProductStatus(product.stock,
                                            product.minimumStock, product.active, product.category?.active)) }}</v-icon>
                                        {{ getProductStatusText(getProductStatus(product.stock, product.minimumStock,
                                            product.active, product.category?.active)) }}
                                    </v-chip>
                                </div>

                                <!-- Quick Actions -->
                                <div class="quick-actions">
                                    <v-menu>
                                        <template v-slot:activator="{ props }">
                                            <v-btn v-bind="props" icon="mdi-dots-vertical" size="small" variant="elevated"
                                                color="white" class="action-btn" @click.stop></v-btn>
                                        </template>
                                        <v-list density="compact" class="actions-menu">
                                            <v-list-item @click="$emit('view', product)" class="action-item">
                                                <template v-slot:prepend>
                                                    <v-icon color="info" size="16">mdi-eye</v-icon>
                                                </template>
                                                <v-list-item-title>عرض التفاصيل</v-list-item-title>
                                            </v-list-item>

                                            <v-list-item @click="$emit('edit', product)" class="action-item">
                                                <template v-slot:prepend>
                                                    <v-icon color="primary" size="16">mdi-pencil</v-icon>
                                                </template>
                                                <v-list-item-title>تعديل المنتج</v-list-item-title>
                                            </v-list-item>

                                            <v-list-item @click="duplicateProduct(product)" class="action-item">
                                                <template v-slot:prepend>
                                                    <v-icon color="success" size="16">mdi-content-copy</v-icon>
                                                </template>
                                                <v-list-item-title>نسخ المنتج</v-list-item-title>
                                            </v-list-item>

                                            <v-divider class="my-1"></v-divider>

                                            <v-list-item @click="$emit('delete', product)"
                                                class="action-item action-danger">
                                                <template v-slot:prepend>
                                                    <v-icon color="error" size="16">mdi-delete</v-icon>
                                                </template>
                                                <v-list-item-title class="text-error">حذف المنتج</v-list-item-title>
                                            </v-list-item>
                                        </v-list>
                                    </v-menu>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Product Content -->
                    <v-card-text class="content">
                        <!-- Category Badge -->
                        <div class="product-category-section">
                            <v-chip :color="product.category.color" size="x-small" variant="tonal"
                                class="product-category-chip">
                                <v-icon start size="10">{{ product.category.icon }}</v-icon>
                                {{ product.category.name }}
                            </v-chip>
                        </div>

                        <!-- Product Title -->
                        <h3 class="title">
                            {{ product.name }}
                        </h3>

                        <!-- Product SKU -->
                        <div class="product-sku">
                            <v-icon size="12" class="me-1">mdi-barcode</v-icon>
                            {{ product.sku }}
                        </div>

                        <!-- Product Description -->
                        <p v-if="product.description" class="description">
                            {{ truncateText(product.description, 60) }}
                        </p>

                        <!-- Price Section -->
                        <div class="product-price-section">
                            <div class="product-price-main product-price-main-grid">{{ formatCurrency(product.price) }}
                            </div>
                            <div class="product-price-label">السعر</div>
                        </div>

                        <!-- Stock Section -->
                        <div class="stock-section">
                            <div class="stock-info">
                                <div class="product-stock-indicator"
                                    :class="`product-stock-${getStockLevel(product.stock, product.minimumStock)}`">
                                    <div class="product-stock-dot"></div>
                                    <span class="product-stock-text">{{ getStockMeta(product.stock,
                                        product.minimumStock).text
                                    }}</span>
                                </div>
                                <div class="product-stock-count">{{ product.stock }} في المخزون</div>
                            </div>

                            <!-- Stock Progress Bar -->
                            <v-progress-linear :model-value="getStockPercentage(product.stock, product.minimumStock)"
                                :color="getStockMeta(product.stock, product.minimumStock).color" height="4" rounded
                                class="stock-progress"></v-progress-linear>
                        </div>
                    </v-card-text>

                    <!-- Product Actions Footer -->
                    <v-card-actions class="actions-footer">
                        <v-btn variant="tonal" color="primary" size="small" class="action-button" prepend-icon="mdi-eye"
                            @click.stop="$emit('view', product)">
                            عرض
                        </v-btn>

                        <v-btn variant="tonal" color="success" size="small" class="action-button" prepend-icon="mdi-pencil"
                            @click.stop="$emit('edit', product)">
                            تعديل
                        </v-btn>

                        <v-spacer></v-spacer>

                        <v-btn icon="mdi-delete" size="small" variant="text" color="error" class="delete-button"
                            @click.stop="$emit('delete', product)"></v-btn>
                    </v-card-actions>
                </v-card>
            </v-col>
        </v-row>

        <!-- Empty State -->
        <div v-if="!loading && filteredItems.length === 0" class="no-data-state">
            <div class="no-data-content">
                <div class="empty-icon">
                    <v-icon size="80" color="grey-lighten-2">mdi-package-variant-closed</v-icon>
                </div>
                <h3 class="no-data-title">لا توجد منتجات</h3>
                <p class="no-data-subtitle">لم يتم العثور على منتجات مطابقة لمعايير البحث</p>
                <v-btn color="primary" variant="tonal" prepend-icon="mdi-refresh" class="mt-4" @click="$emit('refresh')">
                    تحديث القائمة
                </v-btn>
            </div>
        </div>

        <!-- Enhanced Pagination Card -->
        <TablePagination v-if="!loading && totalItems > 0" :page="page" :items-per-page="itemsPerPage"
            :total-items="totalItems" item-label="منتج" :items-per-page-options="itemsPerPageOptions"
            @update:page="handlePageChange" @update:items-per-page="handleItemsPerPageChange" />

        <!-- Floating Refresh Button -->
        <v-btn v-if="!loading" icon="mdi-refresh" color="primary" size="large" class="refresh-fab" elevation="8"
            @click="$emit('refresh')" :loading="loading">
            <v-icon>mdi-refresh</v-icon>
            <v-tooltip content-class="custom-tooltip" color="black" activator="parent" location="top">
                <span>تحديث المنتجات</span>
            </v-tooltip>
        </v-btn>
    </div>
</template>

<script>
import { formatCurrency } from '@/utils/currency-util'
import {
    getStockMeta,
    getStockLevel,
    getStockPercentage,
    truncateText,
    createDuplicateProduct,
} from '@/utils/product-util';

import { 
  getProductStatusText, 
  getProductStatusColor, 
  getProductStatusIcon,
  getProductStatus 
} from '@/utils/status-util'

import TablePagination from '@/components/common/TablePagination.vue';

export default {
    name: 'ProductGrid',
    components: {
        TablePagination
    },
    props: {
        items: {
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
            default: 12
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
        sortBy: {
            type: Array,
            default: () => []
        }
    },
    emits: ['view', 'edit', 'delete', 'duplicate', 'favorite', 'update:page', 'update:items-per-page', 'update:sort-by', 'refresh'],
    data() {
        return {
            itemsPerPageOptions: [12, 24, 48, 96]
        };
    },
    computed: {
        filteredItems() {
            return this[this.itemsProperty];
        },

        paginatedItems() {
            return this[this.itemsProperty];
        },

        totalPages() {
            return Math.ceil(this.totalItems / this.itemsPerPage);
        },

        startItem() {
            return this.totalItems === 0 ? 0 : (this.page - 1) * this.itemsPerPage + 1;
        },

        endItem() {
            return Math.min(this.page * this.itemsPerPage, this.totalItems);
        },

        itemsProperty() {
            return 'items'; 
        }
    },
    methods: {
        // Import utility functions
        getStockMeta,
        getStockLevel,
        getStockPercentage,
        formatCurrency,
        truncateText,
        getProductStatus,
        getProductStatusText,
        getProductStatusColor,
        getProductStatusIcon,

        // Action methods
        duplicateProduct(product) {
            const duplicatedProduct = createDuplicateProduct(product);
            this.$emit('duplicate', duplicatedProduct);
        },

        toggleFavorite(product) {
            this.$emit('favorite', product);
        },

        handlePageChange(newPage) {
            if (newPage >= 1 && newPage <= this.totalPages && newPage !== this.page) {
                this.$emit('update:page', newPage);
            }
        },
        handleItemsPerPageChange(newItemsPerPage) {
            this.$emit('update:items-per-page', newItemsPerPage);
            // Reset to first page when changing items per page
            if (this.page !== 1) {
                this.$emit('update:page', 1);
            }
        },
    }
};
</script>

<style scoped>
@import '@/styles/product.css';
</style>