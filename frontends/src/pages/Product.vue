<template>
    <v-app>
        <v-main class="modern-main">
            <!-- Clean Header Section -->
            <div class="modern-header">
                <v-container fluid class="px-6 py-6">
                    <div class="d-flex align-center justify-space-between">
                        <div>
                            <h1 class="modern-title">
                                {{ activeTab === 'products' ? 'إدارة المنتجات' : 'إدارة التصنيفات' }}
                            </h1>
                            <p class="modern-subtitle">
                                {{ activeTab === 'products'
                                    ? 'مراقبة وإدارة جميع المنتجات في النظام'
                                    : 'تنظيم وإدارة تصنيفات المنتجات'
                                }}
                            </p>
                        </div>

                        <v-btn color="primary" size="large" class="modern-add-btn" prepend-icon="mdi-plus"
                            @click="openAddDialog">
                            {{ addButtonLabel }}
                        </v-btn>
                    </div>
                </v-container>
            </div>

            <!-- Stats Cards Section -->
            <div class="stats-section">
                <v-container fluid class="px-6">
                    <v-row class="mb-8">
                        <template v-if="activeTab === 'products'">
                            <v-col cols="12" sm="6" lg="3">
                                <div class="stat-card stat-card-blue">
                                    <div class="stat-icon">
                                        <v-icon color="white" size="24">mdi-chart-line</v-icon>
                                    </div>
                                    <div class="stat-content">
                                        <div class="stat-label">إجمالي المنتجات</div>
                                        <div class="stat-value">{{ productStats.total }}</div>
                                    </div>
                                </div>
                            </v-col>
                            <v-col cols="12" sm="6" lg="3">
                                <div class="stat-card stat-card-green">
                                    <div class="stat-icon">
                                        <v-icon color="white" size="24">mdi-map-marker</v-icon>
                                    </div>
                                    <div class="stat-content">
                                        <div class="stat-label">المنتجات النشطة</div>
                                        <div class="stat-value">{{ productStats.active }}</div>
                                    </div>
                                </div>
                            </v-col>
                            <v-col cols="12" sm="6" lg="3">
                                <div class="stat-card stat-card-purple">
                                    <div class="stat-icon">
                                        <v-icon color="white" size="24">mdi-account-multiple</v-icon>
                                    </div>
                                    <div class="stat-content">
                                        <div class="stat-label">مخزون منخفض</div>
                                        <div class="stat-value">{{ productStats.lowStock }}</div>
                                    </div>
                                </div>
                            </v-col>
                            <v-col cols="12" sm="6" lg="3">
                                <div class="stat-card stat-card-yellow">
                                    <div class="stat-icon">
                                        <v-icon color="white" size="24">mdi-currency-usd</v-icon>
                                    </div>
                                    <div class="stat-content">
                                        <div class="stat-label">نفد المخزون</div>
                                        <div class="stat-value">{{ productStats.outOfStock }}</div>
                                    </div>
                                </div>
                            </v-col>
                        </template>
                        <template v-else>
                            <v-col cols="12" sm="6" lg="3">
                                <div class="stat-card stat-card-blue">
                                    <div class="stat-icon">
                                        <v-icon color="white" size="24">mdi-chart-line</v-icon>
                                    </div>
                                    <div class="stat-content">
                                        <div class="stat-label">إجمالي التصنيفات</div>
                                        <div class="stat-value">{{ categoryStats.total }}</div>
                                    </div>
                                </div>
                            </v-col>
                            <v-col cols="12" sm="6" lg="3">
                                <div class="stat-card stat-card-green">
                                    <div class="stat-icon">
                                        <v-icon color="white" size="24">mdi-map-marker</v-icon>
                                    </div>
                                    <div class="stat-content">
                                        <div class="stat-label">التصنيفات النشطة</div>
                                        <div class="stat-value">{{ categoryStats.active }}</div>
                                    </div>
                                </div>
                            </v-col>
                            <v-col cols="12" sm="6" lg="3">
                                <div class="stat-card stat-card-purple">
                                    <div class="stat-icon">
                                        <v-icon color="white" size="24">mdi-account-multiple</v-icon>
                                    </div>
                                    <div class="stat-content">
                                        <div class="stat-label">غير نشطة</div>
                                        <div class="stat-value">{{ categoryStats.inactive }}</div>
                                    </div>
                                </div>
                            </v-col>
                            <v-col cols="12" sm="6" lg="3">
                                <div class="stat-card stat-card-yellow">
                                    <div class="stat-icon">
                                        <v-icon color="white" size="24">mdi-currency-usd</v-icon>
                                    </div>
                                    <div class="stat-content">
                                        <div class="stat-label">متوسط المنتجات</div>
                                        <div class="stat-value">12</div>
                                    </div>
                                </div>
                            </v-col>
                        </template>
                    </v-row>
                </v-container>
            </div>

            <!-- Controls Section -->
            <div class="controls-section">
                <v-container fluid class="px-6">
                    <v-row align="center" class="mb-6">
                        <!-- Left Side - Tabs and Search -->
                        <v-col cols="12" md="8">
                            <v-row align="center" no-gutters>
                                <!-- Tab Toggle -->
                                <v-col cols="auto" class="me-4">
                                    <v-btn-toggle v-model="activeTab" mandatory class="modern-toggle">
                                        <v-btn value="products" class="toggle-btn">
                                            المنتجات
                                        </v-btn>
                                        <v-btn value="categories" class="toggle-btn">
                                            التصنيفات
                                        </v-btn>
                                    </v-btn-toggle>
                                </v-col>

                                <!-- Search -->
                                <v-col>
                                    <v-text-field v-model="searchTerm"
                                        :placeholder="`البحث في ${activeTab === 'products' ? 'المنتجات' : 'التصنيفات'}...`"
                                        prepend-inner-icon="mdi-magnify" variant="outlined" density="compact" hide-details
                                        class="modern-search" style="max-width: 300px;"></v-text-field>
                                </v-col>
                            </v-row>
                        </v-col>

                        <!-- Right Side - Filters and Actions -->
                        <v-col cols="12" md="4">
                            <v-row align="center" justify="end" no-gutters class="ga-3">
                                <!-- Status Filter -->
                                <v-col cols="auto">
                                    <v-select v-model="statusFilter" :items="statusOptions" placeholder="تصفية بالحالة"
                                        variant="outlined" density="compact" hide-details class="modern-filter"
                                        style="min-width: 140px;"></v-select>
                                </v-col>

                                <!-- View Toggle -->
                                <v-col cols="auto">
                                    <v-btn-toggle :model-value="activeTab === 'products' ? viewMode : categoryViewMode"
                                        @update:model-value="activeTab === 'products' ? viewMode = $event : categoryViewMode = $event"
                                        mandatory class="view-toggle">
                                        <v-btn value="grid" icon="mdi-view-grid" size="small"></v-btn>
                                        <v-btn value="list" icon="mdi-view-list" size="small"></v-btn>
                                    </v-btn-toggle>
                                </v-col>

                                <!-- Export Button -->
                                <v-col cols="auto">
                                    <v-btn variant="outlined" class="export-btn" prepend-icon="mdi-download">
                                        تصدير البيانات
                                    </v-btn>
                                </v-col>
                            </v-row>
                        </v-col>
                    </v-row>
                </v-container>
            </div>

            <!-- Content Section -->
            <div class="content-section">
                <v-container fluid class="px-6">
                    <!-- Content Header -->
                    <!-- <div class="content-header">
                        <h2 class="content-title">
                            جميع {{ activeTab === 'products' ? 'المنتجات' : 'التصنيفات' }} 
                            ({{ activeTab === 'products' ? filteredProducts.length : categories.length }})
                        </h2>
                    </div> -->

                    <!-- Content Body -->
                    <v-card class="content-card" elevation="0">
                        <v-window v-model="activeTab">
                            <!-- Products View -->
                            <v-window-item value="products">
                                <div v-if="products.length === 0 && !loading" class="empty-state">
                                    <div class="empty-content">
                                        <v-icon size="80" color="grey-lighten-2">mdi-package-variant</v-icon>
                                        <h3 class="empty-title">لا توجد منتجات</h3>
                                        <p class="empty-subtitle">ابدأ بإنشاء منتجك الأول</p>
                                        <v-btn color="primary" class="modern-add-btn" prepend-icon="mdi-plus"
                                            @click="openAddDialog">
                                            إضافة منتج
                                        </v-btn>
                                    </div>
                                </div>

                                <div v-else>
                                    <ProductGrid v-if="viewMode === 'grid'" :products="products" :loading="loading"
                                        :page="page" :items-per-page="itemsPerPage" :sort-by="sortBy"
                                        :search-term="searchTerm" :status-filter="statusFilter" :total-items="totalProducts"
                                        @edit="editProduct" @delete="confirmDeleteProduct" @view="viewProduct"
                                        @update:page="updatePage" @update:items-per-page="updateItemsPerPage"
                                        @update:sort-by="updateSorting" @refresh="loadCategories"
                                        @toggle-status="updateCategoryStatus" />
                                    <ProductList v-else :products="products" @edit="editProduct"
                                        @delete="confirmDeleteProduct" @view="viewProduct" />
                                </div>
                            </v-window-item>

                            <!-- Categories View -->
                            <v-window-item value="categories">
                                <div v-if="categories.length === 0 && !loading" class="empty-state">
                                    <div class="empty-content">
                                        <v-icon size="80" color="grey-lighten-2">mdi-tag-multiple</v-icon>
                                        <h3 class="empty-title">لا توجد تصنيفات</h3>
                                        <p class="empty-subtitle">ابدأ بإنشاء تصنيفك الأول</p>
                                        <v-btn color="primary" class="modern-add-btn" prepend-icon="mdi-plus"
                                            @click="openAddDialog">
                                            إضافة تصنيف
                                        </v-btn>
                                    </div>
                                </div>

                                <div v-else>
                                    <CategoryGrid v-if="categoryViewMode === 'grid'" :categories="categories"
                                        :loading="loading" :page="page" :items-per-page="itemsPerPage"
                                        :total-items="totalCategories" :sort-by="sortBy" :search-term="searchTerm"
                                        :status-filter="statusFilter" @edit="editCategory" @delete="confirmDeleteCategory"
                                        @update:page="updatePage" @update:items-per-page="updateItemsPerPage"
                                        @update:sort-by="updateSorting" @refresh="loadCategories"
                                        @toggle-status="updateCategoryStatus" />
                                    <CategoryList v-else :categories="categories" :loading="loading" :page="page"
                                        :items-per-page="itemsPerPage" :total-items="totalCategories"
                                        :search-term="searchTerm" :status-filter="statusFilter"
                                        @add-category="openAddDialog" @edit-category="editCategory"
                                        @delete-confirmation="confirmDeleteCategory" @update:page="updatePage"
                                        @update:items-per-page="updateItemsPerPage" @update:search-term="updateSearchTerm"
                                        @update:status-filter="updateStatusFilter" @update:sort-option="updateSortOption"
                                        @refresh="loadCategories" @update:options="updateTableOptions"
                                        @toggle-status="updateCategoryStatus" />
                                </div>
                            </v-window-item>
                        </v-window>
                    </v-card>
                </v-container>
            </div>
        </v-main>

        <!-- Product Dialog -->
        <ProductModal v-model="productDialog" :product="selectedProduct" :categories="categories"
            @save="handleProductSave" />

        <!-- Category Dialog -->
        <CategoryModal v-model="categoryDialog" :category-to-edit="selectedCategory" @save="handleCategorySave" />

        <!-- Delete Confirmation Dialog -->
        <DeleteModal v-model="deleteDialog" ref="deleteModal" @delete-confirmed="handleDeleteConfirmed" />
    </v-app>
