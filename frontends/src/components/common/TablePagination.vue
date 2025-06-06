<template>
    <div class="table-footer">
        <div class="footer-info">
            <span class="text-body-2 text-medium-emphasis">
                عرض {{ startItem }} - {{ endItem }} من أصل {{ totalItems }} {{ itemLabel }}
            </span>
        </div>
        
        <!-- Pagination Controls -->
        <v-pagination 
            v-if="totalPages > 1" 
            :model-value="page"
            :length="totalPages" 
            :total-visible="totalVisible"
            @update:model-value="$emit('update:page', $event)" 
            color="primary" 
            size="small"
            class="table-pagination">
        </v-pagination>
        
        <!-- Items per page selector -->
        <v-select 
            :model-value="itemsPerPage" 
            :items="itemsPerPageOptions" 
            label="عدد العناصر"
            variant="outlined" 
            density="compact" 
            hide-details 
            style="max-width: 120px;"
            class="items-per-page-select"
            @update:model-value="$emit('update:items-per-page', $event)">
        </v-select>
    </div>
</template>

<script>
export default {
    name: 'TablePagination',
    props: {
        page: {
            type: Number,
            required: true
        },
        itemsPerPage: {
            type: Number,
            required: true
        },
        totalItems: {
            type: Number,
            required: true
        },
        itemLabel: {
            type: String,
            default: 'عنصر'
        },
        itemsPerPageOptions: {
            type: Array,
            default: () => [10, 25, 50, 100]
        },
        totalVisible: {
            type: Number,
            default: 5
        }
    },
    emits: ['update:page', 'update:items-per-page'],
    computed: {
        totalPages() {
            return Math.ceil(this.totalItems / this.itemsPerPage);
        },
        startItem() {
            if (this.totalItems === 0) return 0;
            return (this.page - 1) * this.itemsPerPage + 1;
        },
        endItem() {
            return Math.min(this.page * this.itemsPerPage, this.totalItems);
        }
    }
};
</script>

<style scoped>
.table-footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px 24px;
    border-top: 1px solid #e0e0e0;
    background: #fafafa;
    border-radius: 0 0 12px 12px;
    gap: 16px;
}

.footer-info {
    flex-shrink: 0;
}

.table-pagination {
    flex: 1;
    display: flex;
    justify-content: center;
}

.items-per-page-select {
    flex-shrink: 0;
}

.items-per-page-select .v-field {
    border-radius: 8px !important;
}

/* Responsive Design */
@media (max-width: 768px) {
    .table-footer {
        flex-direction: column;
        gap: 12px;
        padding: 12px 16px;
    }
    
    .footer-info {
        order: 1;
        text-align: center;
    }
    
    .table-pagination {
        order: 2;
        width: 100%;
    }
    
    .items-per-page-select {
        order: 3;
        width: 100%;
        max-width: none !important;
    }
}

@media (max-width: 600px) {
    .table-footer {
        padding: 8px 12px;
    }
    
    .footer-info span {
        font-size: 12px;
    }
}

/* Print Styles */
@media print {
    .table-footer {
        display: none !important;
    }
}
</style>