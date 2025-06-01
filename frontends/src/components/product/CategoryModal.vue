<template>
    <v-dialog v-model="dialogVisible" max-width="700px" persistent>
        <v-card rounded="xl" elevation="8">
            <!-- Header -->
            <div class="modal-header">
                <div class="d-flex align-center">
                    <div class="header-icon">
                        <v-icon color="white" size="24">mdi-tag-multiple</v-icon>
                    </div>
                    <div class="ms-4">
                        <h2 class="header-title">
                            {{ editedCategoryId ? 'تعديل التصنيف' : 'إضافة تصنيف جديد' }}
                        </h2>
                        <p class="header-subtitle">
                            {{ editedCategoryId ? 'تحديث بيانات التصنيف' : 'أدخل بيانات التصنيف الجديد' }}
                        </p>
                    </div>
                </div>
                <v-btn icon="mdi-close" variant="text" color="white" size="small" class="close-btn"
                    @click="closeDialog"></v-btn>
            </div>
            <!-- Form Content -->
            <div class="modal-body">
                <v-form ref="categoryForm" v-model="formValid">
                    <div class="form-section">
                        <div class="section-header">
                            <v-icon color="primary" class="me-2">mdi-information</v-icon>
                            <h3 class="section-title">المعلومات الأساسية</h3>
                        </div>
                        <v-row>
                            <!-- Category Name -->
                            <v-col cols="12" md="6">
                                <div class="form-group">
                                    <label class="form-label">
                                        اسم التصنيف <span class="required">*</span>
                                    </label>
                                    <v-text-field v-model="editedCategory.name" :rules="[rules.required]" variant="outlined"
                                        density="comfortable" placeholder="أدخل اسم التصنيف" hide-details="auto"
                                        class="modern-field"></v-text-field>
                                </div>
                            </v-col>

                            <!-- Icon Selection -->
                            <v-col cols="12" md="6">
                                <div class="form-group">
                                    <label class="form-label">
                                        الأيقونة <span class="required">*</span>
                                    </label>
                                    <v-select v-model="editedCategory.icon" :items="iconOptions" item-title="text"
                                        item-value="value" :rules="[rules.required]" variant="outlined"
                                        density="comfortable" placeholder="اختر الأيقونة" hide-details="auto" class="modern-field">
                                        <template v-slot:item="{ props, item }">
                                            <v-list-item v-bind="props">
                                                <template v-slot:prepend>
                                                    <v-icon :icon="item.raw.value" class="me-3"></v-icon>
                                                </template>
                                                <!-- <v-list-item-title>{{ item.raw.text }}</v-list-item-title> -->
                                            </v-list-item>
                                        </template>
                                        <template v-slot:selection="{ item }">
                                            <div class="d-flex align-center">
                                                <v-icon :icon="item.raw.value" class="me-2"></v-icon>
                                                <span>{{ item.raw.text }}</span>
                                            </div>
                                        </template>
                                    </v-select>
                                </div>

                            </v-col>

                            <!-- Color Selection -->
                            <v-col cols="12" md="6">
                                <div class="form-group">
                                    <label class="form-label">
                                        اللون <span class="required">*</span>
                                    </label>
                                    <v-color-picker style="background: transparent;" v-model="editedCategory.color"
                                        mode="hexa" hide-canvas hide-inputs swatches :rules="[rules.required]"
                                        class="modern-field"></v-color-picker>
                                </div>
                            </v-col>

                            <!-- Status Switch -->
                            <v-col cols="12" md="6">
                                <div class="form-group">
                                    <label class="form-label">
                                        حالة التصنيف
                                    </label>
                                    <div class="d-flex align-center mt-2">
                                        <v-switch v-model="editedCategory.active" color="primary" hide-details
                                            class="me-3"></v-switch>
                                        <span class="text-body-1">
                                            {{ editedCategory.active ? 'نشط' : 'غير نشط' }}
                                        </span>
                                    </div>
                                </div>
                            </v-col>

                            <!-- Description -->
                            <v-col cols="12">
                                <div class="form-group">
                                    <label class="form-label">
                                        الوصف
                                    </label>
                                    <v-textarea v-model="editedCategory.description" variant="outlined"
                                        density="comfortable" placeholder="أدخل وصف التصنيف..." rows="3" hide-details="auto"
                                        class="modern-field"></v-textarea>
                                </div>
                            </v-col>
                        </v-row>
                    </div>


                    <!-- Preview Card -->
                    <div class="form-section" v-if="editedCategory.name || editedCategory.icon">
                        <div class="section-header">
                            <v-icon color="primary" class="me-2">mdi-information</v-icon>
                            <h3 class="section-title"> معاينة التصنيف</h3>
                        </div>
                        <v-row>
                            <v-col cols="12" >

                                <div class="form-group">
                                    <v-card elevation="2" rounded="lg" class="pa-4 bg-grey-lighten-5">
                                        <div class="d-flex align-center">
                                            <v-avatar size="40" :color="editedCategory.color || 'grey'" class="me-3">
                                                <v-icon color="white" size="20"
                                                    :icon="editedCategory.icon || 'mdi-folder'"></v-icon>
                                            </v-avatar>
                                            <div class="flex-grow-1">
                                                <div class="text-subtitle-1 font-weight-medium">
                                                    {{ editedCategory.name || 'اسم التصنيف' }}
                                                </div>
                                                <div class="text-body-2 text-grey-darken-1">
                                                    {{ editedCategory.description || 'وصف التصنيف' }}
                                                </div>
                                            </div>
                                            <v-chip :color="editedCategory.active ? 'success' : 'error'" size="small"
                                                class="font-weight-medium">
                                                {{ editedCategory.active ? 'نشط' : 'غير نشط' }}
                                            </v-chip>
                                        </div>
                                    </v-card>
                                </div>

                            </v-col>
                        </v-row>
                    </div>

                </v-form>
            </div>

            <!-- Actions -->
            <div class="modal-actions">
                <div class="d-flex align-center justify-space-between">
                    <div class="form-status">
                        <v-icon :color="formValid ? 'success' : 'warning'" size="16" class="me-1">
                            {{ formValid ? 'mdi-check-circle' : 'mdi-alert-circle' }}
                        </v-icon>
                        <span class="text-caption" :class="formValid ? 'text-success' : 'text-warning'">
                            {{ formValid ? 'النموذج صحيح' : 'يرجى ملء الحقول المطلوبة' }}
                        </span>
                    </div>
                    <div class="d-flex ga-3">
                        <v-btn variant="outlined" color="grey-darken-1" size="large" class="cancel-btn"
                            @click="closeDialog">
                            إلغاء
                        </v-btn>
                        <v-btn color="primary" size="large" :disabled="!formValid" :loading="loading" class="save-btn"
                            @click="saveCategory">
                            {{ editedCategoryId ? 'تحديث التصنيف' : 'حفظ التصنيف' }}
                        </v-btn>
                    </div>

                </div>

            </div>
        </v-card>
    </v-dialog>