</template>

<script>
import ProductGrid from '@/components/product/ProductGrid.vue';
import ProductList from '@/components/product/ProductList.vue';
import CategoryGrid from '@/components/product/CategoryGrid.vue';
import ProductModal from '@/components/product/ProductModal.vue';
import CategoryModal from '@/components/product/CategoryModal.vue';
import DeleteModal from '@/components/layouts/DeleteModal.vue';
import StatsCard from '@/components/common/StatsCard.vue';
import { getCategories, deleteCategory, getProducts } from '@/utils/product-util';
import { success, error } from '@/utils/system-util';
import CategoryList from '@/components/product/CategoryList.vue';
import { updateCateogry } from '@/utils/product-util';

export default {
    name: 'ProductsPage',
    components: {
        ProductGrid,
        ProductList,
        CategoryGrid,
        CategoryList,
        ProductModal,
        CategoryModal,
        DeleteModal,
        StatsCard,

    },
    data() {
        return {
            activeTab: 'products',
            searchTerm: '',
            statusFilter: 'all',
            viewMode: 'grid',
            categoryViewMode: 'grid',

            productDialog: false,
            categoryDialog: false,
            deleteDialog: false,
            selectedProduct: null,
            selectedCategory: null,

            statusOptions: [
                { title: 'جميع الحالات', value: 'all' },
                { title: 'نشط', value: 'active' },
                { title: 'غير نشط', value: 'inactive' },
                { title: 'مخزون منخفض', value: 'low-stock' },
                { title: 'نفد المخزون', value: 'out-of-stock' }
            ],

            // Sample data
            products: [],

            categories: [],
            loading: false,
            page: 1,
            itemsPerPage: 12,
            totalCategories: 0,
            totalProducts: 0,
            sortBy: [],
            sortDesc: []
        };
    },

    computed: {
        addButtonLabel() {
            return this.activeTab === 'products' ? 'إضافة منتج' : 'إضافة تصنيف';
        },

        productStats() {
            return {
                total: this.totalProducts,
                active: this.products.filter(p => p.status === 'active').length,
                lowStock: this.products.filter(p => p.status === 'low-stock').length,
                outOfStock: this.products.filter(p => p.status === 'out-of-stock').length
            };
        },

        categoryStats() {
            return {
                total: this.totalCategories,
                active: this.categories.filter(c => c.active).length,
                inactive: this.categories.filter(c => !c.active).length
            };
        }
    },

    created() {
        // Load categories when component is created
        this.loadCategories();
    },

    watch: {
        activeTab(newTab) {
            if (newTab === 'categories') {
                this.loadCategories();
            }
        },
        searchTerm() {
            // Debounce search
            clearTimeout(this.searchTimeout);
            this.searchTimeout = setTimeout(() => {
                if (this.activeTab === 'categories') {
                    this.page = 1;
                    this.loadCategories();
                }
            }, 500);
        },

        statusFilter() {
            if (this.activeTab === 'categories') {
                this.page = 1;
                this.loadCategories();
            }
        }
    },

    methods: {

        async updateCategoryStatus(categoryData) {
            // Updating existing category
            let response = await updateCateogry(categoryData);
            if (response != null && response.id != null) {
                success('تم تحديث التصنيف بنجاح');
                this.handleCategorySave()
            } else {
                error('فشل تحديث التصنيف');
                console.error(response);
            }
        },

        updateTableOptions(options) {
            this.page = options.page || 1;
            this.itemsPerPage = options.itemsPerPage || 10;
            this.sortBy = options.sortBy || [];
            this.loadCategories();
        },

        async loadProducts() {
            try {
                this.loading = true;
                const response = await getProducts();
                if (response && response.content) {
                    this.products = response.content;

                    this.totalProducts = response.totalElements;
                } else {
                    console.warn('No data received from getProducts');
                    this.products = [];
                    this.totalProducts = 0;
                }
            } catch (err) {
                console.error('Error fetching products:', err);
                this.totalProducts = 0;
                error('فشل تحميل المنتجات');
                this.products = [];
            } finally {
                this.loading = false;
            }
        },

        buildSearchParameter() {
            const params = new URLSearchParams({
                page: this.page - 1, // Convert to 0-based index
                size: this.itemsPerPage
            });

            // Add search filter
            if (this.searchTerm) {
                params.append('search', this.searchTerm);
            }

            // Add status filter
            if (this.statusFilter !== 'all') {
                const isActive = this.statusFilter === 'active';
                params.append('active', isActive);
            }

            // Add sorting if available
            if (this.sortBy && this.sortBy.length > 0) {
                this.sortBy.forEach((sortItem) => {
                    let key, order;

                    if (typeof sortItem === 'string') {
                        key = sortItem;
                        order = this.sortDesc && this.sortDesc[this.sortBy.indexOf(sortItem)] ? 'desc' : 'asc';
                    } else if (typeof sortItem === 'object' && sortItem.key) {
                        key = sortItem.key;
                        order = sortItem.order === 'desc' ? 'desc' : 'asc';
                    } else {
                        return;
                    }

                    // Map frontend field names to backend field names
                    const fieldMapping = {
                        'name': 'name',
                        'active': 'active',
                        'productCount': 'name' // Fallback to name since productCount might not be sortable
                    };

                    const backendField = fieldMapping[key] || key;
                    params.append('sort', `${backendField},${order}`);
                });
            }
            return params;
        },

        async loadCategories() {
            try {
                this.loading = true;

                // Construct query parameters
                let params = this.buildSearchParameter();
                const response = await getCategories(params);

                if (response && response.content) {
                    this.categories = response.content;
                    this.totalCategories = response.totalElements;
                } else {
                    console.warn('No data received from getCategories');
                    this.categories = [];
                    this.totalCategories = 0;
                }
            } catch (err) {
                console.error('Error fetching categories:', err);
                error('فشل تحميل التصنيفات');
                this.categories = [];
                this.totalCategories = 0;
            } finally {
                this.loading = false;
            }
        },

        updatePage(newPage) {
            this.page = newPage;
            if (this.activeTab === 'products') {
                this.loadProducts();
            } else {
                this.loadCategories();
            }
        },

        updateItemsPerPage(newItemsPerPage) {
            this.itemsPerPage = newItemsPerPage;
            this.page = 1; // Reset to first page
            this.loadCategories();
        },

        updateSorting(sortBy) {
            this.sortBy = sortBy;
            this.page = 1; // Reset to first page when sorting changes
            this.loadCategories();
        },
        openAddDialog() {
            if (this.activeTab === 'products') {
                this.selectedProduct = null;
                this.productDialog = true;
            } else {
                this.selectedCategory = null;
                this.categoryDialog = true;
            }
        },

        editProduct(product) {
            this.selectedProduct = { ...product };
            this.productDialog = true;
        },

        editCategory(category) {
            this.selectedCategory = { ...category };
            this.categoryDialog = true;
        },

        viewProduct(product) {
            console.log('View product:', product);
            // Implement view product logic
        },

        confirmDeleteProduct(product) {
            this.$refs.deleteModal.deleteConfirmation('product', product.id, product.name);
            this.deleteDialog = true;
        },

        confirmDeleteCategory(category) {
            this.$refs.deleteModal.deleteConfirmation('category', category.id, category.name);
            this.deleteDialog = true;
        },

        handleDeleteConfirmed(item) {
            if (item.type === 'product') {
                this.deleteProduct(item.id);
            } else if (item.type === 'category') {
                this.deleteCategory(item.id);
            }
            this.deleteDialog = false;
        },

        deleteProduct(productId) {
            const index = this.products.findIndex(p => p.id === productId);
            if (index !== -1) {
                this.products.splice(index, 1);
            }
        },

        async deleteCategory(categoryId) {
            try {
                this.loading = true;
                const response = await deleteCategory(categoryId);
                await this.loadCategories();
                success('تم حذف التصنيف بنجاح');
            } catch (err) {
                console.error('Error deleting category:', err);
                error('فشل حذف التصنيف');
            } finally {
                this.loading = false;
            }
        },

        handleProductSave() {
            this.loadProducts();
            this.productDialog = false;
        },

        handleCategorySave() {
            // Refresh categories list after save
            this.loadCategories();
            this.categoryDialog = false;
        },
        updateSortOption(sortOption) {
            // Convert sort option to sortBy format
            const [field, direction] = sortOption.split('-');
            this.sortBy = [{ key: field, order: direction }];
            this.page = 1;
            this.loadCategories();
        },
    }
};
</script>

