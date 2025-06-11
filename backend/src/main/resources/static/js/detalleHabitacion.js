    // Variables globales de navegación
    let currentUrls = [];
    let currentIndex = 0;
    
    // Leer ID de la URL
    const params = new URLSearchParams(window.location.search);
    const habitacionId = params.get('id');
    if (!habitacionId) window.location.href = "/home.html";
    
    // Inicialización de detalle
    document.addEventListener("DOMContentLoaded", initDetalle);
    async function initDetalle() {
        // Habitacion
        const resHab = await fetch(`/api/habitaciones/${habitacionId}`);
        if (!resHab.ok) {
            document.getElementById("descripcion").innerText = "No se encontró la habitación.";
            return;
        }
        const h = await resHab.json();
        document.getElementById("titulo").innerText = `Habitación ${h.numero} – ${h.tipo}`;
        document.getElementById("descripcion").innerHTML = `
            <p><strong>Piso:</strong> ${h.piso === 0 ? 'PB' : h.piso}</p>
            <p><strong>Capacidad:</strong> ${h.capacidad} personas</p>
            <p><strong>Camas:</strong> ${h.cantCamas}</p>
            <p><strong>Precio por noche:</strong> $${h.precioNoche}</p>
            <p><strong>Comentario:</strong> ${h.comentario || '— sin comentario —'}</p>`;
    
        // Imagenes
    // ──────────  IMÁGENES  ──────────
    try {
        const resImgs = await fetch(`/api/imagenes/ver/${habitacionId}`);
        if (!resImgs.ok) throw new Error("No se pudieron cargar las imágenes");

        /**  lista = ["42::/api/imagenes/ver/una/42", …]   ó   ["/api/imagenes/ver/una/42", …]  */
        currentUrls = (await resImgs.json()).map(item => {
            return item.includes("::") ? item.split("::")[1] : item;   // siempre devuelvo la URL pura
        });

        const gal = document.getElementById("galeria");
        gal.innerHTML = "";                       // limpio por si recargo
        currentUrls.forEach((url, idx) => {
            const img = document.createElement("img");
            img.src = url;
            img.addEventListener("click", () => mostrarImagenGrande(idx));
            gal.appendChild(img);
        });

        // por si NO había imágenes
        if (currentUrls.length === 0) gal.innerHTML = "<p>No hay imágenes para esta habitación.</p>";

    } catch (e) {
        console.error("Error cargando imágenes:", e);
        document.getElementById("galeria").innerHTML =
            `<p style="color:red;">No se pudieron cargar las imágenes.</p>`;
    }
   }
    
    // Mostrar modal en índice dado
    function mostrarImagenGrande(idx) {
        currentIndex = idx;
        document.getElementById("modalImg").src = currentUrls[currentIndex];
        document.getElementById("modalImagen").style.display = "flex";
    }
    
    // Navegación
    document.getElementById("prevBtn").addEventListener("click", e => {
        e.stopPropagation();
        currentIndex = (currentIndex - 1 + currentUrls.length) % currentUrls.length;
        document.getElementById("modalImg").src = currentUrls[currentIndex];
    });
    document.getElementById("nextBtn").addEventListener("click", e => {
        e.stopPropagation();
        currentIndex = (currentIndex + 1) % currentUrls.length;
        document.getElementById("modalImg").src = currentUrls[currentIndex];
    });
    
    // Cierre modal
    document.getElementById("modalImagen").addEventListener("click", () => {
        document.getElementById("modalImagen").style.display = "none";
    });
    document.addEventListener("keydown", e => {
        if (e.key === "Escape") document.getElementById("modalImagen").style.display = "none";
        if (e.key === "ArrowLeft" && document.getElementById("modalImagen").style.display === "flex") {
            document.getElementById("prevBtn").click();
        }
        if (e.key === "ArrowRight" && document.getElementById("modalImagen").style.display === "flex") {
            document.getElementById("nextBtn").click();
        }
    });
    
    // Botón de reserva
    // ─── Botón de reserva ─────────────────────────────────────────────────────
    document.getElementById("btnReservar").addEventListener("click", async () => {
      // 1. ¿estoy autenticado?
      const token = sessionStorage.getItem("jwt");
      if (!token) {                       // ⇢ login si falta token
        window.location.href = "/login.html";
        return;
      }
      // 2. ¿soy cliente?
      try {
        const me = await fetch("/api/usuarios/current",
          { headers: { Authorization: `Bearer ${token}` } });
        if (!me.ok) throw new Error();
        const usr = await me.json();
        if (usr.role !== "CLIENTE") {
          alert("Sólo los clientes pueden hacer reservas.");
          return;
        }
      } catch {               // cualquier fallo ⇒ forzamos login
        sessionStorage.clear();
        window.location.href = "/login.html";
        return;
      }
      // 3. ok ⇒ vamos a crearReserva.html pasando el id
      window.location.href = `/crearReserva.html?id=${habitacionId}`;
    });
