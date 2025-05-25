fetch('http://localhost:8080/reservas')
  .then(response => response.json())
  .then(reservas => {
    const tbody = document.querySelector('#reservas-table tbody');
    reservas.forEach(r => {
      const row = document.createElement('tr');
      row.innerHTML = `
        <td>${r.id}</td>
        <td>${r.fechaReserva || '-'}</td>
        <td>${r.fechaIngreso || '-'}</td>
        <td>${r.fechaSalida || '-'}</td>
        <td>${r.habitacionId || 'N/D'}</td>
        <td>${r.usuarioId || 'N/D'}</td>
        <td>${r.estado || '-'}</td>
      `;
      tbody.appendChild(row);
    });
  })
  .catch(error => {
    console.error('Error al cargar reservas:', error);
    const tbody = document.querySelector('#reservas-table tbody');
    const row = document.createElement('tr');
    row.innerHTML = `<td colspan="7">Error al cargar las reservas</td>`;
    tbody.appendChild(row);
  });
