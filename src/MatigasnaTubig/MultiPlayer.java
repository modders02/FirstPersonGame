package MatigasnaTubig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MultiPlayer extends JFrame implements KeyListener {
	static final int WIDTH = 1300;
	static final int HEIGHT = 800;
	static final int PLAYER_SIZE = 40;
	static final int BULLET_SIZE = 4;
	static final int MAX_BULLETS = 200;
	static final int BULLET_LIFETIME = 3000;

	BufferedImage buffer;
	int playerX = WIDTH / 4;
	int playerY = HEIGHT / 2;
	int player2X = WIDTH * 3 / 4;
	int player2Y = HEIGHT / 2;
	boolean[] keys = new boolean[256];
	protected List<Bullet> bullets = new ArrayList<>();

	public MultiPlayer() {
		setTitle("Pixelated FPS Game");
		setSize(WIDTH, HEIGHT);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
		addKeyListener(this);

		buffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

		// MultiPlayer game loop
		new Thread(() -> {
			while (true) {
				update();
				render();
				try {
					Thread.sleep(16);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void respawnPlayer(int playerIndex) {
		if (playerIndex == 1) {
			playerX = WIDTH / 4;
			playerY = HEIGHT / 2;
		} else if (playerIndex == 2) {
			player2X = WIDTH * 3 / 4;
			player2Y = HEIGHT / 2;
		}
	}

	protected void update() {
		// Player movement
		if (keys[KeyEvent.VK_W])
			playerY -= 2;
		if (keys[KeyEvent.VK_S])
			playerY += 2;
		if (keys[KeyEvent.VK_A])
			playerX -= 2;
		if (keys[KeyEvent.VK_D])
			playerX += 2;

		if (keys[KeyEvent.VK_UP])
			player2Y -= 2;
		if (keys[KeyEvent.VK_DOWN])
			player2Y += 2;
		if (keys[KeyEvent.VK_LEFT])
			player2X -= 2;
		if (keys[KeyEvent.VK_RIGHT])
			player2X += 2;

		// Bullet movement
		Iterator<Bullet> iterator = bullets.iterator();
		while (iterator.hasNext()) {
			Bullet bullet = iterator.next();
			bullet.move();

			// Check collision with blue player
			if (!bullet.isPlayerBullet()) {
				if (checkCollision(playerX, playerY, bullet.x, bullet.y, BULLET_SIZE)) {
					player1Points -= 10;
					iterator.remove();

					if (player1Points <= 0) {
						player1Points = 200;
						respawnPlayer(1);
						JOptionPane.showMessageDialog(this, "Player 1 died!");
					}
				}
			} else {
				// Check collision with red player and exclude red player's bullets
				if (checkCollision(player2X, player2Y, bullet.x, bullet.y, BULLET_SIZE)
						&& !bullet.isRedPlayerBullet()) {
					player2Points -= 10;
					iterator.remove();

					if (player2Points <= 0) {
						player2Points = 200;
						respawnPlayer(2);
						JOptionPane.showMessageDialog(this, "Player 2 (Red Player) died!");
					}
				}
			}
		}
	}

	private boolean checkCollision(int x1, int y1, int x2, int y2, int size) {
		return (x1 - size / 2 < x2 + size / 1 && x1 + size / 2 > x2 - size / 1 && y1 - size / 2 < y2 + size / 1
				&& y1 + size / 2 > y2 - size / 1);
	}

	protected int player1Points = 200;
	protected int player2Points = 200;

	private void render() {
		if (buffer == null)
			return;
		Graphics g = buffer.getGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WIDTH, HEIGHT);

		g.setColor(Color.BLUE);
		g.drawRect(playerX - PLAYER_SIZE / 2, playerY - PLAYER_SIZE / 2, PLAYER_SIZE, PLAYER_SIZE);
		g.setColor(Color.WHITE);
		g.drawRect(player2X - PLAYER_SIZE / 2, player2Y - PLAYER_SIZE / 2, PLAYER_SIZE, PLAYER_SIZE);

		// Draw player
		g.setColor(Color.WHITE);
		g.fillRect(playerX - PLAYER_SIZE / 2, playerY - PLAYER_SIZE / 2, PLAYER_SIZE, PLAYER_SIZE);
		g.drawLine(playerX, playerY, playerX, playerY + PLAYER_SIZE); // Body
		g.drawLine(playerX, playerY + PLAYER_SIZE / 2, playerX - PLAYER_SIZE / 2, playerY + PLAYER_SIZE * 3 / 4);
		g.drawLine(playerX, playerY + PLAYER_SIZE / 2, playerX + PLAYER_SIZE / 2, playerY + PLAYER_SIZE * 3 / 4);
		g.drawLine(playerX, playerY + PLAYER_SIZE, playerX - PLAYER_SIZE / 2, playerY + PLAYER_SIZE * 3 / 2);
		g.drawLine(playerX, playerY + PLAYER_SIZE, playerX + PLAYER_SIZE / 2, playerY + PLAYER_SIZE * 3 / 2);

		g.setColor(Color.RED);
		g.fillRect(player2X - PLAYER_SIZE / 2, player2Y - PLAYER_SIZE / 2, PLAYER_SIZE, PLAYER_SIZE);
		g.drawLine(player2X, player2Y, player2X, player2Y + PLAYER_SIZE); // Body
		g.drawLine(player2X, player2Y + PLAYER_SIZE / 2, player2X - PLAYER_SIZE / 2, player2Y + PLAYER_SIZE * 3 / 4);
		g.drawLine(player2X, player2Y + PLAYER_SIZE / 2, player2X + PLAYER_SIZE / 2, player2Y + PLAYER_SIZE * 3 / 4);
		g.drawLine(player2X, player2Y + PLAYER_SIZE, player2X - PLAYER_SIZE / 2, player2Y + PLAYER_SIZE * 3 / 2);
		g.drawLine(player2X, player2Y + PLAYER_SIZE, player2X + PLAYER_SIZE / 2, player2Y + PLAYER_SIZE * 3 / 2);

		g.setColor(Color.YELLOW);
		for (Bullet bullet : bullets) {
			g.fillRect(bullet.x - BULLET_SIZE / 2, bullet.y - BULLET_SIZE / 2, BULLET_SIZE, BULLET_SIZE);
		}

		drawHealthBar(g, 50, 50, 200, Color.WHITE, player1Points);
		drawHealthBar(g, 850, 50, 200, Color.RED, player2Points);

		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", Font.BOLD, 20));
		g.drawString("" + player1Points, 50, 100);
		g.drawString("" + player2Points, 1210, 100);

		getGraphics().drawImage(buffer, 0, 0, null);
	}

	private void drawHealthBar(Graphics g, int x, int y, int maxHealth, Color color, int currentHealth) {
		g.setColor(color);
		g.fillRect(x, y, currentHealth * 2, 20);
		g.setColor(Color.WHITE);
		g.drawRect(x, y, maxHealth * 2, 20);
	}

	void shoot(int playerX, int playerY, int directionX, int directionY) {
		bullets.add(new Bullet(playerX, playerY, directionX, directionY, true, false));
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;

		// Shooting mechanism for the blue player
		if (e.getKeyCode() == KeyEvent.VK_SPACE && bullets.size() < MAX_BULLETS) {
			int directionX = 0, directionY = 0;

			if (keys[KeyEvent.VK_W])
				directionY = -1;
			if (keys[KeyEvent.VK_S])
				directionY = 1;
			if (keys[KeyEvent.VK_A])
				directionX = -1;
			if (keys[KeyEvent.VK_D])
				directionX = 1;
			shoot(playerX, playerY, directionX, directionY);
		}

		// Shooting mechanism for the red player
		if (e.getKeyCode() == KeyEvent.VK_SHIFT && bullets.size() < MAX_BULLETS) {
			int directionX2 = 0, directionY2 = 0;

			// Check the arrow keys for movement direction
			if (keys[KeyEvent.VK_UP])
				directionY2 = -1;
			if (keys[KeyEvent.VK_DOWN])
				directionY2 = 1;
			if (keys[KeyEvent.VK_LEFT])
				directionX2 = -1;
			if (keys[KeyEvent.VK_RIGHT])
				directionX2 = 1;

			// Create bullet for red player
			bullets.add(new Bullet(player2X, player2Y, directionX2, directionY2, false, true));
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	static class Bullet {
		int x, y, directionX, directionY;
		long timestamp;
		boolean playerBullet;
		private boolean redPlayerBullet;

		Bullet(int x, int y, int directionX, int directionY, boolean playerBullet, boolean redPlayerBullet) {
			this.redPlayerBullet = redPlayerBullet;
			this.x = x;
			this.y = y;
			this.directionX = directionX;
			this.directionY = directionY;
			this.timestamp = System.currentTimeMillis();
			this.playerBullet = playerBullet;
		}

		boolean isRedPlayerBullet() {
			return redPlayerBullet;
		}

		boolean isPlayerBullet() {
			return playerBullet;
		}

		void move() {
			x += 5 * directionX;
			y += 5 * directionY;
		}

		boolean isStationary() {
			return directionX == 0 && directionY == 0;
		}

		long getTimestamp() {
			return timestamp;
		}
	}
}