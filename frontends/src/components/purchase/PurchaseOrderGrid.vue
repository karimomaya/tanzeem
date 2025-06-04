<template>
    <div class="grid-container">
        <!-- Loading State -->
        <div v-if="loading" class="loading-state">
            <div class="loading-content">
                <v-progress-circular indeterminate color="primary" size="60" class="mb-4"></v-progress-circular>
                <h4 class="loading-title">جاري تحميل أوامر الشراء...</h4>
                <p class="loading-subtitle">يرجى الانتظار قليلاً</p>
            </div>
        </div>

        <!-- Purchase Orders Grid -->
        <v-row v-else>
            <v-col v-for="order in paginatedOrders" :key="order.id" cols="12" sm="6" md="4" lg="3" xl="3">
                <v-card class="order-card card-base grid-card" elevation="0" @click="viewOrderDetails(order)">
                    <!-- Order Icon Section -->
                    <div class="image-section">
                        <div class="image-container">
                            <div class="order-icon-background" :style="{ background: getStatusGradient(order.status) }">
                                <v-icon color="white" size="32">
                                    {{ getStatusIcon(order.status) }}
                                </v-icon>
                            </div>

                            <!-- Order Overlay -->
                            <div class="overlay">
                                <!-- Status Badge -->
                                <div class="status-badge">
                                    <v-chip :color="getStatusColor(order.status)" size="small" class="status-chip">
                                        <v-icon start size="12">
                                            {{ getStatusIcon(order.status) }}
                                        </v-icon>
                                        {{ getStatusText(order.status) }}
                                    </v-chip>
                                </div>

                                <!-- Quick Actions -->
                                <div class="quick-actions">
                                    <v-btn v-if="order.status === 'PENDING'" icon="mdi-check" size="small" 
                                        variant="elevated" color="white" class="action-btn favorite-btn"
                                        @click.stop="$emit('mark-received', order)" 
                                        v-tooltip="'تسليم الأمر'"></v-btn>

                                    <v-menu>
                                        <template v-slot:activator="{ props }">
                                            <v-btn v-bind="props" icon="mdi-dots-vertical" size="small" variant="elevated"
                                                color="white" class="action-btn" @click.stop></v-btn>
                                        </template>
                                        <v-list density="compact" class="actions-menu">
                                            <v-list-item @click="viewOrderDetails(order)" class="menu-item action-item">
                                                <template v-slot:prepend>
                                                    <v-icon color="info" size="16">mdi-eye</v-icon>
                                                </template>
                                                <v-list-item-title>عرض التفاصيل</v-list-item-title>
                                            </v-list-item>

                                            <v-list-item v-if="order.status === 'PENDING'" @click="$emit('edit', order)" 
                                                class="menu-item action-item">
                                                <template v-slot:prepend>
                                                    <v-icon color="primary" size="16">mdi-pencil</v-icon>
                                                </template>
                                                <v-list-item-title>تعديل الأمر</v-list-item-title>
                                            </v-list-item>

                                            <v-list-item @click="duplicateOrder(order)" class="menu-item action-item">
                                                <template v-slot:prepend>
                                                    <v-icon color="success" size="16">mdi-content-copy</v-icon>
                                                </template>
                                                <v-list-item-title>نسخ الأمر</v-list-item-title>
                                            </v-list-item>

                                            <v-list-item v-if="order.status === 'PENDING'" 
                                                @click="$emit('mark-received', order)" class="menu-item action-item">
                                                <template v-slot:prepend>
                                                    <v-icon color="success" size="16">mdi-check</v-icon>
                                                </template>
                                                <v-list-item-title>تسليم الأمر</v-list-item-title>
                                            </v-list-item>

                                            <v-divider v-if="order.status === 'PENDING'" class="my-1"></v-divider>

                                            <v-list-item v-if="order.status === 'PENDING'" @click="$emit('delete', order)"
                                                class="menu-item action-item action-danger">
                                                <template v-slot:prepend>
                                                    <v-icon color="error" size="16">mdi-delete</v-icon>
                                                </template>
                                                <v-list-item-title class="text-error">حذف الأمر</v-list-item-title>
                                            </v-list-item>
                                        </v-list>
                                    </v-menu>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Order Content -->
                    <v-card-text class="content">
                        <!-- Order Number & Date -->
                        <h3 class="title">
                            {{ order.orderNumber }}
                        </h3>

                        <!-- Order Date -->
                        <p class="description">
                            <v-icon size="12" class="me-1">mdi-calendar</v-icon>
                            {{ formatDate(order.orderDate) }}
                        </p>

                        <!-- Supplier Info -->
                        <div class="supplier-info">
                            <div class="supplier-name">
                                <v-icon size="16" color="primary" class="me-2">mdi-truck</v-icon>
                                <span class="font-weight-medium">{{ order.supplier?.name || 'غير محدد' }}</span>
                            </div>
                            <div v-if="order.supplier?.phone" class="supplier-phone">
                                <v-icon size="12" class="me-1">mdi-phone</v-icon>
                                <span class="text-caption">{{ order.supplier.phone }}</span>
                            </div>
                        </div>

                        <!-- Order Metadata -->
                        <div class="meta-section">
                            <div class="meta-item">
                                <v-icon size="12" class="me-1">mdi-calendar-plus</v-icon>
                                <span class="meta-text">{{ formatDate(order.createdAt, 'created') }}</span>
                                <span v-if="order.createdBy" class="meta-by"> بواسطة {{ order.createdBy }}</span>
                            </div>
                        </div>

                        <!-- Order Stats -->
                        <div class="order-stats">
                            <div class="stat-item">
                                <div class="stat-icon">
                                    <v-icon size="16" color="primary">mdi-package-variant</v-icon>
                                </div>
                                <div class="stat-info">
                                    <div class="stat-value">{{ order.items?.length || 0 }}</div>
                                    <div class="stat-label">منتج</div>
                                </div>
                            </div>

                            <div class="stat-divider"></div>

                            <div class="stat-item">
                                <div class="stat-icon">
                                    <v-icon size="16" color="info">mdi-cube-outline</v-icon>
                                </div>
                                <div class="stat-info">
                                    <div class="stat-value">{{ getTotalQuantity(order.items) }}</div>
                                    <div class="stat-label">قطعة</div>
                                </div>
                            </div>
                        </div>

                        <!-- Total Amount -->
                        <div class="amount-section">
                            <div class="amount-info">
                                <span class="amount-label">المبلغ الإجمالي</span>
                                <span class="amount-value">{{ formatCurrency(order.totalAmount) }}</span>
                            </div>
                        </div>

                        <!-- Notes indicator -->
                        <div v-if="order.notes" class="notes-indicator">
                            <v-icon size="16" color="info" class="me-2">mdi-note-text</v-icon>
                            <span class="text-caption">يحتوي على ملاحظات</span>
                        </div>
                    </v-card-text>

                    <!-- Order Actions Footer -->
                    <v-card-actions class="actions-footer">
                        <v-btn variant="tonal" color="primary" size="small" class="action-button" prepend-icon="mdi-eye"
                            @click.stop="viewOrderDetails(order)">
                            عرض
                        </v-btn>

                        <v-btn v-if="order.status === 'PENDING'" variant="tonal" color="success" size="small" 
                            class="action-button" prepend-icon="mdi-pencil" @click.stop="$emit('edit', order)">
                            تعديل
                        </v-btn>

                        <v-spacer></v-spacer>

                        <v-btn v-if="order.status === 'PENDING'" icon="mdi-check" size="small" variant="text" 
                            color="success" class="receive-button" @click.stop="$emit('mark-received', order)"
                            v-tooltip="'تسليم الأمر'"></v-btn>

                        <v-btn v-if="order.status === 'PENDING'" icon="mdi-delete" size="small" variant="text" 
                            color="error" class="delete-button" @click.stop="$emit('delete', order)"
                            v-tooltip="'حذف الأمر'"></v-btn>
                    </v-card-actions>
                </v-card>
            </v-col>
        </v-row>

        <!-- Empty State -->
        <div v-if="!loading && filteredOrders.length === 0" class="empty-state no-data-state">
            <div class="empty-content no-data-content">
                <div class="empty-icon">
                    <v-icon size="80" color="grey-lighten-2">mdi-clipboard-list</v-icon>
                </div>
                <h3 class="empty-title no-data-title">لا توجد أوامر شراء</h3>
                <p class="empty-subtitle no-data-subtitle">لم يتم العثور على أوامر شراء مطابقة لمعايير البحث</p>
                <v-btn color="primary" variant="tonal" prepend-icon="mdi-refresh" class="mt-4" @click="$emit('refresh')">
                    تحديث القائمة
                </v-btn>
            </div>
        </div>

        <!-- Enhanced Pagination Card -->
        <v-card v-if="!loading && totalItems > 0" class="pagination-card" elevation="0">
            <v-card-text class="pagination-content">
                <!-- Pagination Info -->
                <div class="pagination-info">
                    <div class="info-section pagination-info-section">
                        <span class="text-body-2 text-large-emphasis">
                            عرض {{ startItem }} - {{ endItem }} من أصل {{ totalItems }} أمر شراء
                        </span>
                        <div class="pagination-stats">
                            <v-chip size="x-small" color="primary" variant="tonal" class="me-2">
                                <v-icon start size="12">mdi-layers</v-icon>
                                صفحة {{ page }} من {{ totalPages }}
                            </v-chip>
                            <v-chip size="x-small" color="info" variant="tonal">
                                <v-icon start size="12">mdi-grid</v-icon>
                                {{ itemsPerPage }} عنصر/صفحة
                            </v-chip>
                        </div>
                    </div>
                </div>

                <!-- Main Pagination Controls -->
                <div class="pagination-controls">
                    <v-pagination :model-value="page" :length="totalPages" :total-visible="getPaginationVisible"
                        @update:model-value="handlePageChange" color="primary" size="small" class="pagination-control"
                        :disabled="loading" show-first-last-page></v-pagination>
                </div>

                <!-- Items Per Page & Actions -->
                <div class="pagination-actions">
                    <div class="items-per-page">
                        <span class="text-body-2 text-large-emphasis me-2">عرض:</span>
                        <v-select :model-value="itemsPerPage" :items="itemsPerPageOptions" variant="outlined"
                            density="compact" hide-details style="width: 90px;" class="items-select"
                            @update:model-value="handleItemsPerPageChange" :disabled="loading"></v-select>
                    </div>

                    <!-- Quick Jump -->
                    <div class="quick-jump">
                        <span class="text-body-2 text-large-emphasis me-2">انتقال لصفحة:</span>
                        <v-text-field v-model="jumpToPage" type="number" :min="1" :max="totalPages" variant="outlined"
                            density="compact" hide-details style="width: 80px;" class="jump-input"
                            @keyup.enter="handleQuickJump" @blur="handleQuickJump"
                            :disabled="loading || totalPages <= 1"></v-text-field>
                    </div>
                </div>
            </v-card-text>

            <!-- Mobile Pagination -->
            <div class="mobile-pagination d-md-none">
                <v-card-text class="mobile-pagination-content">
                    <div class="mobile-info">
                        <span class="text-body-2 text-large-emphasis">
                            {{ startItem }} - {{ endItem }} من {{ totalItems }}
                        </span>
                    </div>

                    <div class="mobile-controls">
                        <v-btn icon="mdi-chevron-right" size="small" variant="outlined" color="primary"
                            :disabled="page >= totalPages || loading" @click="handlePageChange(page + 1)"></v-btn>

                        <div class="mobile-page-info">
                            <span class="text-body-2 font-weight-large">
                                {{ page }} / {{ totalPages }}
                            </span>
                        </div>

                        <v-btn icon="mdi-chevron-left" size="small" variant="outlined" color="primary"
                            :disabled="page <= 1 || loading" @click="handlePageChange(page - 1)"></v-btn>
                    </div>

                    <div class="mobile-items-per-page">
                        <v-select :model-value="itemsPerPage" :items="itemsPerPageOptions" label="عدد العناصر"
                            variant="outlined" density="compact" hide-details class="mobile-items-select"
                            @update:model-value="handleItemsPerPageChange" :disabled="loading"></v-select>
                    </div>
                </v-card-text>
            </div>
        </v-card>

        <!-- Floating Refresh Button -->
        <v-btn v-if="!loading" icon="mdi-refresh" color="primary" size="large" class="refresh-fab" elevation="8"
            @click="$emit('refresh')" :loading="loading">
            <v-icon>mdi-refresh</v-icon>
            <v-tooltip content-class="custom-tooltip" color="black" activator="parent" location="top">
                <span>تحديث أوامر الشراء</span>
            </v-tooltip>
        </v-btn>
    </div>
