var stompClient = null;

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
        roomId = data.id;
        document.getElementById("current-room-id").textContent = "Room ID: " + roomId;
        connectToRoom(roomId);
    })
    .catch(error => {
        console.error('Failed to create room:', error);
    });
}

function joinRoom() {
    roomId = document.getElementById("join-room-id").value.trim();
    if (roomId.length > 0) {
        document.getElementById("current-room-id").textContent = "Room ID: " + roomId;
        connectToRoom(roomId);
    } else {
        alert("Please enter a valid room ID");
    }
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
            var positions = JSON.parse(message.body);
            updatePositions(positions);
        });

        stompClient.subscribe('/topic/players', function(message) {
            var playerCount = message.body;
            console.log('Received player count: ' + playerCount);
            displayPlayerCount(playerCount);
        });
    });
}

function sendMessage() {
    var messageInput = document.getElementById("message-input").value;
    if (messageInput.length > 0) {
        stompClient.send("/app/room/" + roomId, {}, messageInput);
        document.getElementById("message-input").value = '';
    }
}

function fetchInitialData(roomId) {
    //Change with the room specific endpoint
    fetch('/api/text')
        .then(response => response.text())
        .then(data => {
            console.log('Received fixed text:', data);
            displayFixedText(data);
        })
        .catch(error => {
            console.error('Failed to fetch fixed text:', error);
        });

    // Fetch initial player count
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
    var fixedTextContainer = document.getElementById("fixedTextContainer");
    fixedTextContainer.textContent = text;
}

function displayPlayerCount(playerCount) {
    var playerCountContainer = document.getElementById("status");
    playerCountContainer.textContent = playerCount + " players connected";
}

function updatePositions(positions) {
    var fixedTextContainer = document.getElementById("fixedTextContainer");
    var fixedText = fixedTextContainer.textContent;

    console.log("Positions received: ", positions);

    var htmlContent = '';
    for (var i = 0; i < fixedText.length; i++) {
        var charClass = '';
        for (var sessionId in positions) {
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
    var messageInput = document.getElementById("message-input");
    messageInput.focus();
}
