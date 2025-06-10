<template>
    <div class="grid-container">
        <!-- Loading State -->
        <div v-if="loading" class="loading-state">
            <div class="loading-content">
                <v-progress-circular indeterminate color="primary" size="60" class="mb-4"></v-progress-circular>
                <h4 class="loading-title">جاري تحميل الموردين...</h4>
                <p class="loading-subtitle">يرجى الانتظار قليلاً</p>
            </div>
        </div>

        <!-- Suppliers Grid -->
        <v-row v-else>
            <v-col v-for="supplier in paginatedItems" :key="supplier.id" cols="12" sm="6" md="4" lg="3" xl="2">
                <v-card class="supplier-card card-base grid-card" elevation="0" @click="viewSupplierDetails(supplier)">
                    <!-- Supplier Icon Section -->
                    <div class="image-section">
                        <div class="image-container">
                            <div class="supplier-icon-background" :style="{ background: supplier.color || '#366091' }">
                                <v-icon :color="'white'" size="32">
                                    {{ supplier.icon || 'mdi-truck' }}
                                </v-icon>
                            </div>

                            <!-- Supplier Overlay -->
                            <div class="overlay">
                                <!-- Status Badge -->
                                <div class="status-badge">
                                    <v-chip :color="supplier.isActive ? 'success' : 'error'" size="small"
                                        class="status-chip">
                                        <v-icon start size="12">
                                            {{ supplier.isActive ? 'mdi-check-circle' : 'mdi-close-circle' }}
                                        </v-icon>
                                        {{ supplier.isActive ? 'نشط' : 'غير نشط' }}
                                    </v-chip>
                                </div>

                                <!-- Quick Actions -->
                                <div class="quick-actions">
                                    <v-btn icon="mdi-heart-outline" size="small" variant="elevated" color="white"
                                        class="action-btn favorite-btn" @click.stop="toggleFavorite(supplier)"></v-btn>

                                    <v-menu>
                                        <template v-slot:activator="{ props }">
                                            <v-btn v-bind="props" icon="mdi-dots-vertical" size="small" variant="elevated"
                                                color="white" class="action-btn" @click.stop></v-btn>
                                        </template>
                                        <v-list density="compact" class="actions-menu">
                                            <v-list-item @click="viewSupplierDetails(supplier)"
                                                class="menu-item action-item">
                                                <template v-slot:prepend>
                                                    <v-icon color="info" size="16">mdi-eye</v-icon>
                                                </template>
                                                <v-list-item-title>عرض التفاصيل</v-list-item-title>
                                            </v-list-item>

                                            <v-list-item @click="$emit('edit', supplier)" class="menu-item action-item">
                                                <template v-slot:prepend>
                                                    <v-icon color="primary" size="16">mdi-pencil</v-icon>
                                                </template>
                                                <v-list-item-title>تعديل المورد</v-list-item-title>
                                            </v-list-item>

                                            <v-list-item @click="duplicateSupplier(supplier)" class="menu-item action-item">
                                                <template v-slot:prepend>
                                                    <v-icon color="success" size="16">mdi-content-copy</v-icon>
                                                </template>
                                                <v-list-item-title>نسخ المورد</v-list-item-title>
                                            </v-list-item>

                                            <v-list-item @click="toggleSupplierStatus(supplier)"
                                                class="menu-item action-item">
                                                <template v-slot:prepend>
                                                    <v-icon :color="supplier.isActive ? 'warning' : 'success'" size="16">
                                                        {{ supplier.isActive ? 'mdi-pause' : 'mdi-play' }}
                                                    </v-icon>
                                                </template>
                                                <v-list-item-title>
                                                    {{ supplier.isActive ? 'إلغاء تفعيل' : 'تفعيل' }}
                                                </v-list-item-title>
                                            </v-list-item>

                                            <v-divider class="my-1"></v-divider>

                                            <v-list-item @click="$emit('delete', supplier)"
                                                class="menu-item action-item action-danger">
                                                <template v-slot:prepend>
                                                    <v-icon color="error" size="16">mdi-delete</v-icon>
                                                </template>
                                                <v-list-item-title class="text-error">حذف المورد</v-list-item-title>
                                            </v-list-item>
                                        </v-list>
                                    </v-menu>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Supplier Content -->
                    <v-card-text class="content">
                        <!-- Supplier Title -->
                        <h3 class="title">
                            {{ supplier.name }}
                        </h3>

                        <!-- Supplier Code -->
                        <p v-if="supplier.code" class="description">
                            كود المورد: {{ supplier.code }}
                        </p>

                        <!-- Contact Information -->
                        <div class="contact-section">
                            <div class="contact-item" v-if="supplier.contactPerson">
                                <v-icon size="14" color="primary" class="me-1">mdi-account</v-icon>
                                <span class="contact-text">{{ supplier.contactPerson }}</span>
                            </div>
                            <div class="contact-item" v-if="supplier.phone">
                                <v-icon size="14" color="success" class="me-1">mdi-phone</v-icon>
                                <span class="contact-text">{{ supplier.phone }}</span>
                            </div>
                            <div class="contact-item" v-if="supplier.email">
                                <v-icon size="14" color="info" class="me-1">mdi-email</v-icon>
                                <span class="contact-text">{{ truncateText(supplier.email, 20) }}</span>
                            </div>
                        </div>

                        <!-- Address Section -->
                        <div v-if="supplier.address" class="address-section">
                            <div class="address-item">
                                <v-icon size="14" color="orange" class="me-1">mdi-map-marker</v-icon>
                                <span class="address-text">{{ truncateText(supplier.address, 30) }}</span>
                            </div>
                        </div>

                        <!-- Supplier Metadata -->
                        <div class="meta-section">
                            <div class="meta-item">
                                <v-icon size="12" class="me-1">mdi-calendar-plus</v-icon>
                                <span class="meta-text">{{ formatDate(supplier.createdAt, 'created') }}</span>
                                <span v-if="supplier.createdBy" class="meta-by"> بواسطة {{ supplier.createdBy.name ||
                                    supplier.createdBy }}</span>
                            </div>
                            <div v-if="supplier.updatedAt && isUpdatedRecently(supplier.createdAt, supplier.updatedAt)"
                                class="meta-item">
                                <v-icon size="12" class="me-1">mdi-calendar-edit</v-icon>
                                <span class="meta-text">{{ formatDate(supplier.updatedAt, 'updated') }}</span>
                                <span v-if="supplier.updatedBy" class="meta-by"> بواسطة {{ supplier.updatedBy.name ||
                                    supplier.updatedBy }}</span>
                            </div>
                        </div>

                        <!-- Supplier Stats -->
                        <div class="supplier-stats">
                            <div class="stat-item">
                                <div class="stat-icon">
                                    <v-icon size="16" color="primary">mdi-clipboard-list</v-icon>
                                </div>
                                <div class="stat-info">
                                    <div class="stat-value">{{ supplier.totalOrders || 0 }}</div>
                                    <div class="stat-label">أوامر شراء</div>
                                </div>
                            </div>

                            <div class="stat-divider"></div>

                            <div class="stat-item">
                                <div class="stat-icon">
                                    <v-icon size="16" color="success">mdi-currency-usd</v-icon>
                                </div>
                                <div class="stat-info">
                                    <div class="stat-value">{{ formatCurrency(supplier.totalAmount || 0) }}</div>
                                    <div class="stat-label">إجمالي المشتريات</div>
                                </div>
                            </div>
                        </div>

                        <!-- Supplier Performance -->
                        <div class="stock-section">
                            <div class="progress-info stock-info">
                                <span class="progress-label">تقييم المورد</span>
                                <span class="progress-value">{{ supplier.rating || 85 }}%</span>
                            </div>
                            <v-progress-linear :model-value="supplier.rating || 85"
                                :color="getSupplierRatingColor(supplier.rating || 85)" height="6" rounded
                                class="rating-progress stock-progress"></v-progress-linear>
                        </div>
                    </v-card-text>

                    <!-- Supplier Actions Footer -->
                    <v-card-actions class="actions-footer">
                        <v-btn variant="tonal" color="primary" size="small" class="action-button" prepend-icon="mdi-eye"
                            @click.stop="viewSupplierDetails(supplier)">
                            عرض
                        </v-btn>

                        <v-btn variant="tonal" color="success" size="small" class="action-button" prepend-icon="mdi-pencil"
                            @click.stop="$emit('edit', supplier)">
                            تعديل
                        </v-btn>

                        <v-spacer></v-spacer>

                        <v-btn icon="mdi-delete" size="small" variant="text" color="error" class="delete-button"
                            @click.stop="$emit('delete', supplier)"></v-btn>
                    </v-card-actions>
                </v-card>
            </v-col>
        </v-row>

        <!-- Empty State -->
        <div v-if="!loading && filteredItems.length === 0" class="empty-state no-data-state">
            <div class="empty-content no-data-content">
                <div class="empty-icon">
                    <v-icon size="80" color="grey-lighten-2">mdi-truck</v-icon>
                </div>
                <h3 class="empty-title no-data-title">لا توجد موردين</h3>
                <p class="empty-subtitle no-data-subtitle">لم يتم العثور على موردين مطابقين لمعايير البحث</p>
                <v-btn color="primary" variant="tonal" prepend-icon="mdi-refresh" class="mt-4" @click="$emit('refresh')">
                    تحديث القائمة
                </v-btn>
            </div>
        </div>

        <!-- Enhanced Pagination Card -->
        <TablePagination v-if="!loading && totalItems > 0" :page="page" :items-per-page="itemsPerPage"
            :total-items="totalItems" item-label="مورد" :items-per-page-options="itemsPerPageOptions"
            @update:page="handlePageChange" @update:items-per-page="handleItemsPerPageChange" />
        <!-- Floating Refresh Button -->
        <v-btn v-if="!loading" icon="mdi-refresh" color="primary" size="large" class="refresh-fab" elevation="8"
            @click="$emit('refresh')" :loading="loading">
            <v-icon>mdi-refresh</v-icon>
            <v-tooltip content-class="custom-tooltip" color="black" activator="parent" location="top">
                <span>تحديث الموردين</span>
            </v-tooltip>
        </v-btn>
    </div>
