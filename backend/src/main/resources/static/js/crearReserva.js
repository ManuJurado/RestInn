    // CrearReserva.js embebido:

    document.addEventListener("DOMContentLoaded", () => {
      const form = document.getElementById('formReserva');
      const container = document.getElementById('huespedesContainer');
      const fechaIngresoInput = document.getElementById('fechaIngreso');
      const fechaSalidaInput  = document.getElementById('fechaSalida');
      const cantidadInput     = document.getElementById('cantidadHuespedes');
      const habitacionSelect  = document.getElementById('habitacionSelect');

      // Carga y actualización de habitaciones disponibles
      async function cargarHabitacionesDisponibles() {
        const desde = fechaIngresoInput.value;
        const hasta = fechaSalidaInput.value;
        let cant  = Number(cantidadInput.value);
        if (isNaN(cant) || cant < 1) cant = 1;
        if (cant > 5) cant = 5;

        // Validación básica de fechas
        if (!desde || !hasta || desde > hasta) {
          habitacionSelect.innerHTML = `<option value="" disabled selected>— Seleccioná fechas y cantidad válidas —</option>`;
          habitacionSelect.disabled = true;
          container.innerHTML = '';
          return;
        }

        habitacionSelect.disabled = false;
        habitacionSelect.innerHTML = `<option disabled selected>Cargando...</option>`;

        try {
          const token = sessionStorage.getItem('jwt');
          const res = await fetch(
            `/api/habitaciones/disponibles?desde=${desde}&hasta=${hasta}`,
            { headers: { 'Authorization': `Bearer ${token}` } }
          );
          if (!res.ok) throw new Error(`HTTP ${res.status}`);
          const habitaciones = await res.json();

          // Filtrar por capacidad mínima
          const filtradas = habitaciones.filter(h => h.capacidad >= cant);

          if (filtradas.length) {
            habitacionSelect.innerHTML = filtradas.map(h =>
              `<option value="${h.id}" data-capacidad="${h.capacidad}">` +
                `${h.tipo} N°${h.numero} — Piso ${h.piso} — Capacidad ${h.capacidad} — $${h.precioNoche}` +
              `</option>`
            ).join('');
          } else {
            habitacionSelect.innerHTML = `<option disabled selected>No hay habitaciones para ${cant} huéspedes</option>`;
          }

          // Generar campos de huésped según cantidad
          generarCamposHuespedes(cant);
        } catch (err) {
          console.error(err);
          habitacionSelect.innerHTML = `<option disabled selected>Error al cargar</option>`;
        }
      }

      // Genera los inputs de huésped
      function generarCamposHuespedes(cant) {
        container.innerHTML = '';
        for (let i = 1; i <= cant; i++) {
          const div = document.createElement('div');
          div.className = 'grupo-huesped';
          div.innerHTML = `
            <label>Huésped ${i}:
              <input type="text" placeholder="Nombre" class="inputs nombre" required>
              <input type="text" placeholder="Apellido" class="inputs apellido" required>
              <input type="number" placeholder="DNI" class="inputs dni" required>
            </label>`;
          container.appendChild(div);
        }
      }

      // Listeners: fechas y cantidad disparan recarga
      fechaIngresoInput.addEventListener('change', cargarHabitacionesDisponibles);
      fechaSalidaInput.addEventListener('change', cargarHabitacionesDisponibles);
      cantidadInput.addEventListener('change', () => {
        let val = Number(cantidadInput.value);
        if (val > 5) { mostrarAlerta('Máximo 5 huéspedes'); cantidadInput.value = 5; val = 5; }
        if (val < 1) { cantidadInput.value = 1; val = 1; }
        cargarHabitacionesDisponibles();
      });

      // Envío del formulario
      form.addEventListener('submit', async e => {
        e.preventDefault();
        const desde = fechaIngresoInput.value;
        const hasta = fechaSalidaInput.value;
        const cant  = Number(cantidadInput.value);
        const habitId = Number(habitacionSelect.value);

        if (!desde || !hasta || !habitId) {
          mostrarAlerta('Completá todos los campos correctamente.');
          return;
        }

        const huespedes = Array.from(container.children).map(div => ({
          nombre: div.querySelector('.nombre').value,
          apellido: div.querySelector('.apellido').value,
          dni: div.querySelector('.dni').value
        }));

        if (huespedes.length !== cant) {
          mostrarAlerta(`Se esperaban ${cant} huéspedes, pero hay ${huespedes.length}.`);
          return;
        }

        try {
          const token = sessionStorage.getItem('jwt');
          const dto = { fechaIngreso: desde, fechaSalida: hasta, habitacionId: habitId, huespedes };
          const res = await fetch('/api/reservas', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
            body: JSON.stringify(dto)
          });
          if (!res.ok) throw new Error(await res.text());

          mostrarAlerta('Reserva creada exitosamente');
          setTimeout(() => window.location.href = '/clientes/reservas.html', 1000);
        } catch (err) {
          mostrarAlerta('Error al crear la reserva: ' + err.message);
        }
      });
      // Inicializar
      cargarHabitacionesDisponibles();
    });