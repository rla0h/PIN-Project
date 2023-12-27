const clock = document.querySelector("h2#clock");
const date = new Date();

function getClock() {
    const date = new Date();
    const hour = String(date.getHours()).padStart(2, "0");
    const min = String(date.getMinutes()).padStart(2, "0");
    const seconds = String(date.getSeconds()).padStart(2, "0");

    clock.innerText = `${hour}:${min}:${seconds}`;
}

getClock();
setInterval(getClock, 1000);