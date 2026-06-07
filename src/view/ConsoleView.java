package view;

import model.GameData;
import model.Hero;
import model.dungeon.Floor;
import model.dungeon.Room;
import model.combat.Combat;
import model.combat.Enemy;
import java.util.List;

/**
 * Simple console view for testing (before implementing Zen GUI).
 */
public class ConsoleView implements GameView {

    @Override
    public void display(GameData gameData) {
        System.out.println("\n=== BACKPACK HERO ===");
        System.out.println();

        Hero hero = gameData.getHero();
        displayHeroStatus(hero);
        System.out.println();

        switch (gameData.getState()) {
            case EXPLORING:
                displayExploration(gameData);
                break;
            case COMBAT:
                displayCombat(gameData.getCurrentCombat());
                break;
            case TREASURE:
                displayTreasure(gameData);
                break;
            case MERCHANT:
                displayMerchant(gameData);
                break;
            case HEALER:
                displayHealer(gameData);
                break;
            case VICTORY:
                System.out.println("*** VICTORY! You escaped the dungeon! ***");
                break;
            case DEFEAT:
                System.out.println("*** DEFEAT! You have fallen... ***");
                break;
        }

        System.out.println("\n====================\n");
    }

    private void displayHeroStatus(Hero hero) {
        System.out.println("HP: " + hero.getCurrentHealthPoints() + "/" + hero.getMaxHealthPoints());
        System.out.println("Energy: " + hero.getEnergy() + "/" + hero.getMaxEnergy());
        System.out.println("Mana: " + hero.getMana());
        System.out.println("Gold: " + hero.getGold());
        System.out.println("Level: " + hero.getLevel() + " (XP: " + hero.getExperience() + ")");
        System.out.println("Protection: " + hero.getProtection());
    }

    private void displayExploration(GameData gameData) {
        Floor floor = gameData.getDungeon().getCurrentFloor();
        Room currentRoom = floor.getCurrentRoom();

        System.out.println("Floor " + floor.getFloorNumber());
        System.out.println("Current room: " + currentRoom.getType());
        System.out.println("Position: [" + currentRoom.getRow() + ", " + currentRoom.getCol() + "]");

        // Display simple map
        displayMap(floor);
    }

    private void displayMap(Floor floor) {
        System.out.println("\nMap:");
        Room current = floor.getCurrentRoom();

        for (int i = 0; i < floor.getRows(); i++) {
            for (int j = 0; j < floor.getCols(); j++) {
                Room room = floor.getRoom(i, j);
                if (room == null) {
                    System.out.print("  ");
                } else if (room == current) {
                    System.out.print("@ ");
                } else if (room.isVisited()) {
                    System.out.print(getRoomSymbol(room) + " ");
                } else {
                    System.out.print("? ");
                }
            }
            System.out.println();
        }
    }

    private char getRoomSymbol(Room room) {
        switch (room.getType()) {
            case CORRIDOR: return '.';
            case ENEMY: return 'E';
            case TREASURE: return 'T';
            case MERCHANT: return 'M';
            case HEALER: return 'H';
            case EXIT: return 'X';
            default: return '?';
        }
    }

    private void displayCombat(Combat combat) {
        System.out.println("*** COMBAT ***");

        System.out.println("\nEnemies:");
        List<Enemy> enemies = combat.getEnemies();
        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            System.out.println((i + 1) + ". " + enemy.getName() +
                " - HP: " + enemy.getCurrentHealthPoints() + "/" + enemy.getMaxHealthPoints() +
                " (Next action: " + enemy.getNextAction() + ")");
        }

        if (combat.isHeroTurn()) {
            System.out.println("\n>>> Your turn! <<<");
        } else {
            System.out.println("\n>>> Enemy turn... <<<");
        }
    }

    private void displayTreasure(GameData gameData) {
        System.out.println("*** TREASURE ROOM ***");
        Room room = gameData.getDungeon().getCurrentFloor().getCurrentRoom();

        if (room.getContent() != null) {
            System.out.println("\nAvailable items:");
            room.getContent().getEquipment().forEach(eq ->
                System.out.println("- " + eq.getName()));
        }
    }

    private void displayMerchant(GameData gameData) {
        System.out.println("*** MERCHANT ***");
        System.out.println("You can buy and sell equipment here.");
    }

    private void displayHealer(GameData gameData) {
        System.out.println("*** HEALER ***");
        Room room = gameData.getDungeon().getCurrentFloor().getCurrentRoom();

        if (room.getContent() != null) {
            System.out.println("Healing costs: " + room.getContent().getHealingCost() + " gold");
            System.out.println("Healing amount: " + room.getContent().getHealingAmount() + " HP");
        }
    }

    @Override
    public int getWidth() {
        return 800;
    }

    @Override
    public int getHeight() {
        return 600;
    }
}
