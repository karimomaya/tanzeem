<template>
    <v-dialog v-model="dialogVisible" max-width="1000px" persistent>
        <v-card rounded="xl" elevation="8">
            <!-- Header -->
            <ModalHeader :icon="'mdi-tag-multiple'"
                :title="editedCategoryId ? 'تعديل التصنيف' : 'إضافة تصنيف جديد'"
                :subtitle="editedCategoryId ? 'تحديث بيانات التصنيف' : 'أدخل بيانات التصنيف الجديد' "
                @close="closeDialog" />
            <!-- Form Content -->
            <div class="modal-body">
                <v-form ref="categoryForm" v-model="formValid">
                    <FormSection title="المعلومات الأساسية" icon="mdi-information" :color="SECTION_COLORS.basic">
                        <v-row>
                            <!-- Category Name -->
                            <v-col cols="12" md="6">
                                <div class="form-group">
                                    <label class="form-label">
                                        اسم التصنيف <span class="required">*</span>
                                    </label>
                                    <v-text-field v-model="editedCategory.name"
                                        :rules="fieldValidations.required('التصنيف')" variant="outlined"
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
                                    <v-select v-model="editedCategory.icon" :items="CATEGORY_ICON_OPTIONS" item-title="text"
                                        item-value="value" :rules="fieldValidations.required('الأيقونة')" variant="outlined"
                                        density="comfortable" placeholder="اختر الأيقونة" hide-details="auto"
                                        class="modern-field">
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
                                        mode="hexa" hide-canvas hide-inputs :swatches="colorSwatches"
                                        :rules="fieldValidations.required('اللون')" class="modern-field"></v-color-picker>
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
                    </FormSection>


                    <!-- Preview Card -->
                    <FormSection title="معاينة التصنيف" icon="mdi-eye" :color="SECTION_COLORS.preview"  v-if="editedCategory.name || editedCategory.icon">
                        <v-row>
                            <v-col cols="12">

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
                                            <v-chip :color="editedCategory.active ? 'success' : 'error'" size="small" class="font-weight-medium">
                                            {{ editedCategory.active ? 'نشط' : 'غير نشط' }}
                                            </v-chip>
                                        </div>
                                    </v-card>
                                </div>
                            </v-col>
                        </v-row>
                    </FormSection>
                </v-form>
            </div>
            <!-- Actions -->
            <ModalActions :form-valid="formValid" :loading="loading"
                :primary-text="editedCategoryId ? 'تحديث التصنيف' : 'حفظ التصنيف'"
                :primary-icon="editedCategoryId ? 'mdi-content-save' : 'mdi-plus'"
                :primary-disabled="!formValid" :cancel-disabled="loading"
                @cancel="closeDialog" @primary-action="saveCategory" />
        </v-card>
    </v-dialog>
</template>

<script>
import { saveCategory, updateCategory } from '@/services/product-service';
import { success, error } from '@/utils/system-util';
import { fieldValidations } from '@/utils/validation-util';
import { CATEGORY_ICON_OPTIONS } from '@/constants/icons';
import ModalHeader from '@/components/common/ModalHeader.vue'
import ModalActions from '@/components/common/ModalActions.vue'
import { SECTION_COLORS } from '@/constants/colors'
import FormSection from '@/components/common/FormSection.vue'

export default {
    name: 'CategoryModal',
    components: {
        ModalHeader,
        ModalActions,
        FormSection
    },
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
            SECTION_COLORS,
            CATEGORY_ICON_OPTIONS,
            fieldValidations,
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
            colorSwatches: [
                ['#F44336', '#E91E63', '#9C27B0', '#673AB7'],
                ['#3F51B5', '#2196F3', '#03A9F4', '#00BCD4'],
                ['#009688', '#4CAF50', '#8BC34A', '#CDDC39'],
                ['#FFEB3B', '#FFC107', '#FF9800', '#FF5722']
            ],
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
}
</style>