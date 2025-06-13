document.addEventListener('DOMContentLoaded', async () => {
    const token = sessionStorage.getItem('jwt');
    const params = new URLSearchParams(window.location.search);
    const clienteId = params.get('id'); // ← se saca de la URL

    if (!token || !clienteId) {
        mostrarAlerta('Sesión expirada o acceso inválido.');
        location.href = '/login.html';
        return;
    }

    const tabla = document.getElementById('tablaFacturas');

    try {
        const res = await fetch(`/api/facturas/cliente/${clienteId}`, {
            headers: { Authorization: `Bearer ${token}` }
        });

        const facturas = await res.json();

        if (!res.ok) throw new Error(facturas.message || 'Error al obtener facturas');

        if (facturas.length === 0) {
            tabla.innerHTML = `<tr><td colspan="5">No hay facturas disponibles.</td></tr>`;
            return;
        }

        facturas.forEach(f => {
            const fila = document.createElement('tr');

            fila.innerHTML = `
                <td>${f.fechaEmision ?? '—'}</td>
                <td>$${f.totalFinal?.toFixed(2) ?? '—'}</td>
                <td>${f.reservaId ?? '—'}</td>
                <td>${f.estado ?? '—'}</td>
                <td>
                    <button onclick="verDetalles(${f.id})" class="small-button">Ver detalles</button>
                    <button onclick="descargarPDF(${f.id})" class="small-button">PDF</button>
                </td>
            `;

            tabla.appendChild(fila);
        });

    } catch (err) {
        mostrarAlerta(err.message);
    }
});

function verDetalles(facturaId) {
    window.location.href = `/clientes/factura-detalle.html?factura=${facturaId}`;
}

async function descargarPDF(facturaId) {
    const token = sessionStorage.getItem('jwt');

    try {
        const res = await fetch(`/api/facturas/${facturaId}/pdf`, {
            headers: { Authorization: `Bearer ${token}` }
        });

        if (!res.ok) throw new Error('No se pudo descargar el PDF.');

        const blob = await res.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `factura_${facturaId}.pdf`;
        document.body.appendChild(a);
        a.click();
        a.remove();
        window.URL.revokeObjectURL(url);
    } catch (e) {
        mostrarAlerta(e.message);
    }
}
