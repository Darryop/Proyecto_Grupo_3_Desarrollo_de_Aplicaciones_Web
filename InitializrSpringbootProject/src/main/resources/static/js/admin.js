// admin.js - Funcionalidades específicas del panel de administración

document.addEventListener('DOMContentLoaded', () => {
    // Inicializar tooltips de Bootstrap
    const tooltipTriggerList = [...document.querySelectorAll('[data-bs-toggle="tooltip"]')];
    tooltipTriggerList.forEach(el => new bootstrap.Tooltip(el));

    // Confirmación antes de eliminar (usa confirmAction si está disponible en main.js)
    const deleteButtons = document.querySelectorAll('.btn-delete');
    deleteButtons.forEach(button => {
        button.addEventListener('click', e => {
            if (typeof confirmAction === 'function') {
                confirmAction('¿Estás seguro de eliminar este elemento?', () => {});
            } else if (!confirm('¿Estás seguro de eliminar este elemento?')) {
                e.preventDefault();
            }
        });
    });

    // Filtros de búsqueda con debounce
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        let timeout;
        searchInput.addEventListener('keyup', function () {
            clearTimeout(timeout);
            timeout = setTimeout(() => {
                const filter = this.value.toLowerCase();
                const rows = document.querySelectorAll('table tbody tr');
                rows.forEach(row => {
                    const text = row.textContent.toLowerCase();
                    row.style.display = text.includes(filter) ? '' : 'none';
                });
            }, 300);
        });
    }

    // Ordenar tablas (soporta texto, números y fechas)
    const sortableHeaders = document.querySelectorAll('.sortable');
    sortableHeaders.forEach(header => {
        header.addEventListener('click', function () {
            const table = this.closest('table');
            const tbody = table.querySelector('tbody');
            const rows = Array.from(tbody.querySelectorAll('tr'));
            const columnIndex = Array.from(this.parentNode.children).indexOf(this);
            const isAscending = this.classList.contains('asc');

            sortableHeaders.forEach(h => h.classList.remove('asc', 'desc'));

            rows.sort((a, b) => {
                const aValue = a.children[columnIndex].textContent.trim();
                const bValue = b.children[columnIndex].textContent.trim();

                // Detectar tipo de dato
                const aNum = parseFloat(aValue.replace(',', '.'));
                const bNum = parseFloat(bValue.replace(',', '.'));
                if (!isNaN(aNum) && !isNaN(bNum)) {
                    return isAscending ? bNum - aNum : aNum - bNum;
                }
                const aDate = Date.parse(aValue);
                const bDate = Date.parse(bValue);
                if (!isNaN(aDate) && !isNaN(bDate)) {
                    return isAscending ? bDate - aDate : aDate - bDate;
                }
                return isAscending ? bValue.localeCompare(aValue) : aValue.localeCompare(bValue);
            });

            rows.forEach(row => tbody.appendChild(row));
            this.classList.toggle('asc', !isAscending);
            this.classList.toggle('desc', isAscending);
        });
    });
});

// Exportar datos
function exportData(format) {
    const table = document.querySelector('table');
    if (!table) return;

    const headers = [...table.querySelectorAll('thead th')].map(th => th.textContent);
    const data = [headers];

    const rows = table.querySelectorAll('tbody tr');
    rows.forEach(row => {
        const rowData = [...row.querySelectorAll('td')].map(td => td.textContent);
        data.push(rowData);
    });

    if (format === 'csv') {
        const csv = data.map(row => row.join(',')).join('\n');
        downloadFile(csv, 'data.csv', 'text/csv');
    } else if (format === 'excel') {
        showToast('Exportar a Excel aún no implementado', 'info');
    }
}

function downloadFile(content, filename, mimeType) {
    const blob = new Blob([content], { type: mimeType });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
}

// Validación de formularios
function validateAdminForm(formId) {
    const form = document.getElementById(formId);
    if (!form) return true;

    const requiredFields = form.querySelectorAll('[required]');
    let isValid = true;

    requiredFields.forEach(field => {
        if (!field.value.trim()) {
            field.classList.add('is-invalid');
            isValid = false;
        } else {
            field.classList.remove('is-invalid');
        }
    });

    return isValid;
}

// Cargar datos para edición
async function loadDataForEdit(endpoint, id) {
    try {
        const response = await fetch(`${endpoint}/${id}`);
        if (response.ok) return await response.json();
    } catch (error) {
        showToast('Error al cargar datos', 'error');
    }
    return null;
}

// Subir imagen
async function uploadImage(inputId) {
    const input = document.getElementById(inputId);
    if (!input || !input.files[0]) return null;

    const formData = new FormData();
    formData.append('image', input.files[0]);

    try {
        const response = await fetch('/admin/upload/image', { method: 'POST', body: formData });
        if (response.ok) {
            const data = await response.json();
            return data.url;
        }
    } catch (error) {
        showToast('Error al subir imagen', 'error');
    }
    return null;
}