</template>

<script>
import { saveCategory, updateCategory } from '@/utils/product-util';
import { success, error } from '@/utils/system-util';

export default {
    name: 'CategoryModal',
    props: {
        modelValue: {
            type: Boolean,
            default: false
        },
        categoryToEdit: {
            type: Object,
            default: null
        }
    },
    emits: ['update:modelValue', 'save'],
    data() {
        return {
            formValid: false,
            loading: false,
            editedCategoryId: null,
            editedCategory: {
                name: '',
                icon: '',
                color: '',
                description: '',
                active: true
            },
            iconOptions: [
                { text: 'أجهزة كمبيوتر', value: 'mdi-laptop' },
                { text: 'هواتف ذكية', value: 'mdi-cellphone' },
                { text: 'أجهزة لوحية', value: 'mdi-tablet' },
                { text: 'كاميرا', value: 'mdi-camera' },
                { text: 'سماعات', value: 'mdi-headphones' },
                { text: 'ساعات ذكية', value: 'mdi-watch' },
                { text: 'ألعاب', value: 'mdi-gamepad-variant' },
                { text: 'مكتبة', value: 'mdi-book-open-variant' },
                { text: 'ملابس', value: 'mdi-tshirt-crew' },
                { text: 'أحذية', value: 'mdi-shoe-formal' },
                { text: 'مجوهرات', value: 'mdi-diamond' },
                { text: 'رياضة', value: 'mdi-basketball' },
                { text: 'منزل وحديقة', value: 'mdi-home' },
                { text: 'طعام ومشروبات', value: 'mdi-food' },
                { text: 'صحة وجمال', value: 'mdi-heart' },
                { text: 'أطفال', value: 'mdi-baby-carriage' },
                { text: 'سيارات', value: 'mdi-car' },
                { text: 'أدوات', value: 'mdi-hammer-screwdriver' },
                { text: 'مكتب', value: 'mdi-briefcase' },
                { text: 'فنون وحرف', value: 'mdi-palette' },
                // إضافات من أصناف SQL
                { text: 'فواكه', value: 'mdi-fruit-cherries' },
                { text: 'خضروات', value: 'mdi-carrot' },
                { text: 'مخبوزات', value: 'mdi-bread-slice' },
                { text: 'مشروبات', value: 'mdi-glass-cocktail' },
                { text: 'ألبان', value: 'mdi-cow' },
                { text: 'لحوم', value: 'mdi-food-steak' },
                { text: 'حلويات', value: 'mdi-cupcake' },
                { text: 'بهارات', value: 'mdi-shaker' },
                { text: 'حبوب', value: 'mdi-seed' },
                { text: 'معلبات', value: 'mdi-food-variant' },
                { text: 'مكسرات', value: 'mdi-peanut' },
                { text: 'معلبات لحوم', value: 'mdi-food-drumstick' },
                { text: 'زيوت ودهون', value: 'mdi-bottle-tonic' },
                { text: 'معكرونة وأرز', value: 'mdi-noodles' },
                { text: 'منتجات تنظيف', value: 'mdi-spray-bottle' },
                { text: 'منتجات أطفال', value: 'mdi-baby-face-outline' },
                { text: 'منتجات تجميل', value: 'mdi-lipstick' },
                { text: 'أطعمة مجمدة', value: 'mdi-snowflake' },
                { text: 'منتجات صحية', value: 'mdi-leaf' },
                { text: 'وجبات خفيفة', value: 'mdi-food' }
            ],
            rules: {
                required: value => !!value || 'هذا الحقل مطلوب'
            }
        };
    },
    computed: {
        dialogVisible: {
            get() {
                return this.modelValue;
            },
            set(value) {
                this.$emit('update:modelValue', value);
            }
        }
    },
    watch: {
        categoryToEdit: {
            immediate: true,
            handler(newCategory) {
                if (newCategory) {
                    this.editedCategory = { ...newCategory };
                    this.editedCategoryId = newCategory.id;
                } else {
                    this.resetForm();
                }
            }
        },
        modelValue(newValue) {
            if (!newValue) {
                this.resetForm();
            }
        }
    },
    methods: {
        resetForm() {
            this.editedCategoryId = null;
            this.editedCategory = {
                name: '',
                icon: '',
                color: '',
                description: '',
                active: true
            };
            if (this.$refs.categoryForm) {
                this.$refs.categoryForm.resetValidation();
            }
        },

        closeDialog() {
            this.dialogVisible = false;
        },

        async saveCategory() {
            if (!this.$refs.categoryForm.validate()) return;

            this.loading = true;
            try {
                const categoryData = {
                    ...this.editedCategory,
                    id: this.editedCategoryId,
                    productCount: this.editedCategoryId ? undefined : 0 // Don't update product count for existing categories
                };

                if (categoryData.id == null) {
                    // Creating new category
                    let response = await saveCategory(categoryData);
                    if (response != null && response.id != null) {
                        success('تم حفظ التصنيف بنجاح');
                        this.$emit('save', response);
                    } else {
                        error('فشل حفظ التصنيف');
                        console.log(response);
                    }
                } else {
                    // Updating existing category
                    let response = await updateCategory(categoryData);
                    if (response != null && response.id != null) {
                        success('تم تحديث التصنيف بنجاح');
                        this.$emit('save', response);
                    } else {
                        error('فشل تحديث التصنيف');
                        console.error(response);
                    }
                }

                this.closeDialog();
            } catch (err) {
                console.error('Error saving category:', err);
                error('فشل حفظ التصنيف');
            } finally {
                this.loading = false;
            }
        }
    }
};
</script>

<style scoped>
@import '@/styles/modal.css';

.modern-field .v-color-picker {
    border-radius: 8px !important;
}

.preview-card {
    border: 2px dashed #e2e8f0;
    border-radius: 8px;
    background: white;
}</style>