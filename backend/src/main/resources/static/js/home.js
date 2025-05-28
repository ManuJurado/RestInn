async function obtenerUsuarioActual() {
    const token = localStorage.getItem("jwt");
    if (!token) {
        window.location.href = "/login.html";
        return null;
    }

    const res = await fetch("/api/usuarios/current", {
        headers: { "Authorization": `Bearer ${token}` }
    });

    if (res.status === 401) {
        window.location.href = "/login.html";
        return null;
    }
    if (!res.ok) throw new Error("Error inesperado: " + res.status);

    return await res.json();
}

// Función para actualizar UI con datos del usuario
function mostrarNombreUsuario(nombreLogin) {
    const spanUsuario = document.getElementById("username");
    if (spanUsuario) {
        spanUsuario.textContent = nombreLogin || "...";
    }
}

// Función para configurar eventos de botones
function configurarEventos() {
    const btnReservas = document.getElementById("btnReservas");
    const btnHabitaciones = document.getElementById("btnHabitaciones");
    const btnDatos = document.getElementById("btnDatos");
    const btnCerrar = document.getElementById("btnCerrar");

    if (btnReservas) btnReservas.addEventListener("click", () => window.location.href = "/clientes/reservas.html");
    if (btnHabitaciones) btnHabitaciones.addEventListener("click", () => window.location.href = "/habitaciones.html");
    if (btnDatos) btnDatos.addEventListener("click", () => window.location.href = "/cliente/mis-datos.html");

    if (btnCerrar) {
        btnCerrar.addEventListener("click", () => {
            // Para "cerrar sesión" con JWT, simplemente borramos el token local
            localStorage.removeItem("jwt");
            window.location.href = "/login.html";
        });
    }
}

// Función principal para inicializar home
async function initHome() {
    const usuario = await obtenerUsuarioActual();
    if (!usuario) return; // Ya redirige a login en obtenerUsuarioActual

    mostrarNombreUsuario(usuario.nombreLogin);
    configurarEventos();
}

document.addEventListener("DOMContentLoaded", initHome);