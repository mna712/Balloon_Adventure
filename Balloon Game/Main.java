import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Balloon Adventure");
        GameLogic gamePanel = new GameLogic();

        frame.add(gamePanel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

        gamePanel.addKeyListener(gamePanel);
        gamePanel.addMouseListener(gamePanel);
        gamePanel.addMouseMotionListener(gamePanel);
    }
}