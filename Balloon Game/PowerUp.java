import java.awt.Image;

class PowerUp {
    Image img;
    int x, y, width, height;
    String type;

    public PowerUp(Image img, int x, int y, int width, int height, String type) {
        this.img = img;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;
    }
}
