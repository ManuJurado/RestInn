document.addEventListener("DOMContentLoaded", async () => {
  try {
    // 1. Obtener usuario autenticado
    const userRes = await fetch('/api/usuarios/current', {
      credentials: 'include'
    });
    if (!userRes.ok) throw new Error(`No se pudo obtener el usuario: ${userRes.status}`);
    const user = await userRes.json();
    const userName = user.nombreLogin;

    // 2. Obtener reservas
    const res = await fetch(`/reservas/${userName}/misReservas`, {
      credentials: 'include'
    });
    if (!res.ok) {
      const errText = await res.text();
      throw new Error(`Error al obtener reservas: ${res.status} - ${errText}`);
    }
    const reservas = await res.json();

    // 3. Referencia al contenedor
    const contenedor = document.getElementById('contenedorReservas');
    if (!contenedor) throw new Error("No se encontró el contenedor de reservas");

    if (reservas.length === 0) {
      contenedor.textContent = 'No tenés reservas activas.';
      return;
    }

    // 4. Crear tabla
    const table = document.createElement('table');
    table.style.borderCollapse = 'collapse';
    table.style.width = '100%';
    table.style.marginTop = '20px';

    // Encabezado
    const thead = document.createElement('thead');
    const headerRow = document.createElement('tr');
    ['ID', 'Ingreso', 'Salida', 'Reserva', 'Estado', 'Habitación', 'Huéspedes'].forEach(text => {
      const th = document.createElement('th');
      th.textContent = text;
      th.style.border = '1px solid #ddd';
      th.style.padding = '8px';
      th.style.backgroundColor = '#f2f2f2';
      headerRow.appendChild(th);
    });
    thead.appendChild(headerRow);
    table.appendChild(thead);

    // Cuerpo
    const tbody = document.createElement('tbody');
    reservas.forEach(r => {
      const tr = document.createElement('tr');
      const fmt = f => f
        ? new Date(f).toLocaleDateString('es-AR', { year: 'numeric', month: 'long', day: 'numeric' })
        : 'N/A';
      const cols = [
        r.id,
        fmt(r.fechaIngreso),
        fmt(r.fechaSalida),
        fmt(r.fechaReserva),
        r.estado || 'Desconocido',
        r.habitacionNumero || r.habitacionId || 'N/A',
        (r.huespedes || []).map(h =>
          typeof h === 'string' ? h : `${h.nombre} ${h.apellido}`
        ).join(', ')
      ];
      cols.forEach(text => {
        const td = document.createElement('td');
        td.textContent = text;
        td.style.border = '1px solid #ddd';
        td.style.padding = '8px';
        tr.appendChild(td);
      });
      tbody.appendChild(tr);
    });
    table.appendChild(tbody);
    contenedor.appendChild(table);
  } catch (error) {
    console.error('Error:', error);
    alert('No se pudieron cargar las reservas: ' + error.message);
  }
});
