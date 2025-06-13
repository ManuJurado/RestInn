// 1) Obtener datos del usuario autenticado (solo si está logueado)
//    Igual que en home.js, usamos JWT en sessionStorage para validar sesión.
async function obtenerUsuarioActual() {
    const token = sessionStorage.getItem("jwt");
    if (!token) {
        // Si no hay token, redirigimos al login
        window.location.href = "/login.html";
        return null;
    }

    const res = await fetch("/api/usuarios/current", {
        headers: { "Authorization": `Bearer ${token}` }
    });
    if (res.status === 401) {
        sessionStorage.removeItem("jwt");
        window.location.href = "/login.html";
        return null;
    }
    if (!res.ok) throw new Error("Error inesperado: " + res.status);
    return await res.json();
}

// 2) Mostrar el nombre de usuario (o rol) en la interfaz
function mostrarNombreUsuario(nombreLogin) {
    const span = document.getElementById("usernameDisplay");
    if (span) span.textContent = `Usuario: ${nombreLogin}`;
}

// 3) Ajustar margen del contenido según el alto de la barra de botones
function ajustarMargenContenido() {
    const contIzq = document.querySelector('.button-container-left');
    const contDer = document.querySelector('.button-container-right');
    const contenido = document.querySelector('.contenido-container');

    if (contIzq && contDer && contenido) {
        const maxAltura = Math.max(contIzq.offsetHeight, contDer.offsetHeight);
        contenido.style.marginTop = (maxAltura + 20) + 'px';
    }
}

// 4) Función para mostrar alertas desde js
function mostrarAlerta(mensaje) {
    const alerta = document.getElementById("alerta");
    const span = alerta.querySelector(".msg");
    alerta.style.display = "block";
    span.textContent = mensaje;
}

function cerrarAlerta() {
    const alerta = document.getElementById("alerta");
    alerta.style.display = "none";
}

// 5) Configurar eventos de los botones del menú administrador
function configurarEventos() {
    // Botón "Gestión de Habitaciones"
    const btnGH = document.getElementById("btnGestionHabitaciones");
    if (btnGH) {
        btnGH.addEventListener("click", () => {
            window.location.href = "../admin/gestion-habitaciones/menuGestionHabitaciones.html";
        });
    }

    // Botón "Gestión de Empleados"
    const btnGE = document.getElementById("btnGestionEmpleados");
    if (btnGE) {
        btnGE.addEventListener("click", () => {
            window.location.href = "/admin/gestion-empleados/menuGestionEmpleados.html";
        });
    }

    // Botón "Gestión de Reservas"
    const btnGR = document.getElementById("btnGestionReservas");
    if (btnGR) {
        btnGR.addEventListener("click", () => {
            window.location.href = "/admin/gestion-reservas/menuGestionReservas.html";
        });
    }

    // Botón "Ver Lista de Clientes"
    const btnLC = document.getElementById("btnListaClientes");
    if (btnLC) {
        btnLC.addEventListener("click", () => {
            window.location.href = "/admin/lista-clientes.html";
        });
    }

   // Botón "Visualizar Facturas"
   const btnVF = document.getElementById("btnVerFacturas");
   if (btnVF) {
       btnVF.addEventListener("click", () => {
           window.location.href = "/admin/lista-facturas.html";
       });
   }

    // Botón "Cerrar sesión"
    const btnCerrar = document.getElementById("btnCerrarSesion");
    if (btnCerrar) {
        btnCerrar.addEventListener("click", () => {
            sessionStorage.removeItem("jwt");
            window.location.href = "/login.html";
        });
    }
}

// 6) Inicialización al cargar la página
async function initAdminMenu() {
    // Ajustar margen del contenido
    ajustarMargenContenido();
    window.addEventListener('resize', ajustarMargenContenido);

    // Intentamos obtener el usuario actual
    const usuario = await obtenerUsuarioActual();
    if (usuario) {
        mostrarNombreUsuario(usuario.nombreLogin);
        configurarEventos();
    } else {
        // Si no hay usuario, no mostramos nada más (ya se redireccionó al login)
    }
}

// Ejecutar al cargar el DOM
document.addEventListener("DOMContentLoaded", initAdminMenu);
