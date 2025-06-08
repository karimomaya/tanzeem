// validation-util.js
// Centralized validation utilities for forms across the application

/**
 * Basic validation rules
 */
export const validationRules = {
    // Required field validation
    required: (fieldName = 'هذا الحقل') => (value) => {
        return !!value || `${fieldName} مطلوب`;
    },

    // Email validation
    email: (value) => {
        if (!value) return true; // Optional field
        const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return pattern.test(value) || 'البريد الإلكتروني غير صحيح';
    },

    // Phone validation (Arabic countries format)
    phone: (value) => {
        if (!value) return true; // Optional field
        const pattern = /^[0-9+\-\s()]+$/;
        return pattern.test(value) || 'رقم الهاتف غير صحيح';
    },

    // URL validation
    url: (value) => {
        if (!value) return true; // Optional field
        const pattern = /^https?:\/\/.+/i;
        return pattern.test(value) || 'يرجى إدخال رابط صحيح';
    },

    // Positive number validation
    positive: (value) => {
        return value > 0 || 'يجب أن يكون أكبر من صفر';
    },

    // Non-negative number validation
    nonNegative: (value) => {
        return value >= 0 || 'يجب أن يكون صفر أو أكبر';
    },

    // Number range validation
    numberRange: (min, max) => (value) => {
        if (value < min || value > max) {
            return `يجب أن يكون بين ${min} و ${max}`;
        }
        return true;
    },

    // String length validation
    minLength: (minLength) => (value) => {
        if (!value) return true; // Optional field
        return value.length >= minLength || `يجب أن يكون ${minLength} أحرف على الأقل`;
    },

    maxLength: (maxLength) => (value) => {
        if (!value) return true; // Optional field
        return value.length <= maxLength || `يجب أن يكون ${maxLength} أحرف كحد أقصى`;
    },

    // SKU validation (alphanumeric with dashes/underscores)
    sku: (value) => {
        if (!value) return true; // Optional field
        const pattern = /^[A-Z0-9-_]+$/i;
        return pattern.test(value) || 'رمز المنتج يجب أن يحتوي على أحرف وأرقام فقط';
    },

    // Barcode validation (numbers only)
    barcode: (value) => {
        if (!value) return true; // Optional field
        const pattern = /^[0-9]+$/;
        return pattern.test(value) || 'الباركود يجب أن يحتوي على أرقام فقط';
    },

    // Postal code validation (5 digits)
    postalCode: (value) => {
        if (!value) return true; // Optional field
        const pattern = /^[0-9]{5}$/;
        return pattern.test(value) || 'الرمز البريدي يجب أن يكون 5 أرقام';
    },

    // Tax number validation (alphanumeric)
    taxNumber: (value) => {
        if (!value) return true; // Optional field
        const pattern = /^[A-Z0-9]+$/i;
        return pattern.test(value) || 'الرقم الضريبي يجب أن يحتوي على أحرف وأرقام فقط';
    },

    // Code validation (for supplier codes, etc.)
    code: (value) => {
        if (!value) return true; // Optional field
        const pattern = /^[A-Z0-9-_]+$/i;
        return pattern.test(value) || 'الكود يجب أن يحتوي على أحرف وأرقام فقط';
    },

    // File validation
    maxFileSize: (maxSizeMB) => (files) => {
        if (!files || files.length === 0) return true; // Optional
        const maxSize = maxSizeMB * 1024 * 1024;
        
        const file = Array.isArray(files) ? files[0] : files;
        if (file.size > maxSize) {
            return `حجم الملف يجب أن يكون أقل من ${maxSizeMB} ميجابايت`;
        }
        return true;
    },

    // Image file validation
    imageFile: (maxSizeMB = 10) => (files) => {
        if (!files || files.length === 0) return true; // Optional
        
        const file = Array.isArray(files) ? files[0] : files;
        const maxSize = maxSizeMB * 1024 * 1024;
        const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];

        if (file.size > maxSize) {
            return `حجم الملف يجب أن يكون أقل من ${maxSizeMB} ميجابايت`;
        }
        if (!allowedTypes.includes(file.type)) {
            return 'نوع الملف غير مدعوم. يرجى اختيار صورة بصيغة JPG, PNG, GIF, أو WebP';
        }
        return true;
    },

    // Multiple files validation
    maxFileCount: (maxCount) => (files) => {
        if (!files || files.length === 0) return true; // Optional
        return files.length <= maxCount || `يمكن رفع ${maxCount} ملفات كحد أقصى`;
    },

    // Attachment files validation (for purchase orders)
    attachmentFiles: (maxCount = 10, maxSizeMB = 10) => (files) => {
        if (!files || files.length === 0) return true; // Optional

        // Check count
        if (files.length > maxCount) {
            return `يمكن رفع ${maxCount} ملفات كحد أقصى`;
        }

        // Check size
        const maxSize = maxSizeMB * 1024 * 1024;
        const invalidFiles = files.filter(file => file.size > maxSize);
        if (invalidFiles.length > 0) {
            return `الملفات التالية تتجاوز ${maxSizeMB} ميجابايت: ${invalidFiles.map(f => f.name).join(', ')}`;
        }

        // Check file types
        const allowedTypes = ['.pdf', '.jpg', '.jpeg', '.png', '.doc', '.docx', '.xlsx', '.xls'];
        const invalidTypeFiles = files.filter(file => {
            const extension = '.' + file.name.split('.').pop().toLowerCase();
            return !allowedTypes.includes(extension);
        });
        if (invalidTypeFiles.length > 0) {
            return `نوع الملفات غير مدعوم: ${invalidTypeFiles.map(f => f.name).join(', ')}`;
        }

        return true;
    }
};

