function mostrarAlerta(mensaje) {
  const alerta = document.getElementById("alerta");
  alerta.querySelector(".msg").textContent = mensaje;

  alerta.classList.remove("hide");
  alerta.classList.add("show", "showAlert");

  alerta.style.pointerEvents = "auto";
  alerta.style.opacity = "1";

  // Limpiamos cualquier eventListener previo para evitar múltiples disparos
  alerta.removeEventListener("animationend", handleAnimationEnd);

  // Auto cerrar la alerta después de 5 segundos
  setTimeout(() => {
    cerrarAlerta();
  }, 5000);
}

function handleAnimationEnd() {
  const alerta = document.getElementById("alerta");
  alerta.classList.remove("hide");
  alerta.style.pointerEvents = "none";
  alerta.style.opacity = "0";
}

function cerrarAlerta() {
  const alerta = document.getElementById("alerta");

  alerta.classList.remove("show");
  alerta.classList.remove("showAlert");
  alerta.classList.add("hide");

  // Escuchamos que termine la animación de salida para resetear estilos
  alerta.addEventListener("animationend", () => {
    handleAnimationEnd();
  }, { once: true });
}


async function login() {
    const user = document.getElementById("usuario").value;
    const pass = document.getElementById("clave").value;

    try {
        const authRes = await fetch("/api/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username: user, password: pass })
        });

        if (!authRes.ok) throw new Error("Usuario o contraseña incorrectos");
        const { token } = await authRes.json();
        sessionStorage.setItem("jwt", token);

        const userRes = await fetch("/api/usuarios/current", {
            headers: { "Authorization": `Bearer ${token}` }
        });

        if (!userRes.ok) throw new Error("No se pudo obtener datos de usuario");
        const usuario = await userRes.json();

        sessionStorage.setItem("usuarioId", usuario.id);
        sessionStorage.setItem("usuarioLogin", usuario.nombreLogin || usuario.nombre_login);
        const role = usuario.role || usuario.rol || usuario.rol_empleado;
        sessionStorage.setItem("rol", role);

        switch (role) {
            case "CLIENTE":
                window.location.href = "/home.html";
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
                mostrarAlerta(`Rol desconocido: "${role}"`);
                sessionStorage.clear();
        }

    } catch (err) {
        mostrarAlerta(err.message);
    }
}

function initLoginEvents() {
    document.getElementById("btnIngresar")?.addEventListener("click", login);

    document.addEventListener("keydown", function(event) {
        if (event.key === "Enter") login();
    });
}

window.addEventListener("DOMContentLoaded", initLoginEvents);