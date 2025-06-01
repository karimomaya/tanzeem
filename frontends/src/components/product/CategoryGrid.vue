<template>
    <div class="grid-container">
        <!-- Loading State -->
        <div v-if="loading" class="loading-state">
            <div class="loading-content">
                <v-progress-circular indeterminate color="primary" size="60" class="mb-4"></v-progress-circular>
                <h4 class="loading-title">جاري تحميل التصنيفات...</h4>
                <p class="loading-subtitle">يرجى الانتظار قليلاً</p>
            </div>
        </div>

        <!-- Categories Grid -->
        <v-row v-else>
            <v-col v-for="category in paginatedCategories" :key="category.id" cols="12" sm="6" md="4" lg="3" xl="2">
                <v-card class="category-card card-base grid-card" elevation="0" @click="viewCategoryDetails(category)">
                    <!-- Category Icon Section -->
                    <div class="image-section">
                        <div class="image-container">
                            <div class="category-icon-background" :style="{ background: category.color }">
                                <v-icon :color="'white'" size="32">
                                    {{ category.icon || 'mdi-folder' }}
                                </v-icon>
                            </div>

                            <!-- Category Overlay -->
                            <div class="overlay">
                                <!-- Status Badge -->
                                <div class="status-badge status-badge">
                                    <v-chip :color="category.active ? 'success' : 'error'" size="small" class="status-chip">
                                        <v-icon start size="12">
                                            {{ category.active ? 'mdi-check-circle' : 'mdi-close-circle' }}
                                        </v-icon>
                                        {{ category.active ? 'نشط' : 'غير نشط' }}
                                    </v-chip>
                                </div>

                                <!-- Quick Actions -->
                                <div class="quick-actions">
                                    <v-btn icon="mdi-heart-outline" size="small" variant="elevated" color="white"
                                        class="action-btn action-btn favorite-btn"
                                        @click.stop="toggleFavorite(category)"></v-btn>

                                    <v-menu>
                                        <template v-slot:activator="{ props }">
                                            <v-btn v-bind="props" icon="mdi-dots-vertical" size="small" variant="elevated"
                                                color="white" class="action-btn action-btn" @click.stop></v-btn>
                                        </template>
                                        <v-list density="compact" class="actions-menu">
                                            <v-list-item @click="viewCategoryDetails(category)"
                                                class="menu-item action-item">
                                                <template v-slot:prepend>
                                                    <v-icon color="info" size="16">mdi-eye</v-icon>
                                                </template>
                                                <v-list-item-title>عرض التفاصيل</v-list-item-title>
                                            </v-list-item>

                                            <v-list-item @click="$emit('edit', category)" class="menu-item action-item">
                                                <template v-slot:prepend>
                                                    <v-icon color="primary" size="16">mdi-pencil</v-icon>
                                                </template>
                                                <v-list-item-title>تعديل التصنيف</v-list-item-title>
                                            </v-list-item>

                                            <v-list-item @click="duplicateCategory(category)" class="menu-item action-item">
                                                <template v-slot:prepend>
                                                    <v-icon color="success" size="16">mdi-content-copy</v-icon>
                                                </template>
                                                <v-list-item-title>نسخ التصنيف</v-list-item-title>
                                            </v-list-item>

                                            <v-list-item @click="toggleCategoryStatus(category)"
                                                class="menu-item action-item">
                                                <template v-slot:prepend>
                                                    <v-icon :color="category.active ? 'warning' : 'success'" size="16">
                                                        {{ category.active ? 'mdi-pause' : 'mdi-play' }}
                                                    </v-icon>
                                                </template>
                                                <v-list-item-title>
                                                    {{ category.active ? 'إلغاء تفعيل' : 'تفعيل' }}
                                                </v-list-item-title>
                                            </v-list-item>

                                            <v-divider class="my-1"></v-divider>

                                            <v-list-item @click="$emit('delete', category)"
                                                class="menu-item action-item action-danger">
                                                <template v-slot:prepend>
                                                    <v-icon color="error" size="16">mdi-delete</v-icon>
                                                </template>
                                                <v-list-item-title class="text-error">حذف التصنيف</v-list-item-title>
                                            </v-list-item>
                                        </v-list>
                                    </v-menu>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Category Content -->
                    <v-card-text class="content">
                        <!-- Category Title -->
                        <h3 class="title">
                            {{ category.name }}
                        </h3>

                        <!-- Category Description -->
                        <p v-if="category.description" class="description">
                            {{ truncateText(category.description) }}
                        </p>
                        <p v-else class="description placeholder">
                            لا يوجد وصف لهذا التصنيف
                        </p>

                        <!-- Category Metadata -->
                        <div class="meta-section">
                            <div class="meta-item">
                                <v-icon size="12" class="me-1">mdi-calendar-plus</v-icon>
                                <span class="meta-text">{{ formatDateText(category.createdAt, 'created') }}</span>
                                <span v-if="category.createdBy" class="meta-by"> بواسطة {{ category.createdBy.name ||
                                    category.createdBy }}</span>
                            </div>
                            <div v-if="category.updatedAt && isUpdatedRecently(category.createdAt, category.updatedAt)"
                                class="meta-item">
                                <v-icon size="12" class="me-1">mdi-calendar-edit</v-icon>
                                <span class="meta-text">{{ formatDateText(category.updatedAt, 'updated') }}</span>
                                <span v-if="category.updatedBy" class="meta-by"> بواسطة {{ category.updatedBy.name ||
                                    category.updatedBy }}</span>
                            </div>
                        </div>

                        <!-- Category Stats -->
                        <div class="category-stats">
                            <div class="stat-item">
                                <div class="stat-icon">
                                    <v-icon size="16" color="primary">mdi-package-variant</v-icon>
                                </div>
                                <div class="stat-info">
                                    <div class="stat-value">{{ category.productCount || 0 }}</div>
                                    <div class="stat-label">منتج</div>
                                </div>
                            </div>

                            <div class="stat-divider"></div>

                            <div class="stat-item">
                                <div class="stat-icon">
                                    <v-icon size="16" :color="category.active ? 'success' : 'error'">
                                        {{ category.active ? 'mdi-check-circle' : 'mdi-close-circle' }}
                                    </v-icon>
                                </div>
                                <div class="stat-info">
                                    <div class="stat-value">{{ category.active ? 'نشط' : 'غير نشط' }}</div>
                                    <div class="stat-label">الحالة</div>
                                </div>
                            </div>
                        </div>

                        <!-- Category Progress -->
                        <div class="stock-section">
                            <div class="progress-info stock-info">
                                <span class="progress-label">نشاط التصنيف</span>
                                <span class="progress-value">{{ category.activity }}%</span>
                            </div>
                            <v-progress-linear :model-value="category.activity"
                                :color="getCategoryActivityColor(category.activity)" height="6" rounded
                                class="activity-progress stock-progress"></v-progress-linear>
                        </div>
                    </v-card-text>

                    <!-- Category Actions Footer -->
                    <v-card-actions class="actions-footer">
                        <v-btn variant="tonal" color="primary" size="small" class="action-button" prepend-icon="mdi-eye"
                            @click.stop="viewCategoryDetails(category)">
                            عرض
                        </v-btn>

                        <v-btn variant="tonal" color="success" size="small" class="action-button" prepend-icon="mdi-pencil"
                            @click.stop="$emit('edit', category)">
                            تعديل
                        </v-btn>

                        <v-spacer></v-spacer>

                        <v-btn icon="mdi-delete" size="small" variant="text" color="error" class="delete-button"
                            @click.stop="$emit('delete', category)"></v-btn>
                    </v-card-actions>
                </v-card>
            </v-col>
        </v-row>

        <!-- Empty State -->
        <div v-if="!loading && filteredCategories.length === 0" class="empty-state no-data-state">
            <div class="empty-content no-data-content">
                <div class="empty-icon">
                    <v-icon size="80" color="grey-lighten-2">mdi-tag-multiple</v-icon>
                </div>
                <h3 class="empty-title no-data-title">لا توجد تصنيفات</h3>
                <p class="empty-subtitle no-data-subtitle">لم يتم العثور على تصنيفات مطابقة لمعايير البحث</p>
                <v-btn color="primary" variant="tonal" prepend-icon="mdi-refresh" class="mt-4" @click="$emit('refresh')">
                    تحديث القائمة
                </v-btn>
            </div>
        </div>

        <!-- Enhanced Pagination Card -->
        <v-card v-if="!loading && totalItems > 0" class="pagination-card" elevation="0">
            <v-card-text class="pagination-content pagination-content">
                <!-- Pagination Info -->
                <div class="pagination-info pagination-info">
                    <div class="info-section pagination-info-section">
                        <span class="text-body-2 text-large-emphasis">
                            عرض {{ startItem }} - {{ endItem }} من أصل {{ totalItems }} تصنيف
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
                <span>تحديث التصنيفات</span>
            </v-tooltip>
        </v-btn>
    </div>
