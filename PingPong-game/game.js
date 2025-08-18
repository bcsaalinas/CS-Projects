const canvas = document.getElementById("pongCanvas");
const ctx = canvas.getContext("2d");

const CANVAS_WIDTH = canvas.width;
const CANVAS_HEIGHT = canvas.height;

const PADDLE_WIDTH = 15;
const PADDLE_HEIGHT = 100;
const BALL_RADIUS = 12;

let playerY = (CANVAS_HEIGHT - PADDLE_HEIGHT) / 2;
let aiY = (CANVAS_HEIGHT - PADDLE_HEIGHT) / 2;

let ballX = CANVAS_WIDTH / 2;
let ballY = CANVAS_HEIGHT / 2;
let ballSpeedX = 6 * (Math.random() > 0.5 ? 1 : -1);
let ballSpeedY = 4 * (Math.random() * 2 - 1);

const PADDLE_SPEED = 6;
const AI_SPEED = 5;

// score for player
let score = 0;
const scoreElem = document.getElementById("score");
const gameOverMsg = document.getElementById("gameOverMsg");
const playAgainBtn = document.getElementById("playAgainBtn");
let isGameOver = false;

function drawRect(x, y, w, h, color) {
  ctx.fillStyle = color;
  ctx.fillRect(x, y, w, h);
}

function drawCircle(x, y, r, color) {
  ctx.fillStyle = color;
  ctx.beginPath();
  ctx.arc(x, y, r, 0, Math.PI * 2, false);
  ctx.closePath();
  ctx.fill();
}

function drawNet() {
  ctx.strokeStyle = "#fff2";
  ctx.lineWidth = 4;
  for (let i = 0; i < CANVAS_HEIGHT; i += 30) {
    ctx.beginPath();
    ctx.moveTo(CANVAS_WIDTH / 2, i);
    ctx.lineTo(CANVAS_WIDTH / 2, i + 20);
    ctx.stroke();
  }
}

function resetBall() {
  ballX = CANVAS_WIDTH / 2;
  ballY = CANVAS_HEIGHT / 2;
  ballSpeedX *= Math.random() > 0.5 ? 1 : -1;
  ballSpeedY = 4 * (Math.random() * 2 - 1);
  // if you want to reset the score each round, uncomment below:
  // score = 0;
  // if (scoreElem) scoreElem.textContent = score;
}

function gameLoop() {
  if (isGameOver) return;
  // move the ball
  ballX += ballSpeedX;
  ballY += ballSpeedY;

  // bounce off the top and bottom walls
  if (ballY - BALL_RADIUS < 0) {
    ballY = BALL_RADIUS;
    ballSpeedY *= -1;
  } else if (ballY + BALL_RADIUS > CANVAS_HEIGHT) {
    ballY = CANVAS_HEIGHT - BALL_RADIUS;
    ballSpeedY *= -1;
  }

  // did the ball hit the player's paddle?
  if (
    ballX - BALL_RADIUS < PADDLE_WIDTH &&
    ballY > playerY &&
    ballY < playerY + PADDLE_HEIGHT
  ) {
    ballX = PADDLE_WIDTH + BALL_RADIUS;
    ballSpeedX *= -1.1;
    // add some spin depending on where the ball hit the paddle
    let collidePoint = ballY - (playerY + PADDLE_HEIGHT / 2);
    ballSpeedY = collidePoint * 0.25;
    // increment score and update display
    score++;
    if (scoreElem) scoreElem.textContent = score;
    // check for game over
    if (score >= 10) {
      isGameOver = true;
      if (gameOverMsg) gameOverMsg.style.display = "block";
      return;
    }
  }

  // did the ball hit the ai's paddle?
  if (
    ballX + BALL_RADIUS > CANVAS_WIDTH - PADDLE_WIDTH &&
    ballY > aiY &&
    ballY < aiY + PADDLE_HEIGHT
  ) {
    ballX = CANVAS_WIDTH - PADDLE_WIDTH - BALL_RADIUS;
    ballSpeedX *= -1.1;
    let collidePoint = ballY - (aiY + PADDLE_HEIGHT / 2);
    ballSpeedY = collidePoint * 0.25;
  }

  // if the ball goes out of bounds, reset it
  if (ballX - BALL_RADIUS < 0 || ballX + BALL_RADIUS > CANVAS_WIDTH) {
    resetBall();
  }

  // move the ai paddle to follow the ball (simple ai)
  let aiCenter = aiY + PADDLE_HEIGHT / 2;
  if (aiCenter < ballY - 20) {
    aiY += AI_SPEED;
  } else if (aiCenter > ballY + 20) {
    aiY -= AI_SPEED;
  }
  // keep the ai paddle inside the canvas
  aiY = Math.max(Math.min(aiY, CANVAS_HEIGHT - PADDLE_HEIGHT), 0);

  // draw everything on the screen
  ctx.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
  drawNet();

  // draw the player's paddle
  drawRect(0, playerY, PADDLE_WIDTH, PADDLE_HEIGHT, "#5dfdcb");
  // draw the ai's paddle
  drawRect(
    CANVAS_WIDTH - PADDLE_WIDTH,
    aiY,
    PADDLE_WIDTH,
    PADDLE_HEIGHT,
    "#fd5d5d"
  );
  // draw the ball
  drawCircle(ballX, ballY, BALL_RADIUS, "#fff");

  requestAnimationFrame(gameLoop);
}

// let the player move their paddle with the mouse
canvas.addEventListener("mousemove", function (evt) {
  const rect = canvas.getBoundingClientRect();
  const scaleY = CANVAS_HEIGHT / rect.height;
  let mouseY = (evt.clientY - rect.top) * scaleY;
  playerY = mouseY - PADDLE_HEIGHT / 2;
  // keep the players paddle inside the canvas
  playerY = Math.max(Math.min(playerY, CANVAS_HEIGHT - PADDLE_HEIGHT), 0);
});

// play again button click
if (playAgainBtn) {
  playAgainBtn.onclick = function () {
    score = 0;
    if (scoreElem) scoreElem.textContent = score;
    isGameOver = false;
    if (gameOverMsg) gameOverMsg.style.display = "none";
    resetBall();
    gameLoop();
  };
}

// start the game loop
gameLoop();
