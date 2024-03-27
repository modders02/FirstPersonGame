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

public class ZombieMode extends JPanel implements Runnable, KeyListener {
    // Constants
    private static final long serialVersionUID = 1L;
    private static final int WIDTH = 1300;
    private static final int HEIGHT = 800;
    private static final int PLAYER_SIZE = 40;
    private static final int ENEMY_SIZE = 40;
    private static int ENEMY_MOVE_SPEED = 2;
    private static final int ENEMY_SPAWN_INTERVAL = 3000; // in milliseconds
    private static final int MAX_ENEMIES = 10;
    private static final int SPEED_INCREASE_INTERVAL = 10000; // in milliseconds
    private static final int SPEED_INCREASE_AMOUNT = 1;

    // Player variables
    private int playerX = WIDTH / 4;
    private int playerY = HEIGHT / 2;

    // Enemy list
    private List<Enemy> enemies = new ArrayList<>();

    // Timers
    private Timer enemySpawnTimer;
    private Timer speedIncreaseTimer;

    // Key press flags
    private boolean upPressed, downPressed, leftPressed, rightPressed;

    // Constructor
    public ZombieMode() {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
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
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    // Render method
    private void render(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setColor(Color.BLUE);
        g.drawRect(playerX - PLAYER_SIZE / 2, playerY - PLAYER_SIZE / 2, PLAYER_SIZE, PLAYER_SIZE);
        g.setColor(Color.WHITE);

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
            g.fillRect(enemy.x - ENEMY_SIZE / 2, enemy.y - ENEMY_SIZE / 2, ENEMY_SIZE, ENEMY_SIZE);
            g.drawLine(enemy.x, enemy.y, enemy.x, enemy.y + ENEMY_SIZE); // Body
            g.drawLine(enemy.x, enemy.y + ENEMY_SIZE / 2, enemy.x - ENEMY_SIZE / 2, enemy.y + ENEMY_SIZE * 3 / 4);
            g.drawLine(enemy.x, enemy.y + ENEMY_SIZE / 2, enemy.x + ENEMY_SIZE / 2, enemy.y + ENEMY_SIZE * 3 / 4);
            g.drawLine(enemy.x, enemy.y + ENEMY_SIZE, enemy.x - ENEMY_SIZE / 2, enemy.y + ENEMY_SIZE * 3 / 2);
            g.drawLine(enemy.x, enemy.y + ENEMY_SIZE, enemy.x + ENEMY_SIZE / 2, enemy.y + ENEMY_SIZE * 3 / 2);
        }
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
            ;
            int dy = playerY - y;
            double angle = Math.atan2(dy, dx);

            // Move enemy based on direction
            int speed = ENEMY_MOVE_SPEED;
            x += (int) (speed * Math.cos(angle));
            y += (int) (speed * Math.sin(angle));
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
        ENEMY_MOVE_SPEED += SPEED_INCREASE_AMOUNT;
    }

    // Entry point of the program
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Zombie Mode");
            ZombieMode game = new ZombieMode();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(game);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            new Thread(game).start();
        });
    }
}
