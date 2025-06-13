document.addEventListener("DOMContentLoaded", async () => {
  const contenedor = document.getElementById("infoUsuario");
  const token = sessionStorage.getItem("jwt");
  if (!token) {
    mostrarAlerta("Sesión expirada");
    return window.location.href = "/login.html";
  }

  const params = new URLSearchParams(window.location.search);
  const id = params.get("id");
  if (!id) {
    mostrarAlerta("ID de usuario no especificado");
    return;
  }

  try {
    const res = await fetch(`/api/usuarios/${id}`, {
      headers: { Authorization: `Bearer ${token}` }
    });
    if (!res.ok) throw new Error("No se pudo cargar el usuario");

    const usuario = await res.json();
    renderUsuario(usuario);
  } catch (e) {
    mostrarAlerta("Error: " + e.message);
  }

  function renderUsuario(u) {
    contenedor.innerHTML = `
      <label class="labels">Nombre
        <input class="inputs" value="${u.nombre}" readonly />
      </label>
      <label class="labels">Apellido
        <input class="inputs" value="${u.apellido}" readonly />
      </label>
      <label class="labels">DNI
        <input class="inputs" value="${u.dni}" readonly />
      </label>
      <label class="labels">Teléfono
        <input class="inputs" value="${u.phoneNumber}" readonly />
      </label>
      <label class="labels">Email
        <input class="inputs" value="${u.email}" readonly />
      </label>
      <label class="labels">CUIT
        <input class="inputs" value="${u.cuit}" readonly />
      </label>
      <label class="labels">Rol
        <input class="inputs" value="${u.role}" readonly />
      </label>
      <label class="labels">Activo
        <input class="inputs" value="${u.activo ? 'Sí' : 'No'}" readonly />
      </label>
    `;
  }
});
