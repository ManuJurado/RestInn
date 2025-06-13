const tokenHdr = { 'Authorization': `Bearer ${sessionStorage.getItem('jwt')}` };

document.addEventListener('DOMContentLoaded', initMisDatos);

async function initMisDatos() {
  try {
    // 1) Cargo datos actuales
    const res  = await fetch('/api/clientes/me', { headers: tokenHdr });
    if (!res.ok) throw new Error('No autorizado');
    const u = await res.json();

    // 2) Relleno el formulario
    document.getElementById('nombreLogin').value  = u.nombreLogin;
    document.getElementById('email').value         = u.email;
    document.getElementById('dni').value           = u.dni;
    document.getElementById('nombre').value        = u.nombre;
    document.getElementById('apellido').value      = u.apellido;
    document.getElementById('phoneNumber').value   = u.phoneNumber || '';
    document.getElementById('cuit').value          = u.cuit || '';

    // 3) Capturo el id para el PUT
    misDatos.usuarioId = u.id;

  } catch (err) {
    mostrarAlerta(err.message);
    document.getElementById('formDatos').style.display = 'none';
  }
}

// Guardamos el id en el objeto
const misDatos = { usuarioId: null };

document.getElementById('formDatos')
  .addEventListener('submit', async e => {
    e.preventDefault();
    // campos básicos...
    const dto = {
      nombre     : document.getElementById('nombre').value.trim(),
      apellido   : document.getElementById('apellido').value.trim(),
      phoneNumber: document.getElementById('phoneNumber').value.trim() || null,
      cuit: document.getElementById('cuit').value.trim() || null,
      // nuevos de contraseña:
      oldPassword: document.getElementById('oldPassword').value || null,
      password   : document.getElementById('newPassword').value || null
    };

    // Si quiso cambiar contraseña, validamos confirm:
    if (dto.oldPassword && dto.password) {
      const confirm   = document.getElementById('confirmPassword').value;
      if (dto.password !== confirm) {
        mostrarAlerta('Las contraseñas no coinciden');
        return;
      }
    }

    try {
      const res = await fetch(
        `/api/clientes/${misDatos.usuarioId}`,
        {
          method : 'PUT',
          headers: {
            'Content-Type': 'application/json',
            ...tokenHdr
          },
          body: JSON.stringify(dto)
        }
      );
      const body = await res.json();
      if (!res.ok) throw new Error(body.message);

      alert('✅ Cambios guardados');
    } catch (err) {
      mostrarAlerta(err.message);
    }
});