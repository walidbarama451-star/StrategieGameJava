package controller;

import java.util.ArrayList;
import java.util.List;
import model.*;
import model.buildings.CommandCenter;

public class GameController {
    private Map gameMap;
    private Player humanPlayer;
    private AIPlayer aiPlayer;
    private Player currentPlayer;
    private boolean gameOver;
    private String gameStatus;
    private List<String> eventLog;
    private javax.swing.Timer turnTimer;
    private int remainingTime;
    private static final int TURN_DURATION = 30;

    public GameController() {
        this.gameMap = new Map(20, 15);
        this.humanPlayer = new Player("Joueur", true);
        this.aiPlayer = new AIPlayer("IA");
        this.currentPlayer = humanPlayer;
        this.gameOver = false;
        this.gameStatus = "En cours";
        this.eventLog = new ArrayList<>();

        initializeGame();
    }

    private void initializeGame() {
        Tile humanStart = gameMap.getTile(1, 1);
        if (humanStart != null) {
            CommandCenter humanCC = new CommandCenter();
            humanCC.setOwner(humanPlayer);
            humanCC.setPosition(1, 1);
            humanCC.setCompleted(true);
            humanStart.setBuilding(humanCC);
            humanStart.setOwner(humanPlayer);
            humanPlayer.addBuilding(humanCC);
        }
        Tile aiStart = gameMap.getTile(gameMap.getWidth() - 2, gameMap.getHeight() - 2);
        if (aiStart != null) {
            CommandCenter aiCC = new CommandCenter();
            aiCC.setOwner(aiPlayer);
            aiCC.setPosition(aiStart.getX(), aiStart.getY());
            aiCC.setCompleted(true);
            aiStart.setBuilding(aiCC);
            aiStart.setOwner(aiPlayer);
            aiPlayer.addBuilding(aiCC);
        }

        addEvent("Partie démarrée! Votre centre de commandement est placé.");
        startTurnTimer();
    }

    private void startTurnTimer() {
        if (turnTimer != null) {
            turnTimer.stop();
        }
        remainingTime = TURN_DURATION;
        turnTimer = new javax.swing.Timer(1000, e -> {
            remainingTime--;
            if (remainingTime <= 0) {
                endHumanTurn();
            }
        });
        turnTimer.start();
    }

    private void stopTurnTimer() {
        if (turnTimer != null) {
            turnTimer.stop();
        }
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public Map getMap() {
        return gameMap;
    }

    public Player getHumanPlayer() {
        return humanPlayer;
    }

    public AIPlayer getAIPlayer() {
        return aiPlayer;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public String getGameStatus() {
        return gameStatus;
    }

    public List<String> getEventLog() {
        return new ArrayList<>(eventLog);
    }

    public void addEvent(String event) {
        eventLog.add(event);
        if (eventLog.size() > 50) {
            eventLog.remove(0);
        }
    }

    public void endHumanTurn() {
        if (gameOver)
            return;

        stopTurnTimer();

        for (Building building : humanPlayer.getBuildings()) {
            if (building.isCompleted()) {
                building.performAction(this);
            }
        }
        if (aiPlayer.isDefeated()) {
            gameOver = true;
            gameStatus = "Victoire!";
            addEvent("Vous avez gagné!");
            return;
        }
        currentPlayer = aiPlayer;
        for (Unit unit : aiPlayer.getUnits()) {
            unit.setDefending(false);
        }
        aiPlayer.playTurn(this);
        if (humanPlayer.isDefeated()) {
            gameOver = true;
            gameStatus = "Défaite!";
            addEvent("Vous avez perdu!");
            return;
        }
        currentPlayer = humanPlayer;

        // Passive income per round
        humanPlayer.getResources().addResource(ResourceType.WOOD, 20);
        humanPlayer.getResources().addResource(ResourceType.STONE, 20);
        humanPlayer.getResources().addResource(ResourceType.FOOD, 20);

        for (Unit unit : humanPlayer.getUnits()) {
            unit.setDefending(false);
        }
        addEvent("Tour de l'IA terminé.");
        startTurnTimer();
    }

    public boolean buildBuilding(Building building, int x, int y) {
        if (gameOver || currentPlayer != humanPlayer) {
            return false;
        }
        Tile tile = gameMap.getTile(x, y);
        if (tile == null || !tile.getType().isWalkable() || !tile.isEmpty()) {
            addEvent("Impossible de construire ici.");
            return false;
        }

        if (!humanPlayer.getResources().canAfford(building.getCost())) {
            addEvent("Ressources insuffisantes.");
            return false;
        }
        boolean nearTerritory = false;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                Tile nearby = gameMap.getTile(x + dx, y + dy);
                if (nearby != null && nearby.getOwner() == humanPlayer) {
                    nearTerritory = true;
                    break;
                }
            }
        }

        if (!nearTerritory && !humanPlayer.hasCommandCenter()) {
            nearTerritory = true;
        }

        if (!nearTerritory) {
            addEvent("Doit construire près de votre territoire.");
            return false;
        }

        if (humanPlayer.getResources().payCost(building.getCost())) {
            building.setOwner(humanPlayer);
            building.setPosition(x, y);
            tile.setBuilding(building);
            tile.setOwner(humanPlayer);
            humanPlayer.addBuilding(building);
            addEvent(building.getName() + " en construction à (" + x + ", " + y + ").");
            return true;
        }

        return false;
    }

