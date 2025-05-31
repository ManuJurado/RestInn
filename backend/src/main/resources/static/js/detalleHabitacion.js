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
    try {
            const resImgs = await fetch(`/api/imagenes/ver/${habitacionId}`);
            if (resImgs.ok) {
                currentUrls = await resImgs.json();
                const gal = document.getElementById("galeria");
                currentUrls.forEach((u, idx) => {
                    const img = document.createElement("img");
                    img.src = u;
                    img.addEventListener("click", () => mostrarImagenGrande(idx));
                    gal.appendChild(img);
                });
            }
        } catch (e) {
            console.error(e);
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
    document.getElementById("btnReservar").addEventListener("click", () => {
        const token = sessionStorage.getItem("jwt");
        window.location.href = token ? `/reservar.html?id=${habitacionId}` : "/login.html";
    });