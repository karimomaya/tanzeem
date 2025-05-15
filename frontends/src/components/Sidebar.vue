<script setup>
import { ref, computed, onMounted, onBeforeUnmount, provide } from 'vue';
import { useMediaQuery } from '@vueuse/core';
import { Collapsible, CollapsibleContent, CollapsibleTrigger } from 'radix-vue';
import { PanelLeftIcon, LogOutIcon, UsersIcon, LayoutDashboardIcon, PackageIcon, ShoppingBagIcon, CreditCardIcon, ReceiptIcon } from 'lucide-vue-next';

// Constants
const SIDEBAR_COOKIE_NAME = "sidebar:state";
const SIDEBAR_COOKIE_MAX_AGE = 60 * 60 * 24 * 7; // 7 days
const SIDEBAR_WIDTH = "16rem";
const SIDEBAR_WIDTH_MOBILE = "18rem";
const SIDEBAR_WIDTH_ICON = "3rem";
const SIDEBAR_KEYBOARD_SHORTCUT = "b";

// Props and emits
const props = defineProps({
  side: {
    type: String,
    default: 'left',
    validator: (value) => ['left', 'right'].includes(value)
  },
  variant: {
    type: String,
    default: 'sidebar',
    validator: (value) => ['sidebar', 'floating', 'inset'].includes(value)
  },
  collapsible: {
    type: String,
    default: 'offcanvas',
    validator: (value) => ['offcanvas', 'icon', 'none'].includes(value)
  },
  defaultOpen: {
    type: Boolean,
    default: true
  }
});

// State
const isMobile = useMediaQuery('(max-width: 768px)');
const open = ref(props.defaultOpen);
const openMobile = ref(false);
const state = computed(() => open.value ? 'expanded' : 'collapsed');

// Navigation items
const navItems = [
  {
    title: "Dashboard",
    path: "/",
    icon: LayoutDashboardIcon
  },
  {
    title: "Products",
    path: "/products",
    icon: PackageIcon
  },
  {
    title: "Purchases",
    path: "/purchases",
    icon: ShoppingBagIcon
  },
  {
    title: "Sales",
    path: "/sales",
    icon: CreditCardIcon
  },
  {
    title: "Expenses",
    path: "/expenses",
    icon: ReceiptIcon
  },
  {
    title: "Users",
    path: "/users",
    icon: UsersIcon
  }
];

// Helper functions
const setOpen = (value) => {
  const openState = typeof value === 'function' ? value(open.value) : value;
  open.value = openState;
  
  // Set cookie to remember state
  document.cookie = `${SIDEBAR_COOKIE_NAME}=${openState}; path=/; max-age=${SIDEBAR_COOKIE_MAX_AGE}`;
};

const toggleSidebar = () => {
  return isMobile.value
    ? (openMobile.value = !openMobile.value)
    : setOpen(!open.value);
};

// Keyboard shortcut
const handleKeyDown = (event) => {
  if (event.key === SIDEBAR_KEYBOARD_SHORTCUT && (event.metaKey || event.ctrlKey)) {
    event.preventDefault();
    toggleSidebar();
  }
};

// Lifecycle hooks
onMounted(() => {
  window.addEventListener('keydown', handleKeyDown);
  
  // Read the cookie value on mount
  const cookies = document.cookie.split(';');
  for (const cookie of cookies) {
    const [name, value] = cookie.trim().split('=');
    if (name === SIDEBAR_COOKIE_NAME) {
      open.value = value === 'true';
      break;
    }
  }
});

onBeforeUnmount(() => {
  window.removeEventListener('keydown', handleKeyDown);
});

// Provide sidebar context to child components
provide('sidebar', {
  state, 
  open,
  setOpen,
  openMobile,
  isMobile,
  toggleSidebar
});
</script>

