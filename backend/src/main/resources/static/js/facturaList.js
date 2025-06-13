// js/facturaList.js

document.addEventListener('DOMContentLoaded', async () => {
  const $ = id => document.getElementById(id);
  const token = sessionStorage.getItem('jwt');
  if (!token) {
    mostrarAlerta('Sesión expirada. Por favor, inicia sesión.');
    return location.href = '/login.html';
  }

  // Referencias DOM
  const tablaBody    = document.querySelector('#tablaFacturas tbody');
  const filtroEstado = $('filtroEstado');
  const filtroTipo   = $('filtroTipo');
  const btnFiltros   = $('btnAplicarFiltros');

  // 1) Traer todas las facturas
  async function fetchFacturas() {
    const res = await fetch('/api/facturas', {
      headers: { Authorization: `Bearer ${token}` }
    });
    if (res.status === 401) {
      sessionStorage.removeItem('jwt');
      return location.href = '/login.html';
    }
    if (!res.ok) {
      const err = await res.json().catch(() => ({ message: res.statusText }));
      throw new Error(err.message);
    }
    return await res.json();
  }

  // 2) Filtrar por estado y tipo
  function aplicarFiltros(facturas) {
    return facturas.filter(f => {
      return (!filtroEstado.value || f.estado === filtroEstado.value)
          && (!filtroTipo.value   || f.tipoFactura === filtroTipo.value);
    });
  }

  // 3) Renderizar la tabla según el array
  function renderTabla(facturas) {
    tablaBody.innerHTML = '';
    facturas.forEach(f => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${new Date(f.fechaEmision).toLocaleDateString()}</td>
        <td>$${f.totalFinal.toFixed(2)}</td>
        <td>${f.tipoFactura}</td>
        <td>${f.estado}</td>
        <td>${f.clienteNombre}</td>
        <td>${f.reservaId}</td>
        <td>
          <button class="normal-button" data-id="${f.id}" onclick="descargarPDF(this)">
            PDF
          </button>
        </td>
      `;
      tablaBody.appendChild(tr);
    });
  }

  // 4) Descargar PDF de una factura
  window.descargarPDF = async (btn) => {
    const facturaId = btn.dataset.id;
    try {
      const res = await fetch(`/api/facturas/${facturaId}/pdf`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      if (!res.ok) throw new Error('Error al generar PDF');
      const blob = await res.blob();
      const url  = URL.createObjectURL(blob);
      const a    = document.createElement('a');
      a.href     = url;
      a.download = `factura_${facturaId}.pdf`;
      document.body.appendChild(a);
      a.click();
      a.remove();
      URL.revokeObjectURL(url);
    } catch (e) {
      mostrarAlerta(e.message);
    }
  };

  // 5) Flujo principal
  try {
    const todas = await fetchFacturas();
    renderTabla(todas);

    btnFiltros.addEventListener('click', () => {
      const filtradas = aplicarFiltros(todas);
      renderTabla(filtradas);
    });
  } catch (e) {
    mostrarAlerta(e.message);
  }
});
