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

    stompClient.subscribe('/topic/fixed-text', function(message) {
        var fixedText = message.body;
        displayFixedText(fixedText);
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
    fetch('/app/fixed-text')
        .then(response => response.text())
        .then(data => {
            console.log('Received fixed text:', data);
            stompClient.send('/app/fixed-text', {}, data);
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
