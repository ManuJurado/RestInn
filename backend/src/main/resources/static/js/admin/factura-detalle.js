document.addEventListener('DOMContentLoaded', async () => {
    const $ = id => document.getElementById(id);
    const token = sessionStorage.getItem('jwt');

    if (!token) {
        mostrarAlerta('Sesión expirada. Por favor, inicia sesión.');
        location.href = '/login.html';
        return;
    }

    const params = new URLSearchParams(location.search);
    const facturaId = params.get('factura');

    if (!facturaId) {
        mostrarAlerta('No se especificó el ID de factura.');
        history.back();
        return;
    }

    let factura;

    async function cargarFactura() {
        const res = await fetch(`/api/facturas/${facturaId}`, {
            headers: { Authorization: `Bearer ${token}` }
        });
        const data = await res.json();
        if (!res.ok) throw new Error(data.message || 'Error al obtener factura');
        return data;
    }

    function mostrarFactura() {
        $('facturaId').textContent = factura.id;
        $('reservaId').textContent = factura.reservaId ?? '—';
        $('estadoFactura').textContent = factura.estado ?? '—';
        $('ingreso').textContent = factura.ingreso ?? '—';
        $('salida').textContent = factura.salida ?? '—';
        $('cliente').textContent = factura.clienteNombre ?? '—';
        $('habitacion').textContent = factura.habitacionNumero ?? '—';

        $('subtotal').textContent = factura.subtotal.toFixed(2);
        $('total').textContent = factura.totalFinal.toFixed(2);

        if (factura.descuento > 0) {
            $('descuento').textContent = factura.descuento;
            $('descuentoRow').style.display = 'block';
        } else {
            $('descuentoRow').style.display = 'none';
        }

        if (factura.interes > 0) {
            $('interes').textContent = factura.interes;
            $('interesRow').style.display = 'block';
        } else {
            $('interesRow').style.display = 'none';
        }

        const detalle = $('detalleConsumos');
        detalle.innerHTML = '';
        factura.consumos?.forEach(c => {
            const p = document.createElement('p');
            p.textContent = `• ${c.descripcion} x${c.cantidad} - $${c.subtotal.toFixed(2)}`;
            detalle.appendChild(p);
        });

        if (factura.estado === 'EN_PROCESO') {
            $('seccionMetodo').style.display = 'block';
            $('metodo').value = factura.metodoPago ?? 'EFECTIVO';
            $('cuotas').value = factura.cuotas ?? 1;
        } else {
            $('seccionMetodo').style.display = 'none';
        }
    }

    $('btnActualizar').addEventListener('click', async () => {
        const body = {
            metodoPago: $('metodo').value,
            cuotas: Number($('cuotas').value || 1)
        };
        try {
            const res = await fetch(`/api/facturas/${factura.id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`
                },
                body: JSON.stringify(body)
            });
            const updated = await res.json();
            if (!res.ok) throw new Error(updated.message || 'Error al actualizar factura');
            factura = updated;
            mostrarFactura();
            mostrarAlerta('Factura actualizada correctamente.');
        } catch (e) {
            mostrarAlerta(e.message);
        }
    });

    $('btnDescargar').addEventListener('click', async () => {
        try {
            const res = await fetch(`/api/facturas/${factura.id}/pdf`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            if (!res.ok) {
                const txt = await res.text();
                throw new Error(txt || 'Error al generar PDF');
            }
            const blob = await res.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `factura_${factura.id}.pdf`;
            document.body.appendChild(a);
            a.click();
            a.remove();
            window.URL.revokeObjectURL(url);
        } catch (e) {
            mostrarAlerta(e.message);
        }
    });

    try {
        factura = await cargarFactura();
        mostrarFactura();
    } catch (e) {
        mostrarAlerta(e.message);
    }
});
