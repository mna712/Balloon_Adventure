import java.awt.Image;

public class Coin {
    int x, y, width, height;
    Image img;
    boolean collected = false;
    int points;

    Coin(Image img, int x, int y, int width, int height, int points) {
        this.img = img;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.points = points;
    }
}
