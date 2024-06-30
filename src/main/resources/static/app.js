var stompClient = Stomp.over(new SockJS('/ws'));

stompClient.connect({}, function(frame) {
    console.log('Connected: ' + frame);

    // Fetch initial data
    fetchInitialData();

    // Subscribe to topics
    stompClient.subscribe('/topic/positions', function(message) {
        var positions = JSON.parse(message.body);
        updatePositions(positions);
    });

    stompClient.subscribe('/topic/players', function(message) {
        var playerCount = message.body;
        console.log('Received player count: ' + playerCount);
        displayPlayerCount(playerCount);
    });
});


function sendMessage() {
    var messageInput = document.getElementById("message-input").value;
    if (messageInput.length > 0) {
        stompClient.send("/app/hello", {}, messageInput);
        document.getElementById("message-input").value = '';
    }
}

function fetchInitialData() {
    // Fetch initial fixed text
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
        const roomId = data.id;
        document.getElementById("current-room-id").textContent = roomId;
        //joinRoom(); // Automatically join the room after creating it
    })
    .catch(error => {
        console.error('Failed to create room:', error);
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
    var isFinished = false;

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

    // Focus on the message input field
    messageInput.focus();
}
