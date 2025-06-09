<template>
    <div class="advanced-search-wrapper">
      <!-- Main Search Input -->
      <v-text-field
        v-model="localSearchTerm"
        :placeholder="placeholder"
        prepend-inner-icon="mdi-magnify"
        :append-inner-icon="searchAppendIcon"
        variant="outlined"
        density="compact"
        hide-details
        class="search-field"
        clearable
        @click:append-inner="toggleAdvanced"
        @input="onSearchInput"
        @keydown.enter="handleQuickSearch"
      >
        <!-- Active Filters Badge -->
        <template v-if="hasActiveFilters" v-slot:append>
          <v-badge 
            :content="activeFiltersCount" 
            color="primary" 
            overlap
            offset-x="8"
            offset-y="8"
          >
            <v-icon size="20">mdi-filter</v-icon>
          </v-badge>
        </template>
      </v-text-field>
  
      <!-- Advanced Search Menu -->
      <v-menu 
        v-model="showAdvanced" 
        :close-on-content-click="false" 
        location="bottom start" 
        offset="10"
        max-width="600"
      >
        <template v-slot:activator="{ props }">
          <div v-bind="props" style="display: none;"></div>
        </template>
        
        <v-card class="advanced-search-card" elevation="8">
          <v-card-title class="d-flex align-center pa-4">
            <v-icon class="me-3" color="primary">mdi-filter-cog</v-icon>
            <span>بحث متقدم</span>
            <v-spacer />
            <v-btn 
              icon="mdi-close" 
              variant="text" 
              size="small"
              @click="showAdvanced = false"
            />
          </v-card-title>
          
          <v-divider />
          
          <v-card-text class="pa-4">
            <v-row dense>
              <!-- Basic Text Search -->
              <v-col cols="12" v-if="searchFields.text">
                <v-text-field 
                  label="البحث النصي" 
                  v-model="filters.text"
                  prepend-inner-icon="mdi-text-search"
                  variant="outlined"
                  density="compact"
                  hint="البحث في جميع الحقول النصية"
                  persistent-hint
                  clearable
                />
              </v-col>
              
              <!-- Status Filter -->
              <v-col cols="6" v-if="searchFields.status && statusOptions.length > 0">
                <v-select 
                  label="الحالة" 
                  :items="statusOptions"
                  v-model="filters.status"
                  prepend-inner-icon="mdi-toggle-switch"
                  variant="outlined"
                  density="compact"
                  clearable
                  hint="تصفية حسب الحالة"
                  persistent-hint
                />
              </v-col>
              
              <!-- Category Filter -->
              <v-col cols="6" v-if="searchFields.category && categoryOptions.length > 0">
                <v-select 
                  label="التصنيف" 
                  :items="categoryOptions"
                  v-model="filters.category"
                  prepend-inner-icon="mdi-tag"
                  variant="outlined"
                  density="compact"
                  clearable
                  hint="تصفية حسب التصنيف"
                  persistent-hint
                />
              </v-col>
              
              <!-- Price Range (Products) -->
              <template v-if="entityType === 'products'">
                <v-col cols="6">
                  <v-text-field 
                    label="السعر من" 
                    type="number" 
                    v-model.number="filters.priceFrom"
                    prepend-inner-icon="mdi-currency-usd"
                    variant="outlined"
                    density="compact"
                    hint="الحد الأدنى للسعر"
                    persistent-hint
                    clearable
                  />
                </v-col>
                <v-col cols="6">
                  <v-text-field 
                    label="السعر إلى" 
                    type="number" 
                    v-model.number="filters.priceTo"
                    prepend-inner-icon="mdi-currency-usd"
                    variant="outlined"
                    density="compact"
                    hint="الحد الأقصى للسعر"
                    persistent-hint
                    clearable
                  />
                </v-col>
              </template>
  
              <!-- Stock Range (Products) -->
              <template v-if="entityType === 'products' && searchFields.stock">
                <v-col cols="6">
                  <v-text-field 
                    label="المخزون من" 
                    type="number" 
                    v-model.number="filters.stockFrom"
                    prepend-inner-icon="mdi-package-variant"
                    variant="outlined"
                    density="compact"
                    clearable
                  />
                </v-col>
                <v-col cols="6">
                  <v-text-field 
                    label="المخزون إلى" 
                    type="number" 
                    v-model.number="filters.stockTo"
                    prepend-inner-icon="mdi-package-variant"
                    variant="outlined"
                    density="compact"
                    clearable
                  />
                </v-col>
              </template>
              
              <!-- Date Range (Orders/General) -->
              <v-col cols="12" v-if="searchFields.dateRange">
                <v-text-field
                  :label="dateRangeLabel"
                  v-model="dateRangeText"
                  prepend-inner-icon="mdi-calendar-range"
                  append-inner-icon="mdi-calendar"
                  variant="outlined"
                  density="compact"
                  readonly
                  clearable
                  @click="showDatePicker = true"
                  @click:append-inner="showDatePicker = true"
                  @click:clear="clearDateRange"
                  hint="اختر نطاق تاريخي للتصفية"
                  persistent-hint
                />
              </v-col>
  
              <!-- Supplier Filter (Orders) -->
              <v-col cols="6" v-if="entityType === 'orders' && supplierOptions.length > 0">
                <v-select 
                  label="المورد" 
                  :items="supplierOptions"
                  v-model="filters.supplier"
                  prepend-inner-icon="mdi-truck"
                  variant="outlined"
                  density="compact"
                  clearable
                />
              </v-col>
  
              <!-- Order Status (Orders) -->
              <v-col cols="6" v-if="entityType === 'orders'">
                <v-select 
                  label="حالة الأمر" 
                  :items="orderStatusOptions"
                  v-model="filters.orderStatus"
                  prepend-inner-icon="mdi-clipboard-check"
                  variant="outlined"
                  density="compact"
                  clearable
                />
              </v-col>
  
              <!-- Business Type (Suppliers) -->
              <v-col cols="6" v-if="entityType === 'suppliers' && businessTypeOptions.length > 0">
                <v-select 
                  label="نوع النشاط" 
                  :items="businessTypeOptions"
                  v-model="filters.businessType"
                  prepend-inner-icon="mdi-briefcase"
                  variant="outlined"
                  density="compact"
                  clearable
                />
              </v-col>
  
              <!-- Rating Range (Suppliers) -->
              <template v-if="entityType === 'suppliers' && searchFields.rating">
                <v-col cols="12">
                  <v-label class="text-body-2 mb-2">نطاق التقييم</v-label>
                  <v-range-slider
                    v-model="filters.ratingRange"
                    :min="0"
                    :max="100"
                    :step="5"
                    thumb-label="always"
                    class="mt-4"
                  >
                    <template v-slot:thumb-label="{ modelValue }">
                      {{ modelValue }}%
                    </template>
                  </v-range-slider>
                </v-col>
              </template>
            </v-row>
  
            <!-- Quick Filter Tags -->
            <div v-if="quickFilters.length > 0" class="mt-4">
              <v-label class="text-body-2 mb-2">تصفية سريعة</v-label>
              <div class="d-flex flex-wrap ga-2">
                <v-chip
                  v-for="quickFilter in quickFilters"
                  :key="quickFilter.key"
                  :color="isQuickFilterActive(quickFilter) ? 'primary' : 'default'"
                  :variant="isQuickFilterActive(quickFilter) ? 'flat' : 'outlined'"
                  size="small"
                  clickable
                  @click="toggleQuickFilter(quickFilter)"
                >
                  <v-icon start size="16">{{ quickFilter.icon }}</v-icon>
                  {{ quickFilter.label }}
                </v-chip>
              </div>
            </div>
          </v-card-text>
          
          <v-divider />
          
          <v-card-actions class="pa-4">
            <v-btn 
              variant="text" 
              color="warning"
              prepend-icon="mdi-filter-remove"
              @click="clearAllFilters"
              :disabled="!hasActiveFilters"
            >
              مسح الكل
            </v-btn>
            
            <v-spacer />
            
            <v-btn 
              variant="text" 
              @click="showAdvanced = false"
            >
              إلغاء
            </v-btn>
            
            <v-btn 
              color="primary" 
              prepend-icon="mdi-magnify"
              @click="applyFilters"
            >
              تطبيق البحث
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-menu>
  
      <!-- Date Picker Dialog -->
      <v-dialog v-model="showDatePicker" max-width="400">
        <v-card>
          <v-card-title class="d-flex align-center">
            <v-icon class="me-2">mdi-calendar-range</v-icon>
            {{ dateRangeLabel }}
          </v-card-title>
          
          <v-card-text>
            <v-date-picker 
              v-model="filters.dateRange" 
              range 
              show-adjacent-months
              :locale="'ar'"
              elevation="0"
            />
          </v-card-text>
          
          <v-card-actions>
            <v-btn variant="text" @click="clearDateRange">مسح</v-btn>
            <v-spacer />
            <v-btn variant="text" @click="showDatePicker = false">إلغاء</v-btn>
            <v-btn color="primary" @click="confirmDateRange">تأكيد</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
  
      <!-- Active Filters Display -->
      <v-expand-transition>
        <div v-if="hasActiveFilters && showActiveFilters" class="active-filters mt-2">
          <div class="d-flex align-center flex-wrap ga-1">
            <span class="text-caption text-medium-emphasis me-2">المرشحات النشطة:</span>
            
            <v-chip
              v-for="filter in activeFilterChips"
              :key="filter.key"
              size="small"
              closable
              variant="outlined"
              color="primary"
              @click:close="removeFilter(filter.key)"
            >
              <v-icon start size="12">{{ filter.icon }}</v-icon>
              {{ filter.label }}
            </v-chip>
            
            <v-btn
              size="x-small"
              variant="text"
              color="warning"
              @click="clearAllFilters"
            >
              مسح الكل
            </v-btn>
          </div>
        </div>
      </v-expand-transition>
    </div>
  </template>
  
  <script>
  export default {
    name: 'AdvancedSearch',
    props: {
      // Basic props
      placeholder: {
        type: String,
        default: 'البحث...'
      },
      entityType: {
        type: String,
        required: true,
        validator: value => ['products', 'categories', 'orders', 'suppliers'].includes(value)
      },
      searchTerm: {
        type: String,
        default: ''
      },
  
      // Options for dropdowns
      statusOptions: {
        type: Array,
        default: () => []
      },
      categoryOptions: {
        type: Array,
        default: () => []
      },
      supplierOptions: {
        type: Array,
        default: () => []
      },
      businessTypeOptions: {
        type: Array,
        default: () => []
      },
  
      // Field visibility control
      searchFields: {
        type: Object,
        default: () => ({
          text: true,
          status: true,
          category: true,
          dateRange: true,
          stock: true,
          rating: true
        })
      },
  
      // UI control
      showActiveFilters: {
        type: Boolean,
        default: true
      },
  
      // Initial filters
      initialFilters: {
        type: Object,
        default: () => ({})
      }
    },
    
    emits: [
      'update:search-term', 
      'apply-filters', 
      'clear-filters',
      'filter-change'
    ],
    
    data() {
      return {
        localSearchTerm: this.searchTerm,
        showAdvanced: false,
        showDatePicker: false,
        filters: this.initializeFilters(),
        
        // Debounce timer
        searchTimeout: null
      };
    },
    
    computed: {
      searchAppendIcon() {
        return this.hasActiveFilters ? 'mdi-filter' : 'mdi-filter-variant';
      },
  
      hasActiveFilters() {
        return Object.entries(this.filters).some(([key, value]) => {
          if (key === 'text') return false; // Text search is separate
          return this.isFilterActive(value);
        });
      },
  
      activeFiltersCount() {
        return Object.entries(this.filters).filter(([key, value]) => {
          if (key === 'text') return false;
          return this.isFilterActive(value);
        }).length;
      },
  
      dateRangeText() {
        if (!this.filters.dateRange || this.filters.dateRange.length !== 2) {
          return '';
        }
        const [start, end] = this.filters.dateRange;
        return `${this.formatDisplayDate(start)} - ${this.formatDisplayDate(end)}`;
      },
  
      dateRangeLabel() {
        switch (this.entityType) {
          case 'orders':
            return 'نطاق تاريخ الأمر';
          case 'products':
            return 'نطاق تاريخ الإنشاء';
          case 'suppliers':
            return 'نطاق تاريخ التسجيل';
          default:
            return 'نطاق التاريخ';
        }
      },
  
      orderStatusOptions() {
        return [
          { title: 'في الانتظار', value: 'PENDING' },
          { title: 'مؤكد', value: 'CONFIRMED' },
          { title: 'تم التسليم', value: 'DELIVERED' },
          { title: 'مكتمل', value: 'COMPLETED' },
          { title: 'ملغي', value: 'CANCELLED' }
        ];
      },
  
      quickFilters() {
        const filters = [];
        
        if (this.entityType === 'products') {
          filters.push(
            { key: 'lowStock', label: 'مخزون منخفض', icon: 'mdi-alert', filter: { stockLow: true } },
            { key: 'outOfStock', label: 'نفد المخزون', icon: 'mdi-alert-circle', filter: { stockTo: 0 } },
            { key: 'highPrice', label: 'عالي السعر', icon: 'mdi-trending-up', filter: { priceHigh: true } }
          );
        } else if (this.entityType === 'orders') {
          filters.push(
            { key: 'pending', label: 'معلقة', icon: 'mdi-clock', filter: { orderStatus: 'PENDING' } },
            { key: 'thisMonth', label: 'هذا الشهر', icon: 'mdi-calendar-month', filter: { dateThisMonth: true } }
          );
        } else if (this.entityType === 'suppliers') {
          filters.push(
            { key: 'topRated', label: 'تقييم عالي', icon: 'mdi-star', filter: { ratingRange: [80, 100] } },
            { key: 'newSuppliers', label: 'موردين جدد', icon: 'mdi-new-box', filter: { dateRecent: true } }
          );
        }
        
        return filters;
      },
  
      activeFilterChips() {
        const chips = [];
        
        Object.entries(this.filters).forEach(([key, value]) => {
          if (key === 'text' || !this.isFilterActive(value)) return;
          
          chips.push(this.createFilterChip(key, value));
        });
        
        return chips.filter(Boolean);
      }
    },
  
    methods: {
      initializeFilters() {
        const base = {
          text: '',
          status: null,
          category: null,
          dateRange: null,
          ...this.initialFilters
        };
        
        // Add entity-specific filters
        if (this.entityType === 'products') {
          return { 
            ...base, 
            priceFrom: null, 
            priceTo: null,
            stockFrom: null,
            stockTo: null
          };
        }
        
        if (this.entityType === 'orders') {
          return { 
            ...base, 
            supplier: null,
            orderStatus: null
          };
        }
        
        if (this.entityType === 'suppliers') {
          return { 
            ...base, 
            businessType: null,
            ratingRange: [0, 100]
          };
        }
        
        return base;
      },
  
      isFilterActive(value) {
        if (value === null || value === undefined || value === '') return false;
        if (Array.isArray(value)) {
          if (value.length === 0) return false;
          // For rating range, check if it's different from default [0, 100]
          if (value.length === 2 && value[0] === 0 && value[1] === 100) return false;
          return true;
        }
        return true;
      },
  
      createFilterChip(key, value) {
        const chipConfig = {
          status: { icon: 'mdi-toggle-switch', label: this.getStatusLabel(value) },
          category: { icon: 'mdi-tag', label: this.getCategoryLabel(value) },
          priceFrom: { icon: 'mdi-currency-usd', label: `من ${value}` },
          priceTo: { icon: 'mdi-currency-usd', label: `إلى ${value}` },
          stockFrom: { icon: 'mdi-package-variant', label: `مخزون من ${value}` },
          stockTo: { icon: 'mdi-package-variant', label: `مخزون إلى ${value}` },
          dateRange: { icon: 'mdi-calendar-range', label: 'نطاق تاريخي' },
          supplier: { icon: 'mdi-truck', label: this.getSupplierLabel(value) },
          orderStatus: { icon: 'mdi-clipboard-check', label: this.getOrderStatusLabel(value) },
          businessType: { icon: 'mdi-briefcase', label: this.getBusinessTypeLabel(value) },
          ratingRange: { icon: 'mdi-star', label: `تقييم ${value[0]}% - ${value[1]}%` }
        };
  
        return chipConfig[key] ? { key, ...chipConfig[key] } : null;
      },
  
      getStatusLabel(value) {
        const option = this.statusOptions.find(opt => opt.value === value);
        return option ? option.title : value;
      },
  
      getCategoryLabel(value) {
        const option = this.categoryOptions.find(opt => opt.value === value);
        return option ? option.title : value;
      },
  
      getSupplierLabel(value) {
        const option = this.supplierOptions.find(opt => opt.value === value);
        return option ? option.title : value;
      },
  
      getOrderStatusLabel(value) {
        const option = this.orderStatusOptions.find(opt => opt.value === value);
        return option ? option.title : value;
      },
  
      getBusinessTypeLabel(value) {
        const option = this.businessTypeOptions.find(opt => opt.value === value);
        return option ? option.title : value;
      },
  
      toggleAdvanced() {
        this.showAdvanced = !this.showAdvanced;
      },
  
      onSearchInput() {
        // Clear timeout if user is still typing
        clearTimeout(this.searchTimeout);
        
        // Debounce search input
        this.searchTimeout = setTimeout(() => {
          this.$emit('update:search-term', this.localSearchTerm);
          this.$emit('filter-change', { text: this.localSearchTerm });
        }, 300);
      },
  
      handleQuickSearch() {
        this.applyFilters();
      },
  
      applyFilters() {
        // Merge text search with advanced filters
        const allFilters = {
          ...this.filters,
          text: this.localSearchTerm
        };
        
        this.$emit('apply-filters', allFilters);
        this.$emit('update:search-term', this.localSearchTerm);
        this.showAdvanced = false;
      },
  
      clearAllFilters() {
        this.filters = this.initializeFilters();
        this.localSearchTerm = '';
        this.$emit('clear-filters');
        this.$emit('update:search-term', '');
      },
  
      removeFilter(filterKey) {
        if (filterKey === 'ratingRange') {
          this.filters[filterKey] = [0, 100];
        } else {
          this.filters[filterKey] = null;
        }
        this.applyFilters();
      },
  
      clearDateRange() {
        this.filters.dateRange = null;
        this.showDatePicker = false;
      },
  
      confirmDateRange() {
        this.showDatePicker = false;
      },
  
      isQuickFilterActive(quickFilter) {
        return Object.keys(quickFilter.filter).every(key => {
          const filterValue = this.filters[key];
          const quickValue = quickFilter.filter[key];
          
          if (Array.isArray(quickValue)) {
            return JSON.stringify(filterValue) === JSON.stringify(quickValue);
          }
          
          return filterValue === quickValue;
        });
      },
  
      toggleQuickFilter(quickFilter) {
        if (this.isQuickFilterActive(quickFilter)) {
          // Remove quick filter
          Object.keys(quickFilter.filter).forEach(key => {
            if (key === 'ratingRange') {
              this.filters[key] = [0, 100];
            } else {
              this.filters[key] = null;
            }
          });
        } else {
          // Apply quick filter
          Object.assign(this.filters, quickFilter.filter);
        }
      },
  
      formatDisplayDate(dateString) {
        if (!dateString) return '';
        const date = new Date(dateString);
        return date.toLocaleDateString('ar-EG', {
          year: 'numeric',
          month: 'short',
          day: 'numeric'
        });
      }
    },
  
    watch: {
      searchTerm(newVal) {
        this.localSearchTerm = newVal;
      },
  
      initialFilters: {
        handler(newFilters) {
          Object.assign(this.filters, newFilters);
        },
        deep: true
      }
    },
  
    beforeUnmount() {
      if (this.searchTimeout) {
        clearTimeout(this.searchTimeout);
      }
    }
  };
  </script>
  
  <style scoped>
  .advanced-search-wrapper {
    position: relative;
    width: 100%;
  }
  
  .search-field {
    width: 100%;
  }
  
  .search-field :deep(.v-field__append-inner) {
    align-items: center;
    gap: 8px;
  }
  
  .advanced-search-card {
    border-radius: 12px !important;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12) !important;
  }
  
  .active-filters {
    background: rgba(54, 96, 145, 0.05);
    border-radius: 8px;
    padding: 8px 12px;
    border: 1px solid rgba(54, 96, 145, 0.1);
  }
  
  /* Range slider styling */
  :deep(.v-slider-thumb__label) {
    background-color: var(--v-primary-base) !important;
  }
  
  /* Date picker styling */
  :deep(.v-date-picker) {
    border-radius: 8px;
  }
  
  /* Quick filter chips */
  .quick-filters .v-chip {
    transition: all 0.2s ease;
  }
  
  .quick-filters .v-chip:hover {
    transform: translateY(-1px);
  }
  
  /* Mobile responsiveness */
  @media (max-width: 768px) {
    .advanced-search-card {
      max-width: 95vw !important;
      margin: 0 auto;
    }
    
    .active-filters {
      padding: 6px 8px;
    }
    
    .active-filters .v-chip {
      font-size: 11px;
      height: 20px;
    }
  }
  
  @media (max-width: 600px) {
    .search-field :deep(.v-field__append-inner) {
      gap: 4px;
    }
    
    .active-filters .d-flex {
      flex-direction: column;
      align-items: flex-start;
      gap: 8px;
    }
  }
  
  /* Print styles */
  @media print {
    .advanced-search-wrapper {
      display: none !important;
    }
  }
  </style>