<template>
    <div>
        <!-- Main Stats Row -->
        <v-row class="mb-6">
            <v-col cols="12" sm="6" lg="3">
                <div class="stat-card stat-card-blue">
                    <div class="stat-icon">
                        <v-icon color="white" size="24">mdi-chart-line</v-icon>
                    </div>
                    <div class="stat-content">
                        <div class="stat-label">إجمالي المنتجات</div>
                        <div class="stat-value">{{ productStats.total || 0 }}</div>
                    </div>
                </div>
            </v-col>
            <v-col cols="12" sm="6" lg="3">
                <div class="stat-card stat-card-green">
                    <div class="stat-icon">
                        <v-icon color="white" size="24">mdi-check-circle</v-icon>
                    </div>
                    <div class="stat-content">
                        <div class="stat-label">المنتجات النشطة</div>
                        <div class="stat-value">{{ productStats.active || 0 }}</div>
                    </div>
                </div>
            </v-col>
            <v-col cols="12" sm="6" lg="3">
                <div class="stat-card stat-card-purple">
                    <div class="stat-icon">
                        <v-icon color="white" size="24">mdi-alert-circle</v-icon>
                    </div>
                    <div class="stat-content">
                        <div class="stat-label">مخزون منخفض</div>
                        <div class="stat-value">{{ productStats.lowStock || 0 }}</div>
                    </div>
                </div>
            </v-col>
            <v-col cols="12" sm="6" lg="3">
                <div class="stat-card stat-card-yellow">
                    <div class="stat-icon">
                        <v-icon color="white" size="24">mdi-close-circle</v-icon>
                    </div>
                    <div class="stat-content">
                        <div class="stat-label">نفد المخزون</div>
                        <div class="stat-value">{{ productStats.outOfStock || 0 }}</div>
                    </div>
                </div>
            </v-col>
        </v-row>

        <!-- Additional Stats Row -->
        <!-- <v-row class="mb-6">
            <v-col cols="12" sm="6" lg="6">
                <div class="stat-card stat-card-orange">
                    <div class="stat-icon">
                        <v-icon color="white" size="24">mdi-alert-outline</v-icon>
                    </div>
                    <div class="stat-content">
                        <div class="stat-label">منتجات بدون حد أدنى للمخزون</div>
                        <div class="stat-value">{{ productStats.withoutMinimumStock || 0 }}</div>
                        <div class="stat-description">منتجات لا تحتوي على تنبيهات مخزون</div>
                    </div>
                </div>
            </v-col>
            <v-col cols="12" sm="6" lg="6">
                <div class="stat-card stat-card-indigo">
                    <div class="stat-icon">
                        <v-icon color="white" size="24">mdi-view-grid</v-icon>
                    </div>
                    <div class="stat-content">
                        <div class="stat-label">عدد التصنيفات المستخدمة</div>
                        <div class="stat-value">{{ categoriesWithProducts }}</div>
                        <div class="stat-description">تصنيفات تحتوي على منتجات</div>
                    </div>
                </div>
            </v-col>
        </v-row>

        <v-row v-if="productStats.productsByCategory && productStats.productsByCategory.length > 0">
            <v-col cols="12">
                <v-card class="category-stats-card" elevation="2">
                    <v-card-title class="d-flex align-center">
                        <v-icon color="primary" class="me-2">mdi-chart-donut</v-icon>
                        توزيع المنتجات حسب التصنيف
                    </v-card-title>
                    <v-card-text>
                        <v-row>
                            <v-col 
                                v-for="(category, index) in topCategories" 
                                :key="category.categoryName"
                                cols="12" 
                                sm="6" 
                                md="4" 
                                lg="3"
                            >
                                <div class="category-stat-item">
                                    <div class="category-stat-header">
                                        <v-chip 
                                            :color="getCategoryColor(index)" 
                                            size="small" 
                                            variant="tonal"
                                            class="mb-2"
                                        >
                                            <v-icon start size="12">mdi-tag</v-icon>
                                            {{ category.categoryName }}
                                        </v-chip>
                                    </div>
                                    <div class="category-stat-count">
                                        {{ category.productCount }} منتج
                                    </div>
                                    <v-progress-linear
                                        :model-value="getCategoryPercentage(category.productCount)"
                                        :color="getCategoryColor(index)"
                                        height="4"
                                        rounded
                                        class="mt-2"
                                    ></v-progress-linear>
                                </div>
                            </v-col>
                        </v-row>
                        
                       
                        <div v-if="productStats.productsByCategory.length > 8" class="text-center mt-4">
                            <v-chip color="grey" variant="tonal" size="small">
                                +{{ productStats.productsByCategory.length - 8 }} تصنيفات أخرى
                            </v-chip>
                        </div>
                    </v-card-text>
                </v-card>
            </v-col>
        </v-row> -->
    </div>
