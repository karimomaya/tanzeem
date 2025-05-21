<template>
    <v-dialog v-model="dialogVisible" max-width="700px">
        <v-card>
            <v-card-title class="d-flex justify-space-between align-center pa-4">
                <v-btn icon="mdi-close" variant="text" density="comfortable" @click="closeDialog"></v-btn>
                <span class="text-h6 font-weight-bold">
                    {{ editedCategoryId ? 'تعديل تصنيف جديد' : 'إضافة تصنيف جديد' }}
                </span>
            </v-card-title>

            <v-card-text>
                <v-container>
                    <v-form ref="categoryForm" v-model="formValid">
                        <!-- Row 1: Category Name -->
                        <v-row>
                            <v-col cols="12" sm="6" class="d-flex flex-column">
                                <v-text-field label="اسم التصنيف" v-model="editedCategory.name" :rules="[rules.required]"
                                    variant="outlined" density="comfortable" hide-details class="mb-4"
                                    placeholder="ادخل اسم التصنيف"></v-text-field>
                            </v-col>

                            <!-- Row 2: Icon Selector -->
                            <v-col cols="12" sm="6" class="d-flex flex-column">
                                <v-select v-model="editedCategory.icon" :items="iconOptions" label="اختر الأيقونة"
                                    :rules="[rules.required]" variant="outlined" density="comfortable" item-value="value"
                                    item-title="text" hide-details class="mb-4" return-object>
                                    <template v-slot:item="{ props, item }">
                                        <v-list-item v-bind="props" :title="item.raw.text">
                                            <template v-slot:prepend>
                                                <v-icon :icon="item.raw.value" class="me-2"></v-icon>
                                            </template>
                                        </v-list-item>
                                    </template>
                                    <template v-slot:selection="{ item }">
                                        <v-icon :icon="item.value" class="me-2"></v-icon>
                                        <span>{{ item.raw.text }}</span>
                                    </template>
                                </v-select>
                            </v-col>
                        </v-row>

                        <!-- Row 3: Description -->
                        <v-row>
                            <v-col cols="12" class="d-flex flex-column">
                                <v-textarea v-model="editedCategory.description" label="الوصف" placeholder="ادخل وصف الفئة"
                                    variant="outlined" density="comfortable" rows="3" hide-details
                                    class="mb-4"></v-textarea>
                            </v-col>
                        </v-row>

                        <!-- Row 4: Active Switch -->
                        <v-row>
                            <v-col cols="12" class="d-flex flex-column">
                                <v-switch v-model="editedCategory.active" color="primary" label="نشط"
                                    hide-details></v-switch>
                            </v-col>
                        </v-row>
                    </v-form>
                </v-container>
            </v-card-text>

            <v-card-actions class="pa-4">
                <v-row justify="end" class="ma-0">
                    <v-btn color="primary" variant="flat" width="100" :disabled="!formValid" @click="saveCategory">
                        حفظ
                    </v-btn>
                    <v-btn color="grey-lighten-1" variant="flat" width="100" class="mr-3" @click="closeDialog">
                        إلغاء
                    </v-btn>
                </v-row>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
import { saveCateogry, updateCateogry } from '@/utils/product-util';
export default {
    name: 'CategoryModal',
    data() {
        return {
            formValid: false,
            editedCategoryId: null,

            editedCategory: {
                name: '',
                icon: '',
                description: '',
                active: true
            },
            iconOptions: [
                { text: 'لابتوب', value: 'mdi-laptop' },
                { text: 'موبيل', value: 'mdi-cellphone' },
                { text: 'كاميرا', value: 'mdi-camera' }
            ],
            rules: {
                required: value => !!value || 'هذا الحقل مطلوب'
            }
        };
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
    watch: {
        categoryToEdit: {
            immediate: true,
            handler(newCategory) {
                if (newCategory) {
                    this.editedCategory = { ...newCategory };
                    this.editedCategoryId = newCategory.id;
                }
            }
        }
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
    methods: {
        closeDialog() {
            this.dialogVisible = false;
        },
        async saveCategory() {
            if (!this.$refs.categoryForm.validate()) return;

            const categoryData = {
                ...this.editedCategory,
                id: this.editedCategoryId,
                icon: this.editedCategory.icon?.value || this.editedCategory.icon // Extract the value
            };

            if (categoryData.id == null) {
                for (var i = 0; i < 24; i++) {
                    categoryData.name = categoryData.name + i;
                    let response = await saveCateogry(categoryData);
                    if (response != null && response.id != null) {
                        // this.$emit(response);
                        this.$emit('save', response);
                    } else {
                        console.log(response);
                    }
                }
            } else {
                let response = await updateCateogry(categoryData);
                if (response != null && response.id != null) {
                    // this.$emit(response);
                    this.$emit('save', response);
                } else {
                    console.log(response);
                }
            }

            this.dialogVisible = false;

        }
    }
};
</script>