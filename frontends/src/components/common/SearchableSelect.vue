<template>
    <v-autocomplete
      v-model="selectedValue"
      :items="searchableItems"
      :search="searchTerm"
      @update:search="onSearch"
      :item-title="itemTitle"
      :item-value="itemValue"
      :rules="rules"
      :variant="variant"
      :density="density"
      :placeholder="placeholder"
      :hide-details="hideDetails"
      :class="fieldClass"
      :loading="loading"
      :no-data-text="noDataText"
      :clearable="clearable"
      :menu-props="menuProps"
      v-bind="$attrs">
      
      <!-- Forward all slots -->
      <template v-for="(_, slot) of $slots" v-slot:[slot]="slotProps">
        <slot :name="slot" v-bind="slotProps" />
      </template>
    </v-autocomplete>
  </template>
  
  <script>
  export default {
    name: 'SearchableSelect',
    inheritAttrs: false,
    props: {
      modelValue: [String, Number],
      searchParam: {
        type: String,
        default: 'search'
      },
      itemTitle: {
        type: String,
        default: 'name'
      },
      itemValue: {
        type: String, 
        default: 'id'
      },
      pageSize: {
        type: Number,
        default: 10
      },
      initialLoad: {
        type: Boolean,
        default: true
      },
      debounceDelay: {
        type: Number,
        default: 300
      },
      // Current item for edit mode
      currentItem: {
        type: Object,
        default: null
      },
      // API service function
      apiService: {
        type: Function,
        required: true
      },
      // UI Props
      variant: {
        type: String,
        default: 'outlined'
      },
      density: {
        type: String,
        default: 'comfortable'
      },
      placeholder: String,
      hideDetails: {
        type: [Boolean, String],
        default: 'auto'
      },
      fieldClass: String,
      noDataText: {
        type: String,
        default: 'لا توجد نتائج'
      },
      clearable: {
        type: Boolean,
        default: true
      },
      menuProps: {
        type: Object,
        default: () => ({ maxHeight: 300 })
      },
      rules: Array
    },
    
    emits: ['update:modelValue'],
    
    data() {
      return {
        searchableItems: [],
        searchTerm: '',
        loading: false,
        searchTimeout: null
      }
    },
    
    computed: {
      selectedValue: {
        get() {
          return this.modelValue
        },
        set(value) {
          this.$emit('update:modelValue', value)
        }
      }
    },
    
    watch: {
      currentItem: {
        immediate: true,
        handler(newItem) {
          if (newItem) {
            this.ensureItemInList(newItem)
          }
        }
      }
    },
    
    mounted() {
      if (this.initialLoad) {
        this.loadInitialItems()
      }
    },
    
    methods: {
      async loadInitialItems() {
        this.loading = true
        try {
          const params = new URLSearchParams({
            page: 0,
            size: this.pageSize
          })
          
          const response = await this.apiService(params)
          this.searchableItems = response?.content || response || []
          
          // Ensure current item is in list if provided
          if (this.currentItem) {
            this.ensureItemInList(this.currentItem)
          }
        } catch (error) {
          console.error('Error loading initial items:', error)
          this.searchableItems = []
        } finally {
          this.loading = false
        }
      },
      
      async searchFromBackend(searchTerm) {
        this.loading = true
        try {
          const params = new URLSearchParams({
            page: 0,
            size: this.pageSize
          })
          
          if (searchTerm) {
            params.append(this.searchParam, searchTerm)
          }
          
          const response = await this.apiService(params)
          this.searchableItems = response?.content || response || []
        } catch (error) {
          console.error('Error searching items:', error)
          this.searchableItems = []
        } finally {
          this.loading = false
        }
      },
      
      onSearch(searchValue) {
        this.searchTerm = searchValue
        
        // Clear previous timeout
        if (this.searchTimeout) {
          clearTimeout(this.searchTimeout)
        }
        
        // If search is empty, load initial items
        if (!searchValue || searchValue.trim() === '') {
          this.loadInitialItems()
          return
        }
        
        // Debounce search
        this.searchTimeout = setTimeout(() => {
          this.searchFromBackend(searchValue.trim())
        }, this.debounceDelay)
      },
      
      ensureItemInList(item) {
        const itemId = item[this.itemValue]
        const existingItem = this.searchableItems.find(i => i[this.itemValue] === itemId)
        
        if (!existingItem) {
          this.searchableItems.unshift(item)
        }
      }
    }
  }
  </script>