// main.js - Funcionalidades generales del sitio

document.addEventListener('DOMContentLoaded', () => {
    // Inicializar tooltips y popovers
    [...document.querySelectorAll('[data-bs-toggle="tooltip"]')].forEach(el => new bootstrap.Tooltip(el));
    [...document.querySelectorAll('[data-bs-toggle="popover"]')].forEach(el => new bootstrap.Popover(el));

    // Inicializar validación de formularios
    initFormValidation();

    // Manejar mensajes flash
    initFlashMessages();

    // Actualizar contador del carrito si el usuario está autenticado
    if (document.querySelector('[data-user-authenticated="true"]')) {
        updateCartCount();
    }
});

// ✅ Validación de formularios
function initFormValidation() {
    const forms = document.querySelectorAll('form[needs-validation]');
    forms.forEach(form => {
        form.addEventListener('submit', event => {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        });
    });
}

// ✅ Manejo de mensajes flash
function initFlashMessages() {
    const alerts = document.querySelectorAll('.alert:not(.alert-permanent)');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.classList.add('fade');
            setTimeout(() => alert.remove(), 500);
        }, 5000);
    });
}

// ✅ Actualizar contador del carrito
async function updateCartCount() {
    try {
        const response = await fetch('/carrito/count');
        if (response.ok) {
            const count = await response.json();
            const cartBadges = document.querySelectorAll('.cart-count-badge');
            cartBadges.forEach(badge => {
                badge.textContent = count;
                badge.style.display = count > 0 ? 'inline-block' : 'none';
            });
        }
    } catch (error) {
        console.error('Error al actualizar contador del carrito:', error);
    }
}

// ✅ Mostrar toast notifications
function showToast(message, type = 'info') {
    let toastContainer = document.getElementById('toast-container');
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.id = 'toast-container';
        toastContainer.className = 'toast-container position-fixed top-0 end-0 p-3';
        document.body.appendChild(toastContainer);
    }

    const toastId = 'toast-' + crypto.randomUUID();
    const toastHtml = `
        <div id="${toastId}" class="toast align-items-center text-bg-${type} border-0" role="alert">
            <div class="d-flex">
                <div class="toast-body">${message}</div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        </div>
    `;
    toastContainer.insertAdjacentHTML('beforeend', toastHtml);

    const toastElement = document.getElementById(toastId);
    const toast = new bootstrap.Toast(toastElement, { autohide: true, delay: 3000 });
    toast.show();

    toastElement.addEventListener('hidden.bs.toast', () => toastElement.remove());
}

// ✅ Confirmación de acciones (puede usarse en admin.js y carrito.js)
function confirmAction(message, callback) {
    if (confirm(message)) {
        callback();
    }
}

// ✅ Manejo de errores de formulario
function handleFormError(formId, errors) {
    const form = document.getElementById(formId);
    if (!form) return;

    // Limpiar errores previos
    form.querySelectorAll('.invalid-feedback').forEach(el => el.remove());

    // Agregar nuevos errores
    Object.keys(errors).forEach(field => {
        const input = form.querySelector(`[name="${field}"]`);
        if (input) {
            input.classList.add('is-invalid');
            const errorDiv = document.createElement('div');
            errorDiv.className = 'invalid-feedback';
            errorDiv.textContent = errors[field];
            input.parentNode.appendChild(errorDiv);
        }
    });
}

// ✅ Agregar al carrito
async function addToCart(itemId, type) {
    try {
        const csrfTokenMeta = document.querySelector('meta[name="_csrf"]');
        const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');

        if (!csrfTokenMeta || !csrfHeaderMeta) {
            showToast('Error: falta token CSRF', 'error');
            return;
        }

        const csrfToken = csrfTokenMeta.content;
        const csrfHeader = csrfHeaderMeta.content;

        const response = await fetch(`/carrito/agregar/${type}/${itemId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            }
        });

        if (response.ok) {
            showToast('¡Agregado al carrito exitosamente!', 'success');
            await updateCartCount();
        } else {
            const error = await response.text();
            showToast('Error: ' + error, 'error');
        }
    } catch (error) {
        console.error('Error al agregar al carrito:', error);
        showToast('Error de conexión al servidor', 'error');
    }
}

// ✅ Exportar funciones globales
if (typeof window !== 'undefined') {
    window.addToCart = addToCart;
    window.showToast = showToast;
    window.updateCartCount = updateCartCount;
    window.confirmAction = confirmAction;
    window.handleFormError = handleFormError;
}