<template>
  <div class="sidebar-wrapper flex min-h-screen w-full" :class="{ 'bg-sidebar': variant === 'inset' }"
       :style="{
         '--sidebar-width': SIDEBAR_WIDTH,
         '--sidebar-width-icon': SIDEBAR_WIDTH_ICON,
       }">
    
    <!-- Mobile menu toggle button (outside sidebar) -->
    <div v-if="isMobile" class="fixed top-4 right-4 z-40">
      <button @click="openMobile = !openMobile"
              class="inline-flex items-center justify-center rounded-md border border-gray-200 bg-white px-3 py-2 text-sm font-medium shadow-sm hover:bg-gray-100">
        <PanelLeftIcon class="h-5 w-5" />
      </button>
    </div>
    
    <!-- Mobile sidebar -->
    <Transition name="slide">
      <div v-if="isMobile && openMobile" 
           class="fixed inset-y-0 z-30 flex w-full flex-col bg-white shadow-xl"
           :class="[side === 'left' ? 'left-0' : 'right-0']"
           :style="{ width: SIDEBAR_WIDTH_MOBILE }">
        <div class="flex h-16 items-center border-b px-6">
          <h2 class="text-lg font-bold">Sidebar</h2>
          <button @click="openMobile = false" class="ml-auto">
            <span class="sr-only">Close sidebar</span>
            <span class="text-lg">×</span>
          </button>
        </div>
        
        <div class="flex-1 overflow-auto p-4">
          <nav class="flex flex-col gap-1">
            <router-link v-for="item in navItems" 
                         :key="item.path" 
                         :to="item.path" 
                         v-slot="{ isActive }">
              <a class="flex items-center gap-3 rounded-md px-3 py-2 text-sm font-medium transition-colors"
                 :class="isActive ? 'bg-primary text-primary-foreground' : 'hover:bg-muted text-gray-700 hover:text-gray-900'">
                <component :is="item.icon" class="h-5 w-5" />
                <span>{{ item.title }}</span>
              </a>
            </router-link>
          </nav>
        </div>
        
        <div class="border-t p-4">
          <button class="flex w-full items-center justify-start gap-2 rounded-md border border-gray-200 px-4 py-2 text-sm font-medium hover:bg-gray-100">
            <LogOutIcon class="h-5 w-5" />
            <span>Log out</span>
          </button>
        </div>
      </div>
    </Transition>
    
    <!-- Desktop sidebar -->
    <div v-if="!isMobile" 
         class="group peer hidden md:block text-sidebar-foreground" 
         :data-state="state"
         :data-collapsible="state === 'collapsed' ? collapsible : ''"
         :data-variant="variant"
         :data-side="side">
      
      <!-- This handles the sidebar gap on desktop -->
      <div class="relative h-screen w-[--sidebar-width] bg-transparent transition-[width] duration-200 ease-linear"
           :class="{
             'w-0': collapsible === 'offcanvas' && state === 'collapsed',
             'rotate-180': side === 'right',
             [variant === 'floating' || variant === 'inset' 
               ? 'w-[calc(var(--sidebar-width-icon)_+_1rem)]' 
               : 'w-[--sidebar-width-icon]']: collapsible === 'icon' && state === 'collapsed'
           }">
      </div>
      
      <!-- The actual sidebar -->
      <div class="fixed inset-y-0 z-10 hidden h-screen w-[--sidebar-width] transition-all duration-200 ease-linear md:flex"
           :class="{
             'left-0': side === 'left',
             'right-0': side === 'right',
             'left-[calc(var(--sidebar-width)*-1)]': side === 'left' && collapsible === 'offcanvas' && state === 'collapsed',
             'right-[calc(var(--sidebar-width)*-1)]': side === 'right' && collapsible === 'offcanvas' && state === 'collapsed',
             'p-2': variant === 'floating' || variant === 'inset',
             'border-r': side === 'left',
             'border-l': side === 'right',
             [variant === 'floating' || variant === 'inset' 
               ? 'w-[calc(var(--sidebar-width-icon)_+_1rem_+_2px)]' 
               : 'w-[--sidebar-width-icon]']: collapsible === 'icon' && state === 'collapsed'
           }">
        
        <div data-sidebar="sidebar" 
             class="flex h-full w-full flex-col bg-sidebar"
             :class="{
               'rounded-lg border shadow': variant === 'floating'
             }">
          
          <!-- Sidebar Header -->
          <div class="flex h-16 items-center justify-between border-b px-4">
            <h2 class="text-lg font-bold" :class="{ 'opacity-0': state === 'collapsed' && collapsible === 'icon' }">Sidebar</h2>
            
            <button @click="toggleSidebar"
                    class="inline-flex h-8 w-8 items-center justify-center rounded-md hover:bg-gray-100">
              <PanelLeftIcon class="h-5 w-5" />
              <span class="sr-only">Toggle Sidebar</span>
            </button>
          </div>
          
          <!-- Sidebar Content -->
          <div class="flex min-h-0 flex-1 flex-col gap-2 overflow-auto p-2"
               :class="{ 'overflow-hidden': collapsible === 'icon' && state === 'collapsed' }">
            
            <nav class="flex flex-col gap-1">
              <router-link v-for="item in navItems" 
                          :key="item.path" 
                          :to="item.path" 
                          v-slot="{ isActive }">
                <a class="flex items-center gap-3 rounded-md px-3 py-2 text-sm font-medium transition-colors"
                  :class="isActive ? 'bg-primary text-primary-foreground' : 'hover:bg-muted text-gray-700 hover:text-gray-900'">
                  <component :is="item.icon" class="h-5 w-5" />
                  <span :class="{ 'opacity-0': state === 'collapsed' && collapsible === 'icon' }">{{ item.title }}</span>
                </a>
              </router-link>
            </nav>
            
          </div>
          
          <!-- Sidebar Footer -->
          <div class="border-t p-2">
            <button class="flex w-full items-center justify-start gap-2 rounded-md border border-gray-200 px-4 py-2 text-sm font-medium hover:bg-gray-100">
              <LogOutIcon class="h-5 w-5" />
              <span :class="{ 'opacity-0': state === 'collapsed' && collapsible === 'icon' }">Log out</span>
            </button>
          </div>
          
        </div>
      </div>
    </div>
    
    <!-- Main content -->
    <slot></slot>
  </div>
</template>

<style scoped>
.slide-enter-active,
.slide-leave-active {
  transition: transform 0.3s ease;
}

.slide-enter-from,
.slide-leave-to {
  transform: translateX(100%);
}
</style>