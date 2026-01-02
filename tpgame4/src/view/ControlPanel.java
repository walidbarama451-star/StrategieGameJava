package view;

import controller.GameController;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {
    private GameController controller;
    private GamePanel gamePanel;
    private JButton endTurnButton;

    public ControlPanel(GameController controller, GamePanel gamePanel, InfoPanel infoPanel) {
        this.controller = controller;
        this.gamePanel = gamePanel;

        setLayout(new FlowLayout());
        setBorder(BorderFactory.createTitledBorder("Contrôles"));

        JButton commandCenterBtn = new JButton("Centre de Commandement");
        commandCenterBtn.addActionListener(e -> gamePanel.setBuildingToPlace("CommandCenter"));
        add(commandCenterBtn);

        JButton trainingCampBtn = new JButton("Camp d'Entraînement");
        trainingCampBtn.addActionListener(e -> gamePanel.setBuildingToPlace("TrainingCamp"));
        add(trainingCampBtn);

        JButton mineBtn = new JButton("Mine");
        mineBtn.addActionListener(e -> gamePanel.setBuildingToPlace("Mine"));
        add(mineBtn);

        JButton farmBtn = new JButton("Ferme");
        farmBtn.addActionListener(e -> gamePanel.setBuildingToPlace("Farm"));
        add(farmBtn);

        JButton sawmillBtn = new JButton("Scierie");
        sawmillBtn.addActionListener(e -> gamePanel.setBuildingToPlace("Sawmill"));
        add(sawmillBtn);

        JButton quarryBtn = new JButton("Carrière");
        quarryBtn.addActionListener(e -> gamePanel.setBuildingToPlace("Quarry"));
        add(quarryBtn);

        JButton wallBtn = new JButton("Mur");
        wallBtn.addActionListener(e -> gamePanel.setBuildingToPlace("Wall"));
        add(wallBtn);

        JButton towerBtn = new JButton("Tour");
        towerBtn.addActionListener(e -> gamePanel.setBuildingToPlace("Tower"));
        add(towerBtn);

        JButton soldierBtn = new JButton("Entraîner Soldat");
        soldierBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Cliquez sur un Camp d'Entraînement, puis sur la carte pour entraîner un soldat.");
        });
        add(soldierBtn);
        JButton attackBtn = new JButton("Attaquer");
        attackBtn.addActionListener(e -> {
            if (gamePanel.getSelectedUnit() != null) {
                gamePanel.setAttackMode(true);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Sélectionnez d'abord une unité pour attaquer.");
            }
        });
        add(attackBtn);

        JButton defendBtn = new JButton("Défense");
        defendBtn.addActionListener(e -> {
            if (gamePanel.getSelectedUnit() != null) {
                if (controller.defendUnit(gamePanel.getSelectedUnit())) {
                    gamePanel.repaint();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Sélectionnez une unité.");
            }
        });
        add(defendBtn);

        JButton rangeBtn = new JButton("Portée");
        rangeBtn.addActionListener(e -> {
            gamePanel.toggleShowRange();
            gamePanel.repaint();
        });
        add(rangeBtn);

        JButton costBtn = new JButton("Coût");
        costBtn.addActionListener(e -> {
            if (gamePanel.getSelectedUnit() != null) {
                JOptionPane.showMessageDialog(this,
                        "Coût: " + gamePanel.getSelectedUnit().getCost());
            } else {
                JOptionPane.showMessageDialog(this, "Sélectionnez une unité.");
            }
        });
        add(costBtn);

        endTurnButton = new JButton("Terminer le tour");
        endTurnButton.addActionListener(e -> {
            controller.endHumanTurn();
            gamePanel.repaint();
            infoPanel.update();
        });
        add(endTurnButton);

        JLabel instructions = new JLabel(
                "<html><small>Instructions: Cliquez sur une case pour sélectionner/déplacer. " +
                        "Sélectionnez un bâtiment puis cliquez pour construire/entraîner.</small></html>");
        add(instructions);
    }

    public void update() {
        endTurnButton.setEnabled(!controller.isGameOver());
    }
}
