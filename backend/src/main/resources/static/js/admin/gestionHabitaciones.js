// ===================== VARIABLES =====================
let habitacionesTodas = [];
let habitacionParaImagen = null;

// ===================== CARGAR Y FILTRAR =====================
async function cargarHabitacionesTodas() {
  const cont = document.getElementById("tablaHabitaciones");
  cont.textContent = "Cargando habitaciones…";

  try {
    const res = await fetch("/api/habitaciones/admin/todas", {
      headers: { "Authorization": `Bearer ${sessionStorage.getItem("jwt")}` }
    });
    if (!res.ok) throw new Error("HTTP " + res.status);

    habitacionesTodas = await res.json();

    // ─── ORDENAR AQUÍ MISMO ────────────────────────────────────────────────
    habitacionesTodas.sort((a, b) => Number(a.numero) - Number(b.numero));

    if (!habitacionesTodas.length) {
      cont.innerHTML = "<p>No hay habitaciones registradas.</p>";
      return;
    }

    aplicarFiltros();

  } catch (err) {
    cont.innerHTML = `<p class="error">Error cargando habitaciones: ${err.message}</p>`;
    console.error(err);
  }
}


async function aplicarFiltros() {
  const pisoFilter = document.getElementById("filtroPiso").value.trim();
  const numeroFilter = document.getElementById("filtroNumero").value.trim();
  const estadoFilter = document.getElementById("filtroEstado").value;

  const cont = document.getElementById("tablaHabitaciones");

  let filtradas = habitacionesTodas.filter(h => {
    let pasa = true;
    if (pisoFilter !== "") pasa = pasa && (h.piso === Number(pisoFilter));
    if (numeroFilter !== "") pasa = pasa && (h.numero === Number(numeroFilter));
    if (estadoFilter !== "") pasa = pasa && (h.estado === estadoFilter);
    return pasa;
  });

  if (filtradas.length === 0) {
    cont.innerHTML = "<p>No se encontraron habitaciones con esos filtros.</p>";
    return;
  }

  cont.textContent = "Cargando imágenes...";

const imagenes = await Promise.all(
  filtradas.map(async h => {
    try {
      const res = await fetch(`/api/imagenes/ver/${h.id}`);
      if (!res.ok) return "/img/no-image.png";

      const urls = await res.json();
      if (!urls.length) return "/img/no-image.png";

      /* ────── NUEVO BLOQUE ────── */
      let primera = urls[0];

      // 1) Si viene como string "id::url"
      if (typeof primera === "string") {
        const partes = primera.split("::");         // ["3", "/api/imagenes/ver/una/3"]
        primera = partes.length > 1 ? partes[1]     // nos quedamos con la URL real
                                     : primera;     // o la cadena original si no hay '::'
        return primera;
      }

      // 2) Si viene como objeto { id:…, url:… }
      if (primera.url) return primera.url;

      return "/img/no-image.png";
      /* ────── FIN BLOQUE ────── */
    } catch {
      return "/img/no-image.png";
    }
  })
);


  let html = `<table class="tabla-habitaciones"><thead><tr>
    <th>Habitación</th><th>Imagen</th><th>Acciones</th>
  </tr></thead><tbody>`;

  filtradas.forEach((h, i) => {
    const urlImagen = imagenes[i];
    const claseFila = h.activo ? "" : "fila-inactiva";

    html += `<tr class="${claseFila}">
      <td style="text-align:left; line-height:1.6;">
        <strong>N°:</strong> ${h.numero}<br />
        <strong>Tipo:</strong> ${h.tipo}<br />
        <strong>Piso:</strong> ${h.piso === 0 ? 'PB' : h.piso}<br />
        <strong>Capacidad:</strong> ${h.capacidad} personas<br />
        <strong>Estado:</strong> ${h.estado}<br />
        <strong>Activo:</strong> ${h.activo ? 'Sí' : 'No'}
      </td>
      <td>
        <img src="${urlImagen}" alt="hab ${h.numero}" style="width:240px; height:160px; object-fit:cover; border-radius:8px;" />
      </td>
      <td style="text-align:center; vertical-align:middle;">
        <button onclick="irModificar(${h.id})">Editar</button><br />
        ${h.activo
          ? `<button onclick="borrarLogico(${h.id})">Borrar</button><br />
             <button onclick="abrirSelectorImagen(${h.id})">Agregar imagen</button>`
          : `<button onclick="reactivar(${h.id})">Reactivar</button>`}
      </td>
    </tr>`;
  });

  html += `</tbody></table>`;
  cont.innerHTML = html;
}

