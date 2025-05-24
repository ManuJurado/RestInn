document.addEventListener('DOMContentLoaded', () => {
  fetch('/habitaciones')
    .then(response => {
      if (!response.ok) throw new Error('Error al obtener habitaciones');
      return response.json();
    })
    .then(data => {
      const tbody = document.querySelector('#tabla-habitaciones tbody');
      tbody.innerHTML = ''; // limpiar por si acaso

      data.forEach(h => {
        const fila = document.createElement('tr');
        fila.innerHTML = `
          <td>${h.numero}</td>
          <td>${h.tipo}</td>
          <td>${h.estado}</td>
          <td>${h.capacidad}</td>
          <td>$${h.precioNoche.toFixed(2)}</td>
          <td>${h.comentario || '-'}</td>
        `;
        tbody.appendChild(fila);
      });
    })
    .catch(error => {
      console.error('Error al cargar habitaciones:', error);
      const mensajeError = document.createElement('p');
      mensajeError.textContent = 'No se pudieron cargar las habitaciones.';
      document.body.appendChild(mensajeError);
    });
});
