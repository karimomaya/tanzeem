<template>
    <div class="category-grid">
        <!-- Loading State -->
        <div v-if="loading" class="loading-state">
            <div class="loading-content">
                <v-progress-circular 
                    indeterminate 
                    color="primary" 
                    size="60"
                    class="mb-4"
                ></v-progress-circular>
                <h4 class="loading-title">جاري تحميل التصنيفات...</h4>
                <p class="loading-subtitle">يرجى الانتظار قليلاً</p>
            </div>
        </div>

        <!-- Categories Grid -->
        <v-row v-else>
            <v-col 
                v-for="category in paginatedCategories" 
                :key="category.id"
                cols="12" 
                sm="6" 
                md="4" 
                lg="3"
                xl="2"
            >
                <v-card
                    class="category-card"
                    elevation="0"
                    @click="viewCategoryDetails(category)"
                >
                    <!-- Category Icon Section -->
                    <div class="category-icon-section">
                        <div class="category-icon-container">
                            <div class="category-icon-background" :style="{ background: getCategoryGradient(category.color) }">
                                <v-icon 
                                    :color="'white'" 
                                    size="32"
                                >
                                    {{ category.icon || 'mdi-folder' }}
                                </v-icon>
                            </div>
                            
                            <!-- Category Overlay -->
                            <div class="category-overlay">
                                <!-- Status Badge -->
                                <div class="status-badge">
                                    <v-chip
                                        :color="category.active ? 'success' : 'error'"
                                        size="small"
                                        class="status-chip"
                                    >
                                        <v-icon start size="12">
                                            {{ category.active ? 'mdi-check-circle' : 'mdi-close-circle' }}
                                        </v-icon>
                                        {{ category.active ? 'نشط' : 'غير نشط' }}
                                    </v-chip>
                                </div>
                                
                                <!-- Quick Actions -->
                                <div class="quick-actions">
                                    <v-btn
                                        icon="mdi-heart-outline"
                                        size="small"
                                        variant="elevated"
                                        color="white"
                                        class="action-btn favorite-btn"
                                        @click.stop="toggleFavorite(category)"
                                    ></v-btn>
                                    
                                    <v-menu>
                                        <template v-slot:activator="{ props }">
                                            <v-btn
                                                v-bind="props"
                                                icon="mdi-dots-vertical"
                                                size="small"
                                                variant="elevated"
                                                color="white"
                                                class="action-btn"
                                                @click.stop
                                            ></v-btn>
                                        </template>
                                        <v-list density="compact" class="actions-menu">
                                            <v-list-item @click="viewCategoryDetails(category)" class="menu-item">
                                                <template v-slot:prepend>
                                                    <v-icon color="info" size="16">mdi-eye</v-icon>
                                                </template>
                                                <v-list-item-title>عرض التفاصيل</v-list-item-title>
                                            </v-list-item>
                                            
                                            <v-list-item @click="$emit('edit', category)" class="menu-item">
                                                <template v-slot:prepend>
                                                    <v-icon color="primary" size="16">mdi-pencil</v-icon>
                                                </template>
                                                <v-list-item-title>تعديل التصنيف</v-list-item-title>
                                            </v-list-item>
                                            
                                            <v-list-item @click="duplicateCategory(category)" class="menu-item">
                                                <template v-slot:prepend>
                                                    <v-icon color="success" size="16">mdi-content-copy</v-icon>
                                                </template>
                                                <v-list-item-title>نسخ التصنيف</v-list-item-title>
                                            </v-list-item>
                                            
                                            <v-list-item @click="toggleCategoryStatus(category)" class="menu-item">
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
                                            
                                            <v-list-item @click="$emit('delete', category)" class="menu-item danger">
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
                    <v-card-text class="category-content">
                        <!-- Category Title -->
                        <h3 class="category-title">
                            {{ category.name }}
                        </h3>

                        <!-- Category Description -->
                        <p v-if="category.description" class="category-description">
                            {{ getDescription(category) }}
                        </p>
                        <p v-else class="category-description placeholder">
                            لا يوجد وصف لهذا التصنيف
                        </p>

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
                        <div class="category-progress">
                            <div class="progress-info">
                                <span class="progress-label">نشاط التصنيف</span>
                                <span class="progress-value">{{ getCategoryActivity(category) }}%</span>
                            </div>
                            <v-progress-linear
                                :model-value="getCategoryActivity(category)"
                                :color="getCategoryActivityColor(category)"
                                height="6"
                                rounded
                                class="activity-progress"
                            ></v-progress-linear>
                        </div>
                    </v-card-text>

                    <!-- Category Actions Footer -->
                    <v-card-actions class="category-actions">
                        <v-btn
                            variant="tonal"
                            color="primary"
                            size="small"
                            class="action-button"
                            prepend-icon="mdi-eye"
                            @click.stop="viewCategoryDetails(category)"
                        >
                            عرض
                        </v-btn>
                        
                        <v-btn
                            variant="tonal"
                            color="success"
                            size="small"
                            class="action-button"
                            prepend-icon="mdi-pencil"
                            @click.stop="$emit('edit', category)"
                        >
                            تعديل
                        </v-btn>
                        
                        <v-spacer></v-spacer>
                        
                        <v-btn
                            icon="mdi-delete"
                            size="small"
                            variant="text"
                            color="error"
                            class="delete-button"
                            @click.stop="$emit('delete', category)"
                        ></v-btn>
                    </v-card-actions>
                </v-card>
            </v-col>
        </v-row>

        <!-- Empty State -->
        <div v-if="!loading && filteredCategories.length === 0" class="empty-state">
            <div class="empty-content">
                <div class="empty-icon">
                    <v-icon size="80" color="grey-lighten-2">mdi-tag-multiple</v-icon>
                </div>
                <h3 class="empty-title">لا توجد تصنيفات</h3>
                <p class="empty-subtitle">لم يتم العثور على تصنيفات مطابقة لمعايير البحث</p>
                <v-btn
                    color="primary"
                    variant="tonal"
                    prepend-icon="mdi-refresh"
                    class="mt-4"
                    @click="$emit('refresh')"
                >
                    تحديث القائمة
                </v-btn>
            </div>
        </div>

        <!-- Enhanced Pagination Card -->
        <v-card 
            v-if="!loading && totalItems > 0" 
            class="pagination-card" 
            elevation="0"
        >
            <v-card-text class="pagination-content">
                <!-- Pagination Info -->
                <div class="pagination-info">
                    <div class="info-section">
                        <span class="text-body-2 text-medium-emphasis">
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
                    <v-pagination 
                        :model-value="page"
                        :length="totalPages" 
                        :total-visible="getPaginationVisible"
                        @update:model-value="handlePageChange"
                        color="primary" 
                        size="small"
                        class="pagination-control"
                        :disabled="loading"
                        show-first-last-page
                    ></v-pagination>
                </div>
                
                <!-- Items Per Page & Actions -->
                <div class="pagination-actions">
                    <div class="items-per-page">
                        <span class="text-body-2 text-medium-emphasis me-2">عرض:</span>
                        <v-select 
                            :model-value="itemsPerPage"
                            :items="itemsPerPageOptions" 
                            variant="outlined"
                            density="compact" 
                            hide-details 
                            style="width: 90px;"
                            class="items-select"
                            @update:model-value="handleItemsPerPageChange"
                            :disabled="loading"
                        ></v-select>
                    </div>
                    
                    <!-- Quick Jump -->
                    <div class="quick-jump">
                        <span class="text-body-2 text-medium-emphasis me-2">انتقال لصفحة:</span>
                        <v-text-field
                            v-model="jumpToPage"
                            type="number"
                            :min="1"
                            :max="totalPages"
                            variant="outlined"
                            density="compact"
                            hide-details
                            style="width: 80px;"
                            class="jump-input"
                            @keyup.enter="handleQuickJump"
                            @blur="handleQuickJump"
                            :disabled="loading || totalPages <= 1"
                        ></v-text-field>
                    </div>
                </div>
            </v-card-text>
            
            <!-- Mobile Pagination -->
            <div class="mobile-pagination d-md-none">
                <v-card-text class="mobile-pagination-content">
                    <div class="mobile-info">
                        <span class="text-body-2 text-medium-emphasis">
                            {{ startItem }} - {{ endItem }} من {{ totalItems }}
                        </span>
                    </div>
                    
                    <div class="mobile-controls">
                        <v-btn
                            icon="mdi-chevron-right"
                            size="small"
                            variant="outlined"
                            color="primary"
                            :disabled="page >= totalPages || loading"
                            @click="handlePageChange(page + 1)"
                        ></v-btn>
                        
                        <div class="mobile-page-info">
                            <span class="text-body-2 font-weight-medium">
                                {{ page }} / {{ totalPages }}
                            </span>
                        </div>
                        
                        <v-btn
                            icon="mdi-chevron-left"
                            size="small"
                            variant="outlined"
                            color="primary"
                            :disabled="page <= 1 || loading"
                            @click="handlePageChange(page - 1)"
                        ></v-btn>
                    </div>
                    
                    <div class="mobile-items-per-page">
                        <v-select 
                            :model-value="itemsPerPage"
                            :items="itemsPerPageOptions" 
                            label="عدد العناصر"
                            variant="outlined"
                            density="compact" 
                            hide-details 
                            class="mobile-items-select"
                            @update:model-value="handleItemsPerPageChange"
                            :disabled="loading"
                        ></v-select>
                    </div>
                </v-card-text>
            </div>
        </v-card>

        <!-- Floating Refresh Button -->
        <v-btn
            v-if="!loading"
            icon="mdi-refresh"
            color="primary"
            size="large"
            class="refresh-fab"
            elevation="8"
            @click="$emit('refresh')"
            :loading="loading"
        >
            <v-icon>mdi-refresh</v-icon>
            <v-tooltip content-class="custom-tooltip" color="black" activator="parent" location="top">
                <span>تحديث التصنيفات</span>
            </v-tooltip>
        </v-btn>

        <!-- Quick Stats Floating Card -->
        <!-- <v-card 
            v-if="!loading && categories.length > 0"
            class="quick-stats-fab"
            elevation="8"
        >
            <v-card-text class="quick-stats-content">
                <div class="quick-stat">
                    <v-icon size="16" color="primary">mdi-layers</v-icon>
                    <span class="stat-number">{{ totalItems }}</span>
                </div>
                <div class="quick-stat">
                    <v-icon size="16" color="success">mdi-check-circle</v-icon>
                    <span class="stat-number">{{ activeCount }}</span>
                </div>
                <div class="quick-stat">
                    <v-icon size="16" color="error">mdi-close-circle</v-icon>
                    <span class="stat-number">{{ inactiveCount }}</span>
                </div>
            </v-card-text>
            <v-tooltip content-class="custom-tooltip" color="black" activator="parent" location="top">
                <span>إجمالي: {{ totalItems }} | نشط: {{ activeCount }} | غير نشط: {{ inactiveCount }}</span>
            </v-tooltip>
        </v-card> -->
    </div>
