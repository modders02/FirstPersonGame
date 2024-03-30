package MatigasnaTubig;

import javax.swing.*;
import java.awt.*;

public class GameOver extends JPanel {
    private static final int WIDTH = 1300;
    private static final int HEIGHT = 800;

    public GameOver() {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        g.drawString("GAME OVER", WIDTH / 2 - 150, HEIGHT / 2 - 50);
        g.setFont(new Font("Arial", Font.PLAIN, 30));
        g.drawString("Your base has been destroyed!", WIDTH / 2 - 220, HEIGHT / 2 + 50);
        // You can add more messages or customize the appearance further
    }
}
