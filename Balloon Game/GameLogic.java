import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class GameLogic extends JPanel implements ActionListener, KeyListener, MouseListener, MouseMotionListener {

    // Game board dimensions
    int boardWidth = 540;
    int boardHeight = 960;

    // Background
    int backgroundY1 = 0;
    int backgroundY2 = -boardHeight;
    int backgroundSpeed = 3;

    // Images
    Image background1, balloonImg, bird1, bird2;
    Image coinImg1, coinImg2, shieldImg, SlowTimeImg, ExtraLifeImg, doubleScoreImg, doubleGoldImg;
    Image startButtonImg, startButtonDarkImg, restartButtonImg, restartButtonDarkImg;

    // UI Buttons
    Rectangle startButtonRect, restartButtonRect;
    boolean startButtonHovered = false, restartButtonHovered = false;

    // Game objects
    Balloon balloon;
    ArrayList<Coin> coins = new ArrayList<>();
    ArrayList<Bird> birds = new ArrayList<>();
    ArrayList<PowerUp> powerUps = new ArrayList<>();

    // State variables
    boolean showStartScreen = true;
    boolean gameOver = false;
    boolean shieldActive = false;
    boolean slowTimeActive = false;
    boolean doubleScoreActive = false;
    boolean doubleGoldActive = false;
    boolean onGoing = false; // for pause and continue functions
    boolean isFlashing = false; // to make the balloon flash when it hits a bird

    // Stats
    int lives = 3;
    int flashCount = 0; // to make the balloon flash when it hits a bird
    static int CoinCount = 0;
    static int score = 0;
    static int highScore = 0;

    // Timers
    Timer gameLoop;
    Timer coinSpawnTimer;
    Timer powerUpSpawnTimer;
    Timer flashTimer; // to make the balloon flash when it hits a bird

    // Random generator
    Random random = new Random();

    // Balloon position and size
    int balloonX = boardWidth / 2 - 40;
    int balloonY = boardHeight - 350;
    int balloonWidth = boardWidth / 6;
    int balloonHeight = boardHeight / 8;

    // Constructor
    public GameLogic() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);

        loadImages();
        setupButtons();
        setupBalloon();
        setupBirds();
        setupTimers();
    }

    // Load all images
    private void loadImages() {
        background1 = new ImageIcon(getClass().getResource("./Icons/background.jpeg")).getImage();
        balloonImg = new ImageIcon(getClass().getResource("./Icons/Ballon(colour).png")).getImage();
        bird1 = new ImageIcon(getClass().getResource("./Icons/angry.png")).getImage();
        bird2 = new ImageIcon(getClass().getResource("./Icons/bird1.png")).getImage();
        coinImg1 = new ImageIcon(getClass().getResource("./Icons/Power-Ups/Coin1.png")).getImage();
        coinImg2 = new ImageIcon(getClass().getResource("./Icons/Power-Ups/Coin2.png")).getImage();
        shieldImg = new ImageIcon(getClass().getResource("./Icons/Power-Ups/shield.png")).getImage();
        SlowTimeImg = new ImageIcon(getClass().getResource("./Icons/Power-Ups/Time.png")).getImage();
        doubleScoreImg = new ImageIcon(getClass().getResource("./Icons/Power-Ups/DoubleScore.png")).getImage();
        doubleGoldImg = new ImageIcon(getClass().getResource("./Icons/Power-Ups/DoubleGold.png")).getImage();
        ExtraLifeImg = new ImageIcon(getClass().getResource("./Icons/Power-Ups/Heart.png")).getImage();
        startButtonImg = new ImageIcon(getClass().getResource("./Icons/startt.png")).getImage();
        startButtonDarkImg = new ImageIcon(getClass().getResource("./Icons/startt_black.png")).getImage();
        restartButtonImg = new ImageIcon(getClass().getResource("./Icons/restart.png")).getImage();
        restartButtonDarkImg = new ImageIcon(getClass().getResource("./Icons/restart_dark.png")).getImage();
    }

    // Set up button areas
    private void setupButtons() {
        int buttonWidth = 200;
        int buttonHeight = 100;
        startButtonRect = new Rectangle(boardWidth / 2 - buttonWidth / 2, boardHeight / 2 - 50, buttonWidth,
                buttonHeight);
        restartButtonRect = new Rectangle(boardWidth / 2 - buttonWidth / 2, boardHeight / 2 + 50, buttonWidth,
                buttonHeight);
    }

    // Set up the balloon
    private void setupBalloon() {
        balloon = new Balloon(balloonX, balloonY, balloonWidth, balloonHeight, balloonImg);
    }

    // Initialize birds
    private void setupBirds() {
        for (int i = 0; i < 3; i++) {
            int x = random.nextInt(boardWidth - 60);
            int y = -random.nextInt(boardHeight);

            // Randomly choose bird1 or bird2
            Image chosenBirdImg = random.nextBoolean() ? bird1 : bird2;

            birds.add(new Bird(chosenBirdImg, x, y, 60, 50));
        }
    }

    public void startFlashing() {
        isFlashing = true;
        flashCount = 0;

        if (flashTimer != null) {
            flashTimer.stop();
        }

        flashTimer = new Timer(100, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                isFlashing = !isFlashing;
                flashCount++;
                if (flashCount >= 6) { // 3 flashes (on/off)
                    flashTimer.stop();
                    isFlashing = false;
                }
                repaint();
            }
        });
        flashTimer.start();
    }

    public void activateSlowBoost() {
        slowTimeActive = true;

        Timer slowTimer = new Timer(5000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                slowTimeActive = false;
            }
        });
        slowTimer.setRepeats(false);
        slowTimer.start();
    }

    public void activateDoubleScore() {
        doubleScoreActive = true;

        Timer doubleScoreTimer = new Timer(10000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doubleScoreActive = false;
            }
        });
        doubleScoreTimer.setRepeats(false);
        doubleScoreTimer.start();
    }

    public void activateDoubleGold() {
        doubleGoldActive = true;

        Timer doubleGoldTimer = new Timer(10000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doubleGoldActive = false;
            }
        });
        doubleGoldTimer.setRepeats(false);
        doubleGoldTimer.start();
    }

    // Set up timers
    private void setupTimers() {
        coinSpawnTimer = new Timer(1000, c -> spawnCoin());
        powerUpSpawnTimer = new Timer(5000, p -> spawnPowerUp());
    }

    // Start a new game
    public void startGame() {
        coinSpawnTimer.start();
        powerUpSpawnTimer.start();
        gameLoop = new Timer(10, this);
        gameLoop.start();

        gameOver = false;
        showStartScreen = false;
        lives = 3;
        onGoing = true;

        if (score > highScore)
            highScore = score;
        score = 0;

        balloon.x = balloonX;
        balloon.y = balloonY;

        coins.clear();
        birds.clear();
        powerUps.clear();

        backgroundY1 = 0;
        backgroundY2 = -boardHeight;

        setupBirds();
    }

    // Pause and continue game
    void pauseGame() {
        if (!gameOver) {
            onGoing = false;
            gameLoop.stop();
            coinSpawnTimer.stop();
            powerUpSpawnTimer.stop();
        }
    }

    void continueGame() {
        if (!onGoing) {
            onGoing = true;
            gameLoop.start();
            coinSpawnTimer.start();
            powerUpSpawnTimer.start();
        }
    }

    // Spawn a coin, avoiding overlap with birds and power-ups
    void spawnCoin() {
        int tries = 0;
        while (tries < 10 && !gameOver) {
            int width = 40, height = 40;
            int x = random.nextInt(boardWidth - width);
            int y = random.nextInt(boardHeight / 2);

            Rectangle coinRect = new Rectangle(x, y, width, height);
            boolean overlaps = birds.stream()
                    .anyMatch(bird -> coinRect.intersects(new Rectangle(bird.x, bird.y, bird.width, bird.height)))
                    || powerUps.stream().anyMatch(powerup -> coinRect
                            .intersects(new Rectangle(powerup.x, powerup.y, powerup.width, powerup.height)));

            if (!overlaps) {
                boolean useCoin1 = random.nextBoolean();
                Image coinImg = useCoin1 ? coinImg1 : coinImg2;
                int coinPoints = useCoin1 ? 1 : 5;
                coins.add(new Coin(coinImg, x, y, width, height, coinPoints));
                break;
            }
            tries++;
        }
    }

    // Spawn a PowerUp, avoiding overlap with birds and coins
    void spawnPowerUp() {
        int tries = 0;
        while (tries < 10 && !gameOver) {
            int width = 60, height = 60;
            int x = random.nextInt(boardWidth - width);
            int y = random.nextInt(boardHeight / 2);

            Rectangle powerUpRect = new Rectangle(x, y, width, height);
            boolean overlaps = birds.stream()
                    .anyMatch(b -> powerUpRect.intersects(new Rectangle(b.x, b.y, b.width, b.height)))
                    || coins.stream().anyMatch(c -> powerUpRect.intersects(new Rectangle(c.x, c.y, c.width, c.height)));

            if (!overlaps) {
                int type = random.nextInt(5); // 0 = shield, 1 = slow Time, 2 = Extra life, 3 = double score, 4 = double
                                              // gold
                Image img = shieldImg;
                String powerUpType = "shield";

                switch (type) {
                    case 1:
                        img = SlowTimeImg;
                        powerUpType = "slow";
                        break;
                    case 2:
                        img = ExtraLifeImg;
                        powerUpType = "life";
                        break;
                    case 3:
                        img = doubleScoreImg;
                        powerUpType = "double";
                        break;
                    case 4:
                        img = doubleGoldImg;
                        powerUpType = "gold";
                        break;
                    default:
                        break;
                }

                powerUps.add(new PowerUp(img, x, y, width, height, powerUpType));
                break;
            }
            tries++;
        }
    }

    // Movement logic (background, coins, birds, power-ups)
    public void move() {
        // Background scroll
        backgroundY1 += backgroundSpeed;
        backgroundY2 += backgroundSpeed;

        if (backgroundY1 >= boardHeight) {
            backgroundY1 = backgroundY2 - boardHeight;
        }
        if (backgroundY2 >= boardHeight) {
            backgroundY2 = backgroundY1 - boardHeight;
        }

        // Coins
        coins.forEach(c -> c.y += backgroundSpeed);
        coins.removeIf(c -> {
            if (!c.collected && collisionWithCoin(balloon, c)) {
                if (doubleGoldActive) {
                    CoinCount += c.points * 2;
                } else {
                    CoinCount += c.points;
                }
                c.collected = true;
            }
            return c.collected;
        });

        // Power-ups
        for (int i = 0; i < powerUps.size(); i++) {
            PowerUp p = powerUps.get(i);
            p.y += backgroundSpeed - 2;
            if (p.y > boardHeight) {
                powerUps.remove(i);
                i--;
                continue;
            }
            if (collisionWithPowerUp(balloon, p)) {
                if (p.type.equals("shield")) {
                    shieldActive = true;
                } else if (p.type.equals("slow")) {
                    activateSlowBoost();
                } else if (p.type.equals("life")) {
                    if (lives <= 3) {
                        lives++;
                    }
                } else if (p.type.equals("double")) {
                    activateDoubleScore();
                } else if (p.type.equals("gold")) {
                    activateDoubleGold();
                }
                powerUps.remove(i);
                i--;
            }
        }

        // Birds
        for (Bird b : birds) {
            b.y += backgroundSpeed + 6;
            if (!b.hit && collisionWithBird(balloon, b)) {
                if (shieldActive)
                    shieldActive = false;
                else if (--lives <= 0) {
                    gameOver = true;
                    gameLoop.stop();
                    coinSpawnTimer.stop();
                }
                startFlashing();
                b.hit = true;
            }

            if (b.y > boardHeight) {
                if (doubleScoreActive) {
                    score += 2;
                } else {
                    score++;
                }
                b.y = -b.height;
                b.x = random.nextInt(boardWidth - b.width);
                b.img = random.nextBoolean() ? bird1 : bird2;
                b.hit = false;
            }

        }

        backgroundSpeed = slowTimeActive ? 2 : (3 + (score / 20));
    }

    boolean collisionWithCoin(Balloon a, Coin b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    boolean collisionWithPowerUp(Balloon a, PowerUp b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    boolean collisionWithBird(Balloon a, Bird b) {
        return a.x + a.width > b.x + 35 &&
                a.x < b.x + b.width - 35 &&
                a.y + a.height > b.y + 10 &&
                a.y < b.y + b.height - 20;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw Backgrounds
        g.drawImage(background1, 0, backgroundY1, boardWidth, boardHeight, null);
        g.drawImage(background1, 0, backgroundY2, boardWidth, boardHeight, null);

        // Draw Coins
        for (Coin c : coins) {
            g.drawImage(c.img, c.x, c.y, c.width, c.height, null);
        }

        // Draw Power-Ups
        for (PowerUp p : powerUps) {
            g.drawImage(p.img, p.x, p.y, p.width, p.height, null);
        }

        // Draw Birds
        for (Bird b : birds) {
            if (!b.hit) {
                g.drawImage(b.img, b.x, b.y, b.width, b.height, null);
            }
        }

        // Draw Balloon
        if (!isFlashing) {
            g.drawImage(balloon.img, balloon.x, balloon.y, balloon.width, balloon.height, null);
        }

        // Draw Shield indicator
        if (shieldActive) {
            g.setColor(new Color(0, 200, 255, 100));
            g.fillOval(balloon.x - 5, balloon.y - 5, balloon.width + 10, balloon.height + 10);
            g.drawImage(shieldImg, 0, 85, 35, 35, null);
        }

        // Draw Slow Time indicator
        if (slowTimeActive) {
            g.drawImage(SlowTimeImg, 40, 85, 35, 35, null);
        }
        // Draw Double Score indicator
        if (doubleScoreActive) {
            g.drawImage(doubleScoreImg, 80, 85, 35, 35, null);
        }
        // Draw Double Gold indicator
        if (doubleGoldActive) {
            g.drawImage(doubleGoldImg, 120, 85, 35, 35, null);
        }

        // Draw Start Screen
        if (showStartScreen) {
            g.drawImage(startButtonHovered ? startButtonDarkImg : startButtonImg,
                    startButtonRect.x, startButtonRect.y, startButtonRect.width, startButtonRect.height, null);
        }

        // Draw Game Over screen
        if (gameOver) {
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(0, 0, boardWidth, boardHeight);
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("Game Over", boardWidth / 2 - 120, boardHeight / 2 - 100);

            g.setColor(Color.WHITE);
            g.drawImage(restartButtonHovered ? restartButtonDarkImg : restartButtonImg,
                    restartButtonRect.x, restartButtonRect.y, restartButtonRect.width, restartButtonRect.height, null);

            g.setFont(new Font("Arial", Font.PLAIN, 24));
            g.drawString("Score: " + score, boardWidth / 2 - 60, boardHeight / 2 - 50);
            g.drawString("High Score: " + highScore, boardWidth / 2 - 90, boardHeight / 2 - 20);

            if (score > highScore) {
                g.setColor(Color.YELLOW);
                g.setFont(new Font("Arial", Font.BOLD, 40));
                g.drawString("New High Score!", boardWidth / 2 - 150, boardHeight / 2 + 30);
            }
        }

        // Draw stats
        if (onGoing && !gameOver) {
            g.setColor(Color.darkGray);
            g.setFont(new Font("Arial", Font.BOLD, 35));
            g.drawString("Coins : " + String.valueOf(CoinCount), 350, 35);
            g.drawString("High Score : " + String.valueOf(highScore), 0, 35);
            g.drawString("Score : " + String.valueOf(score), 0, 70);
            for (int i = 0; i < lives; i++) {
                int x = (boardWidth - lives * 40 + (lives - 1) * 10) / 2 + i * (40 + 10);
                g.drawImage(balloonImg, x + 150, 60, 35, 35, null);
            }

        }
    }

    // Actions
    @Override
    public void actionPerformed(ActionEvent e) { // Game Update Loop
        if (!gameOver) {
            move();
            repaint();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_ESCAPE) {
            if (onGoing) {
                pauseGame();
            } else {
                continueGame();
            }
            return;
        }

        if (!gameOver && onGoing) {
            switch (key) {
                case KeyEvent.VK_Q:
                    if (balloon.y > 0 && balloon.x + 15 > 0) {
                        balloon.y -= 15;
                        balloon.x -= 15;
                    }
                    break;
                case KeyEvent.VK_E:
                    if (balloon.y > 0 && balloon.x + 80 < boardWidth) {
                        balloon.y -= 15;
                        balloon.x += 15;
                    }
                    break;
                case KeyEvent.VK_Z:
                    if (balloon.y + balloon.height < boardHeight && balloon.x + 15 > 0) {
                        balloon.y += 15;
                        balloon.x -= 15;
                    }
                    break;
                case KeyEvent.VK_C:
                    if (balloon.y + balloon.height < boardHeight && balloon.x + 80 < boardWidth) {
                        balloon.y += 15;
                        balloon.x += 15;
                    }
                    break;
                case KeyEvent.VK_W:
                    if (balloon.y > 0)
                        balloon.y -= 15;
                    break;
                case KeyEvent.VK_S:
                    if (balloon.y + balloon.height < boardHeight)
                        balloon.y += 15;
                    break;
                case KeyEvent.VK_A:
                    if (balloon.x + 15 > 0)
                        balloon.x -= 15;
                    break;
                case KeyEvent.VK_D:
                    if (balloon.x + 80 < boardWidth)
                        balloon.x += 15;
                    break;
                default:
                    break;
            }
        } else {
            if (key == KeyEvent.VK_SPACE) {
                startGame();
            }
        }

        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (showStartScreen && startButtonRect.contains(e.getPoint())) {
            startGame();
        } else if (gameOver && restartButtonRect.contains(e.getPoint())) {
            startGame();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Point p = e.getPoint();
        startButtonHovered = showStartScreen && startButtonRect.contains(p);
        restartButtonHovered = gameOver && restartButtonRect.contains(p);
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }
}
