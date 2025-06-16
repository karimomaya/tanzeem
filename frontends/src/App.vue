<template>
	<v-app>
		<v-main>
			<MainLayout />
		</v-main>
		
		<!-- Enhanced Error Snackbar -->
		<v-snackbar 
			v-model="showError" 
			:timeout="6000"
			location="top right"
			class="notification-snackbar"
			color="transparent"
			elevation="0"
		>
			<v-card
				class="notification-card error-card"
				rounded="xl"
				elevation="8"
				min-width="320"
				max-width="400"
			>
				<v-card-text class="pa-4">
					<div class="d-flex align-center">
						<v-avatar size="48" color="error" class="me-3 elevation-2">
							<v-icon color="white" size="24">mdi-alert-circle</v-icon>
						</v-avatar>
						<div class="flex-grow-1">
							<div class="text-subtitle-1 font-weight-bold text-error mb-1">خطأ</div>
							<div class="text-body-2 ">{{ errorMessage }}</div>
						</div>
						<v-btn
							icon="mdi-close"
							variant="text"
							size="small"
							color="grey-darken-1"
							@click="showError = false"
						></v-btn>
					</div>
				</v-card-text>
			</v-card>
		</v-snackbar>

		<!-- Enhanced Success Snackbar -->
		<v-snackbar 
			v-model="showSuccess" 
			:timeout="4000"
			location="top right"
			class="notification-snackbar"
			color="transparent"
			elevation="0"
		>
			<v-card
				class="notification-card success-card"
				rounded="xl"
				elevation="8"
				min-width="320"
				max-width="400"
			>
				<v-card-text class="pa-4">
					<div class="d-flex align-center">
						<v-avatar size="48" color="success" class="me-3 elevation-2">
							<v-icon color="white" size="24">mdi-check-circle</v-icon>
						</v-avatar>
						<div class="flex-grow-1">
							<div class="text-subtitle-1 font-weight-bold text-success mb-1">نجح</div>
							<div class="text-body-2 text-white">{{ successMessage }}</div>
						</div>
						<v-btn
							icon="mdi-close"
							variant="text"
							size="small"
							color="grey-darken-1"
							@click="showSuccess = false"
						></v-btn>
					</div>
				</v-card-text>
			</v-card>
		</v-snackbar>

		<!-- Enhanced Warning Snackbar -->
		<v-snackbar 
			v-model="showWarning" 
			:timeout="5000"
			location="top right"
			class="notification-snackbar"
			color="transparent"
			elevation="0"
		>
			<v-card
				class="notification-card warning-card"
				rounded="xl"
				elevation="8"
				min-width="320"
				max-width="400"
			>
				<v-card-text class="pa-4">
					<div class="d-flex align-center">
						<v-avatar size="48" color="warning" class="me-3 elevation-2">
							<v-icon color="white" size="24">mdi-alert</v-icon>
						</v-avatar>
						<div class="flex-grow-1">
							<div class="text-subtitle-1 font-weight-bold text-warning mb-1">تحذير</div>
							<div class="text-body-2 text-white">{{ warningMessage }}</div>
						</div>
						<v-btn
							icon="mdi-close"
							variant="text"
							size="small"
							color="grey-darken-1"
							@click="showWarning = false"
						></v-btn>
					</div>
				</v-card-text>
			</v-card>
		</v-snackbar>

		<!-- Info Snackbar (Bonus) -->
		<v-snackbar 
			v-model="showInfo" 
			:timeout="4000"
			location="top right"
			class="notification-snackbar"
			color="transparent"
			elevation="0"
		>
			<v-card
				class="notification-card info-card"
				rounded="xl"
				elevation="8"
				min-width="320"
				max-width="400"
			>
				<v-card-text class="pa-4">
					<div class="d-flex align-center">
						<v-avatar size="48" color="info" class="me-3 elevation-2">
							<v-icon color="white" size="24">mdi-information</v-icon>
						</v-avatar>
						<div class="flex-grow-1">
							<div class="text-subtitle-1 font-weight-bold text-info mb-1">معلومات</div>
							<div class="text-body-2 text-white">{{ infoMessage }}</div>
						</div>
						<v-btn
							icon="mdi-close"
							variant="text"
							size="small"
							color="grey-darken-1"
							@click="showInfo = false"
						></v-btn>
					</div>
				</v-card-text>
			</v-card>
		</v-snackbar>
	</v-app>
</template>
  
<script>
import MainLayout from '@/components/layouts/MainLayout.vue';
export default {
	components: {
		MainLayout,
	},
	data() {
		return {
			showError: false,
			showSuccess: false,
			showWarning: false,
			showInfo: false, // Optional new property
			errorMessage: '',
			successMessage: '',
			warningMessage: '',
			infoMessage: '' // Optional new property
		};
	},
	created() {
		// Listen for global errors using the emitter
		if (this.$emitter) {
			this.$emitter.on('global-error', (message) => {
				this.errorMessage = message;
				this.showError = true;
			});
			this.$emitter.on('global-success', (message) => {
				this.successMessage = message;
				this.showSuccess = true;
			});
			this.$emitter.on('global-warning', (message) => {
				this.warningMessage = message;
				this.showWarning = true;
			});
			this.$emitter.on('global-info', (message) => {
				this.infoMessage = message;
				this.showInfo = true;
			});
		} else {
			console.error('Emitter is not available in App.vue');
		}
	},
	beforeUnmount() {
		// Clean up the listener
		if (this.$emitter) {
			this.$emitter.off('global-error');
			this.$emitter.off('global-success');
			this.$emitter.off('global-warning');
			this.$emitter.off('global-info');
		}
	}
};
</script>
  
<style>
#app {
	font-family: Avenir, Helvetica, Arial, sans-serif;
	-webkit-font-smoothing: antialiased;
	-moz-osx-font-smoothing: grayscale;
	color: #2c3e50;
}
/* Notification Container Styles */
.notification-snackbar {
	top: 24px !important;
	right: 24px !important;
}

/* Base Card Styles */
.notification-card {
	border-left: 4px solid;
	backdrop-filter: blur(10px);
	background: rgba(255, 255, 255, 0.95) !important;
	animation: slideInRight 0.4s cubic-bezier(0.4, 0, 0.2, 1);
	transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.notification-card:hover {
	transform: translateY(-2px);
	box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15) !important;
}



/* Animation Keyframes */
@keyframes slideInRight {
	from {
		transform: translateX(100%);
		opacity: 0;
	}
	to {
		transform: translateX(0);
		opacity: 1;
	}
}

/* Avatar Animation */
.v-avatar {
	animation: pulseOnce 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes pulseOnce {
	0% {
		transform: scale(0.8);
		opacity: 0.7;
	}
	50% {
		transform: scale(1.05);
		opacity: 1;
	}
	100% {
		transform: scale(1);
		opacity: 1;
	}
}

/* Responsive Design */
@media (max-width: 600px) {
	.notification-snackbar {
		top: 16px !important;
		right: 16px !important;
		left: 16px !important;
	}
	
	.notification-card {
		min-width: auto !important;
		max-width: none !important;
	}
}

/* Dark Mode Support */
@media (prefers-color-scheme: dark) {
	.notification-card {
		background: rgba(30, 30, 30, 0.95) !important;
		border: 1px solid rgba(255, 255, 255, 0.1);
	}
}

/* Accessibility */
.notification-card:focus-within {
	outline: 2px solid rgb(var(--v-theme-primary));
	outline-offset: 2px;
}

/* Enhanced Close Button */
.v-btn:hover {
	transform: scale(1.1);
}
</style>