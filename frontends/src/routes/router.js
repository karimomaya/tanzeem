import { createRouter, createWebHistory } from 'vue-router';
import Login from '../pages/Login.vue'
import Registration from '../pages/Registration.vue'
import Product from '../pages/Product.vue'
import { isTokenValid } from '@/utils/auth-util'; // Adjust the import path as necessary

const routes = [
    {
      path: '/',
      name: 'Home',
      component: Login,
      meta: { hideLayout: true },
    },
    {
      path: '/login',
      name: 'login',
      component: Login,
      meta: { hideLayout: true },
    },
    {
      path: '/registration',
      name: 'registration',
      component: Registration,
      meta: { hideLayout: true },
    },
    {
      path: '/product',
      name: 'product',
      component: Product,
    },
  ];
  
  // Create the Vue Router instance
  const router = createRouter({
    history: createWebHistory(),
    routes,
  });

  router.beforeEach((to, from, next) => {
    if ((to.path == '/login' || to.path == '/registration') && isTokenValid()) {
      // Redirect to dashboard if token is valid
      next('/product');
    } else if ((to.path != '/login' && to.path != '/registration') && !isTokenValid()) {
      next('/login');
    }else {
      next();
    }
  });
  
  export default router;