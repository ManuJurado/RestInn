function login() {
    const user = document.getElementById("usuario").value;
    const pass = document.getElementById("clave").value;

    fetch("http://restinn.sytes.net/api/login", {
        method: "POST",
        credentials: "include",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ username: user, password: pass })
    })
    .then(response => {
        if (response.ok) {
            return response.json();
        } else {
            throw new Error("Usuario o contraseña incorrectos");
        }
    })
    .then(data => {
        window.location.href = "/home.html";
    })
    .catch(err => {
        alert(err.message);
    });
}
