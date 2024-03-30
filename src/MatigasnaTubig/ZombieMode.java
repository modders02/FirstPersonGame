package MatigasnaTubig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ZombieMode extends JPanel implements Runnable, KeyListener {
    // Constants
    private static final long serialVersionUID = 1L;
    private static final int WIDTH = 1300;
    private static final int HEIGHT = 800;
    private static final int PLAYER_SIZE = 40;
    private static final int ENEMY_SIZE = 40;
    private static double ENEMY_MOVE_SPEED = 2.0;
    private static final int ENEMY_SPAWN_INTERVAL = 3000; // in milliseconds
    private static final int MAX_ENEMIES = 10;
    private static final int SPEED_INCREASE_INTERVAL = 10000; // in milliseconds
    private static final double SPEED_INCREASE_AMOUNT = 1;
    private static final int BULLET_SIZE = 4;
    private static final int BULLET_SPEED = 8;

    // Player variables
    private int playerX = WIDTH / 4;
    private int playerY = HEIGHT / 2;

    // Enemy list
    private List<Enemy> enemies = new ArrayList<>();

    // Bullets list
    private List<Bullet> bullets = new ArrayList<>();

    // Timers
    private Timer enemySpawnTimer;
    private Timer speedIncreaseTimer;

    // Key press flags
    private boolean upPressed, downPressed, leftPressed, rightPressed, spacePressed;

    // Health
    private int health = 100;

    // Points
    private int points = 0;

    // Stage
    private int stage = 1;

    // Constructor
    public ZombieMode() {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        // Create points panel and add it to the north center
        JPanel pointsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pointsPanel.setBackground(Color.BLACK);
        pointsPanel.setPreferredSize(new Dimension(WIDTH, 50));
        pointsPanel.add(new JLabel("Points:")); // Add points label
        pointsPanel.add(new JLabel(String.valueOf(points))); // Add points value label
        addKeyListener(this);
        setFocusable(true);
        startGame();
    }

    // KeyListener methods
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W) {
            upPressed = true;
        } else if (key == KeyEvent.VK_S) {
            downPressed = true;
        } else if (key == KeyEvent.VK_A) {
            leftPressed = true;
        } else if (key == KeyEvent.VK_D) {
            rightPressed = true;
        } else if (key == KeyEvent.VK_SPACE) {
            spacePressed = true;
            shoot(); // Fire bullet when space is pressed
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

    // Render method
    private void render(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // Draw health bar
        g.setColor(Color.RED);
        g.fillRect(10, 10, health * 2, 20);

        // Draw health text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Health: " + health, 10, 70);

        // Draw player
        g.setColor(Color.WHITE);
        g.fillRect(playerX - PLAYER_SIZE / 2, playerY - PLAYER_SIZE / 2, PLAYER_SIZE, PLAYER_SIZE);
        g.drawLine(playerX, playerY, playerX, playerY + PLAYER_SIZE); // Body
        g.drawLine(playerX, playerY + PLAYER_SIZE / 2, playerX - PLAYER_SIZE / 2, playerY + PLAYER_SIZE * 3 / 4);
        g.drawLine(playerX, playerY + PLAYER_SIZE / 2, playerX + PLAYER_SIZE / 2, playerY + PLAYER_SIZE * 3 / 4);
        g.drawLine(playerX, playerY + PLAYER_SIZE, playerX - PLAYER_SIZE / 2, playerY + PLAYER_SIZE * 3 / 2);
        g.drawLine(playerX, playerY + PLAYER_SIZE, playerX + PLAYER_SIZE / 2, playerY + PLAYER_SIZE * 3 / 2);

        // Draw enemies
        g.setColor(Color.RED);
        for (Enemy enemy : enemies) {
            g.fillRect(enemy.x, enemy.y, ENEMY_SIZE, ENEMY_SIZE);
            g.drawLine(enemy.x, enemy.y, enemy.x, enemy.y + ENEMY_SIZE); // Body
            g.drawLine(enemy.x, enemy.y + ENEMY_SIZE / 2, enemy.x - ENEMY_SIZE / 2, enemy.y + ENEMY_SIZE * 3 / 4);
            g.drawLine(enemy.x, enemy.y + ENEMY_SIZE / 2, enemy.x + ENEMY_SIZE / 2, enemy.y + ENEMY_SIZE * 3 / 4);
            g.drawLine(enemy.x, enemy.y + ENEMY_SIZE, enemy.x - ENEMY_SIZE / 2, enemy.y + ENEMY_SIZE * 3 / 2);
            g.drawLine(enemy.x, enemy.y + ENEMY_SIZE, enemy.x + ENEMY_SIZE / 2, enemy.y + ENEMY_SIZE * 3 / 2);
        }

        // Draw bullets
        g.setColor(Color.YELLOW);
        for (Bullet bullet : bullets) {
            g.fillRect(bullet.getX() - BULLET_SIZE / 2, bullet.getY() - BULLET_SIZE / 2, BULLET_SIZE, BULLET_SIZE);
        }

        // Draw points text
        g.setColor(Color.WHITE);
        g.drawString("Points: " + points, 600, 70);

        // Draw stage text
        g.drawString("Stage: " + stage, WIDTH - 100, 70);
    }

    // Paint method
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        render(g);
    }

    // Player movement method
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

    // Enemy spawning method
    private void startEnemySpawning() {
        enemySpawnTimer = new Timer();
        enemySpawnTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (enemies.size() < MAX_ENEMIES) {
                    spawnEnemy();
                }
            }
        }, 0, ENEMY_SPAWN_INTERVAL);
    }

    // Spawn enemy
    private void spawnEnemy() {
        int enemyX = WIDTH;
        int enemyY = (int) (Math.random() * (HEIGHT - ENEMY_SIZE));
        enemies.add(new Enemy(enemyX, enemyY));
    }

    // Move enemy
    private void moveEnemies() {
        for (Enemy enemy : enemies) {
            enemy.move(playerX, playerY);
        }
    }

    // Enemy class
    private class Enemy {
        int x, y;

        public Enemy(int x, int y) {
            this.x = x;
            this.y = y;
        }

        // Move enemy towards the player
        public void move(int playerX, int playerY) {
            // Calculate direction towards the player
            int dx = playerX - x;
            int dy = playerY - y;
            double angle = Math.atan2(dy, dx);

            // Move enemy based on direction
            double speed = ENEMY_MOVE_SPEED;
            x += (int) (speed * Math.cos(angle));
            y += (int) (speed * Math.sin(angle));
        }
    }

    // Bullet class
    private class Bullet {
        private int x, y;

        public Bullet(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public void move() {
            // Move bullet horizontally
            x += BULLET_SPEED;
        }
    }

    // Main game loop
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            update();
            repaint();
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupted status
            }
        }
    }

    // Update method
    private void update() {
        movePlayer();
        moveEnemies();
        moveBullets();
        checkCollisions();
        updatePointsAndStage();
    }

    // Move bullets method
    private void moveBullets() {
        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            bullet.move();

            // Remove bullets that go out of bounds
            if (bullet.getX() < 0 || bullet.getX() > WIDTH || bullet.getY() < 0 || bullet.getY() > HEIGHT) {
                bullets.remove(i);
                i--; // Adjust index after removal
            }
        }
    }

    // Player shooting method
    private void shoot() {
        // Create a new bullet at the player's position
        bullets.add(new Bullet(playerX, playerY));
    }

    // Check collisions
    private void checkCollisions() {
        Rectangle playerRect = new Rectangle(playerX - PLAYER_SIZE / 2, playerY - PLAYER_SIZE / 2, PLAYER_SIZE, PLAYER_SIZE);
        for (Enemy enemy : enemies) {
            Rectangle enemyRect = new Rectangle(enemy.x - ENEMY_SIZE / 2, enemy.y - ENEMY_SIZE / 2, ENEMY_SIZE, ENEMY_SIZE);
            if (playerRect.intersects(enemyRect)) {
                health -= 10;
                if (health <= 0) {
                    // Game over
                    System.out.println("Game Over");
                    // Add your game over logic here
                }
                enemies.remove(enemy);
                break;
            }
        }

        // Check bullet-enemy collisions
        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            Rectangle bulletRect = new Rectangle(bullet.getX() - BULLET_SIZE / 2, bullet.getY() - BULLET_SIZE / 2, BULLET_SIZE, BULLET_SIZE);
            for (int j = 0; j < enemies.size(); j++) {
                Enemy enemy = enemies.get(j);
                Rectangle enemyRect = new Rectangle(enemy.x - ENEMY_SIZE / 2, enemy.y - ENEMY_SIZE / 2, ENEMY_SIZE, ENEMY_SIZE);
                if (bulletRect.intersects(enemyRect)) {
                    // Bullet hit enemy
                    bullets.remove(i);
                    enemies.remove(j);
                    points += 1; // Increase points
                    i--; // Adjust bullet index after removal
                    break;
                }
            }
        }
    }

    // Start the game
    private void startGame() {
        startEnemySpawning();
        startSpeedIncreaseTimer();
    }

    // Start timer to increase enemy speed periodically
    private void startSpeedIncreaseTimer() {
        speedIncreaseTimer = new Timer();
        speedIncreaseTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                increaseEnemySpeed();
            }
        }, SPEED_INCREASE_INTERVAL, SPEED_INCREASE_INTERVAL);
    }

    // Increase enemy speed
    private void increaseEnemySpeed() {
        if (points >= 20) {
            stage++;
            points -= 20;
            updateStageLabel();
            ENEMY_MOVE_SPEED += SPEED_INCREASE_AMOUNT;
        }
    }

    // Update points and stage
    private void updatePointsAndStage() {
        if (points >= 20) {
            stage++;
            points -= 20;
            updateStageLabel();
        }
    }

    // Update stage label
    private void updateStageLabel() {
        SwingUtilities.invokeLater(() -> {
            Container parent = this.getParent();
            if (parent.getComponentCount() >= 2) {
                JPanel stagePanel = (JPanel) parent.getComponent(1); // Accessing the stage panel
                JLabel stageLabel = (JLabel) stagePanel.getComponent(0); // Accessing the stage label
                stageLabel.setText("Stage: " + stage);
            }
        });
    }
}
