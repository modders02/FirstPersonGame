package MatigasnaTubig;

import java.awt.Rectangle;

public class Hitbox {
    private int x, y, width, height;

    public Hitbox(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public boolean intersects(Hitbox other) {
        return getBounds().intersects(other.getBounds());
    }
}
