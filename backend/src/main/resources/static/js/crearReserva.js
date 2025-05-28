document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById('formReserva');
  const container = document.getElementById('huespedesContainer');
  const btnAgregar = document.getElementById('btnAgregarHuesped');
  const fechaIngresoInput = document.getElementById('fechaIngreso');
  const fechaSalidaInput  = document.getElementById('fechaSalida');
  const habitacionSelect  = document.getElementById('habitacionSelect');

  // 1) Función para cargar las habitaciones disponibles
  async function cargarHabitacionesDisponibles() {
    const desde = fechaIngresoInput.value;
    const hasta = fechaSalidaInput.value;

    // sólo si ambas fechas están completas y desde <= hasta
    if (!desde || !hasta || desde > hasta) {
      habitacionSelect.innerHTML = `<option value="" disabled selected>— Seleccioná fechas válidas —</option>`;
      habitacionSelect.disabled = true;
      return;
    }

    habitacionSelect.disabled = false;
    habitacionSelect.innerHTML = `<option value="" disabled selected>Cargando...</option>`;

    try {
      const token = sessionStorage.getItem('jwt');
      const res = await fetch(`/api/habitaciones/disponibles?desde=${desde}&hasta=${hasta}`, {
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        }
      });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);

      const habitaciones = await res.json(); // List<HabitacionResponseDTO>
      // reconstruir las opciones
      habitacionSelect.innerHTML = habitaciones.length
        ? habitaciones.map(h => `
            <option value="${h.id}">
              ${h.tipo} N°${h.numero} — Piso ${h.piso} — Capacidad ${h.capacidad} — $${h.precioNoche}
            </option>
          `).join("")
        : `<option value="" disabled selected>No hay habitaciones libres</option>`;

    } catch (err) {
      habitacionSelect.innerHTML = `<option value="" disabled selected>Error al cargar</option>`;
      console.error(err);
    }
  }

  // 2) Disparar recarga al cambiar fechas
  fechaIngresoInput.addEventListener('change', cargarHabitacionesDisponibles);
  fechaSalidaInput.addEventListener('change', cargarHabitacionesDisponibles);

  // 3) Resto de tu lógica de agregar huéspedes...
  btnAgregar.addEventListener('click', () => {
    const div = document.createElement('div');
    div.classList.add('grupo-huesped');
    div.innerHTML = `
      <input type="text" placeholder="Nombre" class="inputs nombre" required>
      <input type="text" placeholder="Apellido" class="inputs apellido" required>
      <input type="number" placeholder="DNI" class="inputs dni" required>
    `;
    container.appendChild(div);
  });

  // 4) Manejar el submit usando el select
  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const fechaIngreso = fechaIngresoInput.value;
    const fechaSalida = fechaSalidaInput.value;
    const habitacionId = Number(habitacionSelect.value);

    const huespedes = Array.from(document.querySelectorAll('.grupo-huesped')).map(div => ({
      nombre: div.querySelector('.nombre').value,
      apellido: div.querySelector('.apellido').value,
      dni: div.querySelector('.dni').value
    }));

    if (!huespedes.length) {
      mostrarAlerta("Agregá al menos un huésped.");
      return;
    }

    try {
      const token = sessionStorage.getItem('jwt');
      const dto = { fechaIngreso, fechaSalida, habitacionId, huespedes };

      const res = await fetch('/api/reservas', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(dto)
      });
      if (!res.ok) {
        const errText = await res.text();
        throw new Error(errText);
      }
      mostrarAlerta("Reserva creada exitosamente");
      setTimeout(() => window.location.href = "/clientes/reservas.html", 1000);
    } catch (err) {
      mostrarAlerta("Error al crear la reserva: " + err.message);
    }
  });

  // 5) Inicializar estado (por si ya hubiera fechas precargadas)
  cargarHabitacionesDisponibles();
});
