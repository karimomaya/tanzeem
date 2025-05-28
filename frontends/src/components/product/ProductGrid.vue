<template>
    <div class="product-grid">
        <v-row>
            <v-col 
                v-for="product in products" 
                :key="product.id"
                cols="12" 
                sm="6" 
                md="4" 
                lg="3"
                xl="2"
            >
                <v-card
                    class="product-card"
                    elevation="0"
                    @click="$emit('view', product)"
                >
                    <!-- Product Image Section -->
                    <div class="product-image-section">
                        <div class="product-image-container">
                            <v-img 
                                v-if="product.imageUrl" 
                                :src="product.imageUrl" 
                                :alt="product.name"
                                cover
                                class="product-image"
                                gradient="to bottom, rgba(0,0,0,0) 0%, rgba(0,0,0,0.1) 100%"
                            >
                                <template v-slot:error>
                                    <div class="image-fallback">
                                        <v-icon color="grey-lighten-1" size="32">mdi-package-variant</v-icon>
                                    </div>
                                </template>
                            </v-img>
                            <div v-else class="image-fallback">
                                <v-icon color="grey-lighten-1" size="32">mdi-package-variant</v-icon>
                            </div>
                            
                            <!-- Product Overlay -->
                            <div class="product-overlay">
                                <!-- Status Badge -->
                                <div class="status-badge">
                                    <v-chip
                                        :color="getStockColor(product.stock, product.minimumStock)"
                                        size="small"
                                        class="status-chip"
                                    >
                                        <v-icon start size="12">{{ getStockIcon(product.stock, product.minimumStock) }}</v-icon>
                                        {{ getStockText(product.stock, product.minimumStock) }}
                                    </v-chip>
                                </div>
                                
                                <!-- Quick Actions -->
                                <div class="quick-actions">
                                    <!-- <v-btn
                                        icon="mdi-heart-outline"
                                        size="small"
                                        variant="elevated"
                                        color="white"
                                        class="action-btn favorite-btn"
                                        @click.stop="toggleFavorite(product)"
                                    ></v-btn> -->
                                    
                                    <v-menu>
                                        <template v-slot:activator="{ props }">
                                            <v-btn
                                                v-bind="props"
                                                icon="mdi-dots-vertical"
                                                size="small"
                                                variant="elevated"
                                                color="white"
                                                class="action-btn"
                                                @click.stop
                                            ></v-btn>
                                        </template>
                                        <v-list density="compact" class="actions-menu">
                                            <v-list-item @click="$emit('view', product)" class="menu-item">
                                                <template v-slot:prepend>
                                                    <v-icon color="info" size="16">mdi-eye</v-icon>
                                                </template>
                                                <v-list-item-title>عرض التفاصيل</v-list-item-title>
                                            </v-list-item>
                                            
                                            <v-list-item @click="$emit('edit', product)" class="menu-item">
                                                <template v-slot:prepend>
                                                    <v-icon color="primary" size="16">mdi-pencil</v-icon>
                                                </template>
                                                <v-list-item-title>تعديل المنتج</v-list-item-title>
                                            </v-list-item>
                                            
                                            <v-list-item @click="duplicateProduct(product)" class="menu-item">
                                                <template v-slot:prepend>
                                                    <v-icon color="success" size="16">mdi-content-copy</v-icon>
                                                </template>
                                                <v-list-item-title>نسخ المنتج</v-list-item-title>
                                            </v-list-item>
                                            
                                            <v-divider class="my-1"></v-divider>
                                            
                                            <v-list-item @click="$emit('delete', product)" class="menu-item danger">
                                                <template v-slot:prepend>
                                                    <v-icon color="error" size="16">mdi-delete</v-icon>
                                                </template>
                                                <v-list-item-title class="text-error">حذف المنتج</v-list-item-title>
                                            </v-list-item>
                                        </v-list>
                                    </v-menu>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Product Content -->
                    <v-card-text class="product-content">
                        <!-- Category Badge -->
                        <div class="category-section">
                            <v-chip
                                :color="getCategoryColor(product.categoryId)"
                                size="x-small"
                                variant="tonal"
                                class="category-chip"
                            >
                                <v-icon start size="10">{{ getCategoryIcon(product.categoryId) }}</v-icon>
                                {{ getCategoryName(product.categoryId) }}
                            </v-chip>
                        </div>

                        <!-- Product Title -->
                        <h3 class="product-title">
                            {{ product.name }}
                        </h3>

                        <!-- Product SKU -->
                        <div class="product-sku">
                            <v-icon size="12" class="me-1">mdi-barcode</v-icon>
                            {{ product.sku }}
                        </div>

                        <!-- Product Description -->
                        <p v-if="product.description" class="product-description">
                            {{ truncateText(product.description, 60) }}
                        </p>

                        <!-- Price Section -->
                        <div class="price-section">
                            <div class="price-main">{{ formatPrice(product.price) }} ر.س</div>
                            <div class="price-label">السعر</div>
                        </div>

                        <!-- Stock Section -->
                        <div class="stock-section">
                            <div class="stock-info">
                                <div class="stock-indicator" :class="`stock-${getStockLevel(product.stock, product.minimumStock)}`">
                                    <div class="stock-dot"></div>
                                    <span class="stock-text">{{ getStockText(product.stock, product.minimumStock) }}</span>
                                </div>
                                <div class="stock-count">{{ product.stock }} في المخزون</div>
                            </div>
                            
                            <!-- Stock Progress Bar -->
                            <v-progress-linear
                                :model-value="getStockPercentage(product.stock, product.minimumStock)"
                                :color="getStockColor(product.stock, product.minimumStock)"
                                height="4"
                                rounded
                                class="stock-progress"
                            ></v-progress-linear>
                        </div>
                    </v-card-text>

                    <!-- Product Actions Footer -->
                    <v-card-actions class="product-actions">
                        <v-btn
                            variant="tonal"
                            color="primary"
                            size="small"
                            class="action-button"
                            prepend-icon="mdi-eye"
                            @click.stop="$emit('view', product)"
                        >
                            عرض
                        </v-btn>
                        
                        <v-btn
                            variant="tonal"
                            color="success"
                            size="small"
                            class="action-button"
                            prepend-icon="mdi-pencil"
                            @click.stop="$emit('edit', product)"
                        >
                            تعديل
                        </v-btn>
                        
                        <v-spacer></v-spacer>
                        
                        <v-btn
                            icon="mdi-delete"
                            size="small"
                            variant="text"
                            color="error"
                            class="delete-button"
                            @click.stop="$emit('delete', product)"
                        ></v-btn>
                    </v-card-actions>
                </v-card>
            </v-col>
        </v-row>

        <!-- Empty State -->
        <div v-if="products.length === 0" class="empty-state">
            <div class="empty-content">
                <div class="empty-icon">
                    <v-icon size="80" color="grey-lighten-2">mdi-package-variant-closed</v-icon>
                </div>
                <h3 class="empty-title">لا توجد منتجات</h3>
                <p class="empty-subtitle">لم يتم العثور على منتجات مطابقة لمعايير البحث</p>
            </div>
        </div>
    </div>
