// index.js
// Carga pública de habitaciones, sin autenticación
async function cargarHabitacionesPublicas() {
    const cont = document.getElementById("tablaHabitaciones");
    if (!cont) return;

    cont.textContent = "Cargando habitaciones...";

    try {
        const res = await fetch("/api/habitaciones/reservables"); // endpoint público
        if (!res.ok) throw new Error("HTTP " + res.status);

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


            html += `<tr>
                <td style="text-align:left; line-height:1.6;">
                    <strong>N°:</strong> ${h.numero}<br />
                    <strong>Tipo:</strong> ${h.tipo}<br />
                    <strong>Piso:</strong> ${h.piso === 0 ? 'PB' : h.piso}<br />
                    <strong>Capacidad:</strong> ${h.capacidad} personas<br />
                    <strong>Precio:</strong> $${h.precioNoche} / noche
                </td>
                <td>
                    <img src="${urlImagen}" alt="hab ${h.numero}"
                         style="width:240px; height:160px; object-fit:cover; border-radius:8px;" />
                </td>
                <td style="text-align:center; vertical-align:middle;">
                    <button class="btn-detalle"
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

// Inicialización para index.html
document.addEventListener('DOMContentLoaded', () => {
    cargarHabitacionesPublicas();
    configurarEventos();
});
