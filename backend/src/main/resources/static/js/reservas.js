document.addEventListener("DOMContentLoaded", async () => {
    try {
        const res = await fetch("/reservas/mias", {
            credentials: "include"
        });

        if (!res.ok) {
            throw new Error("Error al obtener reservas");
        }

        const reservas = await res.json();
        const ul = document.getElementById("listaReservas");

        reservas.forEach(r => {
            const li = document.createElement("li");
            li.textContent = `Reserva ID ${r.id} - Entrada: ${r.fechaEntrada}, Salida: ${r.fechaSalida}`;
            ul.appendChild(li);
        });
    } catch (error) {
        console.error("Error:", error);
        alert("No se pudieron cargar las reservas.");
    }
});
