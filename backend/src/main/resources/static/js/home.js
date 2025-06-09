// home.js

async function obtenerUsuarioActual() {
    const token = sessionStorage.getItem("jwt");
    if (!token) {
        window.location.href = "/login.html";
        return null;
    }

    const res = await fetch("/api/usuarios/current", {
        headers: { "Authorization": `Bearer ${token}` }
    });
    if (res.status === 401) {
        sessionStorage.clear();
        window.location.href = "/login.html";
        return null;
    }
    if (!res.ok) throw new Error("Error inesperado: " + res.status);
    return await res.json();
}

function mostrarNombreUsuario(nombreLogin) {
    const span = document.getElementById("username");
    if (span) span.textContent = nombreLogin;
}

function ajustarMargenTablaHome() {
    const contIzq = document.querySelector('.button-container-left');
    const contDer = document.querySelector('.button-container-right');
    const tabla = document.querySelector('.tabla-container');

    if (contIzq && contDer && tabla) {
        const maxAltura = Math.max(contIzq.offsetHeight, contDer.offsetHeight);
        tabla.style.marginTop = (maxAltura + 20) + 'px';
    }
}

function configurarEventosHome() {
    const btnReservar = document.getElementById("btnReservar");
    const btnDatos = document.getElementById("btnDatos");
    const btnCerrar = document.getElementById("btnCerrar");

    if (btnReservar) {
        btnReservar.addEventListener("click", () => {
            window.location.href = "/clientes/reservas.html";
        });
    }
    if (btnDatos) {
        btnDatos.addEventListener("click", () => {
            window.location.href = "/clientes/mis-datos.html";
        });
    }
    if (btnCerrar) {
        btnCerrar.addEventListener("click", () => {
            sessionStorage.clear();
            window.location.href = "/login.html";
        });
    }
}

async function cargarHabitacionesHome() {
    const cont = document.getElementById("tablaHabitaciones");
    if (!cont) return;

    cont.textContent = "Cargando habitaciones...";
    try {
        const res = await fetch("/api/habitaciones/reservables");
        if (!res.ok) throw new Error("HTTP " + res.status);
        const habs = await res.json();

        if (!habs.length) {
            cont.innerHTML = "<p>No hay habitaciones disponibles.</p>";
            return;
        }

        let html = `<table class="tabla-habitaciones"><thead><tr><th>Habitación</th><th>Imagen</th><th>Acción</th></tr></thead><tbody>`;
        for (const h of habs) {
            let urlImagen = "/img/no-image.png";
            try {
              const imgRes = await fetch(`/api/imagenes/ver/${h.id}`);
              if (imgRes.ok) {
                const urls = await imgRes.json();
                if (urls.length > 0) {
                  if (typeof urls[0] === "string") {
                    const p = urls[0].split("::");
                    urlImagen = p.length > 1 ? p[1] : urls[0];
                  } else if (urls[0].url) {
                    urlImagen = urls[0].url;
                  }
                }
              }
            } catch {}

            html += `<tr><td style="text-align:left; line-height:1.6;"><strong>N°:</strong> ${h.numero}<br /><strong>Tipo:</strong> ${h.tipo}<br /><strong>Piso:</strong> ${h.piso === 0 ? 'PB' : h.piso}<br /><strong>Capacidad:</strong> ${h.capacidad} personas<br /><strong>Precio:</strong> $${h.precioNoche} / noche</td>` +
                `<td><img src="${urlImagen}" alt="hab ${h.numero}" style="width:240px; height:160px; object-fit:cover; border-radius:8px;" /></td>` +
                `<td style="text-align:center; vertical-align:middle;"><button class="btn-detalle" onclick="window.location.href='detalleHabitacion.html?id=${h.id}'">Ver detalle</button></td></tr>`;
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

    switch (usuario.role) {
        case "CLIENTE":
            mostrarNombreUsuario(usuario.nombreLogin);
            configurarEventosHome();
            ajustarMargenTablaHome();
            await cargarHabitacionesHome();
            break;

        case "LIMPIEZA":
            window.location.href = "/empleados/menuLimpieza.html";
            break;

        case "RECEPCIONISTA":
            window.location.href = "/empleados/menuRecepcionista.html";
            break;

        case "CONSERJE":
            window.location.href = "/empleados/menuConserje.html";
            break;

        case "ADMINISTRADOR":
            window.location.href = "/admin/menuAdministrador.html";
            break;

        default:
            alert("Rol de usuario desconocido");
            sessionStorage.clear();
            window.location.href = "/login.html";
    }
}

window.addEventListener('load', initHome);
