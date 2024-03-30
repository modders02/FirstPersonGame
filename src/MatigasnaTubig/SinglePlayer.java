package MatigasnaTubig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SinglePlayer extends JPanel implements Runnable, KeyListener {
    private static final long serialVersionUID = 1L;
    private static final int WIDTH = 1300;
    private static final int HEIGHT = 800;
    private static final int PLAYER_SIZE = 40;
    private static final int ENEMY_SIZE = 40;
    private static int ENEMY_MOVE_SPEED = 4;
    private static final int ENEMY_SPAWN_INTERVAL = 3000; // in milliseconds
    private static final int MAX_ENEMIES = 10;
    private static final int BULLET_SIZE = 4;
    private static final int MAX_BULLETS = 5;
    private static final int BULLET_SPEED = 8;
    private static final int HOUSE_X = PLAYER_SIZE / 2;
    private static final int HOUSE_Y = HEIGHT / 2 - (PLAYER_SIZE * 3 / 2);
    private int stage = 1;
    private long lastShotTime = 0;
    private static final long SHOT_DELAY = 200;

    private int playerX = WIDTH / 4;
    private int playerY = HEIGHT / 2;
    private int playerDirection = KeyEvent.VK_RIGHT; // Initial direction

    private List<Enemy> enemies = new ArrayList<>();

    private List<Bullet> bullets = new ArrayList<>();

    private Timer enemySpawnTimer;

    private boolean upPressed, downPressed, leftPressed, rightPressed, spacePressed;

    private int playerPoints = 0;

    private int playerHealth = 200; // Initial health

    private JFrame frame; // Reference to the JFrame

    private boolean gameOver = false; // Flag to track game over state

    public SinglePlayer(JFrame frame, IntroPanel introPanel) {
        this.frame = frame;
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        addKeyListener(this);
        setFocusable(true);
        startGame();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_ESCAPE) {
            showEscapePanel();
        } else if (key == KeyEvent.VK_W) {
            upPressed = true;
            playerDirection = KeyEvent.VK_UP;
        } else if (key == KeyEvent.VK_S) {
            downPressed = true;
            playerDirection = KeyEvent.VK_DOWN;
        } else if (key == KeyEvent.VK_A) {
            leftPressed = true;
            playerDirection = KeyEvent.VK_LEFT;
        } else if (key == KeyEvent.VK_D) {
            rightPressed = true;
            playerDirection = KeyEvent.VK_RIGHT;
        } else if (key == KeyEvent.VK_SPACE) {
            spacePressed = true;
            shoot();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W) {
            upPressed = false;
        } else if (key == KeyEvent.VK_S) {
            downPressed = false;
        } else if (key == KeyEvent.VK_A) {
            leftPressed = false;
        } else if (key == KeyEvent.VK_D) {
            rightPressed = false;
        } else if (key == KeyEvent.VK_SPACE) {
            spacePressed = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    private void render(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.GRAY);
        int houseWidth = PLAYER_SIZE * 3;
        int houseHeight = PLAYER_SIZE * 3;
        g.fillRect(HOUSE_X, HOUSE_Y, houseWidth, houseHeight);
        int[] roofX = {HOUSE_X, HOUSE_X + houseWidth / 2, HOUSE_X + houseWidth};
        int[] roofY = {HOUSE_Y, HOUSE_Y - houseHeight / 2, HOUSE_Y};
        g.setColor(Color.RED);
        g.fillPolygon(roofX, roofY, 3);
        g.setColor(Color.WHITE);
        g.fillRect(playerX - PLAYER_SIZE / 2, playerY - PLAYER_SIZE / 2, PLAYER_SIZE, PLAYER_SIZE);
        g.setColor(Color.BLUE);
        g.drawRect(playerX - PLAYER_SIZE / 2, playerY - PLAYER_SIZE / 2, PLAYER_SIZE, PLAYER_SIZE);
        g.setColor(Color.WHITE);
        g.drawLine(playerX, playerY, playerX, playerY + PLAYER_SIZE);
        g.drawLine(playerX, playerY + PLAYER_SIZE / 2, playerX - PLAYER_SIZE / 2, playerY + PLAYER_SIZE * 3 / 4);
        g.drawLine(playerX, playerY + PLAYER_SIZE / 2, playerX + PLAYER_SIZE / 2, playerY + PLAYER_SIZE * 3 / 4);
        g.drawLine(playerX, playerY + PLAYER_SIZE, playerX - PLAYER_SIZE / 2, playerY + PLAYER_SIZE * 3 / 2);
        g.drawLine(playerX, playerY + PLAYER_SIZE, playerX + PLAYER_SIZE / 2, playerY + PLAYER_SIZE * 3 / 2);
        g.setColor(Color.RED);
        for (Enemy enemy : enemies) {
            g.fillRect(enemy.x, enemy.y, ENEMY_SIZE, ENEMY_SIZE);
            g.drawLine(enemy.x, enemy.y, enemy.x, enemy.y + ENEMY_SIZE); // Body
            g.drawLine(enemy.x, enemy.y + ENEMY_SIZE / 2, enemy.x - ENEMY_SIZE / 2, enemy.y + ENEMY_SIZE * 3 / 4);
            g.drawLine(enemy.x, enemy.y + ENEMY_SIZE / 2, enemy.x + ENEMY_SIZE / 2, enemy.y + ENEMY_SIZE * 3 / 4);
            g.drawLine(enemy.x, enemy.y + ENEMY_SIZE, enemy.x - ENEMY_SIZE / 2, enemy.y + ENEMY_SIZE * 3 / 2);
            g.drawLine(enemy.x, enemy.y + ENEMY_SIZE, enemy.x + ENEMY_SIZE / 2, enemy.y + ENEMY_SIZE * 3 / 2);
        }

        g.setColor(Color.YELLOW);
        for (Bullet bullet : bullets) {
            g.fillRect(bullet.x - BULLET_SIZE / 2, bullet.y - BULLET_SIZE / 2, BULLET_SIZE, BULLET_SIZE);
        }
        g.setColor(Color.WHITE);
        g.fillRect(10, 10, playerHealth, 20);
        g.setColor(Color.BLACK);
        g.drawRect(10, 10, playerHealth, 20); // Corrected
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 13));
        g.drawString("Health: " + playerHealth, 10, 50);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Player Points: " + playerPoints, WIDTH / 2 - 80, 30);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Stage: " + stage, WIDTH - 120, 30);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        render(g);
    }

    private void movePlayer() {
        if (upPressed && playerY > 0) {
            playerY -= 5;
        }
        if (downPressed && playerY < HEIGHT - PLAYER_SIZE) {
            playerY += 5;
        }
        if (leftPressed && playerX > 0) {
            playerX -= 5;
        }
        if (rightPressed && playerX < WIDTH - PLAYER_SIZE) {
            playerX += 5;
        }
    }

    private void startEnemySpawning() {
        enemySpawnTimer = new Timer();
        enemySpawnTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (enemies.size() < MAX_ENEMIES) {
                    spawnEnemy();
                }
            }
        }, 0, ENEMY_SPAWN_INTERVAL - (stage * 500)); // Adjust spawn time based on stage
    }

    private void updateStage() {
        // Check if the player has reached the next stage
        if (playerPoints >= stage * 10) { // For example, increase stage every 10 points
            stage++;
            enemySpawnTimer.cancel(); // Cancel the current timer
            startEnemySpawning(); // Restart enemy spawning with updated spawn time
        }
    }

    private void spawnEnemy() {
        int enemyX = WIDTH - ENEMY_SIZE;
        int enemyY = (int) (Math.random() * (HEIGHT - ENEMY_SIZE));
        Enemy enemy = new Enemy(enemyX, enemyY);
        enemies.add(enemy);
    }

    private void startGame() {
        startEnemySpawning();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if (!isPaused) {
                update();
                repaint();
            }
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void moveBullets() {
        Iterator<Bullet> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();
            bullet.move();
            if (bullet.x > WIDTH || bullet.x < 0 || bullet.y > HEIGHT || bullet.y < 0) {
                iterator.remove();
            }
        }
    }

    private void checkCollisions() {
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            Rectangle bulletRect = new Rectangle(bullet.x - BULLET_SIZE / 2, bullet.y - BULLET_SIZE / 2, BULLET_SIZE, BULLET_SIZE);
            Iterator<Enemy> enemyIterator = enemies.iterator();
            while (enemyIterator.hasNext()) {
                Enemy enemy = enemyIterator.next();
                Rectangle enemyRect = new Rectangle(enemy.x, enemy.y, ENEMY_SIZE, ENEMY_SIZE);
                if (bulletRect.intersects(enemyRect)) {
                    bulletIterator.remove();
                    enemyIterator.remove();
                    playerPoints++;
                    updateStage();
                }
            }
        }

        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            Rectangle enemyRect = new Rectangle(enemy.x, enemy.y, ENEMY_SIZE, ENEMY_SIZE);
            Rectangle houseRect = new Rectangle(HOUSE_X, HOUSE_Y, PLAYER_SIZE * 3, PLAYER_SIZE * 3);
            if (enemyRect.intersects(houseRect)) {
                playerHealth -= 2;
                enemyIterator.remove();
            }
        }
    }


    private void shoot() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShotTime >= SHOT_DELAY && bullets.size() < MAX_BULLETS) {
            lastShotTime = currentTime;


            int bulletXSpeed = 0;
            int bulletYSpeed = 0;

            switch (playerDirection) {
                case KeyEvent.VK_UP:
                    bulletYSpeed = -BULLET_SPEED;
                    break;
                case KeyEvent.VK_DOWN:
                    bulletYSpeed = BULLET_SPEED;
                    break;
                case KeyEvent.VK_LEFT:
                    bulletXSpeed = -BULLET_SPEED;
                    break;
                case KeyEvent.VK_RIGHT:
                    bulletXSpeed = BULLET_SPEED;
                    break;
            }

            if (upPressed && leftPressed) {
                bulletXSpeed = -BULLET_SPEED;
                bulletYSpeed = -BULLET_SPEED;
            } else if (upPressed && rightPressed) {
                bulletXSpeed = BULLET_SPEED;
                bulletYSpeed = -BULLET_SPEED;
            } else if (downPressed && leftPressed) {
                bulletXSpeed = -BULLET_SPEED;
                bulletYSpeed = BULLET_SPEED;
            } else if (downPressed && rightPressed) {
                bulletXSpeed = BULLET_SPEED;
                bulletYSpeed = BULLET_SPEED;
            }

            bullets.add(new Bullet(playerX, playerY, bulletXSpeed, bulletYSpeed));
        }
    }

    private class Enemy {
        int x, y;
        boolean shouldRemove = false;

        public Enemy(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void move(int playerX, int playerY) {
            double angle = Math.atan2(HOUSE_Y - y, HOUSE_X - x);
            int speed = ENEMY_MOVE_SPEED;
            x += (int) (speed * Math.cos(angle));
            y += (int) (speed * Math.sin(angle));

            Rectangle enemyRect = new Rectangle(x, y, ENEMY_SIZE, ENEMY_SIZE);
            Rectangle houseRect = new Rectangle(HOUSE_X, HOUSE_Y, PLAYER_SIZE * 3, PLAYER_SIZE * 3);
            if (enemyRect.intersects(houseRect)) {
                playerHealth -= 100;
                shouldRemove = true;
            }
        }
    }

    private void update() {
        movePlayer();

        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            enemy.move(playerX, playerY);
            if (enemy.shouldRemove) {
                enemyIterator.remove();
            }
        }

        if (spacePressed) {
            shoot();
        }
        moveBullets();
        checkCollisions();

        checkPlayerHealth(); // Check player's health status
    }


    private void checkPlayerHealth() {
        if (!gameOver && playerHealth <= 0) { // Check game over flag before proceeding
            gameOver = true; // Set game over flag to true
            int choice = JOptionPane.showOptionDialog(SwingUtilities.getWindowAncestor(this), "Game Over! Do you want to start a new game or return home?", "Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"New Game", "Home"}, "New Game");

            if (choice == JOptionPane.YES_OPTION) {
                restartGame(); // Restart the game
            } else {
                // Return to the intro panel
                JFrame gameFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                gameFrame.dispose(); // Close the game window
                enemySpawnTimer.cancel(); // Stop enemy spawning

                // Create and show the intro panel
                JFrame introFrame = new JFrame("Intro");
                introFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                introFrame.getContentPane().add(new IntroPanel());
                introFrame.pack();
                introFrame.setLocationRelativeTo(null);
                introFrame.setVisible(true);
            }
        }
    }


    private void restartGame() {
        // Stop enemy spawning
        enemySpawnTimer.cancel();

        // Reset player position, health, and points
        playerX = WIDTH / 4;
        playerY = HEIGHT / 2;
        playerHealth = 200;
        playerPoints = 0;

        // Clear enemies and bullets lists
        enemies.clear();
        bullets.clear();

        // Restart enemy spawning
        startEnemySpawning();
    }

    private boolean isPaused = false;

 // Modify the game loop to check for the pause state
 
 

 // Modify the method to show the escape panel
 private void showEscapePanel() {
     // Pause the game when the escape panel is shown
     isPaused = true;
     
     String[] options = {"Restart Game", "New Game", "Continue", "Home"};
     int choice = JOptionPane.showOptionDialog(frame, "Choose an option", "Game Menu", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
     switch (choice) {
         case 0:
             restartGame();
             break;
         case 1:
             newGame();
             break;
         case 2:
             // Continue playing, do nothing
             break;
         case 3:
             returnHome();
             break;
         default:
             break;
     }

     // Resume the game after the escape panel is dismissed
     isPaused = false;
 }

    private void newGame() {
        // Stop enemy spawning
        enemySpawnTimer.cancel();

        // Clear enemies and bullets lists
        enemies.clear();
        bullets.clear();

        // Reset player position, health, and points
        playerX = WIDTH / 4;
        playerY = HEIGHT / 2;
        playerHealth = 200;
        playerPoints = 0;

        // Restart enemy spawning
        startEnemySpawning();
    }

    private void returnHome() {
        // Close the current game window
        JFrame gameFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        gameFrame.dispose();

        // Stop enemy spawning
        enemySpawnTimer.cancel();

        // Create and show the intro panel
        JFrame introFrame = new JFrame("Intro");
        introFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        introFrame.getContentPane().add(new IntroPanel());
        introFrame.pack();
        introFrame.setLocationRelativeTo(null);
        introFrame.setVisible(true);
    }


    class Bullet {
        int x, y;
        int xSpeed, ySpeed;

        public Bullet(int x, int y, int xSpeed, int ySpeed) {
            this.x = x;
            this.y = y;
            this.xSpeed = xSpeed;
            this.ySpeed = ySpeed;
        }

        public void move() {
            x += xSpeed;
            y += ySpeed;
        }
    }
}
