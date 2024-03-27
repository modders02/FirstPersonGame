package MatigasnaTubig;

import javax.swing.*;

import MatigasnaTubig.MultiPlayer.Bullet;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SinglePlayer extends JPanel implements Runnable, KeyListener {
    // Constants
    private static final long serialVersionUID = 1L;
    private static final int WIDTH = 1300;
    private static final int HEIGHT = 800;
    private static final int PLAYER_SIZE = 40;
    private static final int ENEMY_SIZE = 40;
    private static int ENEMY_MOVE_SPEED = 2;
    private static final int ENEMY_SPAWN_INTERVAL = 3000; // in milliseconds
    private static final int MAX_ENEMIES = 10;
    private static final int BULLET_SIZE = 4;
    private static final int MAX_BULLETS = 5;
    private static final int BULLET_SPEED = 8;

    private long lastShotTime = 0;
    private static final long SHOT_DELAY = 200; 
    // Player variables
    private int playerX = WIDTH / 4;
    private int playerY = HEIGHT / 2;
    private int playerDirection = KeyEvent.VK_RIGHT; // Initial direction

    // Enemy list
    private List<Enemy> enemies = new ArrayList<>();

    // Bullets
    private List<Bullet> bullets = new ArrayList<>();

    // Timers
    private Timer enemySpawnTimer;

    // Key press flags
    private boolean upPressed, downPressed, leftPressed, rightPressed, spacePressed;

    // Constructor
    public SinglePlayer() {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        addKeyListener(this);
        setFocusable(true);
        startGame();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W) {
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

        // Draw bullets
        g.setColor(Color.YELLOW);
        for (Bullet bullet : bullets) {
            g.fillRect(bullet.x - BULLET_SIZE / 2, bullet.y - BULLET_SIZE / 2, BULLET_SIZE, BULLET_SIZE);
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
        int enemyX = WIDTH - ENEMY_SIZE; // Right side
        int enemyY = (int) (
                Math.random() * (HEIGHT - ENEMY_SIZE)); // Random Y position
                enemies.add(new Enemy(enemyX, enemyY));
            }

            // Start the game
            private void startGame() {
                startEnemySpawning();
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

            private void update() {
                movePlayer();
                moveEnemies();
                if (spacePressed) {
                    shoot();
                }
                moveBullets();
            }

            private void moveEnemies() {
                // List to hold enemies to be removed
                List<Enemy> enemiesToRemove = new ArrayList<>();

                // Move each enemy
                for (Iterator<Enemy> iterator = enemies.iterator(); iterator.hasNext();) {
                    Enemy enemy = iterator.next();
                    enemy.move();
                    // Wrap around to the right side if the enemy goes off the left edge
                    if (enemy.x + ENEMY_SIZE < 0) {
                        enemy.x = WIDTH; // Move the enemy to the right side
                    }
                    // Identify enemies that need to be removed
                    if (enemy.x < 0) {
                        enemiesToRemove.add(enemy);
                    }
                }

                // Remove the identified enemies
                enemies.removeAll(enemiesToRemove);
            }

            private void moveBullets() {
                Iterator<Bullet> iterator = bullets.iterator();
                while (iterator.hasNext()) {
                    Bullet bullet = iterator.next();
                    bullet.move();
                    if (bullet.x > WIDTH || bullet.x < 0 || bullet.y > HEIGHT || bullet.y < 0) {
                        iterator.remove(); // Remove bullets that go off the screen
                    }
                }
            }
            
            private void shoot() {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastShotTime >= SHOT_DELAY && bullets.size() < MAX_BULLETS) {
                    lastShotTime = currentTime;

                    // Calculate bullet speed based on player direction
                    int bulletXSpeed = 0;
                    int bulletYSpeed = 0;

                    // Determine bullet direction based on player's movement direction
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

                    // Adjust bullet speed based on simultaneous movement directions
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
           

            // Enemy class
            private class Enemy {
                int x, y;

                public Enemy(int x, int y) {
                    this.x = x;
                    this.y = y;
                }

                // Move enemy
                public void move() {
                    x -= ENEMY_MOVE_SPEED; // Move towards the left
                    if (x < 0) {
                        // Do not remove here
                    }
                }
            }

            // Bullet class
            private class Bullet {
                int x, y;
                int xSpeed, ySpeed;

                public Bullet(int x, int y, int xSpeed, int ySpeed) {
                    this.x = x;
                    this.y = y;
                    this.xSpeed = xSpeed;
                    this.ySpeed = ySpeed;
                }

                // Move bullet
                public void move() {
                    x += xSpeed;
                    y += ySpeed;
                }
            }
        }
   
