<template>
    <v-dialog v-model="dialogVisible" max-width="700px" persistent>
        <v-card rounded="xl" elevation="8">
            <!-- Header -->
            <v-card-title class="pa-0">
                <div class="d-flex align-center justify-space-between pa-6 bg-primary">
                    <div class="d-flex align-center">
                        <v-avatar size="40" color="white" class="me-3">
                            <v-icon color="primary">mdi-tag-multiple</v-icon>
                        </v-avatar>
                        <div>
                            <h2 class="text-h5 font-weight-bold text-white mb-0">
                                {{ editedCategoryId ? 'تعديل التصنيف' : 'إضافة تصنيف جديد' }}
                            </h2>
                            <p class="text-body-2 text-white opacity-90 mb-0">
                                {{ editedCategoryId ? 'تحديث بيانات التصنيف' : 'أدخل بيانات التصنيف الجديد' }}
                            </p>
                        </div>
                    </div>
                    <v-btn 
                        icon="mdi-close" 
                        variant="text" 
                        color="white" 
                        @click="closeDialog"
                    ></v-btn>
                </div>
            </v-card-title>

            <!-- Form Content -->
            <v-card-text class="pa-6">
                <v-form ref="categoryForm" v-model="formValid">
                    <v-row>
                        <!-- Category Name -->
                        <v-col cols="12" md="6">
                            <label class="text-subtitle-1 font-weight-medium text-grey-darken-2 mb-2 d-block">
                                اسم التصنيف *
                            </label>
                            <v-text-field
                                v-model="editedCategory.name"
                                :rules="[rules.required]"
                                variant="outlined"
                                density="comfortable"
                                placeholder="أدخل اسم التصنيف"
                                hide-details="auto"
                                class="mb-4"
                            ></v-text-field>
                        </v-col>

                        <!-- Icon Selection -->
                        <v-col cols="12" md="6">
                            <label class="text-subtitle-1 font-weight-medium text-grey-darken-2 mb-2 d-block">
                                الأيقونة *
                            </label>
                            <v-select
                                v-model="editedCategory.icon"
                                :items="iconOptions"
                                item-title="text"
                                item-value="value"
                                :rules="[rules.required]"
                                variant="outlined"
                                density="comfortable"
                                placeholder="اختر الأيقونة"
                                hide-details="auto"
                                class="mb-4"
                            >
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
                        </v-col>

                        <!-- Color Selection -->
                        <v-col cols="12" md="6">
                            <label class="text-subtitle-1 font-weight-medium text-grey-darken-2 mb-2 d-block">
                                اللون *
                            </label>
                            <v-select
                                v-model="editedCategory.color"
                                :items="colorOptions"
                                item-title="text"
                                item-value="value"
                                :rules="[rules.required]"
                                variant="outlined"
                                density="comfortable"
                                placeholder="اختر اللون"
                                hide-details="auto"
                                class="mb-4"
                            >
                                <template v-slot:item="{ props, item }">
                                    <v-list-item v-bind="props">
                                        <template v-slot:prepend>
                                            <v-avatar size="24" :color="item.raw.value" class="me-3"></v-avatar>
                                        </template>
                                        <!-- <v-list-item-title>{{ item.raw.text }}</v-list-item-title> -->
                                    </v-list-item>
                                </template>
                                <template v-slot:selection="{ item }">
                                    <div class="d-flex align-center">
                                        <v-avatar size="20" :color="item.raw.value" class="me-2"></v-avatar>
                                        <span>{{ item.raw.text }}</span>
                                    </div>
                                </template>
                            </v-select>
                        </v-col>

                        <!-- Status Switch -->
                        <v-col cols="12" md="6">
                            <label class="text-subtitle-1 font-weight-medium text-grey-darken-2 mb-2 d-block">
                                حالة التصنيف
                            </label>
                            <div class="d-flex align-center mt-2">
                                <v-switch
                                    v-model="editedCategory.active"
                                    color="primary"
                                    hide-details
                                    class="me-3"
                                ></v-switch>
                                <span class="text-body-1">
                                    {{ editedCategory.active ? 'نشط' : 'غير نشط' }}
                                </span>
                            </div>
                        </v-col>

                        <!-- Description -->
                        <v-col cols="12">
                            <label class="text-subtitle-1 font-weight-medium text-grey-darken-2 mb-2 d-block">
                                الوصف
                            </label>
                            <v-textarea
                                v-model="editedCategory.description"
                                variant="outlined"
                                density="comfortable"
                                placeholder="أدخل وصف التصنيف..."
                                rows="3"
                                hide-details="auto"
                                class="mb-4"
                            ></v-textarea>
                        </v-col>
                    </v-row>

                    <!-- Preview Card -->
                    <v-col cols="12" v-if="editedCategory.name || editedCategory.icon">
                        <label class="text-subtitle-1 font-weight-medium text-grey-darken-2 mb-2 d-block">
                            معاينة التصنيف
                        </label>
                        <v-card 
                            elevation="2" 
                            rounded="lg" 
                            class="pa-4 bg-grey-lighten-5"
                        >
                            <div class="d-flex align-center">
                                <v-avatar 
                                    size="40" 
                                    :color="editedCategory.color || 'grey'"
                                    class="me-3"
                                >
                                    <v-icon 
                                        color="white" 
                                        size="20"
                                        :icon="editedCategory.icon || 'mdi-folder'"
                                    ></v-icon>
                                </v-avatar>
                                <div class="flex-grow-1">
                                    <div class="text-subtitle-1 font-weight-medium">
                                        {{ editedCategory.name || 'اسم التصنيف' }}
                                    </div>
                                    <div class="text-body-2 text-grey-darken-1">
                                        {{ editedCategory.description || 'وصف التصنيف' }}
                                    </div>
                                </div>
                                <v-chip
                                    :color="editedCategory.active ? 'success' : 'error'"
                                    size="small"
                                    class="font-weight-medium"
                                >
                                    {{ editedCategory.active ? 'نشط' : 'غير نشط' }}
                                </v-chip>
                            </div>
                        </v-card>
                    </v-col>
                </v-form>
            </v-card-text>

            <!-- Actions -->
            <v-card-actions class="pa-6 pt-0">
                <v-spacer></v-spacer>
                <v-btn
                    variant="outlined"
                    color="grey-darken-1"
                    size="large"
                    class="me-3"
                    @click="closeDialog"
                >
                    إلغاء
                </v-btn>
                <v-btn
                    color="primary"
                    size="large"
                    :disabled="!formValid"
                    :loading="loading"
                    @click="saveCategory"
                >
                    {{ editedCategoryId ? 'تحديث التصنيف' : 'حفظ التصنيف' }}
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
import { saveCateogry, updateCateogry } from '@/utils/product-util';
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
                { text: 'فنون وحرف', value: 'mdi-palette' }
            ],
            colorOptions: [
                { text: 'أزرق', value: 'blue' },
                { text: 'بنفسجي', value: 'purple' },
                { text: 'أخضر', value: 'green' },
                { text: 'برتقالي', value: 'orange' },
                { text: 'أحمر', value: 'red' },
                { text: 'أزرق مخضر', value: 'teal' },
                { text: 'وردي', value: 'pink' },
                { text: 'نيلي', value: 'indigo' },
                { text: 'أصفر', value: 'amber' },
                { text: 'بني', value: 'brown' },
                { text: 'رمادي', value: 'grey' },
                { text: 'أزرق رمادي', value: 'blue-grey' }
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
                    let response = await saveCateogry(categoryData);
                    if (response != null && response.id != null) {
                        success('تم حفظ التصنيف بنجاح');
                        this.$emit('save', response);
                    } else {
                        error('فشل حفظ التصنيف');
                        console.log(response);
                    }
                } else {
                    // Updating existing category
                    let response = await updateCateogry(categoryData);
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
.v-card {
    overflow: visible;
}

label {
    font-size: 14px;
}

.v-switch {
    flex: none;
}
</style>