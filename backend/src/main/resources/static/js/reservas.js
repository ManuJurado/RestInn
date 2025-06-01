document.addEventListener("DOMContentLoaded", async () => {
    const contenedor = document.getElementById('contenedorReservas');
    const btnFiltrar = document.getElementById('btnFiltrar');
    const btnLimpiar = document.getElementById('btnLimpiar');
    const inputInicio = document.getElementById('fechaInicio');
    const inputFin = document.getElementById('fechaFin');

    let userName;

    // Reutilizamos obtenerUsuarioActual definida en home.js (o repetirla igual acá)
    async function obtenerUsuarioActual() {
        const token = sessionStorage.getItem('jwt');
        if (!token) throw new Error('No hay token guardado');

        const res = await fetch('/api/usuarios/current', {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!res.ok) throw new Error('No se pudo obtener el usuario autenticado');
        return await res.json();
    }

    try {
        const user = await obtenerUsuarioActual();
        userName = user.nombreLogin;
    } catch (err) {
        mostrarAlerta('No se pudo obtener el usuario autenticado');
        window.location.href = "/login.html";
        return;
    }

    async function fetchReservas(inicio, fin) {
        contenedor.innerHTML = 'Cargando...';
        let url = `/api/reservas/mis-reservas`;
        if (inicio && fin) url += `/${inicio}/${fin}`;

        try {
            const token = sessionStorage.getItem('jwt');
            const res = await fetch(url, {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (!res.ok) {
                const txt = await res.text();
                throw new Error(`${res.status} – ${txt}`);
            }
            const reservas = await res.json();
            return reservas;
        } catch (err) {
            contenedor.innerHTML = `<p class="erFror">Error al cargar reservas: ${err.message}</p>`;
            return null;
        }
    }

    //Comportamiento del button para crear reservas en cliente
    document.getElementById('btnCrearReserva').addEventListener('click', () => {
        window.location.href = '/clientes/crearReserva.html';
    });

    // Ejemplo simple para renderizar las reservas en HTML:
    function renderTabla(reservas) {
        if (!reservas || reservas.length === 0) {
            contenedor.innerHTML = '<p>No hay reservas para mostrar.</p>';
            return;
        }

        const table = document.createElement('table');
        table.classList.add('tabla-reservas');

        // Crear encabezado
        const thead = document.createElement('thead');
        const trHead = document.createElement('tr');
        const headers = ['ID', 'Ingreso', 'Salida', 'Reserva', 'Estado', 'Habitación', 'Huéspedes'];
        headers.forEach(txt => {
            const th = document.createElement('th');
            th.textContent = txt;
            trHead.appendChild(th);
        });
        thead.appendChild(trHead);
        table.appendChild(thead);

        // Crear cuerpo de tabla
        const tbody = document.createElement('tbody');
        reservas.forEach(r => {
            const tr = document.createElement('tr');

            const tdId = document.createElement('td');
            tdId.textContent = r.id;
            tr.appendChild(tdId);

            const tdIngreso = document.createElement('td');
            tdIngreso.textContent = r.fechaIngreso;
            tr.appendChild(tdIngreso);

            const tdSalida = document.createElement('td');
            tdSalida.textContent = r.fechaSalida;
            tr.appendChild(tdSalida);

            const tdReserva = document.createElement('td');
            tdReserva.textContent = r.fechaReserva;
            tr.appendChild(tdReserva);

            const tdEstado = document.createElement('td');
            tdEstado.textContent = r.estado;
            tr.appendChild(tdEstado);

            const tdHabitacion = document.createElement('td');
            tdHabitacion.textContent = r.habitacionNumero;
            tr.appendChild(tdHabitacion);

            const tdHuespedes = document.createElement('td');
            tdHuespedes.textContent = r.huespedes.map(h => `${h.nombre} ${h.apellido}, ${h.dni}`).join(', ');
            tr.appendChild(tdHuespedes);

            tbody.appendChild(tr);
        });

        table.appendChild(tbody);
        contenedor.innerHTML = ''; // Limpiar el contenedor
        contenedor.appendChild(table);
    }

    // Carga inicial de reservas
    const inicial = await fetchReservas();
    renderTabla(inicial);

    btnFiltrar.addEventListener('click', async () => {
        const inicio = inputInicio.value;
        const fin = inputFin.value;
        if (!inicio || !fin) {
            mostrarAlerta('Seleccioná ambas fechas para filtrar.');
            return;
        }
        if (inicio > fin) {
            mostrarAlerta('La fecha "Desde" no puede ser posterior a "Hasta".');
            return;
        }
        const filtradas = await fetchReservas(inicio, fin);
        renderTabla(filtradas);
    });

    btnLimpiar.addEventListener('click', async () => {
        inputInicio.value = '';
        inputFin.value = '';
        const todas = await fetchReservas();
        renderTabla(todas);
    });
});