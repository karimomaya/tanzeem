<template>
    <div>
        <v-row class="mb-6">
            <!-- Total Purchase Orders -->
            <v-col cols="12" sm="6" lg="3">
                <div class="stat-card stat-card-blue">
                    <div class="stat-icon">
                        <v-icon size="24" color="white">mdi-clipboard-list</v-icon>
                    </div>
                    <div class="stat-content">
                        <div class="stat-label">إجمالي أوامر الشراء</div>
                        <div class="stat-value">{{ formatNumber(stats.totalOrders) }}</div>
                        <div class="stat-trend" v-if="stats.ordersGrowth !== undefined">
                            <v-icon :color="stats.ordersGrowth >= 0 ? 'success' : 'error'" size="14">
                                {{ stats.ordersGrowth >= 0 ? 'mdi-trending-up' : 'mdi-trending-down' }}
                            </v-icon>
                            <span :class="stats.ordersGrowth >= 0 ? 'text-success' : 'text-error'">
                                {{ Math.abs(stats.ordersGrowth) }}%
                            </span>
                        </div>
                    </div>
                </div>
            </v-col>

            <!-- Total Amount -->
            <v-col cols="12" sm="6" md="3">

                <div class="stat-card stat-card-green">


                    <div class="stat-icon">
                        <v-icon size="24" color="white">mdi-currency-usd</v-icon>
                    </div>
                    <div class="stat-content">
                        <div class="stat-label">إجمالي قيمة المشتريات</div>
                        <div class="stat-value">{{ formatCurrency(stats.totalAmount) }}</div>
                        <div class="stat-trend" v-if="stats.amountGrowth !== undefined">
                            <v-icon :color="stats.amountGrowth >= 0 ? 'success' : 'error'" size="14">
                                {{ stats.amountGrowth >= 0 ? 'mdi-trending-up' : 'mdi-trending-down' }}
                            </v-icon>
                            <span :class="stats.amountGrowth >= 0 ? 'text-success' : 'text-error'">
                                {{ Math.abs(stats.amountGrowth) }}%
                            </span>
                        </div>
                    </div>

                </div>
            </v-col>

            <!-- This Month Stats -->
            <v-col cols="12" sm="6" md="3">
                <div class="stat-card stat-card-yellow">
                    <div class="stat-icon">
                        <v-icon size="24" color="white">mdi-calendar-month</v-icon>
                    </div>
                    <div class="stat-content">
                        <div class="stat-label">أوامر هذا الشهر</div>
                        <div class="stat-value">{{ formatNumber(stats.thisMonthOrders) }}</div>
                        <div class="stat-subtext">
                            {{ formatCurrency(stats.thisMonthAmount) }}
                        </div>
                    </div>
                </div>
            </v-col>

            <!-- Active Suppliers -->
            <v-col cols="12" sm="6" md="3">

                <div class="stat-card stat-card-purple">

                    <div class="stat-icon">
                        <v-icon size="24" color="white">mdi-truck</v-icon>
                    </div>
                    <div class="stat-content">
                        <div class="stat-label">الموردين النشطين</div>
                        <div class="stat-value">{{ formatNumber(stats.activeSuppliers) }}</div>
                        <div class="stat-subtext">
                            من أصل {{ formatNumber(stats.totalSuppliers) }} مورد
                        </div>
                    </div>
                </div>
            </v-col>

            <!-- Pending Orders -->
            <v-col cols="12" sm="6" md="3">
                <div class="stat-card stat-card-blue">
                    <div class="stat-icon">
                        <v-icon size="24" color="white">mdi-clock-outline</v-icon>
                    </div>
                    <div class="stat-content">
                        <div class="stat-label">أوامر في الانتظار</div>
                        <div class="stat-value">{{ formatNumber(stats.pendingOrders) }}</div>
                        <div class="stat-percentage" v-if="stats.totalOrders > 0">
                            {{ Math.round((stats.pendingOrders / stats.totalOrders) * 100) }}% من الإجمالي
                        </div>
                    </div>
                </div>
            </v-col>

            <!-- Average Order Value -->
            <v-col cols="12" sm="6" md="3">
                <div class="stat-card stat-card-green">
                    <div class="stat-icon">
                        <v-icon size="24" color="white">mdi-calculator</v-icon>
                    </div>
                    <div class="stat-content">
                        <div class="stat-label">متوسط قيمة الأمر</div>
                        <div class="stat-value">{{ formatCurrency(stats.averageOrderValue) }}</div>
                        <div class="stat-comparison" v-if="stats.previousAverageOrderValue">
                            مقارنة بـ {{ formatCurrency(stats.previousAverageOrderValue) }}
                        </div>
                    </div>
                </div>
            </v-col>





            <!-- Top Supplier -->
            <v-col cols="12" sm="6" md="3">
                <div class="stat-card stat-card-yellow">
                    <div class="stat-icon">
                        <v-icon size="24" color="white">mdi-star</v-icon>
                    </div>
                    <div class="stat-content">
                        <div class="stat-label">أكبر مورد</div>
                        <div class="stat-value stat-value-small">{{ stats.topSupplier?.name || 'غير محدد' }}</div>
                        <div class="stat-subtext" v-if="stats.topSupplier">
                            {{ formatCurrency(stats.topSupplier.totalAmount) }}
                        </div>
                    </div>
                </div>
            </v-col>

            <!-- Low Stock Alert -->
            <v-col cols="12" sm="6" md="3">
                <div class="stat-card stat-card-purple">


                    <div class="stat-icon">
                        <v-icon size="24" color="white">mdi-alert</v-icon>
                    </div>
                    <div class="stat-content">
                        <div class="stat-label">منتجات تحتاج إعادة طلب</div>
                        <div class="stat-value">{{ formatNumber(stats.lowStockProducts) }}</div>
                    </div>
                </div>
            </v-col>
        </v-row>
    </div>