<style scoped>
/* Main Layout */
.modern-main {
    background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
    min-height: 100vh;
}

/* Header Section */
.modern-header {
    background: white;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.modern-title {
    font-size: 32px;
    font-weight: 700;
    color: #2d3748;
    margin-bottom: 8px;
}

.modern-subtitle {
    color: #718096;
    font-size: 16px;
    margin: 0;
}

.modern-add-btn {
    background: linear-gradient(135deg, #366091 0%, #4299e1 100%);
    color: white;
    font-weight: 600;
    text-transform: none;
    border-radius: 12px;
    box-shadow: 0 4px 15px rgba(54, 96, 145, 0.3);
    transition: all 0.3s ease;
}

.modern-add-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(54, 96, 145, 0.4);
}

/* Stats Section */
.stats-section {
    background: white;
    padding: 24px 0;
}

.stat-card {
    background: white;
    border-radius: 16px;
    padding: 24px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    display: flex;
    align-items: center;
    gap: 16px;
    transition: all 0.3s ease;
    border-left: 4px solid transparent;
}

.stat-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.stat-card-blue {
    border-left-color: #4299e1;
}

.stat-card-green {
    border-left-color: #48bb78;
}

.stat-card-purple {
    border-left-color: #9f7aea;
}

.stat-card-yellow {
    border-left-color: #ed8936;
}

.stat-icon {
    width: 48px;
    height: 48px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.stat-card-blue .stat-icon {
    background: linear-gradient(135deg, #4299e1 0%, #3182ce 100%);
}

.stat-card-green .stat-icon {
    background: linear-gradient(135deg, #48bb78 0%, #38a169 100%);
}

.stat-card-purple .stat-icon {
    background: linear-gradient(135deg, #9f7aea 0%, #805ad5 100%);
}

.stat-card-yellow .stat-icon {
    background: linear-gradient(135deg, #ed8936 0%, #dd6b20 100%);
}

.stat-content {
    flex: 1;
}

.stat-label {
    color: #718096;
    font-size: 14px;
    font-weight: 500;
    margin-bottom: 4px;
}

.stat-value {
    color: #2d3748;
    font-size: 28px;
    font-weight: 700;
    line-height: 1;
}

/* Controls Section */
.controls-section {
    background: white;
    padding: 20px 0;
    border-bottom: 1px solid #e2e8f0;
}

.modern-toggle {
    background: #f7fafc;
    border-radius: 12px;
    padding: 4px;
    border: 1px solid #e2e8f0;
}

.toggle-btn {
    border-radius: 8px !important;
    padding: 12px 24px !important;
    font-weight: 500 !important;
    text-transform: none !important;
    color: #718096 !important;
    background: transparent !important;
    transition: all 0.2s ease !important;
}

.toggle-btn.v-btn--active {
    background: white !important;
    color: #366091 !important;
    box-shadow: 0 2px 8px rgba(54, 96, 145, 0.15) !important;
}

.modern-search {
    border-radius: 12px;
}

.modern-search .v-field {
    border-radius: 12px !important;
}

.modern-search .v-field__outline {
    border-color: #e2e8f0 !important;
}

.modern-search .v-field--focused .v-field__outline {
    border-color: #366091 !important;
    border-width: 2px !important;
}

.modern-filter {
    border-radius: 12px;
}

.modern-filter .v-field {
    border-radius: 12px !important;
}

.modern-filter .v-field__outline {
    border-color: #e2e8f0 !important;
}

.modern-filter .v-field--focused .v-field__outline {
    border-color: #366091 !important;
    border-width: 2px !important;
}

.view-toggle {
    background: #f7fafc;
    border-radius: 12px;
    padding: 4px;
    border: 1px solid #e2e8f0;
}

.view-toggle .v-btn {
    border-radius: 8px !important;
    color: #718096 !important;
    background: transparent !important;
}

.view-toggle .v-btn.v-btn--active {
    background: white !important;
    color: #366091 !important;
    box-shadow: 0 2px 8px rgba(54, 96, 145, 0.15) !important;
}

.export-btn {
    border-radius: 12px !important;
    border-color: #e2e8f0 !important;
    color: #718096 !important;
    font-weight: 500 !important;
    text-transform: none !important;
    transition: all 0.2s ease !important;
}

.export-btn:hover {
    border-color: #366091 !important;
    color: #366091 !important;
    background: rgba(54, 96, 145, 0.05) !important;
}

/* Content Section */
.content-section {
    padding: 32px 0;
    background-color: white;
}

.content-header {
    margin-bottom: 24px;
}

.content-title {
    font-size: 20px;
    font-weight: 600;
    color: #2d3748;
    margin: 0;
}

.content-card {
    background: white !important;
    border-radius: 16px !important;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08) !important;
    border: 1px solid #e2e8f0 !important;
    overflow: hidden;
}

/* Empty State */
.empty-state {
    padding: 80px 40px;
    text-align: center;
    background: white;
}

.empty-content {
    max-width: 400px;
    margin: 0 auto;
}

.empty-title {
    font-size: 24px;
    font-weight: 600;
    color: #2d3748;
    margin: 24px 0 12px 0;
}

.empty-subtitle {
    color: #718096;
    font-size: 16px;
    margin-bottom: 32px;
}

/* Responsive Design */
@media (max-width: 768px) {
    .modern-title {
        font-size: 24px;
    }

    .modern-subtitle {
        font-size: 14px;
    }

    .stat-card {
        padding: 16px;
        gap: 12px;
    }

    .stat-icon {
        width: 40px;
        height: 40px;
    }

    .stat-value {
        font-size: 24px;
    }

    .controls-section .v-row {
        flex-direction: column;
        gap: 16px;
    }

    .modern-search {
        max-width: 100% !important;
    }

    .modern-filter {
        min-width: 100% !important;
    }

    .content-section {
        padding: 16px 0;
    }

    .empty-state {
        padding: 40px 20px;
    }
}

@media (max-width: 600px) {
    .modern-header .d-flex {
        flex-direction: column;
        align-items: flex-start !important;
        gap: 16px;
    }

    .modern-header .modern-add-btn {
        width: 100%;
        justify-content: center;
    }

    .stat-card {
        flex-direction: column;
        text-align: center;
    }

    .toggle-btn {
        padding: 8px 16px !important;
        font-size: 14px;
    }
}

/* Focus States for Accessibility */
.v-btn:focus-visible {
    outline: 2px solid rgba(54, 96, 145, 0.5);
    outline-offset: 2px;
}

.v-text-field:focus-within,
.v-select:focus-within {
    outline: 2px solid rgba(54, 96, 145, 0.3);
    outline-offset: 2px;
    border-radius: 12px;
}

/* Animation Classes */
.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
    opacity: 0;
}

.slide-enter-active,
.slide-leave-active {
    transition: transform 0.3s ease;
}

.slide-enter-from {
    transform: translateX(100%);
}

.slide-leave-to {
    transform: translateX(-100%);
}

/* Custom Scrollbar */
::-webkit-scrollbar {
    width: 8px;
    height: 8px;
}

::-webkit-scrollbar-track {
    background: #f1f5f9;
    border-radius: 4px;
}

::-webkit-scrollbar-thumb {
    background: linear-gradient(135deg, #366091 0%, #4299e1 100%);
    border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
    background: linear-gradient(135deg, #2d4f73 0%, #3182ce 100%);
}

/* Loading States */
.v-progress-circular {
    color: #366091 !important;
}

/* Table Enhancements (if using tables) */
.v-data-table {
    border-radius: 0 !important;
}

.v-data-table .v-data-table__tr:hover {
    background: rgba(54, 96, 145, 0.02) !important;
}

.v-data-table th {
    background: #f8fafc !important;
    font-weight: 600 !important;
    color: #2d3748 !important;
    border-bottom: 2px solid #e2e8f0 !important;
}

/* Pagination Styling */
.v-pagination .v-pagination__item--is-active {
    background: linear-gradient(135deg, #366091 0%, #4299e1 100%) !important;
    color: white !important;
}

.v-pagination .v-pagination__item {
    color: #718096 !important;
    border-radius: 8px !important;
}

.v-pagination .v-pagination__item:hover {
    background: rgba(54, 96, 145, 0.1) !important;
}

/* Chip Styling */
.v-chip {
    border-radius: 8px !important;
    font-weight: 500 !important;
}

/* Card Hover Effects */
.v-card {
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
}

.v-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12) !important;
}

/* Text Selection */
::selection {
    background: rgba(54, 96, 145, 0.2);
    color: #2d3748;
}

/* Print Styles */
@media print {

    .modern-header,
    .controls-section {
        break-inside: avoid;
    }

    .stat-card {
        break-inside: avoid;
        margin-bottom: 16px;
    }

    .content-card {
        box-shadow: none !important;
        border: 1px solid #e2e8f0 !important;
    }
}
</style>