</template>

<script>
import { getProductsStats } from '@/services/product-service';
export default {
    
    name: 'ProductStats',
    props: {
        refresh: {
            type: Boolean,
            default: false
        }
    },
    data() {
        return {
            productStats: {
                total: 0,
                active: 0,
                lowStock: 0,
                outOfStock: 0,
                withoutMinimumStock: 0,
                productsByCategory: []
            },
            loading: false
        };
    },
    mounted() {
        this.loadProductStats();
    },
    watch: {
        refresh(newValue) {
            if (newValue) {
                this.loadProductStats();
                this.$emit('stats-refreshed');
            }
        }
    },
    computed: {
        categoriesWithProducts() {
            return this.productStats.productsByCategory ? this.productStats.productsByCategory.length : 0;
        },
        
        topCategories() {
            // Show top 8 categories
            return this.productStats.productsByCategory ? this.productStats.productsByCategory.slice(0, 8) : [];
        },
        
        maxProductCount() {
            if (!this.productStats.productsByCategory || this.productStats.productsByCategory.length === 0) return 1;
            return Math.max(...this.productStats.productsByCategory.map(cat => cat.productCount));
        }
    },
    methods: {
        async loadProductStats() {
            try {
                this.loading = true;
                const stats = await getProductsStats();
                
                this.productStats = stats;
                
            } catch (err) {
                console.error('Error fetching product stats:', err);
                // Keep the existing stats or use defaults
                this.productStats = {
                    total: 0,
                    active: 0,
                    lowStock: 0,
                    outOfStock: 0,
                    withoutMinimumStock: 0,
                    productsByCategory: []
                };
            } finally {
                this.loading = false;
            }
        },
        getCategoryColor(index) {
            const colors = [
                'primary', 'success', 'warning', 'error', 
                'info', 'purple', 'indigo', 'teal'
            ];
            return colors[index % colors.length];
        },
        getCategoryPercentage(productCount) {
            return this.maxProductCount > 0 ? (productCount / this.maxProductCount) * 100 : 0;
        }
    },
    emits: ['stats-refreshed']
};
</script>

<style scoped>
@import '@/styles/product.css';

.stat-card-orange {
    background: linear-gradient(135deg, #ff9800 0%, #f57c00 100%);
}

.stat-card-indigo {
    background: linear-gradient(135deg, #3f51b5 0%, #303f9f 100%);
}

.stat-description {
    font-size: 0.75rem;
    color: rgba(255, 255, 255, 0.8);
    margin-top: 4px;
}

.category-stats-card {
    border: 1px solid rgba(0, 0, 0, 0.12);
}

.category-stat-item {
    padding: 16px;
    background: rgba(0, 0, 0, 0.02);
    border-radius: 8px;
    border: 1px solid rgba(0, 0, 0, 0.08);
}

.category-stat-count {
    font-size: 1.25rem;
    font-weight: 600;
    color: rgba(0, 0, 0, 0.87);
}
</style>