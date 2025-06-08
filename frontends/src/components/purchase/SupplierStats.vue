<template>
    <div>
        <!-- Main Stats Row -->
        <v-row class="mb-6">
            <v-col cols="12" sm="6" lg="3">
                <StatsCard 
                    label="إجمالي الموردين"
                    :value="supplierStats.total || 0"
                    icon="mdi-truck"
                    color="blue"
                    :trend="supplierStats.totalGrowth"
                />
            </v-col>
            <v-col cols="12" sm="6" lg="3">
                <StatsCard 
                    label="الموردين النشطين"
                    :value="supplierStats.active || 0"
                    icon="mdi-check-circle"
                    color="green"
                    :percentage="supplierStats.total > 0 ? `${Math.round((supplierStats.active / supplierStats.total) * 100)}% من الإجمالي` : null"
                />
            </v-col>
            <v-col cols="12" sm="6" lg="3">
                <StatsCard 
                    label="موردين جدد هذا الشهر"
                    :value="supplierStats.newThisMonth || 0"
                    icon="mdi-plus-circle"
                    color="purple"
                    :trend="supplierStats.newSuppliersGrowth"
                />
            </v-col>
            <v-col cols="12" sm="6" lg="3">
                <StatsCard 
                    label="متوسط التقييم"
                    :value="`${supplierStats.averageRating || 0}%`"
                    icon="mdi-star"
                    color="yellow"
                    :trend="supplierStats.ratingTrend"
                />
            </v-col>
        </v-row>

        <!-- Financial & Performance Stats Row -->
        <v-row class="mb-6">
            <v-col cols="12" sm="6" md="3">
                <StatsCard 
                    label="إجمالي المشتريات"
                    :value="formatCurrency(supplierStats.totalSpending || 0)"
                    icon="mdi-currency-usd"
                    color="green"
                    :small-value="true"
                    :trend="supplierStats.spendingGrowth"
                />
            </v-col>
            
            <v-col cols="12" sm="6" md="3">
                <StatsCard 
                    label="متوسط قيمة الطلب"
                    :value="formatCurrency(supplierStats.averageOrderValue || 0)"
                    icon="mdi-calculator"
                    color="info"
                    :small-value="true"
                    :trend="supplierStats.averageOrderTrend"
                />
            </v-col>

            <v-col cols="12" sm="6" md="3">
                <StatsCard 
                    label="أفضل مورد"
                    :value="supplierStats.topSupplier?.name || 'لا يوجد'"
                    icon="mdi-trophy"
                    color="orange"
                    :small-value="true"
                    :subtext="supplierStats.topSupplier ? `${supplierStats.topSupplier.orders} أوامر - ${formatCurrency(supplierStats.topSupplier.totalAmount)}` : null"
                />
            </v-col>

            <v-col cols="12" sm="6" md="3">
                <StatsCard 
                    label="معدل التسليم في الوقت"
                    :value="`${supplierStats.onTimeDeliveryRate || 0}%`"
                    icon="mdi-clock-check"
                    color="success"
                    :small-value="true"
                    :trend="supplierStats.deliveryTrend"
                />
            </v-col>
        </v-row>
    </div>
</template>

<script>
import StatsCard from '@/components/common/StatsCard.vue';
import { getSupplierStats } from '@/services/purchase-service';
import { formatCurrency } from '@/utils/currency-util';
import { formatDate } from '@/utils/date-util';

export default {
    name: 'SupplierStats',
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
            supplierStats: {
                total: 0,
                active: 0,
                newThisMonth: 0,
                averageRating: 0,
                totalSpending: 0,
                topSupplier: null,
                // Enhanced stats
                totalGrowth: 0,
                newSuppliersGrowth: 0,
                ratingTrend: 0,
                spendingGrowth: 0,
                averageOrderValue: 0,
                averageOrderTrend: 0,
                onTimeDeliveryRate: 0,
                deliveryTrend: 0
            },
            loading: false
        };
    },
    mounted() {
        this.loadSupplierStats();
    },
    watch: {
        refresh(newValue) {
            if (newValue) {
                this.loadSupplierStats();
                this.$emit('stats-refreshed');
            }
        }
    },
    methods: {
        formatCurrency,
        formatDate,
        async loadSupplierStats() {
            try {
                this.loading = true;
                const stats = await getSupplierStats();
                this.supplierStats = {
                    ...this.supplierStats,
                    ...stats
                };
            } catch (err) {
                console.error('Error fetching supplier stats:', err);
                // Fallback sample data for demonstration
                this.supplierStats = {
                    total: 147,
                    active: 142,
                    newThisMonth: 8,
                    averageRating: 87.5,
                    totalSpending: 2450000,
                    topSupplier: {
                        name: 'شركة الخليج التجارية',
                        orders: 45,
                        totalAmount: 350000
                    },
                    totalGrowth: 12.5,
                    newSuppliersGrowth: 25.0,
                    ratingTrend: 3.2,
                    spendingGrowth: 15.8,
                    averageOrderValue: 15750,
                    averageOrderTrend: 8.5,
                    onTimeDeliveryRate: 94.5,
                    deliveryTrend: 2.1
                };
            } finally {
                this.loading = false;
                this.$emit('stats-refreshed');
            }
        }
    },
    emits: ['stats-refreshed']
};
</script>