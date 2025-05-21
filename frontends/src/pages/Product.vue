<template>
    <v-app>
        <v-main class="bg-grey-lighten-4">

            <v-container fluid>
                <v-spacer></v-spacer>
                <!-- Header -->
                <div class="d-flex align-center justify-space-between mb-6">
                    <v-container>
                        <v-row>
                            <v-col cols="12" sm="6" md="4">
                                <v-text-field v-model="productSearch" label="Search Products"
                                    prepend-inner-icon="mdi-magnify" variant="outlined" density="comfortable"
                                    hide-details></v-text-field>
                            </v-col>
                            <v-col cols="12" sm="6" md="4">
                                <v-select v-model="productCategoryFilter" :items="categories" item-title="name"
                                    item-value="id" label="Filter by Category" variant="outlined" density="comfortable"
                                    hide-details></v-select>
                            </v-col>
                            <v-col cols="12" sm="6" md="4">
                                <v-select v-model="productSort" :items="sortOptions" label="Sort by" variant="outlined"
                                    density="comfortable" hide-details></v-select>
                            </v-col>
                        </v-row>
                    </v-container>
                    <v-container>
                        <v-row justify="end">
                            <v-col cols="1" sm="12">
                                <v-btn class="float-left ma-1 font-weight-bold" color="#37ABE9" width="120" variant="flat"
                                    prepend-icon="mdi-plus"
                                    @click="activeTab === 'products' ? openNewProductDialog() : openNewCategoryDialog()">
                                    {{ addButtonLabel }}
                                </v-btn>

                                <v-btn class="float-left ma-1 font-weight-bold" color="#C4C4C4" width="120" variant="flat"
                                    @click="changeTab">
                                    {{ switchButtonLabel }}
                                </v-btn>
                            </v-col>
                        </v-row>
                    </v-container>

                </div>

                <!-- Main Content -->


                <v-card class="mb-6">
                    <v-window v-model="activeTab">
                        <!-- Products Tab -->
                        <v-window-item class="pt-2" value="products">
                            <v-card flat>
                                <v-card-text>


                                    <v-data-table :headers="productHeaders" :items="filteredProducts"
                                        :search="productSearch">
                                        <template v-slot:item.image="{ item }">
                                            <v-avatar size="40">
                                                <v-img :src="item.image" cover></v-img>
                                            </v-avatar>
                                        </template>
                                        <template v-slot:item.price="{ item }">
                                            ${{ item.price.toFixed(2) }}
                                        </template>
                                        <template v-slot:item.category="{ item }">
                                            <v-chip color="indigo" text-color="white" size="small">
                                                {{ getCategoryName(item.categoryId) }}
                                            </v-chip>
                                        </template>
                                        <template v-slot:item.stock="{ item }">
                                            <v-chip :color="getStockColor(item.stock)" text-color="white" size="small">
                                                {{ item.stock }}
                                            </v-chip>
                                        </template>
                                        <template v-slot:item.actions="{ item }">
                                            <v-btn icon size="small" color="blue-grey" variant="text"
                                                @click="editProduct(item)">
                                                <v-icon size="small">mdi-pencil</v-icon>
                                            </v-btn>
                                            <v-btn icon size="small" color="blue-grey" variant="text"
                                                @click="confirmDeleteProduct(item)">
                                                <v-icon size="small">mdi-delete</v-icon>
                                            </v-btn>
                                        </template>
                                    </v-data-table>
                                </v-card-text>
                            </v-card>
                        </v-window-item>

                        <!-- Categories Tab -->
                        <v-window-item value="categories">
                            <CategoryTable @edit-category="openEditCategoryDialog" ref="categoryTable" />
                        </v-window-item>
                    </v-window>
                </v-card>
            </v-container>
        </v-main>

        <!-- Product Dialog -->
        <v-dialog v-model="productDialog" max-width="700px">

            <v-card>
                <v-card-title class="d-flex justify-space-between align-center pa-4">
                    <span class="text-h6 font-weight-medium">إضافة منتج جديد</span>
                    <v-btn icon="mdi-close" variant="text" density="comfortable" @click="closeProductDialog"></v-btn>
                </v-card-title>

                <v-card-text>
                    <v-container>
                        <!-- Row 1: Product Name and SKU -->
                        <v-row>
                            <v-col cols="12" sm="6" class="d-flex flex-column">
                                <div class="text-subtitle-1 mb-2 text-end">اسم المنتج</div>
                                <v-text-field v-model="editedProduct.name" placeholder="اسم المنتج" variant="outlined"
                                    density="comfortable" hide-details class="mb-4"></v-text-field>
                            </v-col>

                            <v-col cols="12" sm="6" class="d-flex flex-column">
                                <div class="text-subtitle-1 mb-2 text-end">رمز المنتج (SKU)</div>
                                <v-text-field v-model="editedProduct.code" placeholder="مثال: IP15PRO" variant="outlined"
                                    density="comfortable" hide-details class="mb-4"></v-text-field>
                            </v-col>
                        </v-row>

                        <!-- Row 2: Category and Unit -->
                        <v-row>
                            <v-col cols="12" sm="6" class="d-flex flex-column">
                                <div class="text-subtitle-1 mb-2 text-end">الفئة</div>
                                <v-select v-model="editedProduct.category" :items="categories" item-title="name"
                                    item-value="id" placeholder="جوالات" variant="outlined" density="comfortable"
                                    hide-details class="mb-4"></v-select>
                            </v-col>

                            <v-col cols="12" sm="6" class="d-flex flex-column">
                                <div class="text-subtitle-1 mb-2 text-end">وحدة القياس</div>
                                <v-select v-model="editedProduct.unit" :items="['قطعة', 'صندوق', 'كيلوجرام', 'لتر']"
                                    placeholder="قطعة" variant="outlined" density="comfortable" hide-details
                                    class="mb-4"></v-select>
                            </v-col>
                        </v-row>

                        <!-- Row 3: Quantity and Price -->
                        <v-row>
                            <v-col cols="12" sm="6" class="d-flex flex-column">
                                <div class="text-subtitle-1 mb-2 text-end">الكمية</div>
                                <v-text-field v-model.number="editedProduct.quantity" type="number" placeholder="0"
                                    variant="outlined" density="comfortable" hide-details class="mb-4"></v-text-field>
                            </v-col>

                            <v-col cols="12" sm="6" class="d-flex flex-column">
                                <div class="text-subtitle-1 mb-2 text-end">السعر (ر.س)</div>
                                <v-text-field v-model.number="editedProduct.price" type="number" placeholder="0"
                                    variant="outlined" density="comfortable" hide-details class="mb-4"></v-text-field>
                            </v-col>
                        </v-row>
                    </v-container>
                </v-card-text>

                <v-card-actions class="pa-4">
                    <v-row justify="center" class="ma-0">
                        <v-btn color="grey-lighten-1" variant="text" width="150" class="me-4" @click="closeProductDialog">
                            إلغاء
                        </v-btn>
                        <v-btn color="primary" width="150" @click="saveProduct">
                            حفظ المنتج
                        </v-btn>
                    </v-row>
                </v-card-actions>
            </v-card>
        </v-dialog>
        <!-- Category Dialog -->
        <CategoryModal  v-model="categoryDialog" :categories="categories" :category-to-edit="selectedCategory"
            @save="handleCategorySave" />

        <!-- Delete Confirmation Dialog -->
        <v-dialog v-model="deleteDialog" max-width="400px">
            <v-card>
                <v-card-title class="text-h5 bg-error text-white">Delete Confirmation</v-card-title>
                <v-card-text class="pa-4 pt-6">
                    Are you sure you want to delete this {{ deleteType }}?
                    <div class="font-weight-bold mt-2">{{ deleteItemName }}</div>
                    <div v-if="deleteType === 'category' && getCategoryProductCount(deleteItemId) > 0"
                        class="mt-4 text-error">
                        Warning: This category has {{ getCategoryProductCount(deleteItemId) }} products associated with it.
                    </div>
                </v-card-text>
                <v-card-actions class="pa-4">
                    <v-btn variant="text" @click="deleteDialog = false">Cancel</v-btn>
                    <v-spacer></v-spacer>
                    <v-btn color="error" @click="deleteItem">Delete</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>

    </v-app>
