document.addEventListener("DOMContentLoaded", cargarClientes);

async function cargarClientes() {
    const token = sessionStorage.getItem("jwt");
    const res = await fetch("/api/admin/usuarios/clientes", {
        headers: { "Authorization": `Bearer ${token}` }
    });
    const clientes = await res.json();
    mostrarClientes(clientes);
}

function mostrarClientes(clientes) {
    const tbody = document.getElementById("tablaClientesBody");
    tbody.innerHTML = "";

    clientes.forEach(cliente => {
        const tr = document.createElement("tr");

        const nombre = `${cliente.nombre} ${cliente.apellido}`;
        const estado = cliente.activo ? "Activo" : "Inactivo";

        tr.innerHTML = `
            <td>${nombre}</td>
            <td>${cliente.dni}</td>
            <td>${estado}</td>
            <td>
                <button onclick="verDatos(${cliente.id})">Datos</button>
                <button onclick="verFacturas(${cliente.id})">Facturas</button>
                <button onclick="verReservas(${cliente.id})">Reservas</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function aplicarFiltros() {
    const nombreFiltro = document.getElementById("filtroNombre").value.toLowerCase();
    const dniFiltro = document.getElementById("filtroDni").value;

    const filas = document.querySelectorAll("#tablaClientesBody tr");
    filas.forEach(fila => {
        const nombre = fila.cells[0].textContent.toLowerCase();
        const dni = fila.cells[1].textContent;

        const coincideNombre = nombre.includes(nombreFiltro);
        const coincideDni = dni.includes(dniFiltro);

        fila.style.display = (coincideNombre && coincideDni) ? "" : "none";
    });
}

// Acciones (puedes redirigir a nuevas páginas según tu estructura)
function verDatos(id) {
    window.location.href = `/admin/usuario.html?id=${id}`;
}
function verFacturas(id) {
    window.location.href = `/clientes/facturas-cliente.html?id=${id}`;
}
function verReservas(id) {
    window.location.href = `/admin/reservas-cliente.html?id=${id}`;
}