</template>

<script>
import { getStockStatus, getStockText, getStockColor, getStockIcon, getStockLevel, getStockPercentage } from '@/utils/product-util';
export default {
    name: 'ProductGrid',
    props: {
        products: {
            type: Array,
            default: () => []
        }
    },
    emits: ['view', 'edit', 'delete', 'duplicate', 'favorite'],
    data() {
        return {
            categoryMap: {
                1: { name: 'أجهزة كمبيوتر', icon: 'mdi-laptop', color: 'blue' },
                2: { name: 'هواتف ذكية', icon: 'mdi-cellphone', color: 'green' },
                3: { name: 'أجهزة لوحية', icon: 'mdi-tablet', color: 'purple' },
                4: { name: 'إكسسوارات', icon: 'mdi-headphones', color: 'orange' }
            }
        };
    },
    methods: {
        getStockStatus,
        getStockIcon,
        getStockText,
        getStockColor,
        getStockLevel,
        getStockPercentage,


        

        // Category methods
        getCategoryName(categoryId) {
            return this.categoryMap[categoryId]?.name || 'غير محدد';
        },

        getCategoryIcon(categoryId) {
            return this.categoryMap[categoryId]?.icon || 'mdi-folder';
        },

        getCategoryColor(categoryId) {
            return this.categoryMap[categoryId]?.color || 'grey';
        },

        // Utility methods
        formatPrice(price) {
            return new Intl.NumberFormat('ar-SA', {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2
            }).format(price);
        },

        truncateText(text, maxLength) {
            if (!text) return '';
            return text.length > maxLength ? text.substring(0, maxLength) + '...' : text;
        },

        // Action methods
        duplicateProduct(product) {
            const duplicatedProduct = { 
                ...product, 
                id: null, 
                name: `${product.name} - نسخة`,
                sku: `${product.sku}-COPY`
            };
            this.$emit('duplicate', duplicatedProduct);
        },

        toggleFavorite(product) {
            this.$emit('favorite', product);
        }
    }
};
</script>

