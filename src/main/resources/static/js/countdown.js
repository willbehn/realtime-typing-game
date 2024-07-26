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