<template>
  <v-app class="modern-app">
    <v-layout>
      <!-- Enhanced Navigation Drawer -->
      <v-navigation-drawer
        v-model="drawer"
        permanent
        width="280"
        class="modern-sidebar"
        elevation="0"
      >
        <!-- Logo Section -->
        <div class="sidebar-header">
          <div class="logo-container">
            <div class="logo-icon">
              <v-icon color="white" size="28">mdi-cube-outline</v-icon>
            </div>
            <div class="logo-text">
              <h2 class="logo-title">تنظيمي</h2>
              <p class="logo-subtitle">نظام إدارة المخزون</p>
            </div>
          </div>
        </div>

        <!-- User Profile Section -->
        <div class="user-section">
          <div class="user-card">
            <v-avatar size="48" class="user-avatar">
              <v-img src="https://cdn.vuetifyjs.com/images/john.jpg" alt="User"></v-img>
            </v-avatar>
            <div class="user-info">
              <h3 class="user-name">أحمد محمد</h3>
              <p class="user-role">مدير النظام</p>
              <div class="user-status">
                <div class="status-dot"></div>
                <span class="status-text">متصل</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Navigation Menu -->
        <div class="navigation-section">
          <div class="nav-group">
            <h4 class="nav-group-title">القائمة الرئيسية</h4>
            <div class="nav-items">
              <div
                v-for="item in mainItems"
                :key="item.value"
                :class="['nav-item', { active: group === item.value }]"
                @click="handleNavigation(item)"
              >
                <div class="nav-item-content">
                  <div class="nav-item-icon">
                    <v-icon :color="group === item.value ? 'white' : 'primary'" size="20">
                      {{ item.icon }}
                    </v-icon>
                  </div>
                  <span class="nav-item-text">{{ item.title }}</span>
                  <v-badge
                    v-if="item.badge"
                    :content="item.badge"
                    :color="item.badgeColor || 'error'"
                    floating
                    class="nav-item-badge"
                  ></v-badge>
                </div>
              </div>
            </div>
          </div>

          <div class="nav-group">
            <h4 class="nav-group-title">إدارة النظام</h4>
            <div class="nav-items">
              <div
                v-for="item in systemItems"
                :key="item.value"
                :class="['nav-item', { active: group === item.value }]"
                @click="handleNavigation(item)"
              >
                <div class="nav-item-content">
                  <div class="nav-item-icon">
                    <v-icon :color="group === item.value ? 'white' : 'primary'" size="20">
                      {{ item.icon }}
                    </v-icon>
                  </div>
                  <span class="nav-item-text">{{ item.title }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Bottom Actions -->
        <template v-slot:append>
          <div class="sidebar-footer">
            <div class="footer-actions">
              <v-btn
                prepend-icon="mdi-help-circle-outline"
                variant="text"
                color="primary"
                class="footer-btn"
                block
              >
                المساعدة والدعم
              </v-btn>
              <v-btn
                prepend-icon="mdi-logout"
                variant="text"
                color="error"
                class="footer-btn"
                block
              >
                تسجيل الخروج
              </v-btn>
            </div>
          </div>
        </template>
      </v-navigation-drawer>

      <!-- Enhanced App Bar -->
      <v-app-bar
        elevation="0"
        height="80"
        class="modern-appbar"
      >
        <!-- Mobile Menu Toggle -->
        <v-app-bar-nav-icon
          variant="text"
          @click="drawer = !drawer"
          class="d-lg-none mobile-nav-toggle"
          color="primary"
        ></v-app-bar-nav-icon>

        <!-- Page Title Section -->
        <div class="page-header">
          <div class="page-title-section">
            <h1 class="page-title">{{ currentPageTitle }}</h1>
            <p class="page-subtitle">{{ currentPageSubtitle }}</p>
          </div>
          
          <!-- Breadcrumbs -->
          <v-breadcrumbs
            :items="breadcrumbs"
            class="page-breadcrumbs"
          >
            <template v-slot:divider>
              <v-icon icon="mdi-chevron-left" size="14" color="grey-lighten-1"></v-icon>
            </template>
          </v-breadcrumbs>
        </div>

        <v-spacer></v-spacer>

        <!-- Action Section -->
        <div class="action-section">
          <!-- Search -->
          <div class="search-container">
            <v-expand-transition>
              <v-text-field
                v-if="showSearch"
                v-model="searchQuery"
                placeholder="البحث في النظام..."
                prepend-inner-icon="mdi-magnify"
                variant="outlined"
                density="compact"
                hide-details
                autofocus
                class="search-field"
                @blur="hideSearch"
              ></v-text-field>
            </v-expand-transition>
            
            <v-btn
              v-if="!showSearch"
              icon="mdi-magnify"
              variant="text"
              size="large"
              color="grey-darken-2"
              class="action-btn"
              @click="toggleSearch"
            ></v-btn>
          </div>

          <!-- Quick Actions -->
          <v-menu offset-y>
            <template v-slot:activator="{ props }">
              <v-btn
                v-bind="props"
                icon="mdi-plus-circle"
                variant="tonal"
                size="large"
                color="primary"
                class="action-btn quick-action-btn"
              ></v-btn>
            </template>
            <v-card width="320" class="quick-actions-menu">
              <v-card-title class="menu-header">
                <v-icon start color="primary">mdi-lightning-bolt</v-icon>
                الإجراءات السريعة
              </v-card-title>
              <v-card-text class="pa-2">
                <div class="quick-actions-grid">
                  <div
                    v-for="action in quickActions"
                    :key="action.action"
                    class="quick-action-item"
                    @click="handleQuickAction(action.action)"
                  >
                    <div class="quick-action-icon" :style="{ background: getActionGradient(action.color) }">
                      <v-icon color="white" size="20">{{ action.icon }}</v-icon>
                    </div>
                    <span class="quick-action-text">{{ action.title }}</span>
                  </div>
                </div>
              </v-card-text>
            </v-card>
          </v-menu>

          <!-- Notifications -->
          <v-menu offset-y width="400">
            <template v-slot:activator="{ props }">
              <v-btn
                v-bind="props"
                variant="text"
                size="large"
                color="grey-darken-2"
                class="action-btn notification-btn"
              >
                <v-badge
                  v-if="unreadNotifications > 0"
                  :content="unreadNotifications"
                  color="error"
                  floating
                >
                  <v-icon>mdi-bell-outline</v-icon>
                </v-badge>
                <v-icon v-else>mdi-bell-outline</v-icon>
              </v-btn>
            </template>
            
            <v-card class="notifications-menu">
              <div class="notifications-header">
                <h3 class="notifications-title">الإشعارات</h3>
                <v-btn
                  v-if="unreadNotifications > 0"
                  variant="text"
                  size="small"
                  color="primary"
                  @click="markAllAsRead"
                >
                  تحديد الكل كمقروء
                </v-btn>
              </div>
              
              <div class="notifications-content">
                <div
                  v-for="notification in notifications"
                  :key="notification.id"
                  :class="['notification-item', { unread: !notification.read }]"
                  @click="markAsRead(notification.id)"
                >
                  <div class="notification-avatar">
                    <v-avatar size="40" :color="notification.color">
                      <v-icon color="white" size="20">{{ notification.icon }}</v-icon>
                    </v-avatar>
                  </div>
                  <div class="notification-content">
                    <h4 class="notification-title">{{ notification.title }}</h4>
                    <p class="notification-message">{{ notification.message }}</p>
                    <span class="notification-time">{{ notification.time }}</span>
                  </div>
                  <div v-if="!notification.read" class="notification-indicator"></div>
                </div>
                
                <div v-if="notifications.length === 0" class="no-notifications">
                  <v-icon size="48" color="grey-lighten-2">mdi-bell-sleep</v-icon>
                  <p class="no-notifications-text">لا توجد إشعارات جديدة</p>
                </div>
              </div>
              
              <div class="notifications-footer">
                <v-btn
                  variant="text"
                  color="primary"
                  size="small"
                  block
                >
                  عرض جميع الإشعارات
                </v-btn>
              </div>
            </v-card>
          </v-menu>

          <!-- Settings -->
          <v-menu offset-y>
            <template v-slot:activator="{ props }">
              <v-btn
                v-bind="props"
                icon="mdi-cog-outline"
                variant="text"
                size="large"
                color="grey-darken-2"
                class="action-btn"
              ></v-btn>
            </template>
            <v-list width="240" class="settings-menu">
              <v-list-item prepend-icon="mdi-account-outline" title="الملف الشخصي"></v-list-item>
              <v-list-item prepend-icon="mdi-cog-outline" title="إعدادات النظام"></v-list-item>
              <v-list-item prepend-icon="mdi-theme-light-dark" title="تغيير المظهر"></v-list-item>
              <v-list-item prepend-icon="mdi-translate" title="اللغة"></v-list-item>
              <v-divider class="my-2"></v-divider>
              <v-list-item prepend-icon="mdi-help-circle-outline" title="المساعدة والدعم"></v-list-item>
              <v-list-item prepend-icon="mdi-information-outline" title="حول النظام"></v-list-item>
            </v-list>
          </v-menu>
        </div>
      </v-app-bar>

      <!-- Main Content Area -->
      <v-main class="main-content">
        <div class="content-wrapper">
          <!-- Welcome Card (when no specific content) -->
          <div v-if="showWelcomeCard" class="welcome-section">
            <v-card class="welcome-card" elevation="0">
              <v-card-text class="text-center pa-12">
                <div class="welcome-icon">
                  <v-icon size="80" color="primary">mdi-view-dashboard</v-icon>
                </div>
                <h2 class="welcome-title">مرحباً بك في نظام تنظيمي</h2>
                <p class="welcome-subtitle">
                  نظام إدارة المخزون الشامل لإدارة منتجاتك وطلباتك بكفاءة عالية
                </p>
                
                <div class="welcome-stats">
                  <div class="stat-item">
                    <div class="stat-value">{{ stats.products }}</div>
                    <div class="stat-label">منتج</div>
                  </div>
                  <div class="stat-item">
                    <div class="stat-value">{{ stats.orders }}</div>
                    <div class="stat-label">طلب</div>
                  </div>
                  <div class="stat-item">
                    <div class="stat-value">{{ stats.customers }}</div>
                    <div class="stat-label">عميل</div>
                  </div>
                </div>
                
                <div class="welcome-actions">
                  <v-btn
                    v-for="action in quickActions"
                    :key="action.action"
                    :color="action.color"
                    :prepend-icon="action.icon"
                    variant="tonal"
                    size="large"
                    class="welcome-action-btn"
                    @click="handleQuickAction(action.action)"
                  >
                    {{ action.title }}
                  </v-btn>
                </div>
              </v-card-text>
            </v-card>
          </div>
          
          <!-- Router View for actual content -->
          <router-view v-else />
        </div>
      </v-main>
    </v-layout>
  </v-app>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

// Reactive data
const drawer = ref(true)
const group = ref('dashboard')
const searchQuery = ref('')
const showSearch = ref(false)
const showWelcomeCard = ref(true)

// Navigation items
const mainItems = [
  {
    title: 'لوحة التحكم',
    value: 'dashboard',
    icon: 'mdi-view-dashboard-outline'
  },
  {
    title: 'المنتجات',
    value: 'products',
    icon: 'mdi-package-variant-closed'
  },
  {
    title: 'الطلبات',
    value: 'orders',
    icon: 'mdi-cart-outline',
    badge: '5',
    badgeColor: 'success'
  },
  {
    title: 'العملاء',
    value: 'customers',
    icon: 'mdi-account-group-outline'
  },
  {
    title: 'المبيعات',
    value: 'sales',
    icon: 'mdi-point-of-sale'
  },
  {
    title: 'المخزون',
    value: 'inventory',
    icon: 'mdi-warehouse',
    badge: '!',
    badgeColor: 'warning'
  }
]

const systemItems = [
  {
    title: 'التقارير',
    value: 'reports',
    icon: 'mdi-chart-bar'
  },
  {
    title: 'الإعدادات',
    value: 'settings',
    icon: 'mdi-cog-outline'
  }
]

const quickActions = [
  {
    title: 'إضافة منتج',
    icon: 'mdi-plus',
    color: 'primary',
    action: 'add-product'
  },
  {
    title: 'طلب جديد',
    icon: 'mdi-cart-plus',
    color: 'success',
    action: 'new-order'
  },
  {
    title: 'تقرير مبيعات',
    icon: 'mdi-chart-line',
    color: 'info',
    action: 'sales-report'
  },
  {
    title: 'إضافة عميل',
    icon: 'mdi-account-plus',
    color: 'warning',
    action: 'add-customer'
  }
]

const notifications = ref([
  {
    id: 1,
    title: 'طلب جديد تم استلامه',
    message: 'العميل أحمد محمد قام بطلب جديد بقيمة 1,250 ر.س',
    time: 'منذ 5 دقائق',
    icon: 'mdi-cart-plus',
    color: 'success',
    read: false
  },
  {
    id: 2,
    title: 'مخزون منخفض',
    message: 'منتج iPhone 15 أصبح مخزونه أقل من 10 قطع',
    time: 'منذ 15 دقيقة',
    icon: 'mdi-alert',
    color: 'warning',
    read: false
  },
  {
    id: 3,
    title: 'تم تحديث البيانات',
    message: 'تم تحديث قاعدة بيانات المنتجات بنجاح',
    time: 'منذ ساعة',
    icon: 'mdi-database-check',
    color: 'info',
    read: true
  }
])

const stats = ref({
  products: 1247,
  orders: 89,
  customers: 456
})

// Page titles
const pageTitles = {
  dashboard: { title: 'لوحة التحكم', subtitle: 'نظرة عامة على النشاطات والإحصائيات' },
  products: { title: 'المنتجات', subtitle: 'إدارة مخزون المنتجات والأسعار' },
  orders: { title: 'الطلبات', subtitle: 'متابعة الطلبات والمبيعات' },
  customers: { title: 'العملاء', subtitle: 'إدارة قاعدة بيانات العملاء' },
  sales: { title: 'المبيعات', subtitle: 'متابعة المبيعات والأرباح' },
  inventory: { title: 'المخزون', subtitle: 'مراقبة المخزون والكميات' },
  reports: { title: 'التقارير', subtitle: 'تقارير مفصلة وإحصائيات' },
  settings: { title: 'الإعدادات', subtitle: 'إعدادات النظام والتحكم' }
}

// Computed properties
const currentPageTitle = computed(() => pageTitles[group.value]?.title || 'النظام')
const currentPageSubtitle = computed(() => pageTitles[group.value]?.subtitle || 'إدارة النظام')

const unreadNotifications = computed(() => {
  return notifications.value.filter(n => !n.read).length
})

const breadcrumbs = computed(() => {
  const currentPage = pageTitles[group.value]
  if (!currentPage) return []
  
  return [
    { title: 'الرئيسية', disabled: false, href: '/' },
    { title: currentPage.title, disabled: true }
  ]
})

// Methods
const toggleSearch = () => {
  showSearch.value = !showSearch.value
  if (!showSearch.value) {
    searchQuery.value = ''
  }
}

const hideSearch = () => {
  if (!searchQuery.value) {
    showSearch.value = false
  }
}

const markAsRead = (notificationId) => {
  const notification = notifications.value.find(n => n.id === notificationId)
  if (notification) {
    notification.read = true
  }
}

const markAllAsRead = () => {
  notifications.value.forEach(n => n.read = true)
}

const handleQuickAction = (action) => {
  console.log('Quick action:', action)
  // Handle quick actions here
}

const handleNavigation = (item) => {
  group.value = item.value
  showWelcomeCard.value = item.value === 'dashboard'
  console.log('Navigating to:', item.value)
  // Add your navigation logic here
}

const getActionGradient = (color) => {
  const gradients = {
    primary: 'linear-gradient(135deg, #366091 0%, #4a7bb7 100%)',
    success: 'linear-gradient(135deg, #28a745 0%, #20c997 100%)',
    info: 'linear-gradient(135deg, #17a2b8 0%, #007bff 100%)',
    warning: 'linear-gradient(135deg, #ffc107 0%, #fd7e14 100%)',
    error: 'linear-gradient(135deg, #dc3545 0%, #e83e8c 100%)'
  }
  return gradients[color] || gradients.primary
}

// Watch for route changes
watch(() => route.path, (newPath) => {
  // Update group based on route
  const pathSegments = newPath.split('/').filter(Boolean)
  if (pathSegments.length > 0) {
    const routeName = pathSegments[0]
    if (pageTitles[routeName]) {
      group.value = routeName
      showWelcomeCard.value = routeName === 'dashboard'
    }
  }
}, { immediate: true })

onMounted(() => {
  // Initialize based on current route
  const pathSegments = route.path.split('/').filter(Boolean)
  if (pathSegments.length > 0) {
    const routeName = pathSegments[0]
    if (pageTitles[routeName]) {
      group.value = routeName
      showWelcomeCard.value = routeName === 'dashboard'
    }
  }
})
</script>

<style scoped>
/* Modern App Styling */
.modern-app {
  font-family: 'Inter', 'Segoe UI', 'Roboto', sans-serif;
}

/* Sidebar Styling */
.modern-sidebar {
  background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%) !important;
  border-right: 1px solid #e2e8f0 !important;
  box-shadow: 2px 0 12px rgba(54, 96, 145, 0.08) !important;
}

