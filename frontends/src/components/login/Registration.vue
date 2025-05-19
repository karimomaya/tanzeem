<template>
   <v-app>
      <v-main class="bg-grey-lighten-4">
         <v-container fluid class="fill-height">
            <v-row class="fill-height">
               <!-- Left side with illustration and stats -->
               <v-col cols="12" md="7" class="d-flex align-center justify-center position-relative overflow-hidden">
                  <div class="illustration-background rounded-circle"></div>


                  <!-- 3D Character Image -->
                  <v-img
                     src="https://demos.pixinvent.com/vuexy-vuejs-admin-template/demo-1/assets/auth-v2-register-illustration-light-BRfYNCm0.png"
                     alt="3D Character" class="character-image" contain height="500"></v-img>
               </v-col>

               <!-- Right side with registration form -->
               <v-col cols="12" md="5" class="d-flex align-center justify-center">
                  <v-card class="register-card mx-auto" max-width="450" elevation="0" rounded="lg">
                     <v-card-text class="pa-8">
                        <div class="d-flex align-center mb-6">
                           <v-icon color="indigo" icon="mdi-cube-outline" size="32" class="mr-2"></v-icon>
                           <span class="text-h6 font-weight-bold">تنظيمي</span>
                        </div>

                        <h1 class="text-h4 font-weight-bold mb-2">المستقبل يبدء من هنا 🚀</h1>
                        <p class="text-body-1 text-grey mb-6">اجعل إدارة مشروعك أسهل!</p>

                        <v-form ref="form" v-model="isFormValid" @submit.prevent="formOnSubmit">
                           <v-text-field v-model="email" label="البريد الإلكتروني" variant="outlined"
                              prepend-inner-icon="mdi-email-outline" class="mb-3"
                              :rules="[rules.required, rules.email]"></v-text-field>

                           <v-text-field v-model="password" label="كلمة المرور" variant="outlined"
                              prepend-inner-icon="mdi-lock-outline"
                              :append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
                              :type="showPassword ? 'text' : 'password'" @click:append-inner="showPassword = !showPassword"
                              class="mb-3" :rules="[rules.required, rules.minLength(6)]"></v-text-field>

                           <v-text-field v-model="confirmPassword" label="تأكيد كلمة المرور" variant="outlined"
                              prepend-inner-icon="mdi-lock-outline" :type="showPassword ? 'text' : 'password'" class="mb-3"
                              :rules="[rules.required, rules.matchPassword]"></v-text-field>

                           <v-text-field v-model="tenantId" label="رقم المستأجر" variant="outlined"
                              prepend-inner-icon="mdi-lock-outline" class="mb-3" :rules="[rules.required]"></v-text-field>

                           <v-checkbox v-model="agreeTerms" :rules="[rules.requiredCheckbox]" class="mb-4">
                              <template v-slot:label>
                                 <div>
                                    أوافق علي
                                    <v-btn variant="text" color="indigo" class="px-1 text-decoration-underline"
                                       density="compact">سياسة الخصوصية والشروط</v-btn>
                                 </div>
                              </template>
                           </v-checkbox>

                           <v-btn color="indigo" size="large" block class="text-white mb-6" elevation="1" type="submit"
                              :disabled="!isFormValid">
                              إنشاء حساب
                           </v-btn>

                           <div class="text-center">
                              <span class="text-grey">هل لديك حساب بالفعل؟ </span>
                              <v-btn variant="text" color="indigo" class="px-1">
                                 <router-link to="/login">تسجيل الدخول بدلاً من ذلك</router-link>
                              </v-btn>
                           </div>
                        </v-form>
                     </v-card-text>
                  </v-card>
               </v-col>
            </v-row>
         </v-container>
      </v-main>
   </v-app>
</template>
 
<script>
import { registration } from '@/utils/auth-util.js'
import { error, success } from '@/utils/system-util.js'

export default {
   name: 'Registration',
   methods: {
      goToAbout() {
         this.$router.push('/about')
      },
      async formOnSubmit() {
         if (!this.$refs.form.validate()) return;

         let response = await registration({
            tenantId: this.tenantId,
            email: this.email,
            password: this.password
         })

         if(!response || !response.accessToken || !response.refreshToken) return;

         
         success('تم تسجيل الدخول بنجاح');
         localStorage.setItem('accessToken', response.accessToken);
         localStorage.setItem('refreshToken', response.refreshToken);

         window.location.href = '/registration';


      }
   },
   data() {
      return {
         tenantId: '',
         username: '',
         email: '',
         password: '',
         confirmPassword: '',
         showPassword: false,
         agreeTerms: false,
         isFormValid: false,
         rules: {
            required: (value) => !!value || 'هذا الحقل مطلوب',
            email: (value) => /.+@.+\..+/.test(value) || 'البريد الإلكتروني غير صالح',
            minLength: (min) => (value) => value.length >= min || `يجب أن تكون كلمة المرور ${min} أحرف على الأقل`,
            matchPassword: (value) => value === this.password || 'كلمات المرور غير متطابقة',
            requiredCheckbox: (value) => value || 'يجب الموافقة على الشروط والأحكام',
         },
      }
   }
}
</script>
 
<style scoped>
.illustration-background {
   position: absolute;
   width: 800px;
   height: 800px;
   background-color: rgba(103, 58, 183, 0.05);
   z-index: 0;
}

.character-image {
   position: relative;
   z-index: 2;
}

.stat-card {
   position: absolute;
   z-index: 3;
   background-color: white;
}

.satisfaction-card {
   top: 20%;
   left: 15%;
}

.users-card {
   bottom: 25%;
   right: 20%;
}

.register-card {
   background-color: white;
}
</style>