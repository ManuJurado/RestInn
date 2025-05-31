// 1) Solo para la parte de usuario (login/logout/reservas)
async function obtenerUsuarioActual() {
    const token = sessionStorage.getItem("jwt");
    if (!token) {
        // Si no hay token, simplemente no mostramos los botones privados
        return null;
    }

    const res = await fetch("/api/usuarios/current", {
        headers: { "Authorization": `Bearer ${token}` }
    });
    if (res.status === 401) {
        sessionStorage.removeItem("jwt");
        return null;
    }
    if (!res.ok) throw new Error("Error inesperado: " + res.status);
    return await res.json();
}

function mostrarNombreUsuario(nombreLogin) {
    const span = document.getElementById("username");
    if (span) span.textContent = nombreLogin;
}

  function ajustarMargenTabla() {
    const contIzq = document.querySelector('.button-container-left');
    const contDer = document.querySelector('.button-container-right');
    const tabla = document.querySelector('.tabla-container');

    if (contIzq && contDer && tabla) {
      const maxAltura = Math.max(contIzq.offsetHeight, contDer.offsetHeight);
      tabla.style.marginTop = (maxAltura + 20) + 'px';
    }
  }

  window.addEventListener('load', ajustarMargenTabla);
  window.addEventListener('resize', ajustarMargenTabla);

function configurarEventos() {
    // Solo si existen esos botones en el DOM
    const btnR = document.getElementById("btnReservar");
    const btnD = document.getElementById("btnDatos");
    const btnC = document.getElementById("btnCerrar");

    if (btnR) btnR.addEventListener("click", () => window.location.href = "/clientes/reservas.html");
    if (btnD) btnD.addEventListener("click", () => window.location.href = "/clientes/mis-datos.html");
    if (btnC) btnC.addEventListener("click", () => {
        sessionStorage.removeItem("jwt");
        window.location.href = "/login.html";
    });
}

// 2) **Carga pública** de habitaciones, sin auth
async function cargarHabitaciones() {
    const cont = document.getElementById("tablaHabitaciones");
    cont.textContent = "Cargando habitaciones...";
    try {
        const res = await fetch("/api/habitaciones/reservables"); // ← sin headers
        if (!res.ok) throw new Error(res.status);
        const habs = await res.json();

        if (!habs.length) {
            cont.innerHTML = "<p>No hay habitaciones disponibles.</p>";
            return;
        }

        let html = `<table class="tabla-habitaciones">
          <thead><tr>
            <th>Habitación</th>
            <th>Imagen</th>
            <th>Acción</th>
          </tr></thead><tbody>`;

        for (const h of habs) {
            // Cargar primera imagen (o fallback)
            let urlImagen = "/img/no-image.png";
            try {
                const imgRes = await fetch(`/api/imagenes/ver/${h.id}`);
                if (imgRes.ok) {
                    const urls = await imgRes.json();
                    if (urls.length > 0) urlImagen = urls[0];
                }
            } catch {}

            html += `<tr>
              <td style="text-align:left; line-height:1.6;">
                <strong>N°:</strong> ${h.numero}<br />
                <strong>Tipo:</strong> ${h.tipo}<br />
                <strong>Piso:</strong> ${h.piso === 0 ? 'PB' : h.piso}<br />
                <strong>Capacidad:</strong> ${h.capacidad} personas<br />
                <strong>Precio:</strong> $${h.precioNoche} / noche
              </td>
              <td>
                <img
                  src="${urlImagen}"
                  alt="hab ${h.numero}"
                  style="width:240px; height:160px; object-fit:cover; border-radius:8px;" />
              </td>
              <td style="text-align:center; vertical-align:middle;">
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

// 3) Inicialización: primero tabla, luego (si existe) login
async function initHome() {
    await cargarHabitaciones();     // ← carga **siempre** la tabla

    const usuario = await obtenerUsuarioActual();
    if (usuario) {
        mostrarNombreUsuario(usuario.nombreLogin);
        configurarEventos();
    }
}

document.addEventListener("DOMContentLoaded", initHome);
