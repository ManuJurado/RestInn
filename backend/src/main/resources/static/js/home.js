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
            window.location.href = "/login.html";
        });
}

async function cargarHabitaciones() {
    const cont = document.getElementById("tablaHabitaciones");
    cont.textContent = "Cargando habitaciones...";
    const token = sessionStorage.getItem("jwt");

    try {
        const res = await fetch("/api/habitaciones/reservables", {
            headers: { "Authorization": `Bearer ${token}` }
        });
        if (!res.ok) throw new Error(res.status);
        const habs = await res.json();

        if (!habs.length) {
            cont.innerHTML = "<p>No hay habitaciones disponibles.</p>";
            return;
        }

        let html = `<table class="tabla-habitaciones">
          <thead><tr>
            <th>N°</th><th>Tipo</th><th>Piso</th><th>Capacidad</th><th>Precio Noche</th><th>Imagen</th><th>Acciones</th>
          </tr></thead><tbody>`;

        for (const h of habs) {
            // Cargar primera imagen (o fallback)
            let urlImagen = "/img/no-image.png";
            try {
                const imgRes = await fetch(`/api/imagenes/ver/${h.id}`, {
                    headers: { "Authorization": `Bearer ${token}` }
                });
                if (imgRes.ok) {
                    const urls = await imgRes.json();
                    if (urls.length > 0) urlImagen = urls[0];
                }
            } catch { /* ignorar fallos de imagen */ }

            html += `<tr>
              <td>${h.numero}</td>
              <td>${h.tipo}</td>
              <td>${h.piso === 0 ? 'PB' : h.piso}</td>
              <td>${h.capacidad}</td>
              <td>$${h.precioNoche}</td>
              <td>
                <img
                  src="${urlImagen}"
                  alt="hab ${h.numero}"
                  style="width:260px; height:160px; object-fit:cover; border-radius:8px;" />
              </td>
              <td>
                <button
                  class="btn-detalle"
                  onclick="window.location.href='detalleHabitacion.html?id=${h.id}'">
                  Ver detalle
                </button>
              </td>
            </tr>`;
        }

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