<style scoped>
/* Grid Container */
.product-grid {
    width: 100%;
}

/* Product Card */
.product-card {
    background: white;
    border-radius: 16px;
    overflow: hidden;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    border: 1px solid #e2e8f0;
    height: 100%;
    display: flex;
    flex-direction: column;
}

.product-card:hover {
    transform: translateY(-8px);
    box-shadow: 0 12px 40px rgba(54, 96, 145, 0.15);
    border-color: #cbd5e0;
}

/* Image Section */
.product-image-section {
    position: relative;
    height: 200px;
    overflow: hidden;
}

.product-image-container {
    width: 100%;
    height: 100%;
    position: relative;
}

.product-image {
    width: 100%;
    height: 100%;
    transition: transform 0.3s ease;
}

.product-card:hover .product-image {
    transform: scale(1.05);
}

.image-fallback {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #f7fafc 0%, #edf2f7 100%);
}

/* Product Overlay */
.product-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(to bottom, rgba(0,0,0,0) 0%, rgba(0,0,0,0.3) 100%);
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    padding: 16px;
    opacity: 0;
    transition: opacity 0.3s ease;
}

.product-card:hover .product-overlay {
    opacity: 1;
}

.status-badge {
    align-self: flex-end;
}

.status-chip {
    backdrop-filter: blur(8px);
    background: rgba(255, 255, 255, 0.9) !important;
    font-weight: 600 !important;
    font-size: 11px !important;
}

.quick-actions {
    display: flex;
    gap: 8px;
    align-self: flex-end;
}

.action-btn {
    backdrop-filter: blur(8px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15) !important;
}

.favorite-btn:hover {
    color: #e53e3e !important;
}

/* Actions Menu */
.actions-menu {
    border-radius: 12px !important;
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15) !important;
    border: 1px solid #e2e8f0 !important;
}

.menu-item {
    border-radius: 8px !important;
    margin: 4px !important;
    transition: background-color 0.2s ease;
}

.menu-item:hover {
    background: rgba(54, 96, 145, 0.05) !important;
}

.menu-item.danger:hover {
    background: rgba(229, 62, 62, 0.05) !important;
}

/* Product Content */
.product-content {
    flex: 1;
    padding: 20px !important;
    display: flex;
    flex-direction: column;
    gap: 12px;
}

/* Category Section */
.category-section {
    margin-bottom: 4px;
}

.category-chip {
    font-size: 10px !important;
    height: 20px !important;
    font-weight: 600 !important;
}

/* Product Title */
.product-title {
    font-size: 16px;
    font-weight: 600;
    color: #2d3748;
    line-height: 1.3;
    margin: 0;
    overflow: hidden;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    min-height: 42px;
}

/* Product SKU */
.product-sku {
    display: flex;
    align-items: center;
    color: #718096;
    font-size: 12px;
    font-weight: 500;
}

