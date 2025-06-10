document.addEventListener('DOMContentLoaded', () => {
  const formEmail      = document.getElementById('formEmail');
  const formCodigo     = document.getElementById('formCodigo');
  const formNuevaPass  = document.getElementById('formNuevaPass');

  const usuarioInfo   = document.getElementById('usuarioInfo');
  const loginDisplay  = document.getElementById('loginDisplay');

  let correoUsuario    = null;
  let codigoVerificado = null;

  /* Paso 1 */
  formEmail.addEventListener('submit', async e => {
    e.preventDefault();

    const email = document.getElementById('email').value.trim();
    const btnEnviar     = document.getElementById('btnEnviarCodigo');
    const btnEnviando   = document.getElementById('btnEnviandoCodigo');

    // Ocultar el botón de "Enviar código" y mostrar el de "Enviando..."
    btnEnviar.style.display = 'none';
    btnEnviando.style.display = 'inline-block';

    try {
      const res  = await fetch(`/api/auth/recovery?email=${encodeURIComponent(email)}`, { method: 'POST' });
      const body = await res.json();
      if (!res.ok) throw new Error(body.message);

      correoUsuario = email;
      mostrarAlerta(body.message);  // “Si el mail existe…”

      formEmail.style.display = 'none';
      formCodigo.style.display = 'block';
    } catch (err) {
      mostrarAlerta(err.message);

      // Restaurar botones si hubo error
      btnEnviar.style.display = 'inline-block';
      btnEnviando.style.display = 'none';
    }
  });


  /* Paso 2 */
  formCodigo.addEventListener('submit', async e => {
    e.preventDefault();
    const code = document.getElementById('code').value.trim();

    try {
      const res  = await fetch(`/api/auth/recovery/verify?code=${encodeURIComponent(code)}`);
      const body = await res.json();
      if (!res.ok) throw new Error(body.message);

      codigoVerificado = code;
      // ** aquí recibimos body.username **
      loginDisplay.textContent = body.username;
      usuarioInfo.style.display  = 'block';

      mostrarAlerta('Código verificado. Ingresá tu nueva contraseña.');
      formCodigo.style.display    = 'none';
      formNuevaPass.style.display = 'block';
    } catch (err) {
      mostrarAlerta(err.message);
    }
  });


  /* Paso 3 */
  formNuevaPass.addEventListener('submit', async e => {
    e.preventDefault();

    const pass1 = document.getElementById('newPass').value;
    const pass2 = document.getElementById('repeatPass').value;
    if (pass1 !== pass2) {
      mostrarAlerta('Las contraseñas no coinciden.');
      return;
    }

    try {
      const res  = await fetch('/api/auth/recovery/reset', {
        method : 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body   : JSON.stringify({
          email       : correoUsuario,
          code        : codigoVerificado,
          newPassword : pass1
        })
      });
      const body = await res.json();
      if (!res.ok) throw new Error(body.message);

      alert('Contraseña actualizada. Ahora puedes iniciar sesión.');
      window.location.href = '/login.html';
    } catch (err) {
      mostrarAlerta(err.message);
    }
  });
});
