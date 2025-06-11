document.addEventListener("DOMContentLoaded", () => {
  const cont         = document.getElementById("contenedorReservas");
  const filtroU      = document.getElementById("filtroUsuario");
  const filtroE      = document.getElementById("filtroEstado");
  const fechaDesde   = document.getElementById("fechaDesde");
  const fechaHasta   = document.getElementById("fechaHasta");
  const btnFiltrar   = document.getElementById("btnFiltrar");
  const btnLimpiar   = document.getElementById("btnLimpiar");
  const btnCrear     = document.getElementById("btnCrearReserva");

  let todasReservas = [];
  const token = sessionStorage.getItem("jwt");
  if (!token) return window.location.href = "/login.html";

  async function cargarTodas() {
    cont.innerHTML = "Cargando…";
    try {
      const res = await fetch("/api/reservas", {
        headers: { Authorization: `Bearer ${token}` }
      });
      if (!res.ok) throw new Error("No se pudieron cargar reservas");
      todasReservas = await res.json();
      renderTabla(todasReservas);
    } catch (e) {
      cont.innerHTML = `<p style="color:red">${e.message}</p>`;
    }
  }

  function aplicarFiltros() {
    let filt = [...todasReservas];
    const u = filtroU.value.trim().toLowerCase();
    if (u) filt = filt.filter(r => r.usuario.nombreLogin.toLowerCase().includes(u));
    const e = filtroE.value;
    if (e) filt = filt.filter(r => r.estado === e);
    if (fechaDesde.value && fechaHasta.value) {
      filt = filt.filter(r =>
        r.fechaReserva >= fechaDesde.value && r.fechaReserva <= fechaHasta.value
      );
    }
    renderTabla(filt);
  }

  function renderTabla(list) {
    if (!list.length) {
      cont.innerHTML = "<p>No hay reservas.</p>";
      return;
    }

    const tbl = document.createElement("table");
    tbl.className = "tabla-reservas";

    const thead = document.createElement("thead");
    const headRow = document.createElement("tr");
    [
      "ID","Usuario","Ingreso","Salida","Reserva","Estado",
      "Habitación","Acciones"
    ].forEach(txt => {
      const th = document.createElement("th");
      th.textContent = txt;
      headRow.appendChild(th);
    });
    thead.appendChild(headRow);
    tbl.appendChild(thead);

    const tbody = document.createElement("tbody");
    list.forEach(r => {
      const tr = document.createElement("tr");

      // columnas fijas
      [
               r.id,
               `${r.usuario.nombre} ${r.usuario.apellido}`,  // ← aquí mostramos nombre y apellido
               r.fechaIngreso,
               r.fechaSalida,
               r.fechaReserva,
               r.estado,
               r.habitacionNumero
             ].forEach(val => {
        const td = document.createElement("td");
        td.textContent = val || "";
        tr.appendChild(td);
      });

      // acciones
      const tdAcc = document.createElement("td");

      // Ver datos usuario
      const btnVerU = document.createElement("button");
      btnVerU.textContent = "Ver datos usuario";
      btnVerU.className = "small-button";
      btnVerU.onclick = () => {
        window.location.href = `/admin/usuario.html?id=${r.usuario.id}`;
      };
      tdAcc.appendChild(btnVerU);

      // Ver factura
            const btnF = document.createElement("button");
            btnF.textContent = "Ver factura";
            btnF.className = "small-button";
            btnF.onclick = () =>
              window.location.href = `/factura.html?reserva=${r.id}`;
            tdAcc.appendChild(btnF);

      // confirmar
      if (r.estado === "PENDIENTE") {
        const b = document.createElement("button");
        b.textContent = "Confirmar";
        b.className = "small-button";
        b.onclick = async () => await accion(`/api/reservas/confirmar/${r.id}`, "POST");
        tdAcc.appendChild(b);
      }

      // checkin
      if (r.estado === "CONFIRMADA") {
        const b = document.createElement("button");
        b.textContent = "Check-in";
        b.className = "small-button";
        b.onclick = async () => await accion(`/api/reservas/checkin/${r.id}`, "POST");
        tdAcc.appendChild(b);
      }

      // checkout
      if (r.estado === "EN_CURSO") {
        const b = document.createElement("button");
        b.textContent = "Check-out";
        b.className = "small-button";
        b.onclick = async () => await accion(`/api/reservas/checkout/${r.id}`, "POST");
        tdAcc.appendChild(b);
      }

      tr.appendChild(tdAcc);
      tbody.appendChild(tr);
    });

    tbl.appendChild(tbody);

    const wrapper = document.createElement("div");
    wrapper.className = "tabla-container";
    wrapper.appendChild(tbl);

    cont.innerHTML = "";
    cont.appendChild(wrapper);
  }

  async function accion(url, method) {
    try {
      const res = await fetch(url, { method, headers:{ Authorization:`Bearer ${token}` } });
      if (!res.ok) throw new Error(await res.text());
      mostrarAlerta("Operación exitosa");
      await cargarTodas();
    } catch (e) {
      mostrarAlerta("Error: " + e.message);
    }
  }

  btnFiltrar.onclick = aplicarFiltros;
  btnLimpiar.onclick = () => {
    filtroU.value = ""; filtroE.value = ""; fechaDesde.value = ""; fechaHasta.value = "";
    renderTabla(todasReservas);
  };
  btnCrear.onclick = () => window.location.href = "/admin/crearReservaAdmin.html";

  cargarTodas();
});