/* Product Description */
.product-description {
    color: #a0aec0;
    font-size: 12px;
    line-height: 1.4;
    margin: 0;
    overflow: hidden;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
}

/* Price Section */
.price-section {
    margin: 8px 0;
}

.price-main {
    font-size: 20px;
    font-weight: 700;
    color: #366091;
    line-height: 1;
}

.price-label {
    font-size: 11px;
    color: #718096;
    font-weight: 500;
    margin-top: 2px;
}

/* Stock Section */
.stock-section {
    margin-top: auto;
}

.stock-info {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 8px;
}

.stock-indicator {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 12px;
    font-weight: 500;
}

.stock-dot {
    width: 6px;
    height: 6px;
    border-radius: 50%;
}

.stock-empty .stock-dot {
    background: #e53e3e;
}

.stock-low .stock-dot {
    background: #ed8936;
}

.stock-medium .stock-dot {
    background: #3182ce;
}

.stock-high .stock-dot {
    background: #48bb78;
}

.stock-empty .stock-text {
    color: #e53e3e;
}

.stock-low .stock-text {
    color: #ed8936;
}

.stock-medium .stock-text {
    color: #3182ce;
}

.stock-high .stock-text {
    color: #48bb78;
}

.stock-count {
    font-size: 11px;
    color: #718096;
}

.stock-progress {
    border-radius: 4px;
    overflow: hidden;
}

/* Product Actions */
.product-actions {
    padding: 16px 20px !important;
    background: #f8fafc;
    border-top: 1px solid #e2e8f0;
    gap: 8px;
}

.action-button {
    border-radius: 8px !important;
    font-weight: 500 !important;
    text-transform: none !important;
    font-size: 12px !important;
}

.delete-button {
    border-radius: 8px !important;
}

.delete-button:hover {
    background: rgba(229, 62, 62, 0.1) !important;
}

/* Empty State */
.empty-state {
    grid-column: 1 / -1;
    padding: 60px 20px;
    text-align: center;
}

.empty-content {
    max-width: 400px;
    margin: 0 auto;
}

.empty-icon {
    margin-bottom: 24px;
}

.empty-title {
    font-size: 20px;
    font-weight: 600;
    color: #2d3748;
    margin-bottom: 8px;
}

.empty-subtitle {
    color: #718096;
    font-size: 14px;
    line-height: 1.5;
    margin: 0;
}

/* Responsive Design */
@media (max-width: 768px) {
    .product-image-section {
        height: 160px;
    }
    
    .product-content {
        padding: 16px !important;
    }
    
    .product-title {
        font-size: 14px;
        min-height: 36px;
    }
    
    .price-main {
        font-size: 18px;
    }
    
    .product-actions {
        padding: 12px 16px !important;
    }
    
    .action-button {
        font-size: 11px !important;
        padding: 0 12px !important;
    }
    
    .quick-actions {
        gap: 4px;
    }
}

@media (max-width: 600px) {
    .product-image-section {
        height: 140px;
    }
    
    .product-title {
        font-size: 13px;
        min-height: 32px;
    }
    
    .price-main {
        font-size: 16px;
    }
    
    .empty-state {
        padding: 40px 20px;
    }
    
    .empty-title {
        font-size: 18px;
    }
}

/* Animation Classes */
.product-card {
    animation: fadeInUp 0.6s ease-out;
}

@keyframes fadeInUp {
    from {
        opacity: 0;
        transform: translateY(30px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

/* Loading Animation */
.product-card:nth-child(1) { animation-delay: 0.1s; }
.product-card:nth-child(2) { animation-delay: 0.2s; }
.product-card:nth-child(3) { animation-delay: 0.3s; }
.product-card:nth-child(4) { animation-delay: 0.4s; }
.product-card:nth-child(5) { animation-delay: 0.5s; }
.product-card:nth-child(6) { animation-delay: 0.6s; }

/* Print Styles */
@media print {
    .product-overlay,
    .product-actions {
        display: none !important;
    }
    
    .product-card {
        break-inside: avoid;
        box-shadow: none !important;
        border: 1px solid #000 !important;
    }
}
</style>