 // Leer ID de la URL
    const params = new URLSearchParams(window.location.search);
    const habitacionId = params.get('id');
    const token = sessionStorage.getItem("jwt");
    if (!habitacionId || !token) {
      window.location.href = "/home.html";
    }

    // Cargar datos y galerías
    async function initDetalle() {
      // 1) Traer la habitación completa
      const resHab = await fetch(`/api/habitaciones/${habitacionId}`, {
        headers: { "Authorization": `Bearer ${token}` }
      });
      if (!resHab.ok) {
        document.getElementById("detalle").innerText = "No se encontró la habitación.";
        return;
      }
      const h = await resHab.json();

      // Título y descripción
      document.getElementById("titulo").innerText =
        `Habitación ${h.numero} – ${h.tipo}`;
      const desc = document.getElementById("descripcion");
      desc.innerHTML = `
        <p><strong>Piso:</strong> ${h.piso === 0 ? 'PB' : h.piso}</p>
        <p><strong>Capacidad:</strong> ${h.capacidad} personas</p>
        <p><strong>Camas:</strong> ${h.cantCamas}</p>
        <p><strong>Precio por noche:</strong> $${h.precioNoche}</p>
        <p><strong>Comentario:</strong> ${h.comentario || '— sin comentario —'}</p>
      `;

      // 2) Traer URLs de imágenes
      const resImgs = await fetch(`/api/imagenes/ver/${habitacionId}`, {
        headers: { "Authorization": `Bearer ${token}` }
      });
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

    document.getElementById("btnReservar")
      .addEventListener("click", () => {
        window.location.href = `/reservar.html?id=${habitacionId}`;
      });

    document.addEventListener("DOMContentLoaded", initDetalle);