/**
 * Composite validation rules for common form fields
 */
export const fieldValidations = {
    // Product validations
    productName: [validationRules.required('اسم المنتج')],
    productSku: [validationRules.required('رمز المنتج'), validationRules.sku],
    productPrice: [validationRules.required('السعر'), validationRules.positive],
    productStock: [validationRules.required('الكمية'), validationRules.nonNegative],
    productBarcode: [validationRules.barcode],
    productUnit: [validationRules.required('الوحدة')],
    productCategory: [validationRules.required('التصنيف')],
    productImage: [validationRules.imageFile()],

    // Category validations
    categoryName: [validationRules.required('اسم التصنيف')],
    categoryIcon: [validationRules.required('الأيقونة')],
    categoryColor: [validationRules.required('اللون')],

    // Supplier validations
    supplierName: [validationRules.required('اسم المورد')],
    supplierCode: [validationRules.required('كود المورد'), validationRules.code],
    supplierPhone: [validationRules.required('رقم الهاتف'), validationRules.phone],
    supplierEmail: [validationRules.email],
    supplierPostalCode: [validationRules.postalCode],
    supplierTaxNumber: [validationRules.taxNumber],
    supplierCreditLimit: [validationRules.nonNegative],

    // Purchase Order validations
    invoiceNumber: [
        validationRules.required('رقم الأمر'),
        validationRules.minLength(3)
    ],
    purchaseDate: [validationRules.required('تاريخ الأمر')],
    supplier: [validationRules.required('المورد')],
    orderItems: [validationRules.required('المنتجات')],
    attachments: [validationRules.attachmentFiles()],

    // Common validations
    email: [validationRules.email],
    phone: [validationRules.phone],
    url: [validationRules.url],
    required: (fieldName) => [validationRules.required(fieldName)],
    positiveNumber: [validationRules.positive],
    nonNegativeNumber: [validationRules.nonNegative],
    nonNegativeAndRequired: [validationRules.nonNegative, validationRules.required('')]
};

/**
 * Form validation helper functions
 */
export const formHelpers = {
    // Validate entire form object
    validateForm: (formData, fieldRules) => {
        const errors = {};
        
        Object.keys(fieldRules).forEach(field => {
            const rules = fieldRules[field];
            const value = formData[field];
            
            for (const rule of rules) {
                const result = rule(value);
                if (result !== true) {
                    errors[field] = result;
                    break; // Stop at first error
                }
            }
        });

        return {
            isValid: Object.keys(errors).length === 0,
            errors
        };
    },

    // Check if a specific field is valid
    validateField: (value, rules) => {
        for (const rule of rules) {
            const result = rule(value);
            if (result !== true) {
                return { isValid: false, error: result };
            }
        }
        return { isValid: true, error: null };
    },

    // Get validation rules by field type
    getRules: (fieldType, options = {}) => {
        const baseRules = fieldValidations[fieldType] || [];
        
        // Add conditional rules based on options
        if (options.required && !baseRules.some(rule => rule.toString().includes('مطلوب'))) {
            baseRules.unshift(validationRules.required(options.fieldName || fieldType));
        }
        
        return baseRules;
    }
};

/**
 * Custom validation builders for specific use cases
 */
export const customValidations = {
    // Build validation for dropdown/select fields
    selectField: (fieldName, required = true) => {
        return required ? [validationRules.required(fieldName)] : [];
    },

    // Build validation for numeric input with range
    numericField: (fieldName, options = {}) => {
        const rules = [];
        
        if (options.required) {
            rules.push(validationRules.required(fieldName));
        }
        
        if (options.positive) {
            rules.push(validationRules.positive);
        } else if (options.nonNegative) {
            rules.push(validationRules.nonNegative);
        }
        
        if (options.min !== undefined && options.max !== undefined) {
            rules.push(validationRules.numberRange(options.min, options.max));
        }
        
        return rules;
    },

    // Build validation for text input with length constraints
    textField: (fieldName, options = {}) => {
        const rules = [];
        
        if (options.required) {
            rules.push(validationRules.required(fieldName));
        }
        
        if (options.minLength) {
            rules.push(validationRules.minLength(options.minLength));
        }
        
        if (options.maxLength) {
            rules.push(validationRules.maxLength(options.maxLength));
        }
        
        return rules;
    }
};

// Export default for convenient importing
export default {
    rules: validationRules,
    fields: fieldValidations,
    helpers: formHelpers,
    custom: customValidations
};