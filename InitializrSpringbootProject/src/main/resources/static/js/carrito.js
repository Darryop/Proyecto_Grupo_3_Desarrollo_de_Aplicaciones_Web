// carrito.js - Funcionalidades del carrito de compras

// Actualizar cantidad de un ítem
async function updateQuantity(itemId, change) {
    try {
        const response = await fetch(`/carrito/item/${itemId}/cantidad?change=${change}`, { method: 'PUT' });
        if (response.ok) {
            await updateCartCount();
            showToast('Cantidad actualizada', 'success');
        } else {
            showToast('Error al actualizar cantidad', 'error');
        }
    } catch {
        showToast('Error de conexión', 'error');
    }
}

// Eliminar un ítem del carrito
async function removeItem(itemId) {
    confirmAction('¿Estás seguro de eliminar este item del carrito?', async () => {
        try {
            const response = await fetch(`/carrito/item/${itemId}`, { method: 'DELETE' });
            if (response.ok) {
                await updateCartCount();
                showToast('Item eliminado', 'success');
            } else {
                showToast('Error al eliminar item', 'error');
            }
        } catch {
            showToast('Error de conexión', 'error');
        }
    });
}

// Vaciar todo el carrito
async function vaciarCarrito() {
    confirmAction('¿Estás seguro de vaciar todo el carrito?', async () => {
        try {
            const response = await fetch('/carrito/vaciar', { method: 'DELETE' });
            if (response.ok) {
                showToast('Carrito vaciado', 'success');
                await updateCartCount();
            } else {
                showToast('Error al vaciar carrito', 'error');
            }
        } catch {
            showToast('Error de conexión', 'error');
        }
    });
}

// Procesar pago
async function procesarPago(metodoPago) {
    try {
        const response = await fetch('/ventas/procesar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ metodoPago })
        });
        if (response.ok) {
            showToast('¡Compra realizada exitosamente!', 'success');
            setTimeout(() => (window.location.href = '/'), 2000);
        } else {
            const error = await response.text();
            showToast('Error al procesar pago: ' + error, 'error');
        }
    } catch {
        showToast('Error de conexión', 'error');
    }
}
