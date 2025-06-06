<template>
    <div :class="['stat-card', colorClass]">
        <div class="stat-icon">
            <v-icon color="white" size="24">{{ icon }}</v-icon>
        </div>
        <div class="stat-content">
            <div class="stat-label">{{ label }}</div>
            <div :class="['stat-value', { 'stat-value-small': smallValue }]">{{ value }}</div>
            
            <!-- Trend Display -->
            <div v-if="trend !== undefined" class="stat-trend">
                <v-icon :color="trend >= 0 ? 'success' : 'error'" size="14">
                    {{ trend >= 0 ? 'mdi-trending-up' : 'mdi-trending-down' }}
                </v-icon>
                <span :class="trend >= 0 ? 'text-success' : 'text-error'">
                    {{ Math.abs(trend) }}%
                </span>
            </div>
            
            <!-- Subtext Display -->
            <div v-if="subtext" class="stat-subtext">{{ subtext }}</div>
            
            <!-- Percentage Display -->
            <div v-if="percentage" class="stat-percentage">{{ percentage }}</div>
            
            <!-- Comparison Display -->
            <div v-if="comparison" class="stat-comparison">{{ comparison }}</div>
            
            <!-- Description Display -->
            <div v-if="description" class="stat-description">{{ description }}</div>
        </div>
    </div>
</template>

<script>
export default {
    name: 'StatsCard',
    props: {
        label: { type: String, required: true },
        value: { type: [String, Number], required: true },
        icon: { type: String, required: true },
        color: { type: String, default: 'blue' },
        description: { type: String, default: null },
        trend: { type: Number, default: undefined }, // Growth percentage
        subtext: { type: String, default: null }, // Additional info below value
        percentage: { type: String, default: null }, // Percentage text
        comparison: { type: String, default: null }, // Comparison text
        smallValue: { type: Boolean, default: false } // For smaller value text
    },
    computed: {
        colorClass() {
            return `stat-card-${this.color}`;
        }
    }
};
</script>

<style scoped>
@import '@/styles/stats.css';
</style>