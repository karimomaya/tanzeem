import { createRouter, createWebHistory } from 'vue-router';
import Login from '../pages/Login.vue'

const routes = [
    {
      path: '/',
      name: 'login',
      component: Login,
    },
    {
      path: '/login',
      name: 'login',
      component: Login,
    },
  ];
  
  // Create the Vue Router instance
  const router = createRouter({
    history: createWebHistory(),
    routes,
  });
  
  export default router;