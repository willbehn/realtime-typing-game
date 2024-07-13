let stompClient = null;
let currentRoomId = null;
let sessionId = null;
let gameStarted = false;
let timerInterval;

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
        joinCreatedRoom(currentRoomId);
    })
    .catch(error => {
        console.error('Failed to create room:', error);
    });
}

function joinRoom() {
    const roomId = document.getElementById("join-room-id").value.trim();
    console.log("Trying to join room with id: " + roomId);
    if (roomId.length > 0) {
        fetch("/api/rooms/" + roomId +"/join",{method: 'POST'})
        .then(response => {
            if (!response.ok) {
                showAlert("Room not available ", 3000);
                return null;
            } else {
                return response.text();
            }
        })
        .then(data => {
            if (data) {
                sessionId = data;
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

function joinCreatedRoom(roomId) {
    fetch("/api/rooms/" + roomId +"/join",{method: 'POST'})
    .then(response => {
        if (!response.ok) {
            showAlert("Please enter a valid room ID", 3000);
            return null;
        } else {
            return response.text();
        }
    })
    .then(data => {
        if (data) {
            sessionId = data;
            connectToRoom(roomId);
        }
    })
    .catch(error => {
        console.error('Failed to join room:', error);
        showAlert("Failed to join room. Please try again later.", 3000);
    });
}


function leaveRoom() {
    console.log("Leaving room with id: " + currentRoomId);
    if (currentRoomId.length > 0) {
        fetch("/api/rooms/" + currentRoomId +"/leave?sessionId="+ sessionId,{method: 'POST'})
        .then(response => {
            if (!response.ok) {
                //showAlert("Please enter a valid room ID", 3000);
                //return null;
            } else {
                //startButton.disabled = false;
                stopGameTimer();
                document.getElementById("start-screen").style.display = "flex";
                document.getElementById("game-screen").style.display = "none";
                currentRoomId = null;
                gameStarted = false;
                return response.text();
            }
        })
        .catch(error => {
            console.error('Failed to leave room:', error);
            showAlert("Failed to leave room. Please try again later.", 3000);
        });
    }
}

function connectToRoom(roomId) {
    stompClient = Stomp.over(new SockJS('/ws'));
    console.log("SessionId recieved from server: " + sessionId);


    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        document.getElementById("start-screen").style.display = "none";
        document.getElementById("game-screen").style.display = "flex";

        fetchInitialData(roomId);

        stompClient.subscribe('/topic/room/' + roomId + '/positions', function(message) {
            const positions = JSON.parse(message.body);
            updatePositions(positions);
        });

        stompClient.subscribe('/topic/room/' + roomId + '/players', function(message) {
            const playerCount = message.body;
            console.log('Received player count: ' + playerCount);
            displayPlayerCount(playerCount);
        });

        stompClient.subscribe('/topic/room/' + roomId + '/status', function(message) {
            const status = JSON.parse(message.body); 
            handleGameStatus(status); 
        });
    });
}

function sendMessage() {
    const messageInput = document.getElementById("message-input").value;
    if (messageInput.length > 0) {
        stompClient.send("/app/room/" + currentRoomId, {sessionId: sessionId}, messageInput);
        document.getElementById("message-input").value = '';
    }
}

function fetchInitialData() {
    fetch("/api/rooms/" + currentRoomId + "/text",{method: 'GET'})
        .then(response => response.text())
        .then(data => {
            console.log('Received fixed text:', data);
            displayFixedText(data);
        })
        .catch(error => {
            console.error('Failed to fetch fixed text:', error);
        });

    stompClient.send("/app/room/" + currentRoomId + "/players", {}, "");

    const timer = document.getElementById("timer");
    timer.textContent = "0:00";
}

function displayFixedText(text) {
    const fixedTextContainer = document.getElementById("fixedTextContainer");
    fixedTextContainer.textContent = text;
}

function displayPlayerCount(playerCount) {
    const playerCountContainer = document.getElementById("player-count");
    playerCountContainer.textContent = playerCount;
}

function updatePositions(positions) {
    const fixedTextContainer = document.getElementById("fixedTextContainer");
    const fixedText = fixedTextContainer.textContent;

    console.log("Positions received: ", positions);

    let htmlContent = '';
    for (let i = 0; i < fixedText.length; i++) {
        let charClass = '';
        for (const sessionIdTest in positions) {
            if (positions[sessionIdTest] === (fixedText.length)){
                stompClient.send(`/app/room/${currentRoomId}/player/${sessionIdTest}/done`, {}, {});
                return;
            }

            if (positions[sessionIdTest] === i) {
                if (sessionIdTest == sessionId){
                    charClass = 'highlight-client-self';

                } else {
                    charClass = 'highlight-client-opponent';
                }
                break;
            }
        }
        htmlContent += `<span class="${charClass}">${fixedText.charAt(i)}</span>`;
    }
    fixedTextContainer.innerHTML = htmlContent;
}

function handleGameStatus(status) {
    console.log(status);

    if (status.done) {
        document.getElementById("message-input").disabled = true;
        const endTimeKeys = Object.keys(status.endTime);
        showPopup("Player with id: " + endTimeKeys + " won with " + status.endTime[endTimeKeys[0]] + " words per minute!");
        setTimeout(function() {
            leaveRoom();
        }, 10000);
    } else if (status.gameStarted) {
        showAlert("Game started!", 3000);
        document.getElementById("message-input").disabled = false;
        startGameTimer();

        const startButton = document.getElementById('start-game-button');
        startButton.textContent = 'Game going!';
        startButton.disabled = true;
    } 
}


function startGame() {
    stompClient.send(`/app/room/${currentRoomId}/start`, {}, {});
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

function startGameTimer() {
    let gameTime = 0;
    const timerElement = document.getElementById('timer');

    const updateTimer = () => {
        gameTime++;
        const minutes = Math.floor(gameTime / 60);
        const seconds = gameTime % 60;
        timerElement.textContent = `${minutes}:${seconds < 10 ? '0' + seconds : seconds}`;
    };

    updateTimer();
    timerInterval = setInterval(updateTimer, 1000); 
}

function stopGameTimer() {
    clearInterval(timerInterval); 
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

document.addEventListener("DOMContentLoaded", function(){
    var modal = document.getElementById("popup-modal");
    var closeButton = document.getElementsByClassName("close-button")[0];
  
    closeButton.onclick = function() {
      modal.style.display = "none";
    }
  
    window.onclick = function(event) {
      if (event.target === modal) {
        modal.style.display = "none";
      }
    }
  });
  
 
  function showPopup(message) {
    var modal = document.getElementById("popup-modal");
    var messageParagraph = document.getElementById("popup-message");
    messageParagraph.innerText = message;
    modal.style.display = "block";
  }
  
