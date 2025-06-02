<template>
    <div>
        <!-- Main Stats Row -->
        <v-row class="mb-6">
            <v-col cols="12" sm="6" lg="4">
                <div class="stat-card stat-card-blue">
                    <div class="stat-icon">
                        <v-icon color="white" size="24">mdi-chart-line</v-icon>
                    </div>
                    <div class="stat-content">
                        <div class="stat-label">إجمالي التصنيفات</div>
                        <div class="stat-value">{{ categoryStats.total || 0 }}</div>
                    </div>
                </div>
            </v-col>
            <v-col cols="12" sm="6" lg="4">
                <div class="stat-card stat-card-green">
                    <div class="stat-icon">
                        <v-icon color="white" size="24">mdi-check-circle</v-icon>
                    </div>
                    <div class="stat-content">
                        <div class="stat-label">التصنيفات النشطة</div>
                        <div class="stat-value">{{ categoryStats.active || 0 }}</div>
                    </div>
                </div>
            </v-col>
            <v-col cols="12" sm="12" lg="4">
                <div class="stat-card stat-card-purple">
                    <div class="stat-icon">
                        <v-icon color="white" size="24">mdi-close-circle</v-icon>
                    </div>
                    <div class="stat-content">
                        <div class="stat-label">غير نشطة</div>
                        <div class="stat-value">{{ categoryStats.inactive || 0 }}</div>
                    </div>
                </div>
            </v-col>
            <!-- <v-col cols="12" sm="6" lg="3">
                <div class="stat-card stat-card-teal">
                    <div class="stat-icon">
                        <v-icon color="white" size="24">mdi-calculator</v-icon>
                    </div>
                    <div class="stat-content">
                        <div class="stat-label">متوسط المنتجات</div>
                        <div class="stat-value">{{ formatAverage(categoryStats.averageProducts) }}</div>
                    </div>
                </div>
            </v-col> -->
        </v-row>

        <!-- Enhanced Analytics Row -->
        <!-- <v-row class="mb-6">
            <v-col cols="12" sm="6" lg="4">
                <div class="stat-card stat-card-orange">
                    <div class="stat-icon">
                        <v-icon color="white" size="24">mdi-trophy</v-icon>
                    </div>
                    <div class="stat-content">
                        <div class="stat-label">أكثر تصنيف فيه منتجات</div>
                        <div class="stat-value">{{ categoryStats.topCategoryProductCount || 0 }}</div>
                        <div class="stat-description">{{ categoryStats.topCategoryName || 'لا يوجد' }}</div>
                    </div>
                </div>
            </v-col>
            <v-col cols="12" sm="6" lg="4">
                <div class="stat-card stat-card-red">
                    <div class="stat-icon">
                        <v-icon color="white" size="24">mdi-folder-remove</v-icon>
                    </div>
                    <div class="stat-content">
                        <div class="stat-label">تصنيفات بدون منتجات</div>
                        <div class="stat-value">{{ categoryStats.categoriesWithoutProducts || 0 }}</div>
                        <div class="stat-description">تحتاج إلى تنظيف</div>
                    </div>
                </div>
            </v-col>
            <v-col cols="12" sm="6" lg="4">
                <div class="stat-card stat-card-indigo">
                    <div class="stat-icon">
                        <v-icon color="white" size="24">mdi-percent</v-icon>
                    </div>
                    <div class="stat-content">
                        <div class="stat-label">نسبة التصنيفات النشطة</div>
                        <div class="stat-value">{{ formatPercentage(categoryStats.activePercentage) }}%</div>
                        <div class="stat-description">من إجمالي التصنيفات</div>
                    </div>
                </div>
            </v-col>
        </v-row> -->

        <!-- Activity Indicator -->
        <!-- <v-row v-if="categoryStats.total > 0">
            <v-col cols="12">
                <v-card class="activity-card" elevation="2">
                    <v-card-title class="d-flex align-center">
                        <v-icon color="primary" class="me-2">mdi-chart-arc</v-icon>
                        مؤشر نشاط التصنيفات
                    </v-card-title>
                    <v-card-text>
                        <div class="activity-indicators">
                            <div class="indicator-item">
                                <div class="indicator-header">
                                    <span class="indicator-label">التصنيفات النشطة</span>
                                    <span class="indicator-value">{{ categoryStats.active }} / {{ categoryStats.total }}</span>
                                </div>
                                <v-progress-linear
                                    :model-value="categoryStats.activePercentage"
                                    color="success"
                                    height="8"
                                    rounded
                                    class="mt-2"
                                ></v-progress-linear>
                            </div>

                            <div class="indicator-item">
                                <div class="indicator-header">
                                    <span class="indicator-label">تصنيفات تحتوي على منتجات</span>
                                    <span class="indicator-value">{{ categoriesWithProducts }} / {{ categoryStats.total }}</span>
                                </div>
                                <v-progress-linear
                                    :model-value="categoriesWithProductsPercentage"
                                    color="primary"
                                    height="8"
                                    rounded
                                    class="mt-2"
                                ></v-progress-linear>
                            </div>

                            <div class="indicator-item">
                                <div class="indicator-header">
                                    <span class="indicator-label">مؤشر صحة التصنيفات</span>
                                    <span class="indicator-value">{{ healthScore }}%</span>
                                </div>
                                <v-progress-linear
                                    :model-value="healthScore"
                                    :color="getHealthColor(healthScore)"
                                    height="8"
                                    rounded
                                    class="mt-2"
                                ></v-progress-linear>
                            </div>
                        </div>

                        <div v-if="recommendations.length > 0" class="recommendations mt-4">
                            <h4 class="mb-2">
                                <v-icon color="warning" size="20" class="me-1">mdi-lightbulb</v-icon>
                                توصيات
                            </h4>
                            <v-alert
                                v-for="(recommendation, index) in recommendations"
                                :key="index"
                                :color="recommendation.color"
                                variant="tonal"
                                density="compact"
                                class="mb-2"
                            >
                                <v-icon start :icon="recommendation.icon"></v-icon>
                                {{ recommendation.text }}
                            </v-alert>
                        </div>
                    </v-card-text>
                </v-card>
            </v-col>
        </v-row> -->
    </div>
