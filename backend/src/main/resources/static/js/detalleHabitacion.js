// Leer ID de la URL (no requiere token)
const params = new URLSearchParams(window.location.search);
const habitacionId = params.get('id');
if (!habitacionId) {
  window.location.href = "/home.html";
}

// Cargar datos y galerías
async function initDetalle() {
  // 1) Traer la habitación completa (público)
  const resHab = await fetch(`/api/habitaciones/${habitacionId}`);
  if (!resHab.ok) {
    document.getElementById("descripcion").innerText = "No se encontró la habitación.";
    return;
  }
  const h = await resHab.json();

  // Título y descripción
  document.getElementById("titulo").innerText =
    `Habitación ${h.numero} – ${h.tipo}`;
  document.getElementById("descripcion").innerHTML = `
    <p><strong>Piso:</strong> ${h.piso === 0 ? 'PB' : h.piso}</p>
    <p><strong>Capacidad:</strong> ${h.capacidad} personas</p>
    <p><strong>Camas:</strong> ${h.cantCamas}</p>
    <p><strong>Precio por noche:</strong> $${h.precioNoche}</p>
    <p><strong>Comentario:</strong> ${h.comentario || '— sin comentario —'}</p>
  `;

  // 2) Traer URLs de imágenes (público)
  const resImgs = await fetch(`/api/imagenes/ver/${habitacionId}`);
  if (resImgs.ok) {
    const urls = await resImgs.json();
    const gal = document.getElementById("galeria");
    urls.forEach(u => {
      const img = document.createElement("img");
      img.src = u;
      gal.appendChild(img);
    });
  }
}

// Botón de reserva (redirecciona, sin token)
document.getElementById("btnReservar")
  .addEventListener("click", () => {
    const token = sessionStorage.getItem("jwt");
    if (!token) {
      // Si no está autenticado, lo mandamos al login
      window.location.href = "/login.html";
    } else {
      // Si sí está, vamos al formulario de reserva
      window.location.href = `/reservar.html?id=${habitacionId}`;
    }
  });


document.addEventListener("DOMContentLoaded", initDetalle);
