async function obtenerUsuarioActual() {
    const token = sessionStorage.getItem("jwt");
    if (!token) {
        window.location.href = "/login";
        return null;
    }

    const res = await fetch("/api/usuarios/current", {
        headers: { "Authorization": `Bearer ${token}` }
    });

    if (res.status === 401) {
        window.location.href = "/login";
        return null;
    }
    if (!res.ok) throw new Error("Error inesperado: " + res.status);

    return await res.json();
}

function mostrarNombreUsuario(nombreLogin) {
    // Si querés mostrar en algún lado, podrías añadir un <span id="username"></span> en el HTML
    console.log("Usuario:", nombreLogin);
}

function configurarEventos() {
    document.getElementById("btnReservas")
        .addEventListener("click", () => window.location.href = "/clientes/reservas.html");
    document.getElementById("btnDatos")
        .addEventListener("click", () => window.location.href = "/cliente/mis-datos.html");
    document.getElementById("btnCerrar")
        .addEventListener("click", () => {
            sessionStorage.removeItem("jwt");
            window.location.href = "/login";
        });
}

async function cargarHabitaciones() {
    const cont = document.getElementById("tablaHabitaciones");
    cont.textContent = "Cargando habitaciones...";
    const token = sessionStorage.getItem("jwt");

    try {
        const res = await fetch("/api/habitaciones/disponibles", {
            headers: { "Authorization": `Bearer ${token}` }
        });
        if (!res.ok) throw new Error(res.status);
        const habs = await res.json();

        if (!habs.length) {
            cont.innerHTML = "<p>No hay habitaciones disponibles.</p>";
            return;
        }

        // Construir tabla
        let html = `<table class="tabla-habitaciones">
          <thead><tr>
            <th>N°</th><th>Tipo</th><th>Piso</th><th>Capacidad</th><th>Precio Noche</th>
          </tr></thead><tbody>`;

        habs.forEach(h => {
            html += `<tr>
              <td>${h.numero}</td>
              <td>${h.tipo}</td>
              <td>${h.piso}</td>
              <td>${h.capacidad}</td>
              <td>${h.precioNoche}</td>
            </tr>`;
        });

        html += `</tbody></table>`;
        cont.innerHTML = html;

    } catch (err) {
        cont.innerHTML = `<p class="error">Error cargando habitaciones: ${err.message}</p>`;
        console.error(err);
    }
}

async function initHome() {
    const usuario = await obtenerUsuarioActual();
    if (!usuario) return;

    mostrarNombreUsuario(usuario.nombreLogin);
    configurarEventos();
    cargarHabitaciones();   // <-- cargamos la tabla
}

document.addEventListener("DOMContentLoaded", initHome);
