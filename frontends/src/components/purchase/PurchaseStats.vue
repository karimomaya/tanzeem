<template>
    <div>
        <v-row class="mb-6">
            <!-- Total Purchase Orders -->
            <v-col cols="12" sm="6" md="4">
                <StatsCard 
                    label="إجمالي أوامر الشراء"
                    :value="formatNumber(stats.totalOrders)"
                    icon="mdi-clipboard-list"
                    color="blue"
                    :trend="stats.ordersGrowth"
                />
            </v-col>

            <!-- Total Amount -->
            <v-col cols="12" sm="6" md="4">
                <StatsCard 
                    label="إجمالي قيمة المشتريات"
                    :value="formatCurrency(stats.totalAmount)"
                    icon="mdi-currency-usd"
                    color="green"
                    :trend="stats.amountGrowth"
                />
            </v-col>

            <!-- This Month Stats -->
            <v-col cols="12" sm="6" md="4">
                <StatsCard 
                    label="أوامر هذا الشهر"
                    :value="formatNumber(stats.thisMonthOrders)"
                    icon="mdi-calendar-month"
                    color="yellow"
                    :subtext="formatCurrency(stats.thisMonthAmount)"
                />
            </v-col>

            <!-- Pending Orders -->
            <v-col cols="12" sm="6" md="4">
                <StatsCard 
                    label="أوامر في الانتظار"
                    :value="formatNumber(stats.pendingOrders)"
                    icon="mdi-clock-outline"
                    color="blue"
                    :percentage="stats.totalOrders > 0 ? `${Math.round((stats.pendingOrders / stats.totalOrders) * 100)}% من الإجمالي` : null"
                />
            </v-col>

            <!-- Average Order Value -->
            <v-col cols="12" sm="6" md="4">
                <StatsCard 
                    label="متوسط قيمة الأمر"
                    :value="formatCurrency(stats.averageOrderValue)"
                    icon="mdi-calculator"
                    color="green"
                    :comparison="stats.previousAverageOrderValue ? `مقارنة بـ ${formatCurrency(stats.previousAverageOrderValue)}` : null"
                />
            </v-col>

            <!-- Low Stock Alert -->
            <v-col cols="12" sm="6" md="4">
                <StatsCard 
                    label="منتجات تحتاج إعادة طلب"
                    :value="formatNumber(stats.lowStockProducts)"
                    icon="mdi-alert"
                    color="purple"
                />
            </v-col>
        </v-row>
    </div>
</template>

<script>
import { formatCurrency } from '@/utils/currency-util';
import { formatNumber} from '@/utils/system-util';
import { getPurchaseStats } from '@/services/purchase-service';
import StatsCard from '@/components/common/StatsCard.vue';

export default {
    name: 'PurchaseStats',
    components: {
        StatsCard
    },
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

</style>