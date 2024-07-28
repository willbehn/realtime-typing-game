export function setupPopupModal() {
    const modal = document.getElementById("popup-modal");
    const closeButton = document.getElementsByClassName("close-button");

    closeButton.onclick = () => {
        modal.style.display = "none";
    };

    window.onclick = (event) => {
        if (event.target === modal) {
            modal.style.display = "none";
        }
    };
}

export function enableTyping() {
    const messageInput = document.getElementById("message-input");
    messageInput.focus();
}
