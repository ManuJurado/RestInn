/* ===== util ===== */
const tokenHdr = { 'Authorization': `Bearer ${sessionStorage.getItem('jwt')}` };

/* ===== datos generales de la hab ===== */
const form    = document.getElementById('formModificarHab');
const mensaje = document.getElementById('mensaje');
const params  = new URLSearchParams(location.search);
const idHab   = params.get('id');           // ?id=123

if (!idHab) {
  mensaje.textContent = 'No se indicó ID de la habitación';
  mensaje.style.color = 'red';
  form.style.display  = 'none';
} else {
  init();
}

async function init() {
  await cargarHabitacion();
  await cargarImagenesHabitacion();   // lista inicial de imágenes
}

/* ---------- GET datos de habitación ---------- */
async function cargarHabitacion() {
  try {
    const res = await fetch(`/api/habitaciones/admin/${idHab}`, { headers: tokenHdr });
    if (!res.ok) throw new Error('No autorizado o habitación inexistente');

    const h = await res.json();
    // rellenar formulario
    numero.value       = h.numero;
    tipo.value         = h.tipo;
    piso.value         = h.piso;
    capacidad.value    = h.capacidad;
    cantCamas.value    = h.cantCamas;
    precioNoche.value  = h.precioNoche;
    estado.value       = h.estado;
  } catch (err) {
    mensaje.textContent = err.message;
    mensaje.style.color = 'red';
    form.style.display  = 'none';
  }
}

/* ---------- PUT actualizar datos ---------- */
form.addEventListener('submit', async e => {
  e.preventDefault();

  const dto = {
    numero      : Number(numero.value),
    tipo        : tipo.value.trim(),
    piso        : Number(piso.value),
    capacidad   : Number(capacidad.value),
    cantCamas   : Number(cantCamas.value),
    precioNoche : Number(precioNoche.value),
    estado      : estado.value,
  };

  try {
    const res = await fetch(`/api/habitaciones/${idHab}`, {
      method : 'PUT',
      headers: { 'Content-Type':'application/json', ...tokenHdr },
      body   : JSON.stringify(dto)
    });

    if (!res.ok) {
      const msg = await res.text();  // backend debe mandar texto plano o JSON
      throw new Error(msg || 'Error actualizando la habitación');
    }

    alert('✅ Habitación actualizada con éxito');
    history.back(); // vuelve a la pantalla anterior

  } catch (err) {
    mensaje.textContent = err.message;
    mensaje.style.color = 'red';
  }
});

/* ============================================================
   🔸  BLOQUE   —   LISTAR, SUBIR y BORRAR IMÁGENES (máx 15)
   ============================================================ */
const contImgs   = document.getElementById('imagenesContainer');
const inputFile  = document.getElementById('inputNuevaImg');
const btnSubir   = document.getElementById('btnSubirImg');

/* ---- LISTAR ---- */
async function cargarImagenesHabitacion() {
  contImgs.innerHTML = 'Cargando imágenes…';
  try {
    /* backend devuelve: ["3::/api/imagenes/ver/una/3", …] */
    const res  = await fetch(`/api/imagenes/ver/${idHab}`, { headers: tokenHdr });
    if (!res.ok) throw new Error('No se pudieron cargar las imágenes');
    const lista = await res.json();

    contImgs.innerHTML = '';
    if (lista.length === 0) {
      contImgs.innerHTML = '<p>No hay imágenes cargadas.</p>';
      return;
    }

    lista.forEach(item => {
      const [imgId, url] = item.split('::');

      // div contenedor
      const card = document.createElement('div');
      card.style.position = 'relative';
      card.style.width  = '240px';
      card.style.height = '160px';

      // imagen
      const img = document.createElement('img');
      img.src = url;
      img.alt = `Imagen ${imgId}`;
      Object.assign(img.style, {
        width:'100%', height:'100%', objectFit:'cover', borderRadius:'8px'
      });

      // botón borrar
      const btnDel = document.createElement('button');
      btnDel.textContent = 'Borrar';
      btnDel.className   = 'normal-button';
      Object.assign(btnDel.style,{
        position:'absolute', bottom:'6px', left:'50%', transform:'translateX(-50%)',
        fontSize:'0.8rem'
      });
      btnDel.onclick = () => borrarImagen(imgId);

      card.append(img, btnDel);
      contImgs.appendChild(card);
    });

  } catch (err) {
    contImgs.innerHTML = `<p style="color:red;">${err.message}</p>`;
  }
}

/* ---- SUBIR ---- */
btnSubir.addEventListener('click', async () => {
  const file = inputFile.files?.[0];
  if (!file)      return alert('Elegí un archivo antes de subir');
  if (!file.type.startsWith('image/')) return alert('Solo se aceptan imágenes');
  try {
    const formData = new FormData();
    formData.append('archivo', file);

    const res = await fetch(`/api/imagenes/${idHab}`, {
      method : 'POST',
      headers: tokenHdr,          // no pongas Content-Type: multipart, fetch lo setea
      body   : formData
    });

    if (!res.ok) {
      const msg = await res.text();
      throw new Error(msg);
    }

    inputFile.value = '';          // resetea input
    await cargarImagenesHabitacion();

  } catch (err) {
    alert('Error subiendo imagen: ' + err.message);
  }
});

/* ---- BORRAR ---- */
async function borrarImagen(imgId) {
  const ok = confirm('¿Seguro que querés borrar esta imagen?');
  if (!ok) return;

  try {
    const res = await fetch(`/api/imagenes/borrar/${idHab}/${imgId}`, {
      method : 'DELETE',
      headers: tokenHdr
    });
    if (!res.ok) {
      const txt = await res.text();
      throw new Error(txt || 'Error del servidor');
    }
    await cargarImagenesHabitacion();
  } catch (err) {
    alert('Error al borrar: ' + err.message);
  }
}