// frontend/js/app.js
fetch('http://localhost:8080/habitaciones')
  .then(response => response.json())
  .then(data => {
    const lista = document.getElementById('habitaciones-list');
    data.forEach(habitacion => {
      const li = document.createElement('li');
      li.textContent = `Habitación ${habitacion.numero} - Tipo: ${habitacion.tipo}`;
      lista.appendChild(li);
    });
  })
  .catch(error => console.error('Error al obtener habitaciones:', error));
