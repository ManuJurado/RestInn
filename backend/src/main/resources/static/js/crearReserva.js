document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById('formReserva');
    const container = document.getElementById('huespedesContainer');
    const btnAgregar = document.getElementById('btnAgregarHuesped');

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

    async function obtenerUsuarioActual() {
        const token = sessionStorage.getItem('jwt');
        const res = await fetch('/api/usuarios/current', {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!res.ok) throw new Error('No autenticado');
        return await res.json();
    }

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const fechaIngreso = document.getElementById('fechaIngreso').value;
        const fechaSalida = document.getElementById('fechaSalida').value;
        const habitacionId = document.getElementById('habitacionId').value;

        const huespedes = Array.from(document.querySelectorAll('.grupo-huesped')).map(div => ({
            nombre: div.querySelector('.nombre').value,
            apellido: div.querySelector('.apellido').value,
            dni: div.querySelector('.dni').value
        }));

        if (huespedes.length === 0) {
            mostrarAlerta("Agregá al menos un huésped.");
            return;
        }

        try {
            const usuario = await obtenerUsuarioActual();

            const dto = {
                fechaIngreso,
                fechaSalida,
                habitacionId: Number(habitacionId),
                usuarioId: null, // será asignado en el backend por seguridad
                huespedes
            };

            const token = sessionStorage.getItem('jwt');
            const res = await fetch('/api/reservas', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(dto)
            });

            if (!res.ok) {
                const errorText = await res.text();
                throw new Error(errorText);
            }

            mostrarAlerta("Reserva creada exitosamente");
            window.location.href = "/clientes/reservas.html";
        } catch (err) {
            mostrarAlerta("Error al crear la reserva: " + err.message);
        }
    });
});
