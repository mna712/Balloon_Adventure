import java.awt.Image;

public class Bird {
    int x, y, width, height;
    Image img;
    boolean hit = false;

    Bird(Image img, int x, int y, int width, int height) {
        this.img = img;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

}
