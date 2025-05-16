<template>
   <v-app>
     <v-main>
       <v-container style="padding: 0; height: 100%;" fluid class="fill-height d-flex align-center justify-center">
         <v-row>
           <v-col cols="12" md="6">
             <v-card class="pa-6 elevation-3" rounded="lg">
               <h2 class="font-weight-bold mb-4">Login</h2>
               <p class="text-subtitle-1">Don’t have an account yet? <v-btn text>Sign Up</v-btn></p>
 
               <v-form>
                 <v-text-field
                   label="Email Address"
                   type="email"
                   v-model="email"
                   outlined
                   class="mb-3"
                 ></v-text-field>
 
                 <v-text-field
                   label="Password"
                   type="password"
                   v-model="password"
                   outlined
                   class="mb-3"
                 ></v-text-field>
 
                 <v-checkbox v-model="remember" label="Remember me" class="mb-3"></v-checkbox>
 
                 <v-btn color="primary" block class="mb-3">Login</v-btn>
 
                 <p class="text-center my-4">Or continue with</p>
 
                 <v-btn block outlined color="red" class="mb-2">
                   <v-icon left>mdi-google</v-icon> Google
                 </v-btn>
                 <v-btn block outlined color="blue">
                   <v-icon left>mdi-facebook</v-icon> Facebook
                 </v-btn>
               </v-form>
             </v-card>
           </v-col>
 
           <v-col cols="12" md="6" style="height: 100%;" class="d-none d-md-flex align-center justify-center">
             <img style="height: 100%;" :src="loginImage" alt="Login Illustration" class="rounded-lg" />
           </v-col>
         </v-row>
       </v-container>
     </v-main>
   </v-app>
 </template>
 
 <script setup>
 import { ref } from 'vue';
 import loginImage from '@/assets/login-background.jpeg';
 
 const email = ref('');
 const password = ref('');
 const remember = ref(false);
 </script>
 
 <style scoped>
 .v-card {
   max-width: 400px;
 }
 </style>
 