.sidebar-header {
  padding: 24px 24px 16px;
  border-bottom: 1px solid #e2e8f0;
}

.logo-container {
  display: flex;
  align-items: center;
  gap: 16px;
}

.logo-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #366091 0%, #4a7bb7 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(54, 96, 145, 0.3);
}

.logo-title {
  font-size: 20px;
  font-weight: 700;
  color: #2d3748;
  margin: 0;
  line-height: 1.2;
}

.logo-subtitle {
  font-size: 12px;
  color: #718096;
  margin: 2px 0 0 0;
  line-height: 1.3;
}

/* User Section */
.user-section {
  padding: 20px 24px;
  border-bottom: 1px solid #e2e8f0;
}

.user-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: linear-gradient(135deg, #f7fafc 0%, #edf2f7 100%);
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.user-avatar {
  box-shadow: 0 4px 12px rgba(54, 96, 145, 0.15) !important;
  border: 2px solid white !important;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #2d3748;
  margin: 0 0 2px 0;
}

.user-role {
  font-size: 12px;
  color: #718096;
  margin: 0 0 4px 0;
}

.user-status {
  display: flex;
  align-items: center;
  gap: 4px;
}

.status-dot {
  width: 6px;
  height: 6px;
  background: #48bb78;
  border-radius: 50%;
}

.status-text {
  font-size: 11px;
  color: #48bb78;
  font-weight: 500;
}

/* Navigation Section */
.navigation-section {
  flex: 1;
  padding: 20px 16px;
  overflow-y: auto;
}

.nav-group {
  margin-bottom: 32px;
}

.nav-group:last-child {
  margin-bottom: 0;
}

.nav-group-title {
  font-size: 11px;
  font-weight: 600;
  color: #a0aec0;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin: 0 0 12px 8px;
}

.nav-items {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nav-item {
  position: relative;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.nav-item:hover {
  background: rgba(54, 96, 145, 0.05);
  transform: translateX(-2px);
}

.nav-item.active {
  background: linear-gradient(135deg, #366091 0%, #4a7bb7 100%);
  box-shadow: 0 4px 12px rgba(54, 96, 145, 0.25);
}

.nav-item-content {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  position: relative;
}

.nav-item-icon {
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-item-text {
  font-size: 14px;
  font-weight: 500;
  color: #4a5568;
  flex: 1;
}

.nav-item.active .nav-item-text {
  color: white;
}

.nav-item-badge {
  margin-left: auto;
}

/* Sidebar Footer */
.sidebar-footer {
  padding: 16px 24px;
  border-top: 1px solid #e2e8f0;
}

.footer-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.footer-btn {
  justify-content: flex-start !important;
  border-radius: 8px !important;
  font-weight: 500 !important;
  text-transform: none !important;
}

/* App Bar Styling */
.modern-appbar {
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%) !important;
  border-bottom: 1px solid #e2e8f0 !important;
  backdrop-filter: blur(10px);
}

.mobile-nav-toggle {
  margin-right: 8px;
}

.page-header {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: #2d3748;
  margin: 0;
  line-height: 1.2;
}

.page-subtitle {
  font-size: 14px;
  color: #718096;
  margin: 0;
  line-height: 1.3;
}

.page-breadcrumbs {
  padding: 0 !important;
  margin: 0 !important;
}

.page-breadcrumbs :deep(.v-breadcrumbs-item) {
  font-size: 12px;
}

/* Action Section */
.action-section {
  display: flex;
  align-items: center;
  gap: 8px;
}

.search-container {
  position: relative;
}

.search-field {
  width: 300px;
  border-radius: 24px !important;
}

.action-btn {
  border-radius: 12px !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.action-btn:hover {
  background: rgba(54, 96, 145, 0.08) !important;
  transform: scale(1.05);
}

.quick-action-btn {
  background: linear-gradient(135deg, rgba(54, 96, 145, 0.1) 0%, rgba(74, 123, 183, 0.1) 100%) !important;
  border: 1px solid rgba(54, 96, 145, 0.2) !important;
}

/* Menu Styling */
.quick-actions-menu,
.notifications-menu,
.settings-menu {
  border-radius: 16px !important;
  box-shadow: 0 8px 32px rgba(54, 96, 145, 0.15) !important;
  border: 1px solid #e2e8f0 !important;
}

.menu-header {
  padding: 16px 20px 12px !important;
  border-bottom: 1px solid #e2e8f0;
  font-size: 16px !important;
  font-weight: 600 !important;
  color: #2d3748 !important;
}

/* Quick Actions Grid */
.quick-actions-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  padding: 8px;
}

.quick-action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px 12px;
  border-radius: 12px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.quick-action-item:hover {
  background: #f1f5f9;
  border-color: #cbd5e0;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(54, 96, 145, 0.12);
}

.quick-action-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.quick-action-text {
  font-size: 12px;
  font-weight: 500;
  color: #4a5568;
  text-align: center;
  line-height: 1.3;
}

/* Notifications Menu */
.notifications-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid #e2e8f0;
}

.notifications-title {
  font-size: 16px;
  font-weight: 600;
  color: #2d3748;
  margin: 0;
}

.notifications-content {
  max-height: 400px;
  overflow-y: auto;
}

.notification-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px 20px;
  border-bottom: 1px solid #f1f5f9;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
}

.notification-item:hover {
  background: #f8fafc;
}

.notification-item:last-child {
  border-bottom: none;
}

.notification-item.unread {
  background: rgba(54, 96, 145, 0.02);
  border-left: 3px solid #366091;
}

.notification-avatar {
  flex-shrink: 0;
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-title {
  font-size: 14px;
  font-weight: 600;
  color: #2d3748;
  margin: 0 0 4px 0;
  line-height: 1.3;
}

.notification-message {
  font-size: 13px;
  color: #4a5568;
  margin: 0 0 4px 0;
  line-height: 1.4;
}

.notification-time {
  font-size: 11px;
  color: #a0aec0;
}

.notification-indicator {
  width: 8px;
  height: 8px;
  background: #366091;
  border-radius: 50%;
  position: absolute;
  top: 20px;
  right: 16px;
}

.no-notifications {
  text-align: center;
  padding: 40px 20px;
}

.no-notifications-text {
  font-size: 14px;
  color: #a0aec0;
  margin: 12px 0 0 0;
}

.notifications-footer {
  padding: 12px 20px;
  border-top: 1px solid #e2e8f0;
}

/* Main Content */
.main-content {
  background: linear-gradient(135deg, #f8fafc 0%, #ffffff 100%) !important;
  min-height: 100vh;
}

.content-wrapper {
  padding: 32px;
}

/* Welcome Section */
.welcome-section {
  max-width: 1200px;
  margin: 0 auto;
}

.welcome-card {
  background: white !important;
  border: 1px solid #e2e8f0 !important;
  border-radius: 20px !important;
  box-shadow: 0 8px 32px rgba(54, 96, 145, 0.08) !important;
}

.welcome-icon {
  margin-bottom: 24px;
}

.welcome-title {
  font-size: 32px;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 12px;
}

.welcome-subtitle {
  font-size: 16px;
  color: #718096;
  margin-bottom: 40px;
  line-height: 1.6;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.welcome-stats {
  display: flex;
  justify-content: center;
  gap: 48px;
  margin-bottom: 40px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #366091;
  line-height: 1;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #718096;
  font-weight: 500;
}

.welcome-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  flex-wrap: wrap;
}

.welcome-action-btn {
  border-radius: 12px !important;
  font-weight: 500 !important;
  text-transform: none !important;
  padding: 12px 24px !important;
}

/* Responsive Design */
@media (max-width: 1280px) {
  .modern-sidebar {
    width: 260px !important;
  }
  
  .content-wrapper {
    padding: 24px;
  }
}

@media (max-width: 960px) {
  .modern-sidebar {
    position: fixed !important;
    z-index: 1000;
    transform: translateX(-100%);
    transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  }
  
  .modern-sidebar.v-navigation-drawer--active {
    transform: translateX(0);
  }
  
  .page-header {
    margin-right: 16px;
  }
  
  .action-section {
    gap: 4px;
  }
  
  .search-field {
    width: 250px;
  }
  
  .content-wrapper {
    padding: 20px 16px;
  }
  
  .welcome-stats {
    gap: 24px;
  }
  
  .stat-value {
    font-size: 24px;
  }
  
  .welcome-actions {
    gap: 12px;
  }
}

@media (max-width: 600px) {
  .page-title {
    font-size: 20px;
  }
  
  .page-subtitle {
    font-size: 12px;
  }
  
  .search-field {
    width: 200px;
  }
  
  .quick-actions-grid {
    grid-template-columns: 1fr;
  }
  
  .welcome-title {
    font-size: 24px;
  }
  
  .welcome-subtitle {
    font-size: 14px;
  }
  
  .welcome-stats {
    flex-direction: column;
    gap: 16px;
  }
  
  .welcome-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .welcome-action-btn {
    width: 100%;
    max-width: 280px;
  }
}

/* Animation Classes */
.notification-btn .v-badge {
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.7; }
  100% { opacity: 1; }
}

.nav-item {
  animation: slideIn 0.3s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* Scrollbar Styling */
.modern-sidebar :deep(.v-navigation-drawer__content),
.notifications-content {
  scrollbar-width: thin;
  scrollbar-color: rgba(54, 96, 145, 0.3) transparent;
}

.modern-sidebar :deep(.v-navigation-drawer__content)::-webkit-scrollbar,
.notifications-content::-webkit-scrollbar {
  width: 6px;
}

.modern-sidebar :deep(.v-navigation-drawer__content)::-webkit-scrollbar-track,
.notifications-content::-webkit-scrollbar-track {
  background: transparent;
}

.modern-sidebar :deep(.v-navigation-drawer__content)::-webkit-scrollbar-thumb,
.notifications-content::-webkit-scrollbar-thumb {
  background: rgba(54, 96, 145, 0.3);
  border-radius: 3px;
}

.modern-sidebar :deep(.v-navigation-drawer__content)::-webkit-scrollbar-thumb:hover,
.notifications-content::-webkit-scrollbar-thumb:hover {
  background: rgba(54, 96, 145, 0.5);
}

/* Focus States */
.nav-item:focus-visible,
.quick-action-item:focus-visible,
.notification-item:focus-visible {
  outline: 2px solid rgba(54, 96, 145, 0.5);
  outline-offset: 2px;
}

/* Print Styles */
@media print {
  .modern-sidebar,
  .modern-appbar {
    display: none !important;
  }
  
  .main-content {
    padding: 0 !important;
    background: white !important;
  }
  
  .content-wrapper {
    padding: 20px !important;
  }
}

/* Dark Mode Compatibility */
@media (prefers-color-scheme: dark) {
  .modern-sidebar {
    background: linear-gradient(180deg, #1a202c 0%, #2d3748 100%) !important;
    border-color: #4a5568 !important;
  }
  
  .modern-appbar {
    background: linear-gradient(135deg, #1a202c 0%, #2d3748 100%) !important;
    border-color: #4a5568 !important;
  }
  
  .logo-title,
  .page-title,
  .user-name,
  .notifications-title,
  .welcome-title {
    color: #f7fafc !important;
  }
  
  .logo-subtitle,
  .page-subtitle,
  .user-role,
  .welcome-subtitle {
    color: #a0aec0 !important;
  }
}
</style>