</template>






<script>
import { formatCurrency } from '@/utils/currency-util';
import { formatDate, isUpdatedRecently } from '@/utils/date-util';
import TablePagination from '@/components/common/TablePagination.vue';
import {
    truncateText
} from '@/utils/product-util';

export default {
    name: 'SupplierGrid',
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
    emits: ['edit', 'delete', 'update:page', 'update:items-per-page', 'update:sort-by', 'refresh', 'view', 'duplicate', 'favorite', 'toggle-status'],

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
        formatCurrency,
        isUpdatedRecently,
        formatDate,
        truncateText,
        getSupplierRatingColor(rating) {
            if (rating >= 90) return 'success';
            if (rating >= 75) return 'info';
            if (rating >= 60) return 'warning';
            return 'error';
        },

        viewSupplierDetails(supplier) {
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

        toggleFavorite(supplier) {
            this.$emit('favorite', supplier);
        },

        toggleSupplierStatus(supplier) {
            this.$emit('toggle-status', { ...supplier, isActive: !supplier.isActive });
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

/* Supplier-specific styling */
.supplier-card {
    height: 100%;
    display: flex;
    flex-direction: column;
}

.supplier-icon-background {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    overflow: hidden;
}

.supplier-icon-background::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: url("data:image/svg+xml,%3Csvg width='40' height='40' viewBox='0 0 40 40' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='%23ffffff' fill-opacity='0.1'%3E%3Cpath d='M20 20c0-11.046 8.954-20 20-20v20H20z'/%3E%3C/g%3E%3C/svg%3E") repeat;
}

.contact-section {
    margin-bottom: 16px;
}

.contact-item {
    display: flex;
    align-items: center;
    margin-bottom: 8px;
}

.contact-text {
    font-size: 12px;
    color: #4a5568;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.address-section {
    margin-bottom: 16px;
}

.address-item {
    display: flex;
    align-items: flex-start;
    gap: 8px;
}

.address-text {
    font-size: 12px;
    color: #4a5568;
    line-height: 1.4;
    overflow: hidden;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
}

/* Supplier Stats */
.supplier-stats {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 12px 0;
    border-top: 1px solid #e2e8f0;
    border-bottom: 1px solid #e2e8f0;
}

.stat-item {
    display: flex;
    align-items: center;
    gap: 8px;
    flex: 1;
}

.stat-icon {
    width: 32px;
    height: 32px;
    border-radius: 8px;
    background: #f7fafc;
    display: flex;
    align-items: center;
    justify-content: center;
}

.stat-info {
    flex: 1;
}

.stat-value {
    font-size: 12px;
    font-weight: 600;
    color: #2d3748;
    line-height: 1;
}

.stat-label {
    font-size: 10px;
    color: #718096;
    margin-top: 2px;
}

.stat-divider {
    width: 1px;
    height: 24px;
    background: #e2e8f0;
}

/* Rating Progress */
.progress-info {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;
}

.progress-label {
    font-size: 12px;
    color: #718096;
    font-weight: 500;
}

.progress-value {
    font-size: 12px;
    color: #2d3748;
    font-weight: 600;
}

:global(.custom-tooltip) {
    background: #2d3748 !important;
    color: white !important;
    border-radius: 8px !important;
    font-size: 12px !important;
    padding: 8px 12px !important;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15) !important;
}
</style>