</template>

<script>
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
            jumpToPage: null,
            itemsPerPageOptions: [6, 12, 24, 48, 96]
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
        
        getPaginationVisible() {
            // Responsive pagination visibility
            if (this.$vuetify.display.xs) return 3;
            if (this.$vuetify.display.sm) return 5;
            return 7;
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
        getDescription(category) {
            if (category.description && category.description.length > 80) {
                return category.description.substring(0, 80) + '...';
            }
            return category.description || 'لا يوجد وصف';
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

        getCategoryActivity(category) {
            // Calculate activity based on product count and status
            const baseActivity = category.active ? 50 : 10;
            const productBonus = Math.min((category.productCount || 0) * 5, 40);
            return Math.min(baseActivity + productBonus, 100);
        },

        getCategoryActivityColor(category) {
            const activity = this.getCategoryActivity(category);
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
/* Previous styles remain the same... */
/* Grid Container */
.category-grid {
    width: 100%;
    position: relative;
}

/* Category Card */
.category-card {
    background: white;
    border-radius: 16px;
    overflow: hidden;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    border: 1px solid #e2e8f0;
    height: 100%;
    display: flex;
    flex-direction: column;
}

.category-card:hover {
    transform: translateY(-8px);
    box-shadow: 0 12px 40px rgba(54, 96, 145, 0.15);
    border-color: #cbd5e0;
}

/* Icon Section */
.category-icon-section {
    position: relative;
    height: 160px;
    overflow: hidden;
}

.category-icon-container {
    width: 100%;
    height: 100%;
    position: relative;
}

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

/* Category Overlay */
.category-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(to bottom, rgba(0,0,0,0) 0%, rgba(0,0,0,0.2) 100%);
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    padding: 16px;
    opacity: 0;
    transition: opacity 0.3s ease;
}

.category-card:hover .category-overlay {
    opacity: 1;
}

.status-badge {
    align-self: flex-end;
}

.status-chip {
    backdrop-filter: blur(8px);
    background: rgba(255, 255, 255, 0.9) !important;
    font-weight: 600 !important;
    font-size: 11px !important;
}

.quick-actions {
    display: flex;
    gap: 8px;
    align-self: flex-end;
}

.action-btn {
    backdrop-filter: blur(8px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15) !important;
}

.favorite-btn:hover {
    color: #e53e3e !important;
}

/* Actions Menu */
.actions-menu {
    border-radius: 12px !important;
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15) !important;
    border: 1px solid #e2e8f0 !important;
}

.menu-item {
    border-radius: 8px !important;
    margin: 4px !important;
    transition: background-color 0.2s ease;
}

.menu-item:hover {
    background: rgba(54, 96, 145, 0.05) !important;
}

.menu-item.danger:hover {
    background: rgba(229, 62, 62, 0.05) !important;
}

/* Category Content */
.category-content {
    flex: 1;
    padding: 20px !important;
    display: flex;
    flex-direction: column;
    gap: 16px;
}

/* Category Title */
.category-title {
    font-size: 18px;
    font-weight: 600;
    color: #2d3748;
    line-height: 1.3;
    margin: 0;
    overflow: hidden;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    min-height: 48px;
}

/* Category Description */
.category-description {
    color: #718096;
    font-size: 13px;
    line-height: 1.5;
    margin: 0;
    overflow: hidden;
    display: -webkit-box;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
    min-height: 60px;
}

.category-description.placeholder {
    color: #a0aec0;
    font-style: italic;
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
.category-progress {
    margin-top: auto;
}

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

/* Category Actions */
.category-actions {
    padding: 16px 20px !important;
    background: #f8fafc;
    border-top: 1px solid #e2e8f0;
    gap: 8px;
}

.action-button {
    border-radius: 8px !important;
    font-weight: 500 !important;
    text-transform: none !important;
    font-size: 12px !important;
}

.delete-button {
    border-radius: 8px !important;
}

.delete-button:hover {
    background: rgba(229, 62, 62, 0.1) !important;
}

/* Loading State */
.loading-state {
    padding: 80px 20px;
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
    font-size: 18px;
}

.loading-subtitle {
    color: #718096;
    font-size: 14px;
    margin: 0;
}

/* Empty State */
.empty-state {
    padding: 80px 20px;
    text-align: center;
}

.empty-content {
    max-width: 400px;
    margin: 0 auto;
}

.empty-icon {
    margin-bottom: 24px;
}

.empty-title {
    color: #2d3748;
    font-weight: 600;
    margin-bottom: 8px;
    font-size: 20px;
}

.empty-subtitle {
    color: #718096;
    font-size: 14px;
    line-height: 1.5;
    margin: 0;
}

/* Enhanced Pagination Card */
.pagination-card {
    margin-top: 32px;
    border: 1px solid #e2e8f0;
    border-radius: 16px;
    background: white;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.pagination-content {
    display: flex;
    flex-direction: column;
    gap: 20px;
    padding: 24px !important;
}

/* Pagination Info Section */
.pagination-info {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.info-section {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.pagination-stats {
    display: flex;
    align-items: center;
    gap: 8px;
}

/* Pagination Controls */
.pagination-controls {
    display: flex;
    justify-content: center;
}

.pagination-controls .v-btn {
    color: white!important;;
}

.pagination-control {
    margin: 0;
}

/* Pagination Actions */
.pagination-actions {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 16px;
}

.items-per-page {
    display: flex;
    align-items: center;
    gap: 8px;
}

.items-select {
    border-radius: 8px;
}

.items-select .v-field {
    border-radius: 8px !important;
}

.quick-jump {
    display: flex;
    align-items: center;
    gap: 8px;
}

.jump-input {
    border-radius: 8px;
}

.jump-input .v-field {
    border-radius: 8px !important;
}

/* Mobile Pagination */
.mobile-pagination {
    border-top: 1px solid #e2e8f0;
    background: #f8fafc;
}

.mobile-pagination-content {
    display: flex;
    flex-direction: column;
    gap: 16px;
    padding: 20px !important;
}

.mobile-info {
    text-align: center;
}

.mobile-controls {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 16px;
}

.mobile-page-info {
    min-width: 60px;
    text-align: center;
}

.mobile-items-per-page {
    display: flex;
    justify-content: center;
}

.mobile-items-select {
    max-width: 120px;
}

.mobile-items-select .v-field {
    border-radius: 8px !important;
}

/* Floating Refresh Button */
.refresh-fab {
    position: fixed;
    bottom: 24px;
    right: 24px;
    z-index: 1000;
    background: linear-gradient(135deg, #366091 0%, #4299e1 100%) !important;
    color: white !important;
    border-radius: 16px !important;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.refresh-fab:hover {
    transform: translateY(-2px) scale(1.05);
    box-shadow: 0 8px 25px rgba(54, 96, 145, 0.3) !important;
}

/* Quick Stats Floating Card */
.quick-stats-fab {
    position: fixed;
    bottom: 96px;
    right: 24px;
    z-index: 999;
    background: white !important;
    border-radius: 12px !important;
    border: 1px solid #e2e8f0;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.quick-stats-fab:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15) !important;
}

.quick-stats-content {
    display: flex;
    flex-direction: column;
    gap: 8px;
    padding: 12px !important;
    min-width: 60px;
}

.quick-stat {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 12px;
    font-weight: 600;
}

.stat-number {
    color: #2d3748;
    font-weight: 700;
}

/* Responsive Design */
@media (max-width: 960px) {
    .pagination-actions {
        flex-direction: column;
        align-items: stretch;
        gap: 12px;
    }
    
    .items-per-page,
    .quick-jump {
        justify-content: center;
    }
    
    .info-section {
        flex-direction: column;
        gap: 8px;
        text-align: center;
    }
    
    .pagination-stats {
        justify-content: center;
    }
}

@media (max-width: 768px) {
    .category-icon-section {
        height: 120px;
    }
    
    .category-content {
        padding: 16px !important;
        gap: 12px;
    }
    
    .category-title {
        font-size: 16px;
        min-height: 40px;
    }
    
    .category-description {
        min-height: 45px;
        font-size: 12px;
    }
    
    .category-actions {
        padding: 12px 16px !important;
    }
    
    .action-button {
        font-size: 11px !important;
        padding: 0 12px !important;
    }
    
    .pagination-content {
        padding: 20px 16px !important;
        gap: 16px;
    }
    
    .pagination-actions {
        gap: 12px;
    }
    
    .quick-stats-fab {
        bottom: 80px;
        right: 16px;
    }
    
    .refresh-fab {
        bottom: 16px;
        right: 16px;
    }
}

@media (max-width: 600px) {
    .category-icon-section {
        height: 100px;
    }
    
    .category-title {
        font-size: 14px;
        min-height: 35px;
    }
    
    .category-stats {
        gap: 8px;
    }
    
    .stat-value {
        font-size: 14px;
    }
    
    .pagination-content {
        padding: 16px 12px !important;
        gap: 12px;
    }
    
    .quick-jump {
        display: none; /* Hide quick jump on very small screens */
    }
}

.pagination-control :deep(.v-pagination__item) {
    transition: all 0.2s ease;
    border-radius: 8px !important;
}

.pagination-control :deep(.v-pagination__item:hover) {
    transform: translateY(-1px);
}

.pagination-control :deep(.v-pagination__item--is-active) {
     background: #366091!important; /*linear-gradient(135deg, #366091 0%, #4299e1 100%) !important; */
     color: white;
    transform: scale(1.1);
    opacity: 1
}


/* Refresh Button Animation */
.refresh-fab:active {
    transform: translateY(-2px) scale(0.95);
}

.refresh-fab .v-icon {
    transition: transform 0.3s ease;
}

.refresh-fab:hover .v-icon {
    transform: rotate(180deg);
}

/* Print Styles */
@media print {
    .category-overlay,
    .category-actions,
    .pagination-card,
    .refresh-fab,
    .quick-stats-fab,
    .mobile-pagination {
        display: none !important;
    }
    
    .category-card {
        break-inside: avoid;
        box-shadow: none !important;
        border: 1px solid #000 !important;
        margin-bottom: 16px;
    }
    
    .category-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
        gap: 16px;
    }
}

/* Custom Tooltip Styling */
:global(.custom-tooltip) {
    background: #2d3748 !important;
    color: white !important;
    border-radius: 8px !important;
    font-size: 12px !important;
    padding: 8px 12px !important;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15) !important;
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

/* Enhanced Button Styling */
.v-btn {
    transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.v-btn:hover:not(.v-btn--disabled) {
    transform: translateY(-1px);
}

/* Text Selection */
::selection {
    background: rgba(54, 96, 145, 0.2);
    color: #2d3748;
}

/* Focus States */
.v-btn:focus-visible,
.v-text-field:focus-within,
.v-select:focus-within {
    outline: 2px solid rgba(54, 96, 145, 0.5);
    outline-offset: 2px;
}

/* Loading States */
.pagination-control :deep(.v-pagination__item--is-disabled) {
    opacity: 0.5;
    pointer-events: none;
}

/* Enhanced Scrollbar */
.pagination-card::-webkit-scrollbar {
    width: 6px;
}

.pagination-card::-webkit-scrollbar-track {
    background: #f1f5f9;
    border-radius: 3px;
}

.pagination-card::-webkit-scrollbar-thumb {
    background: linear-gradient(135deg, #366091 0%, #4299e1 100%);
    border-radius: 3px;
}

.pagination-card::-webkit-scrollbar-thumb:hover {
    background: linear-gradient(135deg, #2d4f73 0%, #3182ce 100%);
}

/* Accessibility Improvements */
.category-card:focus-visible {
    outline: 2px solid rgba(54, 96, 145, 0.8);
    outline-offset: 2px;
}

.pagination-control :deep(.v-pagination__item):focus-visible {
    outline: 2px solid rgba(54, 96, 145, 0.8);
    outline-offset: 2px;
}
</style>