    public boolean trainUnit(Unit unit, int x, int y) {
        if (gameOver || currentPlayer != humanPlayer) {
            return false;
        }
        boolean hasTrainingCamp = humanPlayer.getBuildings().stream()
                .anyMatch(b -> b instanceof model.buildings.TrainingCamp && b.isCompleted());

        if (!hasTrainingCamp) {
            addEvent("Vous devez construire un Camp d'Entraînement d'abord.");
            return false;
        }

        Tile tile = gameMap.getTile(x, y);
        if (tile == null || !tile.isWalkable() || !tile.isEmpty()) {
            addEvent("Position invalide pour placer l'unité.");
            return false;
        }
        boolean nearTrainingCamp = false;
        for (Building building : humanPlayer.getBuildings()) {
            if (building instanceof model.buildings.TrainingCamp && building.isCompleted()) {
                int dx = Math.abs(building.getX() - x);
                int dy = Math.abs(building.getY() - y);
                if (Math.max(dx, dy) <= 2) {
                    nearTrainingCamp = true;
                    break;
                }
            }
        }

        if (!nearTrainingCamp) {
            addEvent("L'unité doit être entraînée près d'un Camp d'Entraînement.");
            return false;
        }

        if (!humanPlayer.getResources().canAfford(unit.getCost())) {
            addEvent("Ressources insuffisantes.");
            return false;
        }

        if (humanPlayer.getResources().payCost(unit.getCost())) {
            unit.setOwner(humanPlayer);
            unit.setPosition(x, y);
            tile.setUnit(unit);
            humanPlayer.addUnit(unit);
            addEvent(unit.getName() + " entraîné à (" + x + ", " + y + ").");
            return true;
        }

        return false;
    }

    public boolean moveUnit(Unit unit, int newX, int newY) {
        if (gameOver || currentPlayer != humanPlayer || unit.getOwner() != humanPlayer) {
            return false;
        }

        if (!unit.isAlive()) {
            return false;
        }

        Tile targetTile = gameMap.getTile(newX, newY);
        if (targetTile == null || !targetTile.isWalkable()) {
            return false;
        }

        if (!unit.canReach(newX, newY)) {
            return false;
        }

        Tile currentTile = gameMap.getTile(unit.getX(), unit.getY());
        if (currentTile != null) {
            currentTile.setUnit(null);
        }

        unit.setPosition(newX, newY);
        targetTile.setUnit(unit);
        targetTile.setOwner(humanPlayer);
        return true;
    }

    public boolean attackWithUnit(Unit attacker, Unit target) {
        if (gameOver || currentPlayer != humanPlayer || attacker.getOwner() != humanPlayer) {
            return false;
        }

        if (!attacker.isAlive() || !target.isAlive()) {
            return false;
        }

        if (!attacker.isInRange(target)) {
            addEvent("Cible hors de portée.");
            return false;
        }

        int damage = attacker.attack(target);
        addEvent(attacker.getName() + " attaque " + target.getName() + " pour " + damage + " dégâts.");

        if (!target.isAlive()) {
            Tile tile = gameMap.getTile(target.getX(), target.getY());
            if (tile != null) {
                tile.setUnit(null);
            }
            target.getOwner().removeUnit(target);
            addEvent(target.getName() + " détruit!");
        }

        return true;
    }

    public boolean defendUnit(Unit unit) {
        if (gameOver || currentPlayer != humanPlayer || unit.getOwner() != humanPlayer) {
            return false;
        }

        if (!unit.isAlive()) {
            return false;
        }

        unit.setDefending(true);
        addEvent(unit.getName() + " prend une posture défensive.");
        return true;
    }

    public boolean attackBuilding(Unit attacker, Building target) {
        if (gameOver || currentPlayer != humanPlayer || attacker.getOwner() != humanPlayer) {
            return false;
        }

        if (!attacker.isAlive() || target.isDestroyed()) {
            return false;
        }

        int dx = Math.abs(target.getX() - attacker.getX());
        int dy = Math.abs(target.getY() - attacker.getY());
        if (Math.max(dx, dy) > attacker.getRange()) {
            addEvent("Cible hors de portée.");
            return false;
        }

        int damage = attacker.getAttack();
        target.takeDamage(damage);
        addEvent(attacker.getName() + " attaque " + target.getName() + " pour " + damage + " dégâts.");

        if (target.isDestroyed()) {
            Tile tile = gameMap.getTile(target.getX(), target.getY());
            if (tile != null) {
                tile.setBuilding(null);
                tile.setOwner(null);
            }
            target.getOwner().removeBuilding(target);
            addEvent(target.getName() + " détruit!");
        }

        return true;
    }
}