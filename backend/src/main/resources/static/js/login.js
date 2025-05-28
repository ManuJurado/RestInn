function login() {
    const user = document.getElementById("usuario").value;
    const pass = document.getElementById("clave").value;

    fetch("http://restinn.sytes.net/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username: user, password: pass })
    })
    .then(response => {
        if (!response.ok) throw new Error("Usuario o contraseña incorrectos");
        return response.json();
    })
    .then(data => {
        localStorage.setItem("jwt", data.token); // Guardar token en "jwt"
        window.location.href = "/home.html";
    })
    .catch(err => {
        alert(err.message);
    });
}