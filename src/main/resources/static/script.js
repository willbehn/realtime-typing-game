let stompClient = null;
let currentRoomId = null;

function createRoom() {
    fetch('/api/rooms', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok ' + response.statusText);
        }
        return response.json();
    })
    .then(data => {
        console.log('Room created:', data);
        currentRoomId = data.id;
        document.getElementById("current-room-id").textContent = "Room ID: " + currentRoomId;
        connectToRoom(currentRoomId);
    })
    .catch(error => {
        console.error('Failed to create room:', error);
    });
}

function joinRoom() {
    const roomId = document.getElementById("join-room-id").value.trim();
    console.log("Trying to join room with id: " + roomId);
    if (roomId.length > 0) {
        fetch('/api/rooms/' + roomId + '/join?sessionId=test', {
            method: 'POST'
        })
        .then(response => {
            if (!response.ok) {
                showAlert("Please enter a valid room ID", 3000);
            } else {
                currentRoomId = roomId;
                document.getElementById("current-room-id").textContent = "Room ID: " + roomId;
                connectToRoom(roomId);
            }
        })
        .catch(error => {
            console.error('Failed to join room:', error);
            showAlert("Failed to join room. Please try again later.", 3000);
        });
    } else {
        showAlert("Please enter a valid room ID", 3000);
    }
}

function leaveRoom() {
    
}

function connectToRoom(roomId) {
    stompClient = Stomp.over(new SockJS('/ws'));

    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        document.getElementById("start-screen").style.display = "none";
        document.getElementById("game-screen").style.display = "flex";

        // Fetch initial data
        fetchInitialData(roomId);

        // Subscribe to topics
        stompClient.subscribe('/topic/room/' + roomId + '/positions', function(message) {
            const positions = JSON.parse(message.body);
            updatePositions(positions);
        });

        stompClient.subscribe('/topic/players', function(message) {
            const playerCount = message.body;
            console.log('Received player count: ' + playerCount);
            displayPlayerCount(playerCount);
        });
    });
}

function sendMessage() {
    const messageInput = document.getElementById("message-input").value;
    if (messageInput.length > 0) {
        stompClient.send("/app/room/" + currentRoomId, {}, messageInput);
        document.getElementById("message-input").value = '';
    }
}

function fetchInitialData(roomId) {
    // Change with the room-specific endpoint later
    fetch('/api/text')
        .then(response => response.text())
        .then(data => {
            console.log('Received fixed text:', data);
            displayFixedText(data);
        })
        .catch(error => {
            console.error('Failed to fetch fixed text:', error);
        });

    // Change with room specific playercount later
    fetch('/app/players')
        .then(response => response.text())
        .then(data => {
            console.log('Received player count:', data);
            stompClient.send('/app/players', {}, data);
        })
        .catch(error => {
            console.error('Failed to fetch player count:', error);
        });
}

function displayFixedText(text) {
    const fixedTextContainer = document.getElementById("fixedTextContainer");
    fixedTextContainer.textContent = text;
}

function displayPlayerCount(playerCount) {
    const playerCountContainer = document.getElementById("status");
    playerCountContainer.textContent = playerCount + " players connected";
}

function updatePositions(positions) {
    const fixedTextContainer = document.getElementById("fixedTextContainer");
    const fixedText = fixedTextContainer.textContent;

    console.log("Positions received: ", positions);

    let htmlContent = '';
    for (let i = 0; i < fixedText.length; i++) {
        let charClass = '';
        for (const sessionId in positions) {
            if (positions[sessionId] === i) {
                charClass = 'highlight-client';
                break;
            }
        }
        htmlContent += `<span class="${charClass}">${fixedText.charAt(i)}</span>`;
    }
    fixedTextContainer.innerHTML = htmlContent;
}

function enableTyping() {
    const messageInput = document.getElementById("message-input");
    messageInput.focus();
}

function copyToClipboard() {
    const roomIdWithText = document.getElementById("current-room-id").innerText.split(" ");
    const roomId = roomIdWithText[roomIdWithText.length - 1];

    navigator.clipboard.writeText(roomId).then(() => {
        showAlert("Room ID copied!", 3000);
    }).catch(error => {
        console.error('Failed to copy text: ', error);
    });
}

function showAlert(message, duration = 3000) {
    const alertContainer = document.getElementById("alert-container");
    alertContainer.textContent = message;
    alertContainer.classList.remove("hidden");
    alertContainer.classList.add("show");

    setTimeout(() => {
        alertContainer.classList.remove("show");
        alertContainer.classList.add("hidden");
    }, duration);
}

