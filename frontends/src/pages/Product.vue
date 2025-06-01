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
                                        :page="productPagination.page" :items-per-page="productPagination.itemsPerPage"
                                        :sort-by="productPagination.sortBy" :search-term="searchTerm"
                                        :status-filter="statusFilter" :total-items="productPagination.totalItems"
                                        @edit="editProduct" @delete="confirmDeleteProduct" @view="viewProduct"
                                        @update:page="updatePage" @update:items-per-page="updateItemsPerPage"
                                        @update:sort-by="updateSorting" @refresh="loadProducts" />
                                    <ProductList v-else :products="products" :loading="loading"
                                        :total-items="productPagination.totalItems" :page="productPagination.page"
                                        :items-per-page="productPagination.itemsPerPage" :sort-by="productPagination.sortBy"
                                        @edit="editProduct" @delete="confirmDeleteProduct" @view="viewProduct"
                                        @update:options="updateTableOptions" />
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
                                        :loading="loading" :page="categoryPagination.page"
                                        :items-per-page="categoryPagination.itemsPerPage"
                                        :total-items="categoryPagination.totalItems" :sort-by="categoryPagination.sortBy"
                                        :search-term="searchTerm" :status-filter="statusFilter" @edit="editCategory"
                                        @delete="confirmDeleteCategory" @update:page="updatePage"
                                        @update:items-per-page="updateItemsPerPage" @update:sort-by="updateSorting"
                                        @refresh="loadCategories" @toggle-status="updateCategoryStatus" />
                                    <CategoryList v-else :categories="categories" :loading="loading"
                                        :page="categoryPagination.page" :items-per-page="categoryPagination.itemsPerPage"
                                        :total-items="categoryPagination.totalItems" :search-term="searchTerm"
                                        :status-filter="statusFilter" @add-category="openAddDialog"
                                        @edit-category="editCategory" @delete-confirmation="confirmDeleteCategory"
                                        @update:page="updatePage" @update:items-per-page="updateItemsPerPage"
                                        @update:search-term="updateSearchTerm" @update:status-filter="updateStatusFilter"
                                        @update:sort-option="updateSortOption" @refresh="loadCategories"
                                        @update:options="updateTableOptions" @toggle-status="updateCategoryStatus" />
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
import { updateCategory } from '@/utils/product-util';

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
            statusFilter: 'active',
            viewMode: 'grid',
            categoryViewMode: 'grid',

            productDialog: false,
            categoryDialog: false,
            deleteDialog: false,
            selectedProduct: null,
            selectedCategory: null,

            statusOptions: [
                { title: 'نشط', value: 'active' },
                { title: 'غير نشط', value: 'inactive' }
            ],

            productPagination: {
                page: 1,
                itemsPerPage: 10,
                totalItems: 0,
                sortBy: [],
                sortDesc: []
            },
            categoryPagination: {
                page: 1,
                itemsPerPage: 10,
                totalItems: 0,
                sortBy: [],
                sortDesc: []
            },
            // Sample data
            products: [],

            categories: [],
            loading: false,

            totalCategories: 0,
            totalProducts: 0
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
        },
        currentPagination() {
            return this.activeTab === 'products' ? this.productPagination : this.categoryPagination;
        }

    },

    created() {
        // Load categories when component is created
        this.loadCategories();
        this.loadProducts();
    },

    watch: {
        activeTab(newTab) {
            if (newTab === 'categories') {
                this.loadCategories();
            } else if (newTab === 'products') {
                this.loadProducts();
            }
        },
        searchTerm() {
            // Debounce search
            clearTimeout(this.searchTimeout);
            this.searchTimeout = setTimeout(() => {
                if (this.activeTab === 'categories') {
                    this.categoryPagination.page = 1;
                    this.loadCategories();
                } else if (this.activeTab === 'products') {
                    this.productPagination.page = 1;
                    this.loadProducts();
                }
            }, 500);
        },

        statusFilter() {
            if (this.activeTab === 'categories') {
                this.categoryPagination.page = 1;
                this.loadCategories();
            } else if (this.activeTab === 'products') {
                this.productPagination.page = 1;
                this.loadProducts();
            }
        }
    },

    methods: {

        async updateCategoryStatus(categoryData) {
            // Updating existing category
            let response = await updateCategory(categoryData);
            if (response != null && response.id != null) {
                success('تم تحديث التصنيف بنجاح');
                this.handleCategorySave()
            } else {
                error('فشل تحديث التصنيف');
                console.error(response);
            }
        },

        updateTableOptions(options) {
            console.log('Table options updated:', options); // Debug log

            if (this.activeTab === 'products') {
                this.productPagination.page = options.page || 1;
                this.productPagination.itemsPerPage = options.itemsPerPage || 12;
                this.productPagination.sortBy = options.sortBy || [];
                this.loadProducts();
            } else {
                this.categoryPagination.page = options.page || 1;
                this.categoryPagination.itemsPerPage = options.itemsPerPage || 10;
                this.categoryPagination.sortBy = options.sortBy || [];
                this.loadCategories();
            }
        },

        async loadProducts() {
            try {
                this.loading = true;
                let params = this.buildSearchParameter('products');
                const response = await getProducts(params);
                if (response && response.content) {
                    this.products = response.content;
                    this.productPagination.totalItems = response.totalElements;
                    this.totalProducts = response.totalElements; // Backward compatibility
                } else {
                    console.warn('No data received from getProducts');
                    this.products = [];
                    this.productPagination.totalItems = 0;
                    this.totalProducts = 0;
                }
            } catch (err) {
                console.error('Error fetching products:', err);
                this.productPagination.totalItems = 0;
                this.totalProducts = 0;
                error('فشل تحميل المنتجات');
                this.products = [];
            } finally {
                this.loading = false;
            }
        },

        buildSearchParameter(type = null) {
            const pagination = type === 'products' ? this.productPagination : this.categoryPagination;

            const params = new URLSearchParams({
                page: pagination.page - 1, // Convert to 0-based index
                size: pagination.itemsPerPage
            });

            // Add search filter
            if (this.searchTerm) {
                params.append('search', this.searchTerm);
            }

            // Add status filter
            if (this.statusFilter !== 'all') {
                const isActive = this.statusFilter === 'active';
                params.append('isActive', isActive);
            }

            // Add sorting if available
            if (pagination.sortBy && pagination.sortBy.length > 0) {
                pagination.sortBy.forEach((sortItem) => {
                    let key, order;

                    if (typeof sortItem === 'string') {
                        key = sortItem;
                        order = pagination.sortDesc && pagination.sortDesc[pagination.sortBy.indexOf(sortItem)] ? 'desc' : 'asc';
                    } else if (typeof sortItem === 'object' && sortItem.key) {
                        key = sortItem.key;
                        order = sortItem.order === 'desc' ? 'desc' : 'asc';
                    } else {
                        return;
                    }

                    
                    params.append('sort', `${key},${order}`);
                });
            }
            return params;
        },

        async loadCategories() {
            try {
                this.loading = true;

                // Construct query parameters
                let params = this.buildSearchParameter('categories');
                const response = await getCategories(params);
                if (response && response.content) {
                    this.categories = response.content;
                    this.categoryPagination.totalItems = response.totalElements;
                    this.totalCategories = response.totalElements; // Backward compatibility
                } else {
                    console.warn('No data received from getCategories');
                    this.categories = [];
                    this.categoryPagination.totalItems = 0;
                    this.totalCategories = 0;
                }
            } catch (err) {
                console.error('Error fetching categories:', err);
                error('فشل تحميل التصنيفات');
                this.categories = [];
                this.categoryPagination.totalItems = 0;
                this.totalCategories = 0;
            } finally {
                this.loading = false;
            }
        },

        updatePage(newPage) {
            if (this.activeTab === 'products') {
                this.productPagination.page = newPage;
                this.loadProducts();
            } else {
                this.categoryPagination.page = newPage;
                this.loadCategories();
            }
        },

        updateItemsPerPage(newItemsPerPage) {
            if (this.activeTab === 'products') {
                this.productPagination.itemsPerPage = newItemsPerPage;
                this.productPagination.page = 1; // Reset to first page
                this.loadProducts();
            } else {
                this.categoryPagination.itemsPerPage = newItemsPerPage;
                this.categoryPagination.page = 1; // Reset to first page
                this.loadCategories();
            }
        },

        updateSorting(sortBy) {
            if (this.activeTab === 'products') {
                this.productPagination.sortBy = sortBy;
                this.productPagination.page = 1; // Reset to first page when sorting changes
                this.loadProducts();
            } else {
                this.categoryPagination.sortBy = sortBy;
                this.categoryPagination.page = 1; // Reset to first page when sorting changes
                this.loadCategories();
            }
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
            if (this.activeTab === 'products') {
                this.productPagination.sortBy = [{ key: field, order: direction }];
                this.productPagination.page = 1;
                this.loadProducts();
            } else {
                this.categoryPagination.sortBy = [{ key: field, order: direction }];
                this.categoryPagination.page = 1;
                this.loadCategories();
            }
        },
    }
};
</script>

<style scoped>
@import '@/styles/product.css';
</style>