</template>

<script>
import { 
    isUpdatedRecently,
    formatDateText,
    getPaginationVisible,
    truncateText,
    getStockStatus, 
    getStockText, 
    getStockColor, 
    getStockIcon, 
    getStockLevel, 
    getStockPercentage,
    formatPrice,
    createDuplicateProduct,
    calculatePaginationInfo
} from '@/utils/product-util';
export default {
    name: 'CategoryGrid',
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
        sortBy: {
            type: Array,
            default: () => []
        }
    },
    emits: ['edit', 'delete', 'update:page', 'update:items-per-page', 'update:sort-by', 'refresh', 'view', 'duplicate', 'favorite', 'toggle-status'],
    data() {
        return {
            jumpToPage: null,
            itemsPerPageOptions: [10, 25, 50, 100]
        };
    },
    computed: {
        filteredCategories() {
            // Since filtering is now handled on the backend, 
            // we just return the categories as received
            return this.categories;
        },

        paginatedCategories() {
            // Since pagination is handled on the backend,
            // we just return the categories as received
            return this.categories;
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


        activeCount() {
            return this.categories.filter(cat => cat.active).length;
        },

        inactiveCount() {
            return this.categories.filter(cat => !cat.active).length;
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
        isUpdatedRecently,
        formatDateText,
        getPaginationVisible,
        truncateText,

        getCategoryActivityColor(activity) {
            if (activity >= 80) return 'success';
            if (activity >= 50) return 'info';
            if (activity >= 30) return 'warning';
            return 'error';
        },

        viewCategoryDetails(category) {
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

        toggleFavorite(category) {
            this.$emit('favorite', category);
        },

        toggleCategoryStatus(category) {
            this.$emit('toggle-status', { ...category, active: !category.active });
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


/* Icon Section Specific */
.category-icon-background {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    overflow: hidden;
}

.category-icon-background::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: url("data:image/svg+xml,%3Csvg width='40' height='40' viewBox='0 0 40 40' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='%23ffffff' fill-opacity='0.1'%3E%3Cpath d='M20 20c0-11.046 8.954-20 20-20v20H20z'/%3E%3C/g%3E%3C/svg%3E") repeat;
}

/* Category Stats */
.category-stats {
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

/* Category Progress */
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

.activity-progress {
    border-radius: 4px;
    overflow: hidden;
}

/* Favorite button specific styling */
.favorite-btn:hover {
    color: #e53e3e !important;
}


/* Override product.css for category-specific responsive behavior */
@media (max-width: 768px) {
    .category-icon-section {
        height: 120px;
    }

    .category-stats {
        gap: 8px;
    }

    .stat-value {
        font-size: 14px;
    }
}

@media (max-width: 600px) {
    .category-icon-section {
        height: 100px;
    }

    .category-stats {
        gap: 8px;
    }
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








</style>