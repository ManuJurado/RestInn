function mostrarAlerta(mensaje) {
  const alerta = document.getElementById("alerta");
  alerta.querySelector(".msg").textContent = mensaje;

  alerta.classList.remove("hide");
  alerta.classList.add("show", "showAlert");

  alerta.style.pointerEvents = "auto";
  alerta.style.opacity = "1";

    // Auto cerrar la alerta después de 5 segundos
    setTimeout(() => {
      cerrarAlerta();
    }, 4000);
}

function cerrarAlerta() {
  const alerta = document.getElementById("alerta");

  alerta.classList.remove("show");
  alerta.classList.remove("showAlert");
  alerta.classList.add("hide");

  alerta.addEventListener("animationend", () => {
    alerta.classList.remove("hide");
    alerta.style.pointerEvents = "none";
    alerta.style.opacity = "0";
  }, { once: true });
}

document.addEventListener('DOMContentLoaded', () => {
  document
    .querySelectorAll('input[type="text"], input[type="email"], input[type="password"]')
    .forEach(input => {
      if (!input.hasAttribute('maxlength')) {
        input.maxLength = 30;  // o el valor que prefieras
      }
    });
});