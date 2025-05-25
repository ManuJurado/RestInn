document.getElementById('login-form').addEventListener('submit', function(e) {
  e.preventDefault();

  const username = document.getElementById('username').value.trim();
  const password = document.getElementById('password').value;

  fetch('http://localhost:8080/api/login', {  // cambia la URL si es otra
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ username, password })
  })
  .then(async response => {
    if (!response.ok) {
      const error = await response.text();
      throw new Error(error || 'Error en la autenticación');
    }
    return response.json();
  })
  .then(data => {
    // Suponemos que el backend devuelve un token o info del usuario
    console.log('Login exitoso:', data);
    document.getElementById('login-message').style.color = 'green';
    document.getElementById('login-message').textContent = '¡Login exitoso!';

    // Aquí podés guardar token en localStorage/sessionStorage si hay
    // localStorage.setItem('token', data.token);

    // O redirigir a otra página o mostrar contenido privado
  })
  .catch(error => {
    console.error('Error al iniciar sesión:', error);
    document.getElementById('login-message').style.color = 'red';
    document.getElementById('login-message').textContent = 'Usuario o contraseña incorrectos';
  });
});