</template>

<script>
import { getCategoriesStats } from '@/utils/product-util';
export default {
    name: 'CategoryStats',
    props: {
        refresh: {
            type: Boolean,
            default: false
        }
    },
    data() {
        return {
            categoryStats: {
                total: 0,
                active: 0,
                inactive: 0,
                averageProducts: 0,
                topCategoryName: null,
                topCategoryProductCount: 0,
                categoriesWithoutProducts: 0,
                activePercentage: 0
            },
            loading: false
        };
    },
    computed: {
        categoriesWithProducts() {
            return this.categoryStats.total - this.categoryStats.categoriesWithoutProducts;
        },
        
        categoriesWithProductsPercentage() {
            return this.categoryStats.total > 0 
                ? (this.categoriesWithProducts / this.categoryStats.total) * 100 
                : 0;
        },
        
        healthScore() {
            // Calculate health score based on active categories and categories with products
            const activeScore = this.categoryStats.activePercentage;
            const productScore = this.categoriesWithProductsPercentage;
            return Math.round((activeScore + productScore) / 2);
        },
        
        recommendations() {
            const recommendations = [];
            
            if (this.categoryStats.categoriesWithoutProducts > 0) {
                recommendations.push({
                    text: `لديك ${this.categoryStats.categoriesWithoutProducts} تصنيفات فارغة - يمكنك حذفها أو إضافة منتجات لها`,
                    color: 'warning',
                    icon: 'mdi-folder-remove'
                });
            }
            
            if (this.categoryStats.activePercentage < 80) {
                recommendations.push({
                    text: 'نسبة التصنيفات النشطة منخفضة - راجع التصنيفات غير النشطة',
                    color: 'error',
                    icon: 'mdi-alert-circle'
                });
            }
            
            if (this.categoryStats.averageProducts < 2) {
                recommendations.push({
                    text: 'متوسط المنتجات لكل تصنيف منخفض - فكر في دمج بعض التصنيفات',
                    color: 'info',
                    icon: 'mdi-merge'
                });
            }
            
            if (this.healthScore >= 85) {
                recommendations.push({
                    text: 'ممتاز! تصنيفاتك منظمة وصحية',
                    color: 'success',
                    icon: 'mdi-check-circle'
                });
            }
            
            return recommendations;
        }
    },
    mounted() {
        this.loadCategoryStats();
    },
    watch: {
        refresh(newValue) {
            if (newValue) {
                this.loadCategoryStats();
                this.$emit('stats-refreshed');
            }
        }
    },
    methods: {
        async loadCategoryStats() {
            try {
                this.loading = true;
                const stats = await getCategoriesStats();
                this.categoryStats = stats;
                
            } catch (err) {
                console.error('Error fetching category stats:', err);
                // Keep defaults on error
            } finally {
                this.loading = false;
            }
        },
        
        formatAverage(value) {
            return value ? parseFloat(value).toFixed(1) : '0.0';
        },
        
        formatPercentage(value) {
            return value ? parseFloat(value).toFixed(1) : '0.0';
        },
        
        getHealthColor(score) {
            if (score >= 85) return 'success';
            if (score >= 70) return 'warning';
            return 'error';
        }
    },
    emits: ['stats-refreshed']
};
</script>

<style scoped>
@import '@/styles/product.css';
.stat-card-teal {
    background: linear-gradient(135deg, #009688 0%, #00796b 100%);
}

.stat-card-orange {
    background: linear-gradient(135deg, #ff9800 0%, #f57c00 100%);
}

.stat-card-red {
    background: linear-gradient(135deg, #f44336 0%, #d32f2f 100%);
}

.stat-card-indigo {
    background: linear-gradient(135deg, #3f51b5 0%, #303f9f 100%);
}

.activity-card {
    border: 1px solid rgba(0, 0, 0, 0.12);
}

.activity-indicators {
    margin-top: 16px;
}

.indicator-item {
    margin-bottom: 24px;
}

.indicator-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;
}

.indicator-label {
    font-weight: 500;
    color: rgba(0, 0, 0, 0.87);
}

.indicator-value {
    font-weight: 600;
    color: rgba(0, 0, 0, 0.6);
}

.recommendations {
    border-top: 1px solid rgba(0, 0, 0, 0.12);
    padding-top: 16px;
}
</style>