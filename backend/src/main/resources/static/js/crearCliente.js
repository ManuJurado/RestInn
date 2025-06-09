async function registrarCliente(event) {
    event.preventDefault();

    const nombre      = document.getElementById('nombre').value.trim();
    const apellido    = document.getElementById('apellido').value.trim();
    const nombreLogin = document.getElementById('nombreLogin').value.trim();
    const email       = document.getElementById('email').value.trim();
    const dni         = document.getElementById('dni').value.trim();
    const phone       = document.getElementById('phone').value.trim();
    const cuit        = document.getElementById('cuit').value.trim();
    const password    = document.getElementById('password').value;
    const confirm     = document.getElementById('confirm').value;

    if (password !== confirm) {
        mostrarAlerta('Las contraseñas no coinciden');
        return;
    }

    const body = {
        nombre,
        apellido,
        nombreLogin,
        email,
        dni,
        phoneNumber: phone,
        CUIT: cuit,
        password
    };

    try {
        const res = await fetch('/api/usuarios/clientes', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });

        if (res.status === 409) {
            const msg = await res.text();
            throw new Error(msg || 'Nombre de usuario o email duplicados');
        }
        if (!res.ok) {
            const txt = await res.text();
            throw new Error(`${res.status} – ${txt}`);
        }

        mostrarAlerta('Cuenta creada con éxito. Ahora inicia sesión.');
        setTimeout(() => { window.location.href = '/login.html'; }, 1500);

    } catch (err) {
        mostrarAlerta(err.message);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('formCliente').addEventListener('submit', registrarCliente);
});
