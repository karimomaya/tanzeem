<template>
    <div class="modal-header">
        <div class="d-flex align-center">
            <div class="header-icon">
                <v-icon color="white" size="24">{{ icon }}</v-icon>
            </div>
            <div class="ms-4">
                <h2 class="header-title">{{ title }}</h2>
                <p class="header-subtitle">{{ subtitle }}</p>
            </div>
        </div>
        <v-btn 
            icon="mdi-close" 
            variant="text" 
            color="white" 
            size="small" 
            class="close-btn"
            @click="$emit('close')"
        ></v-btn>
    </div>
</template>

<script>
export default {
    name: 'ModalHeader',
    props: {
        icon: {
            type: String,
            required: true
        },
        title: {
            type: String,
            required: true
        },
        subtitle: {
            type: String,
            default: ''
        }
    },
    emits: ['close']
}
</script>

<style scoped>
.modal-header {
    background: linear-gradient(135deg, #1976d2 0%, #1565c0 100%);
    color: white;
    padding: 24px 32px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    border-radius: 24px 24px 0 0;
    box-shadow: 0 4px 12px rgba(25, 118, 210, 0.15);
}

.header-icon {
    width: 56px;
    height: 56px;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.1);
}

.header-title {
    font-size: 1.5rem;
    font-weight: 600;
    margin: 0;
    line-height: 1.2;
}

.header-subtitle {
    font-size: 0.9rem;
    opacity: 0.9;
    margin: 4px 0 0 0;
    font-weight: 400;
}

.close-btn {
    background: rgba(255, 255, 255, 0.1) !important;
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.2);
    transition: all 0.2s ease-in-out;
}

.close-btn:hover {
    background: rgba(255, 255, 255, 0.2) !important;
    transform: scale(1.05);
}
</style>