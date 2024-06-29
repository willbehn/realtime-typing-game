// script.js

var stompClient = Stomp.over(new SockJS('/ws'));
var clientId = 'client1';

stompClient.connect({}, function(frame) {
    document.getElementById('status').innerText = 'Connected';
    fetchFixedText();

    stompClient.subscribe('/topic/positions', function(message) {
        var positions = JSON.parse(message.body);
        updatePositions(positions);
    });

    stompClient.subscribe('/topic/fixed-text', function(message) {
        var fixedText = message.body;
        displayFixedText(fixedText);
    });
});

function sendMessage() {
    var messageInput = document.getElementById("message-input").value;
    if (messageInput.length > 0) {
        stompClient.send("/app/hello", {}, messageInput);
        document.getElementById("message-input").value = '';
    }
}

function displayFixedText(text) {
    var fixedTextContainer = document.getElementById("fixedTextContainer");
    fixedTextContainer.textContent = text;
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

function fetchFixedText() {
    fetch('api/fixed-text')
        .then(response => response.text())
        .then(data => {
            console.log('Received fixed text:', data);
            stompClient.send('/app/fixed-text', {}, data);
        })
        .catch(error => {
            console.error('Failed to fetch fixed text:', error);
        });
}
