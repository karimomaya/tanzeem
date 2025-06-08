<template>
    <div class="modal-actions">
        <div class="d-flex align-center justify-space-between">
            <!-- Form Status -->
            <div class="form-status" v-if="showStatus">
                <v-icon :color="formValid ? 'success' : 'warning'" size="16" class="me-1">
                    {{ formValid ? 'mdi-check-circle' : 'mdi-alert-circle' }}
                </v-icon>
                <span class="text-caption" :class="formValid ? 'text-success' : 'text-warning'">
                    {{ formValid ? validMessage : invalidMessage }}
                </span>
            </div>
            
            <!-- Spacer when no status -->
            <div v-else></div>

            <!-- Action Buttons -->
            <div class="d-flex ga-3">
                <!-- Cancel Button -->
                <v-btn 
                    variant="outlined" 
                    color="grey-darken-1" 
                    size="large" 
                    class="cancel-btn"
                    :disabled="cancelDisabled"
                    @click="$emit('cancel')"
                >
                    {{ cancelText }}
                </v-btn>

                <!-- Primary Action Button -->
                <v-btn 
                    color="primary" 
                    size="large" 
                    :disabled="primaryDisabled" 
                    :loading="loading" 
                    class="save-btn" 
                    @click="$emit('primary-action')"
                >
                    <v-icon start>{{ primaryIcon }}</v-icon>
                    {{ primaryText }}
                </v-btn>

                <!-- Secondary Action Button (Optional) -->
                <v-btn 
                    v-if="showSecondary"
                    :color="secondaryColor" 
                    size="large" 
                    :disabled="secondaryDisabled" 
                    :loading="secondaryLoading" 
                    class="secondary-btn" 
                    @click="$emit('secondary-action')"
                >
                    <v-icon start>{{ secondaryIcon }}</v-icon>
                    {{ secondaryText }}
                </v-btn>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: 'ModalActions',
    props: {
        // Form validation status
        showStatus: {
            type: Boolean,
            default: true
        },
        formValid: {
            type: Boolean,
            default: false
        },
        validMessage: {
            type: String,
            default: 'النموذج صحيح'
        },
        invalidMessage: {
            type: String,
            default: 'يرجى ملء الحقول المطلوبة'
        },
        
        // Cancel button
        cancelText: {
            type: String,
            default: 'إلغاء'
        },
        cancelDisabled: {
            type: Boolean,
            default: false
        },
        
        // Primary action button
        primaryText: {
            type: String,
            required: true
        },
        primaryIcon: {
            type: String,
            default: 'mdi-content-save'
        },
        primaryDisabled: {
            type: Boolean,
            default: false
        },
        loading: {
            type: Boolean,
            default: false
        },
        
        // Secondary action button (optional)
        showSecondary: {
            type: Boolean,
            default: false
        },
        secondaryText: {
            type: String,
            default: ''
        },
        secondaryIcon: {
            type: String,
            default: 'mdi-check'
        },
        secondaryColor: {
            type: String,
            default: 'success'
        },
        secondaryDisabled: {
            type: Boolean,
            default: false
        },
        secondaryLoading: {
            type: Boolean,
            default: false
        }
    },
    emits: ['cancel', 'primary-action', 'secondary-action']
}
</script>

<style scoped>
.modal-actions {
    padding: 20px 32px 24px;
    background: #fafbfc;
    border-top: 1px solid #e2e8f0;
    border-radius: 0 0 24px 24px;
}

.form-status {
    display: flex;
    align-items: center;
    background: white;
    padding: 8px 12px;
    border-radius: 8px;
    border: 1px solid #e2e8f0;
}

.cancel-btn {
    min-width: 100px;
    border-radius: 12px !important;
    font-weight: 500;
    transition: all 0.2s ease-in-out;
}

.cancel-btn:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.save-btn, .secondary-btn {
    min-width: 120px;
    border-radius: 12px !important;
    font-weight: 600;
    transition: all 0.2s ease-in-out;
    box-shadow: 0 2px 8px rgba(25, 118, 210, 0.3);
}

.save-btn:hover, .secondary-btn:hover {
    transform: translateY(-1px);
    box-shadow: 0 6px 20px rgba(25, 118, 210, 0.4);
}

.secondary-btn {
    box-shadow: 0 2px 8px rgba(76, 175, 80, 0.3);
}

.secondary-btn:hover {
    box-shadow: 0 6px 20px rgba(76, 175, 80, 0.4);
}
</style>