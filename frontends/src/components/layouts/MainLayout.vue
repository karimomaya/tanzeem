<template>
	<v-app>

		<!-- Navigation Drawer -->
		<v-navigation-drawer v-if="showLayout" v-model="drawer" permanent app>
			<v-list>
				<div class="d-flex align-center">
					<v-icon color="indigo" icon="mdi-cube-outline" size="32" class="mr-2"></v-icon>
					<span class="text-h6 font-weight-bold">تنظيم</span>
				</div>
			</v-list>

			<v-divider></v-divider>

			<v-list density="compact" nav>
				<v-list-item prepend-icon="mdi-view-dashboard-outline" title="Dashboard" value="dashboard"></v-list-item>
				<v-list-item prepend-icon="mdi-package-variant-closed" title="Products" value="products"
					active></v-list-item>
				<v-list-item prepend-icon="mdi-cart-outline" title="Orders" value="orders"></v-list-item>
				<v-list-item prepend-icon="mdi-account-group-outline" title="Customers" value="customers"></v-list-item>
				<v-list-item prepend-icon="mdi-chart-bar" title="Reports" value="reports"></v-list-item>
				<v-list-item prepend-icon="mdi-cog-outline" title="Settings" value="settings"></v-list-item>
			</v-list>
		</v-navigation-drawer>


		<!-- App Bar -->
		<v-app-bar v-if="showLayout" color="white" elevation="1">
			<v-app-bar-nav-icon @click="drawer = !drawer"></v-app-bar-nav-icon>
			<div class="d-flex align-center">
				<span class="text-h6 font-weight-bold">{{ pageTitle }}</span>
			</div>
			<v-spacer></v-spacer>
			<v-btn icon>
				<v-icon>mdi-magnify</v-icon>
			</v-btn>
			<v-btn icon>
				<v-icon>mdi-bell-outline</v-icon>
				<v-badge dot color="error" floating></v-badge>
			</v-btn>
			<v-btn icon class="ml-2">
				<v-avatar size="36">
					<v-img src="https://cdn.vuetifyjs.com/images/john.jpg" alt="User"></v-img>
				</v-avatar>
			</v-btn>
		</v-app-bar>

		<v-main>
			<router-view  />
		</v-main>=
	</v-app>
</template>
  
<script>
import { useRoute } from 'vue-router';

export default {
	name: 'MainLayout',
	data() {
		return {
			drawer: false,
			pageTitle: 'تنظيمي',
		};
	},
	methods: {
		updatePageTitle(newTitle) {
			this.pageTitle = newTitle;
		},
	},
	computed: {
		showLayout() {
			const route = useRoute();
			console.log('Current route:', route);
			return route && route.meta ? !route.meta.hideLayout : true;
		},
	},
	created() {
		// Listen for the global update-title event
		this.$emitter.on('update-title', this.updatePageTitle);
	},
	beforeUnmount() {
		// Clean up the event listener
		this.$emitter.off('update-title', this.updatePageTitle);
	}
};
</script>