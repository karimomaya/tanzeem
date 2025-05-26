<template>
	<v-app>
		<!-- Modern Navigation Drawer -->
		<v-navigation-drawer 
			v-if="showLayout" 
			v-model="drawer" 
			permanent 
			app
			width="280"
			class="modern-drawer"
			color="grey-lighten-5"
		>
			<!-- Logo Section -->
			<div class="pa-6 pb-4">
				<div class="d-flex align-center">
					<v-avatar size="48" color="primary" class="elevation-3 me-3 logo-avatar">
						<v-icon color="white" size="28">mdi-cube-outline</v-icon>
					</v-avatar>
					<div>
						<div class="text-h5 font-weight-bold text-primary">تنظيم</div>
						<div class="text-body-2 text-grey-darken-1">نظام إدارة المخزون</div>
					</div>
				</div>
			</div>

			<v-divider class="mb-4"></v-divider>

			<!-- Quick Stats Card -->
			<div class="px-4 mb-4">
				<v-card class="stats-card" elevation="2" rounded="lg">
					<v-card-text class="py-3">
						<div class="d-flex align-center justify-space-between mb-2">
							<span class="text-caption text-grey-darken-1">الطلبات اليوم</span>
							<v-chip size="x-small" color="success" variant="flat">+15%</v-chip>
						</div>
						<div class="text-h6 font-weight-bold text-primary">{{ todayOrders }}</div>
					</v-card-text>
				</v-card>
			</div>

			<!-- Navigation Menu -->
			<v-list density="compact" nav class="pa-2">
				<v-list-item 
					v-for="item in navigationItems"
					:key="item.value"
					:prepend-icon="item.icon"
					:title="item.title"
					:value="item.value"
					:active="item.active"
					class="nav-item mb-1"
					rounded="lg"
					@click="handleNavigation(item)"
				>
					<template v-slot:append v-if="item.badge">
						<v-badge 
							:content="item.badge" 
							:color="item.badgeColor || 'error'" 
							inline
							class="nav-badge"
						></v-badge>
					</template>
				</v-list-item>
			</v-list>

			<!-- Quick Actions Section -->
			<div class="px-4 mb-4">
				<v-divider class="mb-3"></v-divider>
				<div class="text-caption text-grey-darken-1 mb-2 px-2">الإجراءات السريعة</div>
				<div class="d-flex ga-2">
					<v-btn 
						size="small" 
						color="primary" 
						variant="tonal"
						icon="mdi-plus"
						class="quick-action-btn"
						@click="handleQuickAction('add-product')"
					></v-btn>
					<v-btn 
						size="small" 
						color="success" 
						variant="tonal"
						icon="mdi-cart-plus"
						class="quick-action-btn"
						@click="handleQuickAction('new-order')"
					></v-btn>
					<v-btn 
						size="small" 
						color="info" 
						variant="tonal"
						icon="mdi-file-chart-outline"
						class="quick-action-btn"
						@click="handleQuickAction('generate-report')"
					></v-btn>
				</div>
			</div>

			<!-- Bottom Section -->
			<template v-slot:append>
				<div class="pa-4">
					<v-divider class="mb-4"></v-divider>
					
					<!-- System Status -->
					<div class="mb-3">
						<v-card class="system-status-card" variant="tonal" color="success" rounded="lg">
							<v-card-text class="py-2 px-3">
								<div class="d-flex align-center">
									<v-icon color="success" size="16" class="me-2">mdi-check-circle</v-icon>
									<span class="text-caption">النظام يعمل بشكل طبيعي</span>
								</div>
							</v-card-text>
						</v-card>
					</div>

					<v-list density="compact" nav>
						<v-list-item
							prepend-icon="mdi-help-circle-outline"
							title="المساعدة والدعم"
							class="nav-item"
							rounded="lg"
							@click="handleBottomNavigation('help')"
						></v-list-item>
						<v-list-item
							prepend-icon="mdi-logout"
							title="تسجيل الخروج"
							class="nav-item text-error"
							rounded="lg"
							@click="handleLogout"
						></v-list-item>
					</v-list>
				</div>
			</template>
		</v-navigation-drawer>

		<!-- Modern App Bar -->
		<v-app-bar 
			v-if="showLayout" 
			elevation="0" 
			class="modern-app-bar"
			height="72"
			style="background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%); border-bottom: 1px solid #e2e8f0;"
		>
			<!-- Mobile Menu Toggle -->
			<v-app-bar-nav-icon 
				@click="drawer = !drawer"
				class="me-2"
				variant="text"
				color="grey-darken-2"
			></v-app-bar-nav-icon>

			<!-- Page Title Section with Breadcrumbs -->
			<div class="d-flex align-center flex-grow-1">
				<div>
					<!-- <div class="d-flex align-center mb-1">
						<v-breadcrumbs 
							:items="breadcrumbs" 
							density="compact"
							class="pa-0"
							divider="/"
						>
							<template v-slot:item="{ item }">
								<v-breadcrumbs-item 
									:disabled="item.disabled"
									class="text-caption"
								>
									{{ item.title }}
								</v-breadcrumbs-item>
							</template>
						</v-breadcrumbs>
					</div> -->
					<h1 class="text-h5 font-weight-bold text-grey-darken-3 mb-1">{{ pageTitle }}</h1>
					<div class="text-body-2 text-grey-darken-1">{{ pageSubtitle }}</div>
				</div>
			</div>

			<v-spacer></v-spacer>

			<!-- Action Buttons -->
			<div class="d-flex align-center ga-2">
				<!-- Theme Toggle -->
				<v-btn 
					:icon="isDark ? 'mdi-weather-sunny' : 'mdi-weather-night'" 
					variant="text" 
					size="large"
					color="grey-darken-2"
					class="action-btn"
					@click="toggleTheme"
				></v-btn>

				<!-- Global Search with Enhanced UI -->
				<v-menu 
					v-model="searchMenu"
					:close-on-content-click="false"
					location="bottom"
					offset="8"
				>
					<template v-slot:activator="{ props }">
						<v-btn 
							v-bind="props"
							icon="mdi-magnify" 
							variant="text" 
							size="large"
							color="grey-darken-2"
							class="action-btn"
						></v-btn>
					</template>
					<v-card width="400" class="search-card" elevation="8">
						<v-card-text class="pa-0">
							<v-text-field
								v-model="searchQuery"
								placeholder="البحث في النظام..."
								prepend-inner-icon="mdi-magnify"
								variant="plain"
								hide-details
								class="search-field px-4 pt-4"
								@input="handleSearch"
							></v-text-field>
							<v-divider></v-divider>
							<div v-if="searchResults.length" class="search-results">
								<v-list density="compact">
									<v-list-item
										v-for="result in searchResults"
										:key="result.id"
										:prepend-icon="result.icon"
										:title="result.title"
										:subtitle="result.category"
										class="search-result-item"
										@click="handleSearchResult(result)"
									></v-list-item>
								</v-list>
							</div>
							<div v-else-if="searchQuery" class="pa-4 text-center text-grey-darken-1">
								<v-icon class="mb-2">mdi-magnify</v-icon>
								<div class="text-body-2">لا توجد نتائج</div>
							</div>
							<div v-else class="pa-4">
								<div class="text-caption text-grey-darken-1 mb-2">البحث السريع</div>
								<div class="d-flex flex-wrap ga-1">
									<v-chip 
										v-for="tag in quickSearchTags"
										:key="tag"
										size="small"
										variant="tonal"
										@click="searchQuery = tag"
									>
										{{ tag }}
									</v-chip>
								</div>
							</div>
						</v-card-text>
					</v-card>
				</v-menu>

				<!-- Enhanced Notifications -->
				<v-menu offset-y>
					<template v-slot:activator="{ props }">
						<v-btn 
							v-bind="props"
							icon="mdi-bell-outline" 
							variant="text" 
							size="large"
							color="grey-darken-2"
							class="action-btn"
						>
							<v-badge 
								:content="unreadNotifications" 
								color="error" 
								floating
								v-if="hasNotifications"
								dot
							>
								<v-icon>mdi-bell-outline</v-icon>
							</v-badge>
							<v-icon v-else>mdi-bell-outline</v-icon>
						</v-btn>
					</template>
					<v-card width="360" class="notification-card" elevation="8">
						<v-card-title class="px-4 py-3 bg-grey-lighten-4">
							<div class="d-flex align-center justify-space-between">
								<span class="font-weight-bold">الإشعارات</span>
								<div class="d-flex align-center ga-2">
									<v-chip size="small" color="primary">{{ notifications.length }}</v-chip>
									<v-btn 
										icon="mdi-check-all" 
										size="x-small" 
										variant="text"
										@click="markAllAsRead"
									></v-btn>
								</div>
							</div>
						</v-card-title>
						<v-list class="notification-list" max-height="300" style="overflow-y: auto;">
							<v-list-item
								v-for="notification in notifications"
								:key="notification.id"
								class="px-4 py-3 notification-item"
								:class="{ 'notification-unread': !notification.read }"
								@click="markAsRead(notification.id)"
							>
								<template v-slot:prepend>
									<v-avatar size="40" :color="notification.color">
										<v-icon color="white" size="20">{{ notification.icon }}</v-icon>
									</v-avatar>
								</template>
								<v-list-item-title class="text-body-2 font-weight-medium">
									{{ notification.title }}
								</v-list-item-title>
								<v-list-item-subtitle class="text-caption">
									{{ notification.time }}
								</v-list-item-subtitle>
								<template v-slot:append v-if="!notification.read">
									<v-icon color="primary" size="8">mdi-circle</v-icon>
								</template>
							</v-list-item>
						</v-list>
						<v-divider></v-divider>
						<v-card-actions class="justify-center">
							<v-btn variant="text" color="primary" size="small">
								عرض جميع الإشعارات
							</v-btn>
						</v-card-actions>
					</v-card>
				</v-menu>

				<!-- Settings Menu -->
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
					<v-list width="200">
						<v-list-item 
							prepend-icon="mdi-account-outline" 
							title="الملف الشخصي"
							@click="handleSettings('profile')"
						></v-list-item>
						<v-list-item 
							prepend-icon="mdi-cog-outline" 
							title="الإعدادات"
							@click="handleSettings('settings')"
						></v-list-item>
						<v-list-item 
							prepend-icon="mdi-theme-light-dark" 
							title="المظهر"
							@click="toggleTheme"
						></v-list-item>
						<v-list-item 
							prepend-icon="mdi-translate" 
							title="اللغة"
							@click="handleSettings('language')"
						></v-list-item>
						<v-divider></v-divider>
						<v-list-item 
							prepend-icon="mdi-logout" 
							title="تسجيل الخروج" 
							class="text-error"
							@click="handleLogout"
						></v-list-item>
					</v-list>
				</v-menu>

				<!-- Enhanced User Profile -->
				<v-menu offset-y>
					<template v-slot:activator="{ props }">
						<v-btn 
							v-bind="props"
							variant="text" 
							class="user-profile-btn pa-2"
						>
							<div class="d-flex align-center">
								<v-avatar size="40" class="elevation-2">
									<v-img 
										:src="userProfile.avatar" 
										alt="User"
									></v-img>
								</v-avatar>
								<div class="ms-3 text-right d-none d-md-block">
									<div class="text-body-2 font-weight-medium text-grey-darken-3">{{ userProfile.name }}</div>
									<div class="text-caption text-grey-darken-1">{{ userProfile.role }}</div>
								</div>
								<v-icon class="ms-2 d-none d-md-block" size="16" color="grey-darken-1">mdi-chevron-down</v-icon>
							</div>
						</v-btn>
					</template>
					<v-card width="280" elevation="8">
						<v-card-text class="px-4 py-3">
							<div class="d-flex align-center mb-3">
								<v-avatar size="48" class="me-3">
									<v-img :src="userProfile.avatar" alt="User"></v-img>
								</v-avatar>
								<div>
									<div class="font-weight-bold">{{ userProfile.name }}</div>
									<div class="text-caption text-grey-darken-1">{{ userProfile.email }}</div>
									<v-chip size="x-small" :color="userProfile.statusColor" class="mt-1">
										{{ userProfile.status }}
									</v-chip>
								</div>
							</div>
						</v-card-text>
						<v-divider></v-divider>
						<v-list density="compact">
							<v-list-item 
								prepend-icon="mdi-account-outline" 
								title="الملف الشخصي"
								@click="handleUserAction('profile')"
							></v-list-item>
							<v-list-item 
								prepend-icon="mdi-cog-outline" 
								title="إعدادات الحساب"
								@click="handleUserAction('account-settings')"
							></v-list-item>
							<v-list-item 
								prepend-icon="mdi-help-circle-outline" 
								title="المساعدة"
								@click="handleUserAction('help')"
							></v-list-item>
							<v-divider></v-divider>
							<v-list-item 
								prepend-icon="mdi-logout" 
								title="تسجيل الخروج" 
								class="text-error"
								@click="handleLogout"
							></v-list-item>
						</v-list>
					</v-card>
				</v-menu>
			</div>
		</v-app-bar>

		<!-- Loading Overlay -->
		<v-overlay 
			v-model="isLoading" 
			class="align-center justify-center"
			contained
		>
			<v-progress-circular
				indeterminate
				size="64"
				color="primary"
			></v-progress-circular>
		</v-overlay>

		<!-- Main Content -->
		<v-main class="main-content">
			<router-view />
		</v-main>

		<!-- Floating Action Button for Mobile -->
		<v-fab
			v-if="$vuetify.display.mobile"
			location="bottom end"
			size="large"
			color="primary"
			icon="mdi-plus"
			class="floating-action-btn"
			@click="handleFloatingAction"
		></v-fab>

		<!-- Toast Notifications -->
		<v-snackbar
			v-model="toast.show"
			:color="toast.color"
			:timeout="toast.timeout"
			location="top right"
			class="toast-notification"
		>
			<div class="d-flex align-center">
				<v-icon class="me-2">{{ toast.icon }}</v-icon>
				{{ toast.message }}
			</div>
			<template v-slot:actions>
				<v-btn
					variant="text"
					@click="toast.show = false"
				>
					إغلاق
				</v-btn>
			</template>
		</v-snackbar>
	</v-app>
