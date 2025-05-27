// home.js

// Función para obtener datos del usuario actual
async function fetchUsuarioActual() {
    try {
        const res = await fetch("/api/usuarios/current", {
            credentials: "include"
        });

        if (res.status === 401) {
            // No logueado, no hago nada (o redirijo)
            return null;
        }

        if (!res.ok) {
            throw new Error("Error inesperado: " + res.status);
        }

        const data = await res.json();
        return data;

    } catch (error) {
        console.error("Error al obtener usuario:", error);
        if (window.location.pathname !== "/login.html") {
            window.location.href = "/login.html";
        }
        return null;
    }
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

    if (btnReservas) {
        btnReservas.addEventListener("click", () => {
            window.location.href = "/clientes/reservas.html";
        });
    }

    if (btnHabitaciones) {
        btnHabitaciones.addEventListener("click", () => {
            window.location.href = "/habitaciones.html";
        });
    }

    if (btnDatos) {
        btnDatos.addEventListener("click", () => {
            window.location.href = "/cliente/mis-datos.html";
        });
    }

    if (btnCerrar) {
        btnCerrar.addEventListener("click", async () => {
            try {
                const res = await fetch("/logout", {
                    method: "POST",
                    credentials: "include"
                });
                if (res.ok) {
                    window.location.href = "/login.html";
                } else {
                    alert("Error al cerrar sesión");
                }
            } catch {
                alert("Error al cerrar sesión");
            }
        });
    }
}

// Función principal para inicializar el home
async function initHome() {
    const usuario = await fetchUsuarioActual();
    if (!usuario) {
        // No logueado, redirigir a login
        window.location.href = "/login.html";
        return;
    }
    mostrarNombreUsuario(usuario.nombreLogin);
    configurarEventos();
}

// Ejecutar al cargar el script
document.addEventListener("DOMContentLoaded", initHome);
