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

export function showPopup(message) {
    var modal = document.getElementById("popup-modal");
    var messageParagraph = document.getElementById("popup-message");
    messageParagraph.innerText = message;
    modal.style.display = "block";
  }

export function displayFixedText(text) {
    const fixedTextContainer = document.getElementById("fixedTextContainer");
    fixedTextContainer.textContent = text;
}

export function displayPlayerCount(playerCount) {
    const playerCountContainer = document.getElementById("player-count");
    playerCountContainer.textContent = playerCount;
}