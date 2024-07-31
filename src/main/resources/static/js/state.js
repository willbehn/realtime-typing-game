const State = {
    stompClient: null,
    currentRoomId: null,
    sessionId: null,
    gameStarted: false,
    totalCharsTyped: 0,
    correctCharsTyped: 0,
    textPos: 0,

    reset() {
        this.stompClient = null;
        this.currentRoomId = null;
        this.sessionId = null;
        this.gameStarted = false;
        this.totalCharsTyped = 0;
        this.correctCharsTyped = 0;
        this.textPos = 0;
    }
};

export default State;
