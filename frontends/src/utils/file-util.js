export function getFileTypeIcon(fileName) {
    const extension = fileName.split('.').pop().toLowerCase();
    const iconMap = {
        pdf: 'mdi-file-pdf-box',
        jpg: 'mdi-file-image',
        jpeg: 'mdi-file-image',
        png: 'mdi-file-image',
        doc: 'mdi-file-word',
        docx: 'mdi-file-word',
        xlsx: 'mdi-file-excel',
        xls: 'mdi-file-excel'
    };
    return iconMap[extension] || 'mdi-file-document';
}

export function getFileTypeColor(fileName) {
    const extension = fileName.split('.').pop().toLowerCase();
    const colorMap = {
        pdf: 'red',
        jpg: 'green',
        jpeg: 'green',
        png: 'green',
        doc: 'blue',
        docx: 'blue',
        xlsx: 'green',
        xls: 'green'
    };
    return colorMap[extension] || 'grey';
}

export function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}