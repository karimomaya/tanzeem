<template>
    <v-card
        class="stats-card"
        elevation="1"
        rounded="xl"
    >
        <v-card-text class="pa-6">
            <div class="d-flex align-center justify-space-between">
                <div class="flex-grow-1">
                    <p class="text-body-2 text-grey-darken-1 mb-1">{{ title }}</p>
                    <p class="text-h4 font-weight-bold text-grey-darken-3 mb-0">{{ formattedValue }}</p>
                    <div v-if="change" class="d-flex align-center mt-2">
                        <v-icon 
                            :color="change.positive ? 'success' : 'error'"
                            size="16"
                            class="me-1"
                        >
                            {{ change.positive ? 'mdi-trending-up' : 'mdi-trending-down' }}
                        </v-icon>
                        <span 
                            class="text-body-2"
                            :class="change.positive ? 'text-success' : 'text-error'"
                        >
                            {{ change.value }}
                        </span>
                    </div>
                </div>
                
                <v-avatar 
                    size="56" 
                    :color="color"
                    class="elevation-2"
                >
                    <v-icon color="white" size="28">{{ icon }}</v-icon>
                </v-avatar>
            </div>
        </v-card-text>
    </v-card>
</template>

<script>
export default {
    name: 'StatsCard',
    props: {
        title: {
            type: String,
            required: true
        },
        value: {
            type: [Number, String],
            required: true
        },
        icon: {
            type: String,
            required: true
        },
        color: {
            type: String,
            default: 'primary'
        },
        change: {
            type: Object,
            default: null
            // Expected format: { positive: boolean, value: string }
            // Example: { positive: true, value: '+12%' }
        }
    },
    computed: {
        formattedValue() {
            if (typeof this.value === 'number') {
                return this.value.toLocaleString('ar-SA');
            }
            return this.value;
        }
    }
};
</script>

<style scoped>
.stats-card {
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.stats-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1) !important;
}
</style>