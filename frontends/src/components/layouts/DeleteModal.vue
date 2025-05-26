<template>
    <v-dialog v-model="dialogVisible" max-width="500px" persistent>
        <v-card rounded="xl" elevation="12" class="overflow-hidden">
            <!-- Header with Icon -->
            <div class="text-center pt-8 pb-4" style="background: linear-gradient(135deg, #ff5252 0%, #f44336 100%);">
                <v-avatar size="80" color="white" class="elevation-4 mb-4">
                    <v-icon size="40" color="error">mdi-delete-alert</v-icon>
                </v-avatar>
                <h2 class="text-h4 font-weight-bold text-white mb-2">تأكيد الحذف</h2>
                <p class="text-body-1 text-white opacity-90 mb-0">هذا الإجراء لا يمكن التراجع عنه</p>
            </div>

            <!-- Content -->
            <v-card-text class="pa-8 text-center">
                <div class="mb-6">
                    <v-icon size="60" color="warning" class="mb-4">mdi-alert-circle-outline</v-icon>
                    <h3 class="text-h5 font-weight-medium text-grey-darken-3 mb-4">
                        هل أنت متأكد من هذا الإجراء؟
                    </h3>
                    <div class="text-body-1 text-grey-darken-1 mb-4">
                        سيتم حذف 
                        <span class="font-weight-bold text-error mx-1">{{ deleteItemName }}</span>
                        نهائياً من النظام
                    </div>
                    <div class="text-body-2 text-grey-darken-1">
                        {{ getDeleteMessage() }}
                    </div>
                </div>

                <!-- Warning Alert -->
                <v-alert
                    type="warning"
                    variant="tonal"
                    rounded="lg"
                    class="mb-6"
                    icon="mdi-alert-triangle"
                    border="start"
                >
                    <v-alert-title class="text-right mb-2">تحذير مهم</v-alert-title>
                    لا يمكن التراجع عن هذا الإجراء. تأكد من أنك تريد المتابعة.
                </v-alert>
            </v-card-text>

            <!-- Actions -->
            <v-card-actions class="pa-6 pt-0">
                <v-row class="ma-0">
                    <v-col cols="12" sm="6" class="pa-1">
                        <v-btn
                            variant="outlined"
                            color="grey-darken-1"
                            size="large"
                            block
                            rounded="lg"
                            class="font-weight-medium"
                            @click="dialogVisible = false"
                        >
                            <v-icon start>mdi-close</v-icon>
                            إلغاء
                        </v-btn>
                    </v-col>
                    <v-col cols="12" sm="6" class="pa-1">
                        <v-btn
                            color="error"
                            size="large"
                            block
                            rounded="lg"
                            class="font-weight-medium"
                            :loading="loading"
                            @click="deleteItem"
                        >
                            <v-icon start>mdi-delete</v-icon>
                            تأكيد الحذف
                        </v-btn>
                    </v-col>
                </v-row>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
export default {
    name: 'DeleteModal',
    props: {
        modelValue: {
            type: Boolean,
            default: false
        }
    },
    emits: ['update:modelValue', 'delete-confirmed'],
    data() {
        return {
            deleteType: '',
            deleteItemId: null,
            deleteItemName: '',
            loading: false
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
    methods: {
        deleteConfirmation(type, id, name) {
            this.deleteType = type;
            this.deleteItemId = id;
            this.deleteItemName = name;
        },

        getDeleteMessage() {
            switch (this.deleteType) {
                case 'product':
                    return 'سيؤثر حذف هذا المنتج على التقارير والإحصائيات المرتبطة به';
                case 'category':
                    return 'سيؤثر حذف هذا التصنيف على جميع المنتجات المرتبطة به';
                default:
                    return 'تأكد من أنك تريد حذف هذا العنصر نهائياً';
            }
        },

        async deleteItem() {
            this.loading = true;
            
            try {
                // Add a small delay for better UX
                await new Promise(resolve => setTimeout(resolve, 500));
                
                this.$emit('delete-confirmed', {
                    id: this.deleteItemId, 
                    type: this.deleteType
                });
                
                this.dialogVisible = false;
            } catch (error) {
                console.error('Error during delete:', error);
            } finally {
                this.loading = false;
            }
        }
    }
};
</script>

<style scoped>
.v-card {
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15) !important;
}

.v-alert {
    border-right: 4px solid rgb(var(--v-theme-warning)) !important;
    border-left: none !important;
}

.v-btn {
    text-transform: none !important;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.v-btn:hover {
    transform: translateY(-1px);
}

.v-avatar {
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.v-avatar:hover {
    transform: scale(1.05);
}
</style>