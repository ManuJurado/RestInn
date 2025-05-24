const express = require('express');
const path = require('path');
const app = express();
const PORT = 3000;

app.use(express.static(path.join(__dirname))); // Sirve todos los archivos estáticos

// Rutas amigables
app.get('/login', (req, res) => {
  res.sendFile(path.join(__dirname, 'login.html'));
});

app.get('/habitaciones', (req, res) => {
  res.sendFile(path.join(__dirname, 'habitaciones.html'));
});

app.listen(PORT, () => {
  console.log(`Frontend corriendo en http://localhost:${PORT}`);
});
