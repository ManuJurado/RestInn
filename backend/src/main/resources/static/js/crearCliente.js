// ../js/crearCliente.js
const form = document.getElementById('formRegistro');
const btnRegistro = form.querySelector('button[type="submit"]');

form.addEventListener('submit', async e => {
  e.preventDefault();
  btnRegistro.disabled = true;
  btnRegistro.textContent = 'Enviando…';

  const nombreLogin = document.getElementById('nombreLogin').value.trim();
  const email       = document.getElementById('email').value.trim();
  const password    = document.getElementById('password').value;
  const confirm     = document.getElementById('confirm').value;
  const nombre      = document.getElementById('nombre').value.trim();
  const apellido    = document.getElementById('apellido').value.trim();
  const dni         = document.getElementById('dni').value.trim();
  const phoneNumber = document.getElementById('phoneNumber').value.trim();
  const cuit        = document.getElementById('cuit').value.trim();

  if (password !== confirm) {
    mostrarAlerta('Las contraseñas no coinciden');
    btnRegistro.disabled = false;
    btnRegistro.textContent = 'Registrarme';
    return;
  }

  const dto = { nombreLogin, email, password, nombre, apellido, dni, phoneNumber, cuit};

  try {
    const res = await fetch('/api/auth/register/initiate', {
      method: 'POST',
      headers: {'Content-Type':'application/json'},
      body: JSON.stringify(dto)
    });
    const body = await res.json();
    if (!res.ok) throw new Error(body.message);

    alert(body.message);
    // redirijo a la página de verificación pasándole el código
    window.location.href = `verificar.html?code=${encodeURIComponent(body.code)}`;
  } catch (err) {
    mostrarAlerta(err.message);
    btnRegistro.disabled = false;
    btnRegistro.textContent = 'Registrarme';
  }
});
