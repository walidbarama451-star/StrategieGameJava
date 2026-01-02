package view;

import controller.GameController;
import java.awt.*;
import javax.swing.*;
import model.*;

public class InfoPanel extends JPanel {
    private GameController controller;
    private JTextArea eventArea;
    private JLabel resourceLabel;
    private JLabel unitLabel;
    private JLabel buildingLabel;
    private JLabel statusLabel;

    public InfoPanel(GameController controller) {
        this.controller = controller;
        setPreferredSize(new Dimension(250, 450));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Information"));

        JPanel resourcePanel = new JPanel();
        resourcePanel.setLayout(new BoxLayout(resourcePanel, BoxLayout.Y_AXIS));
        resourceLabel = new JLabel();
        resourcePanel.add(new JLabel("Ressources:"));
        resourcePanel.add(resourceLabel);

        JPanel unitPanel = new JPanel();
        unitPanel.setLayout(new BoxLayout(unitPanel, BoxLayout.Y_AXIS));
        unitLabel = new JLabel();
        unitPanel.add(new JLabel("Unités:"));
        unitPanel.add(unitLabel);

        JPanel buildingPanel = new JPanel();
        buildingPanel.setLayout(new BoxLayout(buildingPanel, BoxLayout.Y_AXIS));
        buildingLabel = new JLabel();
        buildingPanel.add(new JLabel("Bâtiments:"));
        buildingPanel.add(buildingLabel);

        statusLabel = new JLabel("En cours");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));

        eventArea = new JTextArea(15, 20);
        eventArea.setEditable(false);
        eventArea.setFont(new Font("Courier", Font.PLAIN, 11));
        JScrollPane scrollPane = new JScrollPane(eventArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Événements"));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(statusLabel);
        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(resourcePanel);
        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(unitPanel);
        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(buildingPanel);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void update() {
        Player player = controller.getHumanPlayer();

        ResourceManager resources = player.getResources();
        StringBuilder resText = new StringBuilder("<html>");
        resText.append("Or: ").append(resources.getResource(ResourceType.GOLD)).append("<br>");
        resText.append("Bois: ").append(resources.getResource(ResourceType.WOOD)).append("<br>");
        resText.append("Pierre: ").append(resources.getResource(ResourceType.STONE)).append("<br>");
        resText.append("Nourriture: ").append(resources.getResource(ResourceType.FOOD)).append("</html>");
        resourceLabel.setText(resText.toString());

        int aliveUnits = (int) player.getUnits().stream().filter(Unit::isAlive).count();
        unitLabel.setText("Vivantes: " + aliveUnits + " / " + player.getUnits().size());

        int completedBuildings = (int) player.getBuildings().stream()
                .filter(Building::isCompleted).count();
        buildingLabel.setText("Complétés: " + completedBuildings + " / " + player.getBuildings().size());

        if (controller.isGameOver()) {
            statusLabel.setText("Statut: " + controller.getGameStatus());
            statusLabel.setForeground(Color.RED);
        } else {
            statusLabel.setText(
                    "Statut: " + controller.getGameStatus() + " | Temps: " + controller.getRemainingTime() + "s");
            statusLabel.setForeground(Color.BLACK);
        }

        eventArea.setText("");
        for (String event : controller.getEventLog()) {
            eventArea.append(event + "\n");
        }
        eventArea.setCaretPosition(eventArea.getDocument().getLength());
    }
}
