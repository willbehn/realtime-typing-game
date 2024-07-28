import { showPopup,showAlert, displayFixedText, displayPlayerCount } from './ui.js';
import { handleCountdown, resetCountdown, countdown } from './countdown.js';

let stompClient = null;
let currentRoomId = null;
let sessionId = null;
let timerInterval;
let gameStarted = false;


document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("create-room-button").addEventListener("click", createRoom);
    document.getElementById("join-room-button").addEventListener("click", joinRoom);
    document.getElementById("copy-room-id-button").addEventListener("click", copyToClipboard);
    document.getElementById("leave-room-button").addEventListener("click", leaveRoom);
    document.getElementById("start-game-button").addEventListener("click", startGame);
    document.getElementById("fixedTextContainer").addEventListener("click", enableTyping);
    document.getElementById("message-input").addEventListener("input", sendMessage);
    document.getElementById("start-game-button").addEventListener("click", sendStartToRoom);
    document.getElementById("leave-game-button").addEventListener("click", leaveRoom);

    setupPopupModal();
});

async function createRoom() {
    try {
        const response = await fetch('/api/rooms', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error('Failed creating new room');
        }

        const data = await response.json();
        console.log('Room created:', data);

        currentRoomId = data.id;
        document.getElementById("current-room-id").textContent = `Room ID: ${currentRoomId}`;
        joinCreatedRoom(currentRoomId);

    } catch (error) {
        console.error(error);
        showAlert("Failed creating new room, try again later", 3000);
    }
}


async function joinRoom() {
    const roomId = document.getElementById("join-room-id").value.trim();
    const playerName = document.getElementById("player-name").value;

    if (!roomId) {
        showAlert("Please enter a valid room ID", 3000);
        return;
    }

    console.log(`Trying to join room with id: ${roomId}`);

    try {
        const response = await fetch(`/api/rooms/${roomId}/join`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ playerName })
        });

        if (!response.ok) {
            throw new Error('Room not available');
        }

        const data = await response.json();
        sessionId = data.id;
        console.log(`sessionId received: ${data.id}`);

        currentRoomId = roomId;
        document.getElementById("current-room-id").textContent = `Room ID: ${roomId}`;
        connectToRoom(roomId);

    } catch (error) {
        console.error('Failed to join room:', error);
        showAlert("Failed to join room. Please try again later.", 3000);
    }
}


async function joinCreatedRoom(roomId) {
    const playerName = document.getElementById("player-name").value;
    
    try {
        const response = await fetch(`/api/rooms/${roomId}/join`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ playerName })
        });

        if (!response.ok) {
            throw new Error('Failed to join created room');
        }

        const data = await response.json();
        sessionId = data.id;
        connectToRoom(roomId);

    } catch (error) {
        console.error(error);
        showAlert("Failed to join room. Please try again later.", 3000);
    }
}



async function leaveRoom() {
    console.log(`Leaving room with id: ${currentRoomId}`);

    if (currentRoomId.length === 0) return;

    try {
        const url = `/api/rooms/${currentRoomId}/leave?sessionId=${sessionId}`;
        const response = await fetch(url, { method: 'POST' });

        if (!response.ok) {
            showAlert("Failed leaving room, try again later.", 3000);
            return;
        }

        stopGameTimer();
        document.getElementById("start-screen").style.display = "flex";
        document.getElementById("game-screen").style.display = "none";
        document.getElementById("popup-modal").style.display = "none";
        resetClient();

    } catch (error) {
        console.error('Failed to leave room:', error);
        showAlert("Failed to leave room. Please try again later.", 3000);
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

        stompClient.subscribe('/topic/room/' + roomId + '/status', function(message) {
            const status = JSON.parse(message.body); 
            const playerNamesMap = status.playerNames;
            const playerCount = status.playerCount;
            updatePlayerList(playerNamesMap);
            displayPlayerCount(playerCount);

            handleGameStatus(status); 
        });
        //TODO add to its own function when refactoring playercount
        stompClient.send("/app/room/" + currentRoomId + "/status", {}, "");
    });
}

function sendMessage() {
    const messageInput = document.getElementById("message-input").value;
    if (messageInput.length > 0) {
        stompClient.send("/app/room/" + currentRoomId, {sessionId: sessionId}, messageInput);
        document.getElementById("message-input").value = '';
    }
}

function sendStartToRoom() {
    stompClient.send(`/app/room/${currentRoomId}/start`, {}, {});
}

async function fetchInitialData() {
    try {
        const response = await fetch(`/api/rooms/${currentRoomId}/text`, { method: 'GET' });

        if (!response.ok) {
            throw new Error('Failed to fetch fixed text');
        }

        const data = await response.json();
        console.log('Received fixed text:', data);

        displayFixedText(data.textString);

    } catch (error) {
        console.error('Failed to fetch fixed text:', error);
    }
    document.getElementById("timer").textContent = "0:00";
}




function updatePositions(positionDto) {
    const fixedTextContainer = document.getElementById("fixedTextContainer");
    const fixedText = fixedTextContainer.textContent;
    const positions = positionDto.positions;

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


function startGame() {
    if (countdown > 0) return;

    resetCountdown();

    document.getElementById("message-input").disabled = false;
    startGameTimer();

    const startButton = document.getElementById('start-game-button');
    startButton.textContent = 'Game going!';
    startButton.disabled = true;
}


function handleGameStatus(status) {
    console.log(status);

    if (status.done) {
        document.getElementById("message-input").disabled = true;
        const endTimeKeys = Object.keys(status.endTime);
        stopGameTimer();
        showPopup("Player with id: " + endTimeKeys + " won with " + status.endTime[endTimeKeys[0]] + " words per minute!");
        
    } else if (status.gameStarted) {
        showAlert("Game starting in 5 seconds!", 3000);
        document.getElementById("message-input").disabled = true;

        handleCountdown(startGame);
    }
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

function resetClient(){
    stompClient = null;
    currentRoomId = null;
    sessionId = null;
    gameStarted = false;
   
    const startButton = document.getElementById('start-game-button');
    startButton.textContent = 'Start game';
    startButton.disabled = false;
}


function setupPopupModal() {
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

function copyToClipboard() {
    const roomIdWithText = document.getElementById("current-room-id").innerText.split(" ");
    const roomId = roomIdWithText[roomIdWithText.length - 1];

    navigator.clipboard.writeText(roomId).then(() => {
        showAlert("Room ID copied!", 3000);
    }).catch(error => {
        console.error('Failed to copy text: ', error);
    });
}

function enableTyping() {
    const messageInput = document.getElementById("message-input");
    messageInput.focus();
}

function updatePlayerList(playerNamesMap) {
    const playerListContainer = document.getElementById("player-list");
    playerListContainer.innerHTML = '';

    Object.values(playerNamesMap).forEach(name => {
        const listItem = document.createElement("li");
        listItem.textContent = name;
        playerListContainer.appendChild(listItem);
    });
}
  
 

  
