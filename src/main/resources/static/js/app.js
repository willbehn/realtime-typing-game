import { showPopup, showAlert, displayFixedText, displayPlayerCount, updatePlayerList, updateCurrentRoomDisplay } from './ui.js';
import { handleCountdown, resetCountdown, countdown, startGameTimer, stopGameTimer } from './timer.js';
import { setupPopupModal, enableTyping } from './utils.js';

let stompClient = null;
let currentRoomId = null;
let sessionId = null;
let gameStarted = false;

export let totalCharsTyped = 0;
export let correctCharsTyped = 0;
let textPos = 0;

document.addEventListener("DOMContentLoaded", () => {
    setupEventListeners();
    setupPopupModal();
});

function setupEventListeners() {
    document.getElementById("create-room-button").addEventListener("click", createRoom);
    document.getElementById("join-room-button").addEventListener("click", joinRoom);
    document.getElementById("copy-room-id-button").addEventListener("click", copyToClipboard);
    document.getElementById("leave-room-button").addEventListener("click", leaveRoom);
    document.getElementById("start-game-button").addEventListener("click", startGame);
    document.getElementById("fixedTextContainer").addEventListener("click", enableTyping);
    document.getElementById("message-input").addEventListener("input", sendMessage);
    document.getElementById("start-game-button").addEventListener("click", sendStartToRoom);
    document.getElementById("leave-game-button").addEventListener("click", leaveRoom);
}

async function apiRequest(url, method, body = null) {
    const options = {
        method,
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include'
    };
    if (body) options.body = JSON.stringify(body);

    const response = await fetch(url, options);
    if (!response.ok) throw new Error('Network response was not ok');
    return response.json();
}

function handleError(error, message) {
    console.error(error);
    showAlert(message, 3000);
}

async function createRoom() {
    try {
        const data = await apiRequest('/api/rooms', 'POST');
        currentRoomId = data.id;
        updateCurrentRoomDisplay(currentRoomId);
        joinRoom();
    } catch (error) {
        handleError(error, "Failed creating new room, try again later");
    }
}

async function joinRoom() {
    if (!currentRoomId) {
        currentRoomId = document.getElementById("join-room-id").value.trim();
        if (!currentRoomId) {
            showAlert("Please enter a valid room ID", 3000);
            return;
        }
    }

    const playerName = document.getElementById("player-name").value;
    try {
        const data = await apiRequest(`/api/rooms/${currentRoomId}/join`, 'POST', { playerName });
        sessionId = data.id;
        updateCurrentRoomDisplay(currentRoomId);
        connectToRoom(currentRoomId);
    } catch (error) {
        handleError(error, "Failed to join room. Please try again later.");
    }
}


async function leaveRoom() {
    console.log(`Leaving room with id: ${currentRoomId}`);

    if (currentRoomId.length === 0) return;

    try {
        const url = `/api/rooms/${currentRoomId}/leave?sessionId=${sessionId}`;
        const response = await fetch(url, { 
            'method': 'POST' ,
            'credentials': 'include' });

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
    stompClient = Stomp.over(new SockJS('/ws', null, { withCredentials: true }));
    console.log("SessionId received from server: " + sessionId);

    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        document.getElementById("start-screen").style.display = "none";
        document.getElementById("game-screen").style.display = "flex";

        fetchInitialData(roomId);

        stompClient.subscribe('/topic/room/' + roomId + '/positions', function(message) {
            const positions = JSON.parse(message.body);

            if (positions.status === "success"){
                updatePositions(positions);
            } else {
                console.error("Access denied");
            }
            
        });

        stompClient.subscribe('/topic/room/' + roomId + '/status', function(message) {
            const status = JSON.parse(message.body); 
            const playerNamesMap = status.playerNames;
            const playerCount = status.playerCount;
            updatePlayerList(playerNamesMap);
            displayPlayerCount(playerCount);

            handleGameStatus(status); 
        });
        // TODO: Add to its own function when refactoring player count
        stompClient.send("/app/room/" + currentRoomId + "/status", { sessionId: sessionId }, "");
    });
}

function sendMessage() {
    const messageInput = document.getElementById("message-input").value;
    const fixedTextContainer = document.getElementById("fixedTextContainer");
    const fixedText = fixedTextContainer.textContent; 

    if (messageInput.length > 0) {
        const lastChar = messageInput.charAt(0).toLowerCase(); 
        const expectedChar = fixedText.charAt(textPos).toLowerCase(); 

        totalCharsTyped++;

        if (lastChar === expectedChar) {
            correctCharsTyped++;
            textPos++;
        } 
        updateAccuracyDisplay();

        stompClient.send("/app/room/" + currentRoomId, { sessionId: sessionId }, messageInput);
        document.getElementById("message-input").value = '';
    }
}


function sendStartToRoom() {
    stompClient.send(`/app/room/${currentRoomId}/start`, { sessionId: sessionId }, {});
}

async function fetchInitialData() {
    try {
        const data = await apiRequest(`/api/rooms/${currentRoomId}/text`, 'GET');
        displayFixedText(data.textString);
        document.getElementById("timer").textContent = "0:00";
    } catch (error) {
        console.error('Failed to fetch fixed text:', error);
    }
}

function updatePositions(positionDto) {
    const fixedTextContainer = document.getElementById("fixedTextContainer");
    const fixedText = fixedTextContainer.textContent;
    const positions = positionDto.positions;
    const textLength = fixedText.length;
    
    for (const sessionIdTest in positions) {
        if (positions[sessionIdTest] === textLength) {
            stompClient.send(`/app/room/${currentRoomId}/done`, { sessionId: sessionId }, {});
            return;
        }
    }

    const getHighlightClass = (charIndex) => {
        for (const sessionIdTest in positions) {
            if (positions[sessionIdTest] === charIndex) {
                return sessionIdTest === sessionId ? 'highlight-client-self' : 'highlight-client-opponent';
            }
        }
        return '';
    };

    const buildHtmlContent = () => {
        let html = '';
        for (let i = 0; i < textLength; i++) {
            const charClass = getHighlightClass(i);
            html += `<span class="${charClass}">${fixedText.charAt(i)}</span>`;
        }
        return html;
    };

    fixedTextContainer.innerHTML = buildHtmlContent();
}

function startGame() {
    if (countdown > 0) return;

    resetCountdown();

    const messageInput = document.getElementById("message-input");
    messageInput.disabled = false;
    messageInput.focus(); 
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


function resetClient() {
    stompClient = null;
    currentRoomId = null;
    sessionId = null;
    gameStarted = false;
    totalCharsTyped = 0;
    correctCharsTyped = 0;
    textPos = 0;
    updateAccuracyDisplay();

    const startButton = document.getElementById('start-game-button');
    startButton.textContent = 'Start game';
    startButton.disabled = false;
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

export function updateAccuracyDisplay() {
    const accuracyPercentage = (totalCharsTyped === 0) ? 100 : (correctCharsTyped / totalCharsTyped) * 100;
    document.getElementById("accuracy").textContent = `${accuracyPercentage.toFixed(0)}%`;
}
  
