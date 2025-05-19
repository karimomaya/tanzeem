<template>
    <v-card flat>
        <v-card-text>
            <v-data-table-server
                :headers="categoryHeaders"
                :items="categories"
                :items-per-page="itemsPerPage"
                :page="page"
                :items-length="totalItems"
                :loading="loading"
                @update:options="updateItemsPerPage"
                class="mt-6 rounded"
                loading-text="Loading... Please wait"
            >
                <template v-slot:item.icon="{ item }">
                    <v-icon :color="item.color">{{ item.icon }}</v-icon>
                </template>
                <template v-slot:item.products="{ item }">
                    <v-chip color="indigo" text-color="white" size="small">
                        {{ item.productCount }}
                    </v-chip>
                </template>
                <template v-slot:item.status="{ item }">
                    <v-chip :color="item.active ? 'success' : 'error'" text-color="white" size="small">
                        {{ item.active ? 'Active' : 'Inactive' }}
                    </v-chip>
                </template>
                <template v-slot:item.actions="{ item }">
                    <v-btn icon size="small" color="blue-grey" variant="text" @click="editCategory(item)">
                        <v-icon size="small">mdi-pencil</v-icon>
                    </v-btn>
                    <v-btn icon size="small" color="blue-grey" variant="text" @click="confirmDeleteCategory(item)">
                        <v-icon size="small">mdi-delete</v-icon>
                    </v-btn>
                </template>
            </v-data-table-server>
        </v-card-text>
    </v-card>
</template>

<script>
import { getCategories } from '@/utils/product-util';

export default {
    name: 'CategoryTable',
    data() {
        return {
            categorySearch: '',
            page: 1,
            itemsPerPage: 2, // Changed default to 10
            sortBy: [],
            sortDesc: [],
            totalItems: 0,
            categoryHeaders: [
                { title: 'Icon', key: 'icon', sortable: false },
                { title: 'Name', key: 'name', sortable: true },
                { title: 'Products', key: 'products', sortable: false },
                { title: 'Status', key: 'status', sortable: true },
                { title: 'Actions', key: 'actions', sortable: false, align: 'end' }
            ],
            categories: [],
            selectedCategory: null,
            categoryDialog: false,
            deleteDialog: false,
            deleteType: '',
            deleteItemId: null,
            deleteItemName: '',
            loading: false
        };
    },
    created() {
        // Load categories when component is created
        this.getCategories();
    },
    methods: {
        editCategory(item) {
            // Pass the category to edit to the modal
            this.selectedCategory = { ...item };
            this.categoryDialog = true;
        },
        updatePage(newPage) {
            console.log('Page changed to:', newPage);
            this.page = newPage;
            this.getCategories();
        },
        updateItemsPerPage(newItemsPerPage) {
            debugger
            console.log('Items per page changed to:', newItemsPerPage);
            this.itemsPerPage = newItemsPerPage.itemsPerPage;
            this.page = newItemsPerPage.page || 1; // Reset to first page
            this.getCategories();
        },
        updateSorting(sortBy) {
            console.log('Sorting changed:', sortBy);
            this.sortBy = sortBy;
            this.page = 1; // Reset to first page when sorting changes
            this.getCategories();
        },
        confirmDeleteCategory(item) {
            this.deleteType = 'category';
            this.deleteItemId = item.id;
            this.deleteItemName = item.name;
            this.deleteDialog = true;
        },
        async getCategories() {
            try {
                this.loading = true;
                
                // Construct query parameters
                const params = new URLSearchParams({
                    page: this.page - 1, // Convert to 0-based index
                    size: this.itemsPerPage
                });

                // Add sorting if available
                if (this.sortBy && this.sortBy.length > 0) {
                    const sortParams = this.sortBy.map((sortItem, index) => {
                        // Handle both old and new Vuetify sort formats
                        const key = typeof sortItem === 'string' ? sortItem : sortItem.key;
                        const order = this.sortDesc && this.sortDesc[index] ? 'desc' : 'asc';
                        return `${key},${order}`;
                    }).join(',');
                    params.append('sort', sortParams);
                }

                console.log('Fetching categories with params:', params.toString());
                const response = await getCategories(params);
                
                if (response && response.content) {
                    this.categories = response.content;
                    console.log(response)
                    this.totalItems = response.totalElements;
                    
                    // Add detailed debugging
                    console.log(`Pagination Debug:
                        - Current page: ${this.page}
                        - Items per page: ${this.itemsPerPage}
                        - Total items: ${this.totalItems}
                        - Items loaded: ${this.categories.length}
                        - Total pages: ${Math.ceil(this.totalItems / this.itemsPerPage)}
                        - Has next page: ${this.page < Math.ceil(this.totalItems / this.itemsPerPage)}`);
                        
                    // Force reactivity update
                    this.$forceUpdate();
                } else {
                    console.warn('No data received from getCategories');
                }
            } catch (error) {
                console.error('Error fetching categories:', error);
            } finally {
                this.loading = false;
            }
        }
    }
};
</script>