// ui.js
export function showAlert(message, duration = 3000) {
    const alertContainer = document.getElementById("alert-container");
    alertContainer.textContent = message;
    alertContainer.classList.remove("hidden");
    alertContainer.classList.add("show");

    setTimeout(() => {
        alertContainer.classList.remove("show");
        alertContainer.classList.add("hidden");
    }, duration);
}