// ===================== FUNCIONES PARA IMÁGENES =====================
function abrirSelectorImagen(idHabitacion) {
  habitacionParaImagen = idHabitacion;
  const inputFile = document.getElementById("inputFileImagen");
  inputFile.value = ""; // Reset para poder subir el mismo archivo varias veces
  inputFile.click();
}

document.getElementById("inputFileImagen").addEventListener("change", async function () {
  if (!this.files.length) return;
  const archivo = this.files[0];

  if (!habitacionParaImagen) {
    alert("No se pudo determinar la habitación para la imagen.");
    return;
  }

  try {
    const token = sessionStorage.getItem("jwt");
    const formData = new FormData();
    formData.append("archivo", archivo);

    const res = await fetch(`/api/imagenes/${habitacionParaImagen}`, {
      method: "POST",
      headers: {
        "Authorization": `Bearer ${token}`
      },
      body: formData
    });

    if (!res.ok) {
      const msg = await res.text();
      alert(`Error al subir imagen: ${msg}`);
      return;
    }

    alert("Imagen subida correctamente.");
    await cargarHabitacionesTodas();

  } catch (err) {
    alert(`Error inesperado: ${err.message}`);
  }
});

// ===================== OTRAS FUNCIONES =====================
async function reactivar(id) {
  if (!confirm("¿Marcar la habitación como ACTIVA?")) return;
  const token = sessionStorage.getItem("jwt");
  const res = await fetch(`/api/habitaciones/${id}/activar`, {
    method: "PUT",
    headers: { "Authorization": `Bearer ${token}` }
  });
  if (!res.ok) return mostrarAlerta("No se pudo reactivar la habitación");
  await cargarHabitacionesTodas();
}

function irModificar(id) {
  window.location.href = `modificarHabitacion.html?id=${id}`;
}

async function borrarLogico(id) {
  if (!confirm("¿Estás seguro de borrar (desactivar) esta habitación?")) return;

  const token = sessionStorage.getItem("jwt");
  try {
    const res = await fetch(`/api/habitaciones/${id}/borrar`, {
      method: "PUT",
      headers: {
        "Authorization": `Bearer ${token}`
      }
    });

    if (!res.ok) {
      const mensaje = await res.text();
      mostrarAlerta(`No se pudo borrar: ${mensaje}`);
      return;
    }

    mostrarAlerta("Habitación borrada (desactivada) correctamente.");
    await cargarHabitacionesTodas();

  } catch (err) {
    mostrarAlerta(`Error al borrar la habitación: ${err.message}`);
  }
}

document.getElementById("btnNuevaHab").addEventListener("click", () => {
  window.location.href = "crearHabitacion.html";
});

function limpiarFiltros() {
  document.getElementById("filtroPiso").value = "";
  document.getElementById("filtroNumero").value = "";
  document.getElementById("filtroEstado").value = "";
  aplicarFiltros();
}

document.addEventListener("DOMContentLoaded", () => {
  cargarHabitacionesTodas();

  document.getElementById("filtroPiso").addEventListener("input", aplicarFiltros);
  document.getElementById("filtroNumero").addEventListener("input", aplicarFiltros);
  document.getElementById("filtroEstado").addEventListener("change", aplicarFiltros);

  document.getElementById("btnLimpiarFiltros").addEventListener("click", limpiarFiltros);

});