</template>
  
<script>
import CategoryModal from '@/components/product/CategoryModal.vue';
import CategoryTable from '@/components/product/CategoryTable.vue';
export default {
    name: 'ProductsPage',
    components: {
        CategoryModal,
        CategoryTable
    },
    data() {
        return {
            drawer: false,
            activeTab: 'products',
            title: 'المنتجات',
            switchButtonLabel: 'التصنيفات',
            addButtonLabel: 'إضافة منتج',
            productSearch: '',
            productCategoryFilter: null,
            productSort: 'name-asc',

            productDialog: false,
            categoryDialog: false,
            selectedCategory: null,
            deleteDialog: false,
            deleteType: '',
            deleteItemId: null,
            deleteItemName: '',

            editedProductId: null,
            editedProduct: {
                name: '',
                categoryId: null,
                price: 0,
                stock: 0,
                sku: '',
                description: '',
                image: ''
            },

            productImage: null,

            productHeaders: [
                { title: 'Image', key: 'image', sortable: false },
                { title: 'Name', key: 'name', sortable: true },
                { title: 'SKU', key: 'sku', sortable: true },
                { title: 'Category', key: 'category', sortable: false },
                { title: 'Price', key: 'price', sortable: true },
                { title: 'Stock', key: 'stock', sortable: true },
                { title: 'Actions', key: 'actions', sortable: false, align: 'end' }
            ],



            // Sample data
            products: [
                { id: 1, name: 'MacBook Pro', sku: 'MBP-2023', categoryId: 1, price: 1999.99, stock: 15, image: 'https://cdn.vuetifyjs.com/images/cards/sunshine.jpg', description: 'Latest model with M2 chip' },
                { id: 2, name: 'iPhone 15 Pro', sku: 'IP15-PRO', categoryId: 2, price: 999.99, stock: 28, image: 'https://cdn.vuetifyjs.com/images/cards/plane.jpg', description: 'Latest iPhone model' },
                { id: 3, name: 'Samsung Galaxy S24', sku: 'SGS24', categoryId: 2, price: 899.99, stock: 12, image: 'https://cdn.vuetifyjs.com/images/cards/road.jpg', description: 'Android flagship phone' },
                { id: 4, name: 'iPad Pro', sku: 'IPD-2023', categoryId: 3, price: 799.99, stock: 5, image: 'https://cdn.vuetifyjs.com/images/cards/docks.jpg', description: 'Powerful tablet for professionals' },
                { id: 5, name: 'Dell XPS 15', sku: 'XPS-15-2023', categoryId: 1, price: 1599.99, stock: 0, image: 'https://cdn.vuetifyjs.com/images/cards/house.jpg', description: 'Premium Windows laptop' }
            ],

            sortOptions: [
                { title: 'Name (A-Z)', value: 'name-asc' },
                { title: 'Name (Z-A)', value: 'name-desc' },
                { title: 'Price (Low-High)', value: 'price-asc' },
                { title: 'Price (High-Low)', value: 'price-desc' },
                { title: 'Stock (Low-High)', value: 'stock-asc' },
                { title: 'Stock (High-Low)', value: 'stock-desc' }
            ]
        }
    },

    computed: {
        filteredProducts() {
            let filtered = [...this.products];

            // Apply category filter
            if (this.productCategoryFilter) {
                filtered = filtered.filter(product => product.categoryId === this.productCategoryFilter);
            }

            // Apply sorting
            const [sortField, sortDir] = this.productSort.split('-');

            filtered.sort((a, b) => {
                let comparison = 0;
                if (sortField === 'name') {
                    comparison = a.name.localeCompare(b.name);
                } else {
                    comparison = a[sortField] - b[sortField];
                }

                return sortDir === 'asc' ? comparison : -comparison;
            });

            return filtered;
        }
    },
    provide() {
        return {
            pageTitle: this.title,
        };
    },
    methods: {
        openEditCategoryDialog(category){
            this.selectedCategory = category; 
            this.categoryDialog = true; 
        },
        getCategoryName(categoryId) {
            const category = { "name": "ملابس" };
            return category ? category.name : 'Unknown';
        },

        getCategoryProductCount(categoryId) {
            return this.products.filter(product => product.categoryId === categoryId).length;
        },

        getStockColor(stock) {
            if (stock <= 0) return 'error';
            if (stock < 10) return 'warning';
            return 'success';
        },

        changeTab() {
            this.activeTab = this.activeTab === 'products' ? 'categories' : 'products';
            this.switchButtonLabel = this.activeTab === 'products' ? 'التصنيفات' : 'المنتجات';
            this.title = this.activeTab === 'products'? 'المنتجات' : 'التصنيفات';
            this.$emitter.emit('update-title', this.title);
            this.addButtonLabel = this.activeTab === 'products' ? 'إضافة منتج' : 'إضافة تصنيف'
            if (this.activeTab === 'categories') {
                this.$nextTick(() => {
                    if (this.$refs.categoryTable) {
                        this.$refs.categoryTable.getCategories();
                    }
                });
            }
        },

        openNewProductDialog() {
            this.editedProductId = null;
            this.editedProduct = {
                name: '',
                categoryId: this.categories.length > 0 ? this.categories[0].id : null,
                price: 0,
                stock: 0,
                sku: '',
                description: '',
                image: ''
            };
            this.productImage = null;
            this.productDialog = true;
        },

        closeProductDialog() {
            this.productDialog = false;
            this.editedProductId = null;
            this.editedProduct = {};
            this.productImage = null;
        },

        openNewCategoryDialog() {
            // Open the category dialog with no category selected (for new category)
            this.selectedCategory = null;
            this.categoryDialog = true;
        },

        editProduct(item) {
            this.editedProductId = item.id;
            this.editedProduct = { ...item };
            this.productDialog = true;
        },



        handleCategorySave(categoryData) {
            this.$nextTick(() => {
                if (this.$refs.categoryTable) {
                    this.$refs.categoryTable.getCategories();
                }
            });
        },

        confirmDeleteProduct(item) {
            this.deleteType = 'product';
            this.deleteItemId = item.id;
            this.deleteItemName = item.name;
            this.deleteDialog = true;
        },



        saveProduct() {
            // Here you would add validation logic

            // If there's an image, handle upload and get URL
            if (this.productImage) {
                // In a real app, you would upload the image to a server
                // For demo, we'll just pretend we got an image URL back
                this.editedProduct.image = 'https://cdn.vuetifyjs.com/images/cards/sunshine.jpg';
            }

            if (this.editedProductId === null) {
                // Create new product
                const newProduct = {
                    ...this.editedProduct,
                    id: Math.max(0, ...this.products.map(p => p.id)) + 1
                };
                this.products.push(newProduct);
            } else {
                // Update existing product
                const index = this.products.findIndex(p => p.id === this.editedProductId);
                if (index !== -1) {
                    this.products[index] = { ...this.editedProduct };
                }
            }

            this.productDialog = false;
        },

        deleteItem() {
            if (this.deleteType === 'product') {
                this.products = this.products.filter(p => p.id !== this.deleteItemId);
            } else if (this.deleteType === 'category') {
                this.categories = this.categories.filter(c => c.id !== this.deleteItemId);
                // In a real app, you might want to reassign products in this category
                // to another category or handle them in some way
            }

            this.deleteDialog = false;
        }
    },
    mounted() {
        // Emit the title when the component is mounted
        this.$emitter.emit('update-title', this.title);
        console.log('Product component mounted, emitted title:', this.title);
    }

}
</script>
  
<style scoped>
.v-application {
    /* background-color: #F4F7FA; */
    background-color: #FFF;
}

.v-card {
    border-radius: 8px;
}

.theme--light.v-text-field--outlined:not(.v-input--is-focused):not(.v-input--has-state)>.v-input__control>.v-input__slot fieldset {
    border-color: rgba(0, 0, 0, 0.12);
}
</style>