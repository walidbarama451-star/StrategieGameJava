package view;

import controller.GameController;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GamePanel extends JPanel {
    private GameController controller;
    private int tileSize = 30;
    private Unit selectedUnit = null;
    private int selectedBuildingX = -1, selectedBuildingY = -1;
    private String buildingToPlace = null;
    private boolean attackMode = false;

    public GamePanel(GameController controller) {
        this.controller = controller;
        setPreferredSize(new Dimension(600, 450));
        setBackground(Color.BLACK);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());
            }
        });
    }

    public Unit getSelectedUnit() {
        return selectedUnit;
    }

    private boolean showRange = false;

    public void toggleShowRange() {
        this.showRange = !this.showRange;
    }

    public void setAttackMode(boolean mode) {
        this.attackMode = mode;
        if (mode) {
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        } else {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void handleMouseClick(int x, int y) {
        int mapX = x / tileSize;
        int mapY = y / tileSize;

        Map gameMap = controller.getMap();
        Tile tile = gameMap.getTile(mapX, mapY);

        if (tile == null)
            return;

        // Handle Attack Mode
        if (attackMode && selectedUnit != null) {
            if (tile.getUnit() != null && tile.getUnit().getOwner() != controller.getHumanPlayer()) {
                controller.attackWithUnit(selectedUnit, tile.getUnit());
                setAttackMode(false);
                selectedUnit = null;
            } else if (tile.getBuilding() != null && tile.getBuilding().getOwner() != controller.getHumanPlayer()) {
                controller.attackBuilding(selectedUnit, tile.getBuilding());
                setAttackMode(false);
                selectedUnit = null;
            } else {
                setAttackMode(false);
                return;
            }
            repaint();
            return;
        }

        if (buildingToPlace != null) {
            Building building = createBuildingFromString(buildingToPlace);
            if (building != null) {
                if (controller.buildBuilding(building, mapX, mapY)) {
                    buildingToPlace = null;
                    repaint();
                }
            }
            return;
        }

        if (tile.getUnit() != null && tile.getUnit().getOwner() == controller.getHumanPlayer()) {
            selectedUnit = tile.getUnit();
            selectedBuildingX = -1;
            selectedBuildingY = -1;
            repaint();
            return;
        }

        if (tile.getBuilding() != null && tile.getBuilding().getOwner() == controller.getHumanPlayer()) {
            selectedUnit = null;
            selectedBuildingX = mapX;
            selectedBuildingY = mapY;
            repaint();
            return;
        }

        if (selectedUnit != null && selectedUnit.isAlive()) {
            if (tile.getUnit() != null && tile.getUnit().getOwner() != controller.getHumanPlayer()) {
                controller.attackWithUnit(selectedUnit, tile.getUnit());
                selectedUnit = null;
            } else if (tile.getBuilding() != null && tile.getBuilding().getOwner() != controller.getHumanPlayer()) {
                controller.attackBuilding(selectedUnit, tile.getBuilding());
                selectedUnit = null;
            } else {
                controller.moveUnit(selectedUnit, mapX, mapY);
                selectedUnit = null;
            }
            repaint();
            return;
        }

        if (selectedBuildingX >= 0 && selectedBuildingY >= 0) {
            Building building = gameMap.getTile(selectedBuildingX, selectedBuildingY).getBuilding();
            if (building instanceof model.buildings.TrainingCamp && building.isCompleted()) {
                Unit unit = new model.units.Soldier();
                if (controller.trainUnit(unit, mapX, mapY)) {
                    selectedBuildingX = -1;
                    selectedBuildingY = -1;
                    repaint();
                }
            }
        }
    }

    private Building createBuildingFromString(String buildingType) {
        switch (buildingType) {
            case "CommandCenter":
                return new model.buildings.CommandCenter();
            case "TrainingCamp":
                return new model.buildings.TrainingCamp();
            case "Mine":
                return new model.buildings.Mine();
            case "Farm":
                return new model.buildings.Farm();
            case "Sawmill":
                return new model.buildings.Sawmill();
            case "Quarry":
                return new model.buildings.Quarry();
            case "Wall":
                return new model.buildings.Wall();
            case "Tower":
                return new model.buildings.Tower();
            default:
                return null;
        }
    }

    public void setBuildingToPlace(String buildingType) {
        this.buildingToPlace = buildingType;
        selectedUnit = null;
        selectedBuildingX = -1;
        selectedBuildingY = -1;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Map gameMap = controller.getMap();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int y = 0; y < gameMap.getHeight(); y++) {
            for (int x = 0; x < gameMap.getWidth(); x++) {
                Tile tile = gameMap.getTile(x, y);
                if (tile == null)
                    continue;

                int pixelX = x * tileSize;
                int pixelY = y * tileSize;
                Color tileColor = getTileColor(tile.getType());
                g2d.setColor(tileColor);
                g2d.fillRect(pixelX, pixelY, tileSize, tileSize);

                if (tile.getOwner() != null) {
                    if (tile.getOwner() == controller.getHumanPlayer()) {
                        g2d.setColor(Color.BLUE);
                    } else {
                        g2d.setColor(Color.RED);
                    }
                    g2d.drawRect(pixelX, pixelY, tileSize - 1, tileSize - 1);
                }

                if (tile.getBuilding() != null) {
                    Building building = tile.getBuilding();
                    Color buildingColor = building.getOwner() == controller.getHumanPlayer() ? Color.GREEN : Color.RED;
                    g2d.setColor(buildingColor);
                    if (building.isCompleted()) {
                        g2d.fillRect(pixelX + 2, pixelY + 2, tileSize - 4, tileSize - 4);
                    } else {
                        g2d.drawRect(pixelX + 2, pixelY + 2, tileSize - 4, tileSize - 4);
                    }
                }

                if (tile.getUnit() != null) {
                    Unit unit = tile.getUnit();
                    Color unitColor = unit.getOwner() == controller.getHumanPlayer() ? Color.CYAN : Color.MAGENTA;
                    g2d.setColor(unitColor);
                    int centerX = pixelX + tileSize / 2;
                    int centerY = pixelY + tileSize / 2;
                    g2d.fillOval(centerX - 5, centerY - 5, 10, 10);

                    if (!unit.isAlive())
                        continue;
                    double healthPercent = (double) unit.getCurrentHealth() / unit.getMaxHealth();
                    g2d.setColor(Color.RED);
                    g2d.fillRect(pixelX, pixelY - 3, tileSize, 2);
                    g2d.setColor(Color.GREEN);
                    g2d.fillRect(pixelX, pixelY - 3, (int) (tileSize * healthPercent), 2);
                }

                if (selectedUnit != null && tile.getUnit() == selectedUnit) {
                    g2d.setColor(Color.YELLOW);
                    g2d.drawRect(pixelX - 1, pixelY - 1, tileSize + 1, tileSize + 1);

                    if (showRange) {
                        g2d.setColor(new Color(255, 255, 0, 100)); // Semi-transparent yellow
                        int range = selectedUnit.getRange();
                        int rangePixels = (range * 2 + 1) * tileSize;
                        // Draw approximate range circle/square
                        g2d.drawRect(pixelX - range * tileSize, pixelY - range * tileSize, rangePixels, rangePixels);
                    }
                }

                if (x == selectedBuildingX && y == selectedBuildingY) {
                    g2d.setColor(Color.YELLOW);
                    g2d.drawRect(pixelX - 1, pixelY - 1, tileSize + 1, tileSize + 1);
                }
            }
        }

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        for (int x = 0; x < Math.min(10, gameMap.getWidth()); x++) {
            g2d.drawString(String.valueOf(x), x * tileSize + 2, 10);
        }
        for (int y = 0; y < Math.min(10, gameMap.getHeight()); y++) {
            g2d.drawString(String.valueOf(y), 2, y * tileSize + 12);
        }
    }

    private Color getTileColor(TileType type) {
        switch (type) {
            case GRASS:
                return new Color(34, 139, 34); // Forest green
            case WATER:
                return new Color(0, 119, 190); // Blue
            case MOUNTAIN:
                return new Color(139, 137, 137); // Gray
            case FOREST:
                return new Color(0, 100, 0); // Dark green
            default:
                return Color.GRAY;
        }
    }
}
