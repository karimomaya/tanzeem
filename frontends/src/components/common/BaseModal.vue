<template>
    <v-dialog v-model="dialogVisible" :max-width="maxWidth" persistent scrollable>
        <v-card rounded="xl" elevation="8">
            <!-- Header -->
            <ModalHeader :icon="icon" :title="title" :subtitle="subtitle" @close="$emit('close')" />

            <!-- Content -->
            <div class="modal-body">
                <v-form ref="form" v-model="formValid" lazy-validation>
                    <slot name="content" :form-valid="formValid" />
                </v-form>
            </div>

            <!-- Actions -->
            <ModalActions :form-valid="formValid" :loading="loading" :primary-text="primaryText"
                :primary-icon="primaryIcon" :primary-disabled="primaryDisabled || !formValid" :cancel-disabled="loading"
                @cancel="$emit('close')" @primary-action="$emit('save')" />
        </v-card>
    </v-dialog>
</template>

<script>
import ModalHeader from '@/components/common/ModalHeader.vue'
import ModalActions from '@/components/common/ModalActions.vue'

export default {
    name: 'BaseModal',
    components: { ModalHeader, ModalActions },
    data() {
        return {
            formValid: false
        }
    },
    props: {
        modelValue: { type: Boolean, default: false },
        maxWidth: { type: String, default: '1000px' },
        icon: { type: String, required: true },
        title: { type: String, required: true },
        subtitle: { type: String, required: true },
        primaryText: { type: String, required: true },
        primaryIcon: { type: String, required: true },
        primaryDisabled: { type: Boolean, default: false },
        loading: { type: Boolean, default: false },
    },
    emits: ['update:modelValue', 'close', 'save'],
    computed: {
        dialogVisible: {
            get() { return this.modelValue },
            set(value) { this.$emit('update:modelValue', value) }
        }
    },
    methods: {
        validate() {
            return this.$refs.form?.validate() || false
        },
        resetValidation() {
            this.$refs.form?.resetValidation()
        }
    }
}
</script>

<style scoped>
@import '@/styles/modal.css';
</style>