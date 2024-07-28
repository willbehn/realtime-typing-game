
let timerInterval;
let countdownInterval;
export let countdown = 5;

export function handleCountdown(onCountdownComplete) {
    const timerElement = document.getElementById('timer');
    timerElement.textContent = countdown;

    countdownInterval = setInterval(() => {
        countdown--;
        timerElement.textContent = countdown;

        if (countdown <= 0) {
            clearInterval(countdownInterval);
            onCountdownComplete(); 
        }
    }, 1000);
}

export function resetCountdown() {
    countdown = 5; 
}


export function startGameTimer() {
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

export function stopGameTimer() {
    clearInterval(timerInterval); 
}