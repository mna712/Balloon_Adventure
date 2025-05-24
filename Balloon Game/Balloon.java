import java.awt.Image;

class Balloon {
    int x;
    int y;
    int width;
    int height;
    Image img;

    Balloon(int x, int y, int width, int height, Image img) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.img = img;
    }
}