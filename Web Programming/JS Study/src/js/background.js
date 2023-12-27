const images = ["0.jpeg", "1.jpg"];

const choosenImage = images[Math.floor(Math.random() * images.length)];

const bgImage = document.createElement("img");

bgImage.src=`img/${choosenImage}`;

document.body.appendChild(bgImage);
//document.body.prependChild(bgImage);