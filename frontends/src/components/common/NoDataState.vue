<template>
    <div class="no-data-state">
        <div class="no-data-content">
            <div class="no-data-icon">
                <v-icon size="60" color="grey-lighten-2">{{ icon }}</v-icon>
            </div>
            <h4 class="no-data-title">{{ title }}</h4>
            <p class="no-data-subtitle">{{ subtitle }}</p>
            <v-btn 
                v-if="showAddButton"
                color="primary" 
                variant="tonal" 
                :prepend-icon="addButtonIcon" 
                class="mt-4"
                @click="$emit('add-item')">
                {{ addButtonText }}
            </v-btn>
        </div>
    </div>
</template>

<script>
export default {
    name: 'NoDataState',
    props: {
        icon: { type: String, required: true },
        title: { type: String, required: true },
        subtitle: { type: String, required: true },
        showAddButton: { type: Boolean, default: true },
        addButtonText: { type: String, required: true },
        addButtonIcon: { type: String, default: 'mdi-plus' }
    },
    emits: ['add-item']
};
</script>
<style>
.no-data-icon {
    margin-bottom: 24px;
}
</style>