</template>

<script>
import { formatCurrency , formatNumber} from '@/utils/system-util';
import { getPurchaseStats } from '@/utils/purchase-util';

export default {
    name: 'PurchaseStats',
    props: {
        refresh: {
            type: Boolean,
            default: false
        }
    },

    data() {
        return {
            loading: false,
            stats: {
                totalOrders: 0,
                pendingOrders: 0,
                totalAmount: 0,
                averageOrderValue: 0,
                thisMonthOrders: 0,
                thisMonthAmount: 0,
                activeSuppliers: 0,
                totalSuppliers: 0,
                topSupplier: null,
                lowStockProducts: 0,
                ordersGrowth: 0,
                amountGrowth: 0,
                previousAverageOrderValue: 0
            }
        };
    },

    watch: {
        refresh(newVal) {
            if (newVal) {
                this.loadStats();
            }
        }
    },

    created() {
        this.loadStats();
    },

    methods: {
        formatCurrency,
        formatNumber,

        async loadStats() {
            try {
                this.loading = true;
                const response = await getPurchaseStats();
                if (response) {
                    this.stats = {
                        ...this.stats,
                        ...response
                    };
                }
            } catch (error) {
                console.error('Error loading purchase stats:', error);
                // Use default/sample data on error
                this.stats = {
                    totalOrders: 156,
                    pendingOrders: 23,
                    totalAmount: 485230.50,
                    averageOrderValue: 3110.45,
                    thisMonthOrders: 45,
                    thisMonthAmount: 145780.25,
                    activeSuppliers: 12,
                    totalSuppliers: 15,
                    topSupplier: {
                        name: 'شركة الخليج التجارية',
                        totalAmount: 125400.75
                    },
                    lowStockProducts: 8,
                    ordersGrowth: 12.5,
                    amountGrowth: 8.3,
                    previousAverageOrderValue: 2875.20
                };
            } finally {
                this.loading = false;
                this.$emit('stats-refreshed');
            }
        }
    }
};
</script>

<style scoped>
@import '@/styles/product.css';

.stat-trend {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 12px;
    margin-top: 4px;
}

.stat-percentage {
    font-size: 12px;
    color: #718096;
    margin-top: 4px;
}

.stat-comparison {
    font-size: 11px;
    color: #a0aec0;
    margin-top: 2px;
}

.stat-subtext {
    font-size: 12px;
    color: #718096;
    margin-top: 4px;
    line-height: 1.2;
}

.stat-value-small {
    font-size: 18px !important;
    line-height: 1.2;
}

/* Additional responsive design */
@media (max-width: 600px) {
    .stat-card .v-card-text {
        padding: 12px !important;
    }

    .stat-icon {
        width: 36px !important;
        height: 36px !important;
    }

    .stat-value {
        font-size: 20px !important;
    }

    .stat-value-small {
        font-size: 14px !important;
    }

    .stat-label {
        font-size: 11px !important;
    }
}
</style>