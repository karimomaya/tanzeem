<template>
	<v-app>
		<v-main class="bg-grey-lighten-4">
			<v-container fluid class="fill-height">
				<v-row class="fill-height">
					<v-col cols="12" md="5" class="d-flex align-center justify-center">
						<v-card class="login-card mx-auto" max-width="450" elevation="0" rounded="lg">
							<v-card-text class="pa-8">
								<div class="d-flex align-center mb-6">
									<v-icon color="indigo" icon="mdi-cube-outline" size="32" class="mr-2"></v-icon>
									<span class="text-h6 font-weight-bold">تنظيمي</span>
								</div>

								<h1 class="text-h4 font-weight-bold mb-2">مرحبا بك في تنظيمي! 👋</h1>
								<p class="text-body-1 text-grey mb-6">رجاد تسجيل الدخول لبدء رحلتك</p>

								<v-form ref="form" v-model="isFormValid" @submit.prevent="formOnSubmit">
									<v-text-field v-model="email" label="البريد الإلكتروني" variant="outlined"
										prepend-inner-icon="mdi-email-outline" class="mb-4"
										:rules="[rules.required, rules.email]"></v-text-field>

									<v-text-field v-model="password" label="كلمة المرور" variant="outlined"
										prepend-inner-icon="mdi-lock-outline"
										:append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
										:type="showPassword ? 'text' : 'password'"
										@click:append-inner="showPassword = !showPassword"
										:rules="[rules.required, rules.minLength]"></v-text-field>

									<div class="d-flex justify-space-between align-center my-4">
										<v-checkbox v-model="rememberMe" label="تذكرني" density="compact"
											hide-details></v-checkbox>
										<v-btn variant="text" color="indigo" class="text-caption">لا تتذكر كلمه
											المرور؟</v-btn>
									</div>

									<v-btn color="indigo" size="large" block class="text-white mb-6" elevation="1"
										type="submit" :disabled="!isFormValid">
										تسجيل الدخول
									</v-btn>

									<div class="text-center mb-6">
										<span class="text-grey">هل انت مستخدم جديد؟ </span>
										<v-btn variant="text" color="indigo" class="px-1">
											<router-link to="/registration">انشاء حساب</router-link>
											</v-btn>
									</div>

								</v-form>
							</v-card-text>
						</v-card>
					</v-col>
					<!-- Left side with illustration and stats -->
					<v-col cols="12" md="7" class="d-flex align-center justify-center position-relative overflow-hidden">
						<div class="illustration-background rounded-circle"></div>


						<!-- 3D Character Image -->
						<v-img
							src="https://demos.pixinvent.com/vuexy-vuejs-admin-template/demo-1/assets/auth-v2-login-illustration-light-C4sKfRS1.png"
							alt="3D Character" class="character-image" contain height="500"></v-img>
					</v-col>

				</v-row>
			</v-container>
		</v-main>
	</v-app>
</template>

 
<script>

import { login } from '@/utils/auth-util.js'
import { success } from '@/utils/system-util.js'

export default {
	name: 'Login',
	data() {
		return {
			email: '',
			password: '',
			showPassword: false,
			rememberMe: false,
			isFormValid: false,
			rules: {
				required: value => !!value || 'This field is required.',
				email: value => /.+@.+\..+/.test(value) || 'Enter a valid email.',
				minLength: value => (value && value.length >= 6) || 'Password must be at least 6 characters long.'
			}
		}
	},
	methods: {
		async formOnSubmit() {
			if (!this.isFormValid) return;
			// Handle form submission
			const payload = {
				email: this.email,
				password: this.password,
				rememberMe: this.rememberMe,
			};



			let response = await login(payload)

			if (!response || !response.accessToken || !response.refreshToken) return;


			success('تم تسجيل الدخول بنجاح');
			localStorage.setItem('accessToken', response.accessToken);
			localStorage.setItem('refreshToken', response.refreshToken);

			window.location.href = '/login';

		}
	},
}
</script>

<style scoped>
.illustration-background {
	position: absolute;
	width: 500px;
	height: 500px;
	background-color: rgba(103, 58, 183, 0.05);
	z-index: 0;
	top: 140px;
}

.character-image {
	position: relative;
	z-index: 2;
}

.login-card {
	background-color: white;
}
</style>
 