</template>
  
<script>
import { useRoute } from 'vue-router';
import { useTheme } from 'vuetify';

export default {
	name: 'MainLayout',
	data() {
		return {
			drawer: true,
			pageTitle: 'لوحة التحكم',
			pageSubtitle: 'نظرة عامة على النشاطات',
			hasNotifications: true,
			isLoading: false,
			isDark: false,
			searchMenu: false,
			searchQuery: '',
			todayOrders: 24,
			
			// Enhanced user profile
			userProfile: {
				name: 'أحمد محمد',
				email: 'ahmed@company.com',
				role: 'مدير النظام',
				avatar: 'https://cdn.vuetifyjs.com/images/john.jpg',
				status: 'متصل',
				statusColor: 'success'
			},

			// Breadcrumbs
			breadcrumbs: [
				{ title: 'الرئيسية', disabled: false },
				{ title: 'لوحة التحكم', disabled: true }
			],

			// Quick search tags
			quickSearchTags: ['المنتجات', 'الطلبات', 'العملاء', 'التقارير'],

			// Search results
			searchResults: [],

			// Toast notification system
			toast: {
				show: false,
				message: '',
				color: 'success',
				icon: 'mdi-check-circle',
				timeout: 4000
			},

			navigationItems: [
				{
					title: 'لوحة التحكم',
					icon: 'mdi-view-dashboard-outline',
					value: 'dashboard',
					active: false
				},
				{
					title: 'المنتجات',
					icon: 'mdi-package-variant-closed',
					value: 'products',
					active: true
				},
				{
					title: 'الطلبات',
					icon: 'mdi-cart-outline',
					value: 'orders',
					active: false,
					badge: '12',
					badgeColor: 'success'
				},
				{
					title: 'العملاء',
					icon: 'mdi-account-group-outline',
					value: 'customers',
					active: false
				},
				{
					title: 'التقارير',
					icon: 'mdi-chart-bar',
					value: 'reports',
					active: false
				},
				{
					title: 'المبيعات',
					icon: 'mdi-point-of-sale',
					value: 'sales',
					active: false
				},
				{
					title: 'المخزون',
					icon: 'mdi-warehouse',
					value: 'inventory',
					active: false,
					badge: '!',
					badgeColor: 'warning'
				}
			],
			notifications: [
				{
					id: 1,
					title: 'طلب جديد تم استلامه',
					time: 'منذ 5 دقائق',
					icon: 'mdi-cart-plus',
					color: 'success',
					read: false
				},
				{
					id: 2,
					title: 'مخزون منتج منخفض',
					time: 'منذ 15 دقيقة',
					icon: 'mdi-alert',
					color: 'warning',
					read: false
				},
				{
					id: 3,
					title: 'تم تحديث الملف الشخصي',
					time: 'منذ ساعة',
					icon: 'mdi-account-check',
					color: 'info',
					read: true
				}
			]
		};
	},
	computed: {
		showLayout() {
			const route = useRoute();
			return route && route.meta ? !route.meta.hideLayout : true;
		},
		unreadNotifications() {
			return this.notifications.filter(n => !n.read).length;
		}
	},
	methods: {
		// Enhanced page title management
		updatePageTitle(newTitle) {
			this.pageTitle = newTitle;
			this.updateBreadcrumbs(newTitle);
		},
		updatePageSubtitle(subtitle) {
			this.pageSubtitle = subtitle;
		},
		updateBreadcrumbs(currentPage) {
			this.breadcrumbs = [
				{ title: 'الرئيسية', disabled: false },
				{ title: currentPage, disabled: true }
			];
		},

		// Navigation handlers
		handleNavigation(item) {
			this.navigationItems.forEach(nav => nav.active = false);
			item.active = true;
			
			this.pageTitle = item.title;
			this.updatePageSubtitle(this.getSubtitleForPage(item.value));
			this.updateBreadcrumbs(item.title);
			
			this.showToast(`تم الانتقال إلى ${item.title}`, 'info', 'mdi-navigation');
			console.log('Navigating to:', item.value);
		},

		// Quick actions
		handleQuickAction(action) {
			const actions = {
				'add-product': 'إضافة منتج جديد',
				'new-order': 'طلب جديد',
				'generate-report': 'إنشاء تقرير'
			};
			this.showToast(`تم تنفيذ: ${actions[action]}`, 'success', 'mdi-check-circle');
		},

		// Bottom navigation
		handleBottomNavigation(action) {
			if (action === 'help') {
				this.showToast('فتح صفحة المساعدة', 'info', 'mdi-help-circle');
			}
		},

		// Theme management
		toggleTheme() {
			this.isDark = !this.isDark;
			const theme = useTheme();
			theme.global.name.value = this.isDark ? 'dark' : 'light';
			this.showToast(
				this.isDark ? 'تم التبديل إلى المظهر الداكن' : 'تم التبديل إلى المظهر الفاتح',
				'success',
				'mdi-theme-light-dark'
			);
		},

		// Search functionality
		handleSearch() {
			if (!this.searchQuery) {
				this.searchResults = [];
				return;
			}

			// Simulate search results
			this.searchResults = [
				{
					id: 1,
					title: 'منتج تجريبي',
					category: 'المنتجات',
					icon: 'mdi-package-variant'
				},
				{
					id: 2,
					title: 'طلب #12345',
					category: 'الطلبات',
					icon: 'mdi-cart'
				}
			].filter(item => 
				item.title.includes(this.searchQuery) || 
				item.category.includes(this.searchQuery)
			);
		},

		handleSearchResult(result) {
			this.searchMenu = false;
			this.showToast(`تم اختيار: ${result.title}`, 'info', 'mdi-magnify');
		},

		// Notification management
		markAsRead(notificationId) {
			const notification = this.notifications.find(n => n.id === notificationId);
			if (notification) {
				notification.read = true;
			}
		},

		markAllAsRead() {
			this.notifications.forEach(n => n.read = true);
			this.showToast('تم وضع علامة مقروءة على جميع الإشعارات', 'success', 'mdi-check-all');
		},

		// Settings and user actions
		handleSettings(setting) {
			const settings = {
				'profile': 'الملف الشخصي',
				'settings': 'الإعدادات',
				'language': 'إعدادات اللغة'
			};
			this.showToast(`فتح ${settings[setting]}`, 'info', 'mdi-cog');
		},

		handleUserAction(action) {
			const actions = {
				'profile': 'الملف الشخصي',
				'account-settings': 'إعدادات الحساب',
				'help': 'المساعدة'
			};
			this.showToast(`فتح ${actions[action]}`, 'info', 'mdi-account');
		},

		// Logout functionality
		handleLogout() {
			this.isLoading = true;
			setTimeout(() => {
				this.isLoading = false;
				this.showToast('تم تسجيل الخروج بنجاح', 'success', 'mdi-logout');
			}, 2000);
		},

		// Mobile floating action
		handleFloatingAction() {
			this.showToast('إجراء سريع من الهاتف المحمول', 'primary', 'mdi-plus');
		},

		// Toast notification system
		showToast(message, color = 'success', icon = 'mdi-check-circle', timeout = 4000) {
			this.toast = {
				show: true,
				message,
				color,
				icon,
				timeout
			};
		},

		// Existing methods
		getSubtitleForPage(page) {
			const subtitles = {
				dashboard: 'نظرة عامة على النشاطات',
				products: 'إدارة مخزون المنتجات والأسعار',
				orders: 'متابعة الطلبات والمبيعات',
				customers: 'إدارة قاعدة بيانات العملاء',
				reports: 'تقارير مفصلة وإحصائيات',
				sales: 'متابعة المبيعات والأرباح',
				inventory: 'مراقبة المخزون والكميات'
			};
			return subtitles[page] || 'إدارة النظام';
		}
	},
	created() {
		// Listen for global events
		this.$emitter?.on('update-title', this.updatePageTitle);
		this.$emitter?.on('update-subtitle', this.updatePageSubtitle);
		
		// Initialize theme
		const theme = useTheme();
		this.isDark = theme.global.current.value.dark;
	},
	beforeUnmount() {
		// Clean up event listeners
		this.$emitter?.off('update-title', this.updatePageTitle);
		this.$emitter?.off('update-subtitle', this.updatePageSubtitle);
	}
};
</script>

<style scoped>
/* Enhanced Modern Drawer Styles */
.modern-drawer {
	border-right: 1px solid #e2e8f0 !important;
	background: linear-gradient(180deg, #f8fafc 0%, #ffffff 100%) !important;
}

/* Logo Animation */
.logo-avatar {
	transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.stats-card:hover {
	transform: translateY(-2px);
	box-shadow: 0 8px 25px rgba(var(--v-theme-primary), 0.15) !important;
}

/* Enhanced Navigation Item Styles */
.nav-item {
	margin-bottom: 4px;
	transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
	position: relative;
	overflow: hidden;
}

.nav-item::before {
	content: '';
	position: absolute;
	top: 0;
	left: 0;
	width: 0;
	height: 100%;
	background: linear-gradient(90deg, rgba(var(--v-theme-primary), 0.1), transparent);
	transition: width 0.3s ease;
	z-index: 0;
}

.nav-item:hover::before {
	width: 100%;
}

.nav-item:hover {
	background-color: rgba(var(--v-theme-primary), 0.08) !important;
	transform: translateX(-4px);
}

.nav-item.v-list-item--active {
	background: linear-gradient(135deg, rgba(var(--v-theme-primary), 0.15), rgba(var(--v-theme-primary), 0.08)) !important;
	color: rgb(var(--v-theme-primary)) !important;
	font-weight: 600;
	border-right: 3px solid rgb(var(--v-theme-primary));
}

/* Navigation Badge Animations */
.nav-badge {
	animation: bounce 2s infinite;
}

@keyframes bounce {
	0%, 20%, 50%, 80%, 100% { transform: translateY(0); }
	40% { transform: translateY(-3px); }
	60% { transform: translateY(-2px); }
}

/* Quick Action Buttons */
.quick-action-btn {
	transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
	position: relative;
}

.quick-action-btn:hover {
	transform: scale(1.1) translateY(-2px);
	box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.quick-action-btn:active {
	transform: scale(0.95);
}

/* System Status Card */
.system-status-card {
	animation: pulse-gentle 3s infinite;
}

@keyframes pulse-gentle {
	0%, 100% { opacity: 1; }
	50% { opacity: 0.9; }
}

/* Enhanced App Bar Styles */
.modern-app-bar {
	backdrop-filter: blur(10px);
	transition: all 0.3s ease;
}

/* Enhanced Action Button Styles */
.action-btn {
	transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
	position: relative;
	overflow: hidden;
}

.action-btn::before {
	content: '';
	position: absolute;
	top: 50%;
	left: 50%;
	width: 0;
	height: 0;
	background: radial-gradient(circle, rgba(var(--v-theme-primary), 0.3) 0%, transparent 70%);
	transition: all 0.5s ease;
	transform: translate(-50%, -50%);
	border-radius: 50%;
}

.action-btn:hover::before {
	width: 100px;
	height: 100px;
}

.action-btn:hover {
	background-color: rgba(var(--v-theme-primary), 0.08) !important;
	transform: scale(1.1);
}

.action-btn:active {
	transform: scale(0.95);
}

/* Enhanced User Profile Button */
.user-profile-btn {
	border-radius: 12px !important;
	transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
	background: linear-gradient(135deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.05));
	backdrop-filter: blur(10px);
}

.user-profile-btn:hover {
	background: linear-gradient(135deg, rgba(var(--v-theme-primary), 0.1), rgba(var(--v-theme-primary), 0.05)) !important;
	transform: translateY(-1px);
	box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* Enhanced Search Card */
.search-card {
	border-radius: 16px !important;
	backdrop-filter: blur(20px);
	background: rgba(255, 255, 255, 0.95) !important;
}

.search-field :deep(.v-field__input) {
	font-size: 16px;
}

.search-results {
	max-height: 200px;
	overflow-y: auto;
}

.search-result-item {
	transition: all 0.2s ease;
}

.search-result-item:hover {
	background-color: rgba(var(--v-theme-primary), 0.05) !important;
}

/* Enhanced Notification Card */
.notification-card {
	border-radius: 16px !important;
	backdrop-filter: blur(20px);
	background: rgba(255, 255, 255, 0.95) !important;
}

.notification-list {
	scrollbar-width: thin;
	scrollbar-color: rgba(var(--v-theme-primary), 0.3) transparent;
}

.notification-list::-webkit-scrollbar {
	width: 4px;
}

.notification-list::-webkit-scrollbar-track {
	background: transparent;
}

.notification-list::-webkit-scrollbar-thumb {
	background: rgba(var(--v-theme-primary), 0.3);
	border-radius: 2px;
}

.notification-item {
	transition: all 0.2s ease;
	border-left: 3px solid transparent;
}

.notification-item:hover {
	background-color: rgba(var(--v-theme-primary), 0.03) !important;
}

.notification-unread {
	border-left-color: rgb(var(--v-theme-primary)) !important;
	background-color: rgba(var(--v-theme-primary), 0.02) !important;
}

/* Main Content */
.main-content {
	background: linear-gradient(135deg, #f8fafc 0%, #ffffff 50%, #f1f5f9 100%) !important;
	min-height: 100vh;
}

/* Floating Action Button */
.floating-action-btn {
	position: fixed !important;
	bottom: 24px;
	right: 24px;
	z-index: 1000;
	transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
	box-shadow: 0 6px 20px rgba(var(--v-theme-primary), 0.3);
}

.floating-action-btn:hover {
	transform: scale(1.1) translateY(-2px);
	box-shadow: 0 8px 25px rgba(var(--v-theme-primary), 0.4);
}

.floating-action-btn:active {
	transform: scale(0.95);
}

/* Toast Notifications */
.toast-notification {
	border-radius: 12px !important;
	backdrop-filter: blur(10px);
}

/* Responsive Design Enhancements */
@media (max-width: 960px) {
	.modern-drawer {
		transform: translateX(-100%);
	}
	
	.modern-drawer.v-navigation-drawer--active {
		transform: translateX(0);
		box-shadow: 0 0 30px rgba(0, 0, 0, 0.3);
	}

	.user-profile-btn .d-md-block {
		display: none !important;
	}

	.search-card {
		width: 90vw !important;
		max-width: 400px;
	}

	.notification-card {
		width: 90vw !important;
		max-width: 360px;
	}
}

@media (max-width: 600px) {
	.modern-app-bar {
		height: 64px !important;
	}

	.stats-card {
		margin: 0 8px 16px 8px;
	}

	.quick-action-btn {
		flex: 1;
	}
}

/* Badge Animations Enhanced */
.v-badge {
	animation: pulse-badge 2s infinite;
}

@keyframes pulse-badge {
	0% { opacity: 1; transform: scale(1); }
	50% { opacity: 0.7; transform: scale(1.05); }
	100% { opacity: 1; transform: scale(1); }
}

/* Menu Shadows Enhanced */
.v-menu > .v-overlay__content {
	box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15) !important;
	border-radius: 16px !important;
	backdrop-filter: blur(20px);
	border: 1px solid rgba(255, 255, 255, 0.2);
}

/* Loading Overlay Enhancement */
.v-overlay {
	backdrop-filter: blur(5px);
	background: rgba(255, 255, 255, 0.8) !important;
}

/* Scrollbar Styling Enhanced */
.modern-drawer :deep(.v-navigation-drawer__content) {
	scrollbar-width: thin;
	scrollbar-color: rgba(var(--v-theme-primary), 0.3) transparent;
}

.modern-drawer :deep(.v-navigation-drawer__content)::-webkit-scrollbar {
	width: 6px;
}

.modern-drawer :deep(.v-navigation-drawer__content)::-webkit-scrollbar-track {
	background: transparent;
}

.modern-drawer :deep(.v-navigation-drawer__content)::-webkit-scrollbar-thumb {
	background: linear-gradient(180deg, rgba(var(--v-theme-primary), 0.3), rgba(var(--v-theme-primary), 0.1));
	border-radius: 3px;
}

.modern-drawer :deep(.v-navigation-drawer__content)::-webkit-scrollbar-thumb:hover {
	background: linear-gradient(180deg, rgba(var(--v-theme-primary), 0.5), rgba(var(--v-theme-primary), 0.3));
}

/* Dark Theme Adjustments */
.v-theme--dark .modern-drawer {
	background: linear-gradient(180deg, #1a1a1a 0%, #2d2d2d 100%) !important;
	border-right-color: #404040 !important;
}

.v-theme--dark .modern-app-bar {
	background: linear-gradient(135deg, #1e1e1e 0%, #2a2a2a 100%) !important;
	border-bottom-color: #404040 !important;
}

.v-theme--dark .main-content {
	background: linear-gradient(135deg, #121212 0%, #1e1e1e 50%, #2a2a2a 100%) !important;
}

.v-theme--dark .stats-card {
	background: linear-gradient(135deg, rgba(var(--v-theme-primary), 0.1), rgba(var(--v-theme-primary), 0.05)) !important;
	border-color: rgba(var(--v-theme-primary), 0.2);
}

.v-theme--dark .search-card,
.v-theme--dark .notification-card {
	background: rgba(30, 30, 30, 0.95) !important;
}

/* Accessibility Enhancements */
.nav-item:focus-visible {
	outline: 2px solid rgb(var(--v-theme-primary));
	outline-offset: 2px;
}

.action-btn:focus-visible {
	outline: 2px solid rgb(var(--v-theme-primary));
	outline-offset: 2px;
}

/* Animation Performance Optimizations */
.nav-item,
.action-btn,
.quick-action-btn,
.user-profile-btn {
	will-change: transform;
}

/* High Contrast Mode Support */
@media (prefers-contrast: high) {
	.nav-item.v-list-item--active {
		border-right-width: 5px;
	}
	
	.notification-unread {
		border-left-width: 5px;
	}
}

/* Reduced Motion Support */
@media (prefers-reduced-motion: reduce) {
	*,
	*::before,
	*::after {
		animation-duration: 0.01ms !important;
		animation-iteration-count: 1 !important;
		transition-duration: 0.01ms !important;
	}
}
</style>