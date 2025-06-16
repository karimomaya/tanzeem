<template>
    <v-app>
        <v-main class="modern-main">
            <!-- Clean Header Section -->
            <div class="modern-header">
                <v-container fluid class="px-6 py-6">
                    <div class="d-flex align-center justify-space-between">
                        <div>
                            <h1 class="modern-title">
                                {{ activeTab === 'orders' ? 'إدارة أوامر الشراء' : 'إدارة الموردين' }}
                            </h1>
                            <p class="modern-subtitle">
                                {{ activeTab === 'orders'
                                    ? 'مراقبة وإدارة جميع أوامر الشراء في النظام'
                                    : 'تنظيم وإدارة بيانات الموردين'
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
                    <PurchaseStats v-if="activeTab === 'orders'" :refresh="refreshStats"
                        @stats-refreshed="refreshStats = false" />
                    <SupplierStats v-else :refresh="refreshStats" @stats-refreshed="refreshStats = false" />
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
                                        <v-btn value="orders" class="toggle-btn">
                                            أوامر الشراء
                                        </v-btn>
                                        <v-btn value="suppliers" class="toggle-btn">
                                            الموردين
                                        </v-btn>
                                    </v-btn-toggle>
                                </v-col>

                                <!-- Search -->
                                <v-col>
                                    <AdvancedSearch :entity-type="activeTab"
                                        :placeholder="`البحث في ${activeTab === 'orders' ? 'أوامر الشراء' : 'الموردين'}...`"
                                        :status-options="statusOptions" :supplier-options="supplierOptionsForSearch"
                                        :business-type-options="businessTypeOptionsForSearch" :search-term="searchTerm"
                                        :search-fields="searchFieldsConfig" @update:search-term="searchTerm = $event"
                                        @apply-filters="handleAdvancedFilters" @clear-filters="handleClearFilters"
                                        @filter-change="handleFilterChange" style="max-width: 400px;" />
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

                                <!-- Date Range Filter (for orders) -->
                                <v-col cols="auto" v-if="activeTab === 'orders'">
                                    <v-menu v-model="dateMenu" :close-on-content-click="false">
                                        <template v-slot:activator="{ props }">
                                            <v-btn variant="outlined" v-bind="props" prepend-icon="mdi-calendar">
                                                تصفية بالتاريخ
                                            </v-btn>
                                        </template>
                                        <v-card>
                                            <v-card-text>
                                                <v-date-picker v-model="dateRange" range></v-date-picker>
                                            </v-card-text>
                                            <v-card-actions>
                                                <v-spacer></v-spacer>
                                                <v-btn @click="clearDateFilter">مسح</v-btn>
                                                <v-btn color="primary" @click="applyDateFilter">تطبيق</v-btn>
                                            </v-card-actions>
                                        </v-card>
                                    </v-menu>
                                </v-col>

                                <!-- View Toggle -->
                                <v-col cols="auto">
                                    <v-btn-toggle :model-value="activeTab === 'orders' ? viewMode : supplierViewMode"
                                        @update:model-value="activeTab === 'orders' ? viewMode = $event : supplierViewMode = $event"
                                        mandatory class="view-toggle">
                                        <v-btn value="grid" icon="mdi-view-grid" size="small"></v-btn>
                                        <v-btn value="list" icon="mdi-view-list" size="small"></v-btn>
                                    </v-btn-toggle>
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
                            <!-- Purchase Orders View -->
                            <v-window-item value="orders">
                                <div v-if="purchaseOrders.length === 0 && !loading" class="empty-state">
                                    <div class="empty-content">
                                        <v-icon size="80" color="grey-lighten-2">mdi-clipboard-list</v-icon>
                                        <h3 class="empty-title">لا توجد أوامر شراء</h3>
                                        <p class="empty-subtitle">ابدأ بإنشاء أمر الشراء الأول</p>
                                        <v-btn color="primary" class="modern-add-btn" prepend-icon="mdi-plus"
                                            @click="openAddDialog">
                                            إضافة أمر شراء
                                        </v-btn>
                                    </div>
                                </div>

                                <div v-else>
                                    <PurchaseOrderGrid v-if="viewMode === 'grid'" :items="purchaseOrders"
                                        :loading="loading" :page="orderPagination.page"
                                        :items-per-page="orderPagination.itemsPerPage" :sort-by="orderPagination.sortBy"
                                        :search-term="searchTerm" :status-filter="statusFilter"
                                        :total-items="orderPagination.totalItems" @edit="editPurchaseOrder"
                                        @delete="confirmDeletePurchaseOrder" @view="viewPurchaseOrder"
                                        @mark-received="markAsReceived" @update:page="updatePage"
                                        @update:items-per-page="updateItemsPerPage" @update:sort-by="updateSorting"
                                        @refresh="loadPurchaseOrders" />
                                    <PurchaseOrderList v-else :items="purchaseOrders" :loading="loading"
                                        :total-items="orderPagination.totalItems" :page="orderPagination.page"
                                        :items-per-page="orderPagination.itemsPerPage" :sort-by="orderPagination.sortBy"
                                        @edit="editPurchaseOrder" @delete="confirmDeletePurchaseOrder"
                                        @view="viewPurchaseOrder" @mark-received="markAsReceived"
                                        @duplicate="handleDuplicatePurchaseOrder" @add="openAddDialog"
                                        @update:page="updatePage" @update:items-per-page="updateItemsPerPage"
                                        @update:options="updateTableOptions" />
                                </div>
                            </v-window-item>

                            <!-- Suppliers View -->
                            <v-window-item value="suppliers">
                                <div v-if="suppliers.length === 0 && !loading" class="empty-state">
                                    <div class="empty-content">
                                        <v-icon size="80" color="grey-lighten-2">mdi-truck</v-icon>
                                        <h3 class="empty-title">لا توجد موردين</h3>
                                        <p class="empty-subtitle">ابدأ بإضافة مورد جديد</p>
                                        <v-btn color="primary" class="modern-add-btn" prepend-icon="mdi-plus"
                                            @click="openAddDialog">
                                            إضافة مورد
                                        </v-btn>
                                    </div>
                                </div>

                                <div v-else>
                                    <SupplierGrid v-if="supplierViewMode === 'grid'" :items="suppliers"
                                        :loading="loading" :page="supplierPagination.page"
                                        :items-per-page="supplierPagination.itemsPerPage"
                                        :total-items="supplierPagination.totalItems" :sort-by="supplierPagination.sortBy"
                                        :search-term="searchTerm" :status-filter="statusFilter" @edit="editSupplier"
                                        @delete="confirmDeleteSupplier" @update:page="updatePage"
                                        @update:items-per-page="updateItemsPerPage" @update:sort-by="updateSorting"
                                        @refresh="loadSuppliers" @toggle-status="updateSupplierStatus" />
                                    <SupplierList v-else :items="suppliers" :loading="loading"
                                        :page="supplierPagination.page" :items-per-page="supplierPagination.itemsPerPage"
                                        :total-items="supplierPagination.totalItems" :search-term="searchTerm"
                                        :status-filter="statusFilter" @add="openAddDialog" @edit="editSupplier"
                                        @delete="confirmDeleteSupplier" @update:page="updatePage"
                                        @update:items-per-page="updateItemsPerPage" @update:search-term="updateSearchTerm"
                                        @update:status-filter="updateStatusFilter" @update:sort-option="updateSortOption"
                                        @refresh="loadSuppliers" @toggle-status="updateSupplierStatus" />
                                </div>
                            </v-window-item>
                        </v-window>
                    </v-card>
                </v-container>
            </div>
        </v-main>

        <!-- Purchase Order Dialog -->
        <PurchaseOrderModal v-model="purchaseOrderDialog" :item-to-edit="selectedPurchaseOrder"
            @save="handlePurchaseOrderSave" />

        <!-- Supplier Dialog -->
        <SupplierModal v-model="supplierDialog" :item-to-edit="selectedSupplier" @save="handleSupplierSave" />

        <!-- Delete Confirmation Dialog -->
        <DeleteModal v-model="deleteDialog" ref="deleteModal" @delete-confirmed="handleDeleteConfirmed" />

        <!-- Purchase Order View Dialog -->
        <!-- <PurchaseOrderViewModal v-model="viewDialog" :purchase-order="selectedPurchaseOrder" /> -->
    </v-app>
</template>

<script>
import PurchaseOrderGrid from '@/components/purchase/PurchaseOrderGrid.vue';
import PurchaseOrderList from '@/components/purchase/PurchaseOrderList.vue';
import SupplierGrid from '@/components/purchase/SupplierGrid.vue';
import SupplierList from '@/components/purchase/SupplierList.vue';
import PurchaseOrderModal from '@/components/purchase/PurchaseOrderModal.vue';
import SupplierModal from '@/components/purchase/SupplierModal.vue';
import SupplierStats from '@/components/purchase/SupplierStats.vue';
// import PurchaseOrderViewModal from '@/components/purchase/PurchaseOrderViewModal.vue';
import DeleteModal from '@/components/layouts/DeleteModal.vue';
import PurchaseStats from '@/components/purchase/PurchaseStats.vue';
import { getPurchaseOrders, deletePurchaseOrder, getSuppliers, deleteSupplier, updateSupplier, markPurchaseOrderAsReceived } from '@/services/purchase-service';
import { success, error } from '@/utils/system-util';
import { formatCurrency } from '@/utils/currency-util';
import AdvancedSearch from '@/components/common/AdvancedSearch.vue';
import { STATUS_FILTER_OPTIONS } from '@/utils/status-util';

export default {
    name: 'PurchaseOrderPage',
    components: {
        PurchaseOrderGrid,
        PurchaseOrderList,
        SupplierGrid,
        SupplierList,
        PurchaseOrderModal,
        SupplierModal,
        // PurchaseOrderViewModal,
        DeleteModal,
        PurchaseStats,
        SupplierStats,
        AdvancedSearch
    },
    data() {
        return {
            refreshStats: false,
            activeTab: 'orders',
            searchTerm: '',
            statusFilter: 'all',
            viewMode: 'list',
            supplierViewMode: 'list',
            dateMenu: false,
            dateRange: null,
            purchaseOrderDialog: false,
            supplierDialog: false,
            deleteDialog: false,
            viewDialog: false,
            selectedPurchaseOrder: null,
            selectedSupplier: null,
            orderPagination: {
                page: 1,
                itemsPerPage: 10,
                totalItems: 0,
                sortBy: [],
                sortDesc: []
            },
            supplierPagination: {
                page: 1,
                itemsPerPage: 10,
                totalItems: 0,
                sortBy: [],
                sortDesc: []
            },

            purchaseOrders: [],
            suppliers: [],
            loading: false,
            totalOrders: 0,
            totalSuppliers: 0,
            activeFilters: {},
            supplierOptionsForSearch: [],
            businessTypeOptionsForSearch: [
                { title: 'تاجر تجزئة', value: 'RETAIL' },
                { title: 'تاجر جملة', value: 'WHOLESALE' },
                { title: 'مصنع', value: 'MANUFACTURER' },
                { title: 'موزع', value: 'DISTRIBUTOR' },
                { title: 'أخرى', value: 'OTHER' }
            ]
        };
    },

    computed: {
        addButtonLabel() {
            return this.activeTab === 'orders' ? 'إضافة أمر شراء' : 'إضافة مورد';
        },
        statusOptions() {
            return this.activeTab === 'orders' ? STATUS_FILTER_OPTIONS.order : STATUS_FILTER_OPTIONS.entity;
        },
        currentPagination() {
            return this.activeTab === 'orders' ? this.orderPagination : this.supplierPagination;
        },
        searchFieldsConfig() {
            if (this.activeTab === 'orders') {
                return {
                    text: true,
                    status: true,
                    category: false,
                    dateRange: true,
                    stock: false,
                    rating: false
                };
            } else { // suppliers
                return {
                    text: true,
                    status: true,
                    category: false,
                    dateRange: true,
                    stock: false,
                    rating: true
                };
            }
        }
    },

    created() {
        this.loadSuppliers();
        this.loadPurchaseOrders();
        this.loadSupplierOptionsForSearch();
    },

    watch: {
        activeTab(newTab) {
            this.statusFilter = 'all';
            this.searchTerm = '';
            if (newTab === 'suppliers') {
                this.loadSuppliers();
            } else if (newTab === 'orders') {
                this.loadPurchaseOrders();
            }
        },
        searchTerm() {
            clearTimeout(this.searchTimeout);
            this.searchTimeout = setTimeout(() => {
                if (this.activeTab === 'suppliers') {
                    this.supplierPagination.page = 1;
                    this.loadSuppliers();
                } else if (this.activeTab === 'orders') {
                    this.orderPagination.page = 1;
                    this.loadPurchaseOrders();
                }
            }, 500);
        },
        statusFilter() {
            if (this.activeTab === 'suppliers') {
                this.supplierPagination.page = 1;
                this.loadSuppliers();
            } else if (this.activeTab === 'orders') {
                this.orderPagination.page = 1;
                this.loadPurchaseOrders();
            }
        }
    },

    methods: {
        formatCurrency,
        handleAdvancedFilters(filters) {
            console.log('Advanced filters applied:', filters);
            this.activeFilters = filters;

            if (this.activeTab === 'orders') {
                this.applyOrderFilters(filters);
            } else {
                this.applySupplierFilters(filters);
            }
        },
        handleClearFilters() {
            console.log('Filters cleared');
            this.activeFilters = {};
            this.searchTerm = '';

            if (this.activeTab === 'orders') {
                this.loadPurchaseOrders();
            } else {
                this.loadSuppliers();
            }
        },
        handleFilterChange(filters) {
            if (filters.text !== undefined) {
                this.searchTerm = filters.text;
            }
        },
        applyOrderFilters(filters) {
            this.orderPagination.page = 1;
            this.loadPurchaseOrders();
        },
        handleDuplicatePurchaseOrder(duplicatedOrder) {
            this.selectedPurchaseOrder = duplicatedOrder;
            this.purchaseOrderDialog = true;
        },
        applySupplierFilters(filters) {
            this.supplierPagination.page = 1;
            this.loadSuppliers();
        },
        async loadSupplierOptionsForSearch() {
            try {
                const response = await getSuppliers(new URLSearchParams({ size: 100 }));
                if (response && response.content) {
                    this.supplierOptionsForSearch = response.content.map(supplier => ({
                        title: supplier.name,
                        value: supplier.id
                    }));
                }
            } catch (error) {
                console.error('Error loading suppliers for search:', error);
            }
        },
        async updateSupplierStatus(supplierData) {
            console.log(supplierData)
            let data = {
                ...supplierData,
                country: supplierData.country.code || supplierData.country,
                governorate: supplierData.governorate?.code || supplierData.governorate,
                businessType: supplierData.businessType?.code || supplierData.businessType,
                paymentTerm: supplierData.paymentTerm?.code || supplierData.paymentTerm,
            }
            let response = await updateSupplier(data);
            if (response != null && response.id != null) {
                success('تم تحديث المورد بنجاح');
                this.handleSupplierSave();
            } else {
                error('فشل تحديث المورد');
                console.error(response);
            }
        },

        async markAsReceived(purchaseOrder) {
            try {
                this.loading = true;
                const response = await markPurchaseOrderAsReceived(purchaseOrder.id);
                if (response && response.id) {
                    success('تم تحديث حالة أمر الشراء إلى "مستلم" وتم تحديث المخزون');
                    this.loadPurchaseOrders();
                    this.refreshStats = true;
                } else {
                    error('فشل في تحديث حالة أمر الشراء');
                }
            } catch (err) {
                console.error('Error marking purchase order as received:', err);
                error('فشل في تحديث حالة أمر الشراء');
            } finally {
                this.loading = false;
            }
        },

        updateTableOptions(options) {
            console.log('Table options updated:', options);

            if (this.activeTab === 'orders') {
                this.orderPagination.page = options.page || 1;
                this.orderPagination.itemsPerPage = options.itemsPerPage || 10;
                this.orderPagination.sortBy = options.sortBy || [];
                this.loadPurchaseOrders();
            } else {
                this.supplierPagination.page = options.page || 1;
                this.supplierPagination.itemsPerPage = options.itemsPerPage || 10;
                this.supplierPagination.sortBy = options.sortBy || [];
                this.loadSuppliers();
            }
        },

        async loadPurchaseOrders() {
            try {
                this.loading = true;
                let params = this.buildSearchParameter('orders');
                const response = await getPurchaseOrders(params);
                if (response && response.content) {
                    this.purchaseOrders = response.content;
                    this.orderPagination.totalItems = response.totalElements?? response.page.totalElements;
                    console.log(this.orderPagination.totalItems)
                    this.totalOrders = response.totalElements?? response.page.totalElements;
                } else {
                    console.warn('No data received from getPurchaseOrders');
                    this.purchaseOrders = [];
                    this.orderPagination.totalItems = 0;
                    this.totalOrders = 0;
                }
            } catch (err) {
                console.error('Error fetching purchase orders:', err);
                this.orderPagination.totalItems = 0;
                this.totalOrders = 0;
                error('فشل تحميل أوامر الشراء');
                this.purchaseOrders = [];
            } finally {
                this.loading = false;
            }
        },

        async loadSuppliers() {
            try {
                this.loading = true;
                let params = this.buildSearchParameter('suppliers');
                const response = await getSuppliers(params);
                if (response && response.content) {
                    this.suppliers = response.content;
                    console.log(response)
                    this.supplierPagination.totalItems = response.totalElements?? response.page.totalElements;
                    this.totalSuppliers = response.totalElements?? response.page.totalElements;
                } else {
                    console.warn('No data received from getSuppliers');
                    this.suppliers = [];
                    this.supplierPagination.totalItems = 0;
                    this.totalSuppliers = 0;
                }
            } catch (err) {
                console.error('Error fetching suppliers:', err);
                error('فشل تحميل الموردين');
                this.suppliers = [];
                this.supplierPagination.totalItems = 0;
                this.totalSuppliers = 0;
            } finally {
                this.loading = false;
            }
        },
        buildSearchParameter(type = null) {
            const pagination = type === 'orders' ? this.orderPagination : this.supplierPagination;

            const params = new URLSearchParams({
                page: pagination.page - 1,
                size: pagination.itemsPerPage
            });

            // Basic search
            if (this.searchTerm) {
                params.append('search', this.searchTerm);
            }

            // ✅ Add advanced filters
            if (this.activeFilters && Object.keys(this.activeFilters).length > 0) {
                // Status filter
                if (this.activeFilters.status) {
                    params.append('isActive', this.activeFilters.status);
                } else {
                    params.append('isActive', this.statusFilter);
                }

                // Order-specific filters
                if (type === 'orders') {
                    if (this.activeFilters.supplier) {
                        params.append('supplierId', this.activeFilters.supplier);
                    }
                    if (this.activeFilters.orderStatus) {
                        params.append('orderStatus', this.activeFilters.orderStatus);
                    }
                }

                // Supplier-specific filters
                if (type === 'suppliers') {
                    if (this.activeFilters.businessType) {
                        params.append('businessType', this.activeFilters.businessType);
                    }
                    if (this.activeFilters.ratingRange &&
                        (this.activeFilters.ratingRange[0] !== 0 || this.activeFilters.ratingRange[1] !== 100)) {
                        params.append('ratingFrom', this.activeFilters.ratingRange[0]);
                        params.append('ratingTo', this.activeFilters.ratingRange[1]);
                    }
                }

                // Date range filter
                if (this.activeFilters.dateRange && this.activeFilters.dateRange.length === 2) {
                    params.append('startDate', this.activeFilters.dateRange[0]);
                    params.append('endDate', this.activeFilters.dateRange[1]);
                }
            } else {
                // Fallback to basic status filter
                params.append('isActive', this.statusFilter);
            }

            // Add sorting
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
        updatePage(newPage) {
            if (this.activeTab === 'orders') {
                this.orderPagination.page = newPage;
                this.loadPurchaseOrders();
            } else {
                this.supplierPagination.page = newPage;
                this.loadSuppliers();
            }
        },

        updateItemsPerPage(newItemsPerPage) {
            if (this.activeTab === 'orders') {
                this.orderPagination.itemsPerPage = newItemsPerPage;
                this.orderPagination.page = 1;
                this.loadPurchaseOrders();
            } else {
                this.supplierPagination.itemsPerPage = newItemsPerPage;
                this.supplierPagination.page = 1;
                this.loadSuppliers();
            }
        },

        updateSorting(sortBy) {
            if (this.activeTab === 'orders') {
                this.orderPagination.sortBy = sortBy;
                this.orderPagination.page = 1;
                this.loadPurchaseOrders();
            } else {
                this.supplierPagination.sortBy = sortBy;
                this.supplierPagination.page = 1;
                this.loadSuppliers();
            }
        },

        clearDateFilter() {
            this.dateRange = null;
            this.dateMenu = false;
            this.orderPagination.page = 1;
            this.loadPurchaseOrders();
        },

        applyDateFilter() {
            this.dateMenu = false;
            this.orderPagination.page = 1;
            this.loadPurchaseOrders();
        },

        openAddDialog() {
            if (this.activeTab === 'orders') {
                this.selectedPurchaseOrder = null;
                this.purchaseOrderDialog = true;
            } else {
                this.selectedSupplier = null;
                this.supplierDialog = true;
            }
        },

        editPurchaseOrder(purchaseOrder) {
            this.selectedPurchaseOrder = { ...purchaseOrder };
            this.purchaseOrderDialog = true;
        },

        editSupplier(supplier) {
            this.selectedSupplier = { ...supplier };
            this.supplierDialog = true;
        },

        viewPurchaseOrder(purchaseOrder) {
            this.selectedPurchaseOrder = { ...purchaseOrder };
            this.viewDialog = true;
        },

        confirmDeletePurchaseOrder(purchaseOrder) {
            this.$refs.deleteModal.deleteConfirmation('purchaseOrder', purchaseOrder.id, purchaseOrder.orderNumber);
            this.deleteDialog = true;
        },

        confirmDeleteSupplier(supplier) {
            this.$refs.deleteModal.deleteConfirmation('supplier', supplier.id, supplier.name);
            this.deleteDialog = true;
        },

        handleDeleteConfirmed(item) {
            if (item.type === 'purchaseOrder') {
                this.deletePurchaseOrder(item.id);
            } else if (item.type === 'supplier') {
                this.deleteSupplier(item.id);
            }
            this.deleteDialog = false;
        },

        async deletePurchaseOrder(purchaseOrderId) {
            try {
                this.loading = true;
                const response = await deletePurchaseOrder(purchaseOrderId);
                await this.loadPurchaseOrders();
                success('تم حذف أمر الشراء بنجاح');
            } catch (err) {
                console.error('Error deleting purchase order:', err);
                error('فشل حذف أمر الشراء');
            } finally {
                this.loading = false;
            }
        },

        async deleteSupplier(supplierId) {
            try {
                this.loading = true;
                const response = await deleteSupplier(supplierId);
                await this.loadSuppliers();
                success('تم حذف المورد بنجاح');
            } catch (err) {
                console.error('Error deleting supplier:', err);
                error('فشل حذف المورد');
            } finally {
                this.loading = false;
            }
        },

        handlePurchaseOrderSave() {
            this.loadPurchaseOrders();
            this.refreshStats = true;
            this.purchaseOrderDialog = false;
        },

        handleSupplierSave() {
            this.loadSuppliers();
            this.refreshStats = true;
            this.supplierDialog = false;
        },

        updateSortOption(sortOption) {
            const [field, direction] = sortOption.split('-');
            if (this.activeTab === 'orders') {
                this.orderPagination.sortBy = [{ key: field, order: direction }];
                this.orderPagination.page = 1;
                this.loadPurchaseOrders();
            } else {
                this.supplierPagination.sortBy = [{ key: field, order: direction }];
                this.supplierPagination.page = 1;
                this.loadSuppliers();
            }
        },

        updateSearchTerm(term) {
            this.searchTerm = term;
        },

        updateStatusFilter(filter) {
            this.statusFilter = filter;
        }
    }
};
</script>

<style scoped>
@import '@/styles/product.css';
</style>