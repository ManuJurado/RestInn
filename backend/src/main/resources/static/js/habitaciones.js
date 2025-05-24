// src/main/resources/static/js/habitaciones.js
document.addEventListener('DOMContentLoaded', () => {
  fetch('/habitaciones')  // ruta relativa al backend en el mismo dominio
    .then(response => {
      if (!response.ok) throw new Error('Error al obtener habitaciones');
      return response.json();
    })
    .then(data => {
      const tabla = document.getElementById('tabla-habitaciones');

      // Crear encabezados
      const thead = document.createElement('thead');
      thead.innerHTML = `
        <tr>
          <th>Número</th>
          <th>Tipo</th>
          <th>Estado</th>
          <th>Capacidad</th>
          <th>Precio por Noche</th>
          <th>Comentario</th>
        </tr>
      `;
      tabla.appendChild(thead);

      // Crear cuerpo
      const tbody = document.createElement('tbody');
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
      tabla.appendChild(tbody);
    })
    .catch(error => {
      console.error('Error al cargar habitaciones:', error);
      const mensajeError = document.createElement('p');
      mensajeError.textContent = 'No se pudieron cargar las habitaciones.';
      document.body.appendChild(mensajeError);
    });
});