</template>

<script>
import { formatCurrency } from '@/utils/currency-util';
import { 
    formatDate
} from '@/utils/system-util';
import { 
    getPaginationVisible,
    truncateText
} from '@/utils/product-util';

export default {
    name: 'PurchaseOrderGrid',
    props: {
        purchaseOrders: {
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
        sortBy: {
            type: Array,
            default: () => []
        }
    },
    emits: [
        'edit', 
        'delete', 
        'view',
        'mark-received',
        'duplicate',
        'update:page', 
        'update:items-per-page', 
        'update:sort-by', 
        'refresh'
    ],
    data() {
        return {
            jumpToPage: null,
            itemsPerPageOptions: [10, 25, 50, 100]
        };
    },
    computed: {
        filteredOrders() {
            // Since filtering is now handled on the backend, 
            // we just return the orders as received
            return this.purchaseOrders;
        },

        paginatedOrders() {
            // Since pagination is handled on the backend,
            // we just return the orders as received
            return this.purchaseOrders;
        },

        totalPages() {
            return Math.ceil(this.totalItems / this.itemsPerPage);
        },

        startItem() {
            return this.totalItems === 0 ? 0 : (this.page - 1) * this.itemsPerPage + 1;
        },

        endItem() {
            return Math.min(this.page * this.itemsPerPage, this.totalItems);
        }
    },
    watch: {
        page(newPage) {
            this.jumpToPage = newPage;
        }
    },
    mounted() {
        this.jumpToPage = this.page;
    },
    methods: {
        formatCurrency,
        formatDate,
        getPaginationVisible,
        truncateText,

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

        getStatusIcon(status) {
            const statusIcons = {
                'PENDING': 'mdi-clock-outline',
                'RECEIVED': 'mdi-check-circle',
                'CANCELLED': 'mdi-close-circle'
            };
            return statusIcons[status] || 'mdi-help-circle';
        },

        getStatusGradient(status) {
            const gradients = {
                'PENDING': 'linear-gradient(135deg, #ff9800 0%, #f57c00 100%)',
                'RECEIVED': 'linear-gradient(135deg, #4caf50 0%, #388e3c 100%)',
                'CANCELLED': 'linear-gradient(135deg, #f44336 0%, #d32f2f 100%)'
            };
            return gradients[status] || 'linear-gradient(135deg, #9e9e9e 0%, #757575 100%)';
        },

        getTotalQuantity(items) {
            if (!items || !Array.isArray(items)) return 0;
            return items.reduce((total, item) => total + (item.quantity || 0), 0);
        },

        viewOrderDetails(order) {
            this.$emit('view', order);
        },

        duplicateOrder(order) {
            const duplicatedOrder = {
                ...order,
                id: null,
                orderNumber: null, // Will be generated by backend
                status: 'PENDING',
                orderDate: new Date().toISOString(),
                createdAt: null,
                updatedAt: null
            };
            this.$emit('duplicate', duplicatedOrder);
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

        handleQuickJump() {
            const pageNum = parseInt(this.jumpToPage);
            if (pageNum && pageNum >= 1 && pageNum <= this.totalPages && pageNum !== this.page) {
                this.handlePageChange(pageNum);
            } else {
                // Reset to current page if invalid
                this.jumpToPage = this.page;
            }
        }
    }
};
</script>

<style scoped>
@import '@/styles/product.css';

/* Order specific styling */
.order-card {
    transition: all 0.3s ease;
    cursor: pointer;
}

.order-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

/* Icon Section Specific */
.order-icon-background {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    overflow: hidden;
}

.order-icon-background::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: url("data:image/svg+xml,%3Csvg width='40' height='40' viewBox='0 0 40 40' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='%23ffffff' fill-opacity='0.1'%3E%3Cpath d='M20 20c0-11.046 8.954-20 20-20v20H20z'/%3E%3C/g%3E%3C/svg%3E") repeat;
}

/* Supplier Info */
.supplier-info {
    margin: 12px 0;
    padding: 8px 0;
    border-bottom: 1px solid #e2e8f0;
}

.supplier-name {
    display: flex;
    align-items: center;
    margin-bottom: 4px;
    font-size: 14px;
}

.supplier-phone {
    display: flex;
    align-items: center;
    color: #718096;
    font-size: 12px;
}

/* Order Stats */
.order-stats {
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
    font-size: 16px;
    font-weight: 600;
    color: #2d3748;
    line-height: 1;
}

.stat-label {
    font-size: 11px;
    color: #718096;
    margin-top: 2px;
}

.stat-divider {
    width: 1px;
    height: 24px;
    background: #e2e8f0;
}

/* Amount Section */
.amount-section {
    margin: 12px 0;
    padding: 12px;
    background: #f7fafc;
    border-radius: 8px;
    border: 1px solid #e2e8f0;
}

.amount-info {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.amount-label {
    font-size: 12px;
    color: #718096;
    font-weight: 500;
}

.amount-value {
    font-size: 16px;
    color: #1976d2;
    font-weight: 700;
}

/* Notes Indicator */
.notes-indicator {
    display: flex;
    align-items: center;
    margin-top: 8px;
    color: #718096;
}

/* Action buttons specific styling */
.receive-button:hover {
    background-color: rgba(76, 175, 80, 0.1);
}

.favorite-btn:hover {
    background-color: rgba(76, 175, 80, 0.1);
}

/* Custom tooltip styling override */
:global(.custom-tooltip) {
    background: #2d3748 !important;
    color: white !important;
    border-radius: 8px !important;
    font-size: 12px !important;
    padding: 8px 12px !important;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15) !important;
}

/* Mobile responsive adjustments */
@media (max-width: 768px) {
    .order-stats {
        gap: 8px;
    }

    .stat-value {
        font-size: 14px;
    }

    .amount-value {
        font-size: 14px;
    }
}

@media (max-width: 600px) {
    .order-stats {
        gap: 8px;
    }
    
    .supplier-info {
        margin: 8px 0;
    }
}
</style>