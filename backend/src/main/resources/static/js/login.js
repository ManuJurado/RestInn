function login() {
    const user = document.getElementById("usuario").value;
    const pass = document.getElementById("clave").value;

    fetch("/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username: user, password: pass })
    })
    .then(response => {
        if (!response.ok) throw new Error("Usuario o contraseña incorrectos");
        return response.json();
    })
    .then(data => {
        sessionStorage.setItem("jwt", data.token);
        sessionStorage.setItem("usuarioId", data.id);
        sessionStorage.setItem("usuarioLogin", data.nombreLogin);
        sessionStorage.setItem("rol", data.rol);
        window.location.href = "/home.html";
    })
    .catch(err => {
        mostrarAlerta(err.message);
    });
}

document.addEventListener("keydown", function(event) {
    if (event.key === "Enter") {
        login();
    }
});
