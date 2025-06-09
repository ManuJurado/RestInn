/* ────────────────────────────────────────────────────────────────
   crearReserva.js – versión completa con soporte de “reserva rápida”
   (llega con ?id=<habitacion> desde detalleHabitacion)
   ──────────────────────────────────────────────────────────────── */

document.addEventListener("DOMContentLoaded", () => {

  /* ╭───────────────────────────────────────────────────────────╮
     │  1.  LECTURA DE PARÁMETRO                               │
     ╰───────────────────────────────────────────────────────────╯ */
  const urlParams    = new URLSearchParams(location.search);
  const idSolicitado = urlParams.get("id");     // puede ser null

  /* ╭───────────────────────────────────────────────────────────╮
     │  2.  REFERENCIAS A ELEMENTOS                            │
     ╰───────────────────────────────────────────────────────────╯ */
  const form               = document.getElementById('formReserva');
  const containerHuespedes = document.getElementById('huespedesContainer');
  const fechaIngresoInput  = document.getElementById('fechaIngreso');
  const fechaSalidaInput   = document.getElementById('fechaSalida');
  const cantidadInput      = document.getElementById('cantidadHuespedes');
  const habitacionSelect   = document.getElementById('habitacionSelect');

  /* ╭───────────────────────────────────────────────────────────╮
     │ 3.  FUNCIÓN PRINCIPAL PARA CARGAR HABITACIONES DISPONIB. │
     ╰───────────────────────────────────────────────────────────╯ */
  async function cargarHabitacionesDisponibles() {

    const desde   = fechaIngresoInput.value;
    const hasta   = fechaSalidaInput.value;
    let   cant    = Number(cantidadInput.value);

    /* sanitizar cantidad (1-5) */
    if (isNaN(cant) || cant < 1) cant = 1;
    if (cant > 5) cant = 5;

    /* validar fechas — si algo no está bien, deshabilito select y retorno */
    if (!desde || !hasta || desde > hasta) {
      habitacionSelect.innerHTML =
        `<option value="" disabled selected>— Seleccioná fechas y cantidad válidas —</option>`;
      habitacionSelect.disabled = true;
      containerHuespedes.innerHTML = '';
      return;
    }

    habitacionSelect.disabled = false;
    habitacionSelect.innerHTML = `<option disabled selected>Cargando…</option>`;

    try {
      const token = sessionStorage.getItem('jwt');
      const res   = await fetch(
        `/api/habitaciones/disponibles?desde=${desde}&hasta=${hasta}`,
        { headers: { 'Authorization': `Bearer ${token}` } }
      );
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const habitaciones = await res.json();

      /* Filtrar por capacidad requerida */
      const porCapacidad = habitaciones.filter(h => h.capacidad >= cant);

      /* Si venimos desde detalle ⇒ quedarnos solo con esa habitación */
      const listaFinal = idSolicitado
        ? porCapacidad.filter(h => h.id == idSolicitado)
        : porCapacidad;

      if (listaFinal.length) {
        habitacionSelect.innerHTML = listaFinal.map(h => `
          <option value="${h.id}" ${idSolicitado ? 'selected' : ''}>
            ${h.tipo} N°${h.numero} — Piso ${h.piso} — Cap. ${h.capacidad} — $${h.precioNoche}
          </option>`).join('');
        if (idSolicitado) habitacionSelect.disabled = true; /* bloqueo para reserva directa */
      } else {
        habitacionSelect.innerHTML =
          `<option disabled selected>${idSolicitado
              ? 'Esta habitación no está disponible en ese rango'
              : `No hay habitaciones para ${cant} huésped(es)`}</option>`;
        habitacionSelect.disabled = true;
      }

      /* generar campos huésped */
      generarCamposHuespedes(cant);

    } catch (err) {
      console.error(err);
      habitacionSelect.innerHTML =
        `<option disabled selected>Error al cargar</option>`;
    }
  }

  /* ╭───────────────────────────────────────────────────────────╮
     │ 4.  GENERAR INPUTS DE HUÉSPED SEGÚN CANTIDAD             │
     ╰───────────────────────────────────────────────────────────╯ */
  function generarCamposHuespedes(cant) {
    containerHuespedes.innerHTML = '';
    for (let i = 1; i <= cant; i++) {
      const div = document.createElement('div');
      div.className = 'grupo-huesped';
      div.innerHTML = `
        <label>Huésped ${i}:
          <input type="text" placeholder="Nombre"   class="inputs nombre"    required>
          <input type="text" placeholder="Apellido" class="inputs apellido" REQUIRED>
          <input type="number" placeholder="DNI"    class="inputs dni"       required>
        </label>`;
      containerHuespedes.appendChild(div);
    }
  }

  /* ╭───────────────────────────────────────────────────────────╮
     │ 5.  LISTENERS (fechas, cantidad)                         │
     ╰───────────────────────────────────────────────────────────╯ */
  fechaIngresoInput.addEventListener('change', cargarHabitacionesDisponibles);
  fechaSalidaInput .addEventListener('change', cargarHabitacionesDisponibles);
  cantidadInput    .addEventListener('change', () => {
    let val = Number(cantidadInput.value);
    if (val > 5)  { mostrarAlerta('Máximo 5 huéspedes'); val = 5; }
    if (val < 1)  val = 1;
    cantidadInput.value = val;
    cargarHabitacionesDisponibles();
  });

  /* ╭───────────────────────────────────────────────────────────╮
     │ 6.  ENVÍO DEL FORMULARIO DE RESERVA                      │
     ╰───────────────────────────────────────────────────────────╯ */
  form.addEventListener('submit', async e => {
    e.preventDefault();

    const desde    = fechaIngresoInput.value;
    const hasta    = fechaSalidaInput.value;
    const cant     = Number(cantidadInput.value);
    const habitId  = Number(habitacionSelect.value);

    if (!desde || !hasta || !habitId) {
      mostrarAlerta('Completá todos los campos correctamente.');
      return;
    }

    const huespedes = Array.from(containerHuespedes.children).map(div => ({
      nombre  : div.querySelector('.nombre')  .value.trim(),
      apellido: div.querySelector('.apellido').value.trim(),
      dni     : div.querySelector('.dni')     .value.trim()
    }));

    if (huespedes.length !== cant) {
      mostrarAlerta(`Se esperaban ${cant} huésped(es), pero hay ${huespedes.length}.`);
      return;
    }

    try {
      const token = sessionStorage.getItem('jwt');
      const dto   = { fechaIngreso: desde, fechaSalida: hasta,
                      habitacionId: habitId, huespedes };

      const res = await fetch('/api/reservas', {
        method : 'POST',
        headers: { 'Content-Type':'application/json',
                   'Authorization': `Bearer ${token}` },
        body   : JSON.stringify(dto)
      });

      if (!res.ok) throw new Error(await res.text());

      mostrarAlerta('Reserva creada exitosamente');
      setTimeout(() => window.location.href = '/clientes/reservas.html', 1200);

    } catch (err) {
      mostrarAlerta('Error al crear la reserva: ' + err.message);
    }
  });

  /* ╭───────────────────────────────────────────────────────────╮
     │ 7.  INICIALIZACIÓN                                       │
     ╰───────────────────────────────────────────────────────────╯ */
  cargarHabitacionesDisponibles();            // primera carga
});
