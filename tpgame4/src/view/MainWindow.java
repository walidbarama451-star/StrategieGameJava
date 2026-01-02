package view;

import controller.GameController;
import java.awt.*;
import javax.swing.*;

public class MainWindow extends JFrame {
    private GameController controller;
    private GamePanel gamePanel;
    private InfoPanel infoPanel;
    private ControlPanel controlPanel;

    public MainWindow() {
        setTitle("Jeu de Stratégie");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        controller = new GameController();

        gamePanel = new GamePanel(controller);
        infoPanel = new InfoPanel(controller);
        controlPanel = new ControlPanel(controller, gamePanel, infoPanel);

        add(infoPanel, BorderLayout.EAST);
        add(gamePanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        Timer timer = new Timer(100, e -> {
            gamePanel.repaint();
            infoPanel.update();
            controlPanel.update();
        });
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MainWindow();
        });
    }
}
