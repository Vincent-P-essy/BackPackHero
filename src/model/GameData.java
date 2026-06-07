package model;

import model.dungeon.Dungeon;
import model.dungeon.DungeonGenerator;
import model.dungeon.Room;
import model.dungeon.RoomContent;
import model.combat.Combat;
import model.combat.Enemy;
import model.equipment.Equipment;
import model.Cell.PlacedEquipment;
import java.util.List;
import java.util.Objects;

/**
 * Main game data model.
 * Manages the hero, dungeon, and game state.
 */
public class GameData {
    private final Hero hero;
    private final Dungeon dungeon;
    private Combat currentCombat;
    private GameState state;
    private final HallOfFame hallOfFame;

    /**
     * Game states.
     */
    public enum GameState {
        EXPLORING,     // Exploring the dungeon
        COMBAT,        // In combat
        TREASURE,      // Looting treasure
        MERCHANT,      // At merchant
        HEALER,        // At healer
        VICTORY,       // Won the game
        DEFEAT         // Hero died
    }

    /**
     * Creates a new game.
     */
    public GameData() {
        this.hero = new Hero();
        // Use Phase 3 procedural dungeon generation
        this.dungeon = DungeonGenerator.generateProceduralDungeon(hero, 3);
        this.currentCombat = null;
        this.state = GameState.EXPLORING;
        this.hallOfFame = new HallOfFame();

        // Give hero starting equipment
        initializeHeroEquipment();
    }

    /**
     * Initializes the hero's starting equipment.
     */
    private void initializeHeroEquipment() {
        // Give hero some starting equipment
        // WoodenSword (1x3) at (0,0) occupies columns 0,1,2
        hero.getBackpack().placeEquipment(new model.equipment.WoodenSword(), 0, 0, 0);
        // WoodenShield (2x2) at (0,3) occupies columns 3,4 and rows 0,1
        hero.getBackpack().placeEquipment(new model.equipment.WoodenShield(), 0, 3, 0);
    }

    /**
     * Gets the hero.
     * @return the hero
     */
    public Hero getHero() {
        return hero;
    }

    /**
     * Gets the dungeon.
     * @return the dungeon
     */
    public Dungeon getDungeon() {
        return dungeon;
    }

    /**
     * Gets the current combat.
     * @return current combat, or null if not in combat
     */
    public Combat getCurrentCombat() {
        return currentCombat;
    }

    /**
     * Gets the current game state.
     * @return the state
     */
    public GameState getState() {
        return state;
    }

    /**
     * Tries to move to a room.
     * @param room the target room
     * @return true if move successful
     */
    public boolean moveToRoom(Room room) {
        Objects.requireNonNull(room);

        if (state != GameState.EXPLORING) {
            return false;
        }

        if (!dungeon.getCurrentFloor().moveTo(room)) {
            return false;
        }

        // Handle room entry
        handleRoomEntry(room);
        return true;
    }

    /**
     * Handles entering a room.
     */
    private void handleRoomEntry(Room room) {
        RoomContent content = room.getContent();

        switch (room.getType()) {
            case ENEMY:
                if (content != null && content.hasEnemies()) {
                    startCombat(content.getEnemies());
                } else {
                    // Room already cleared, stay in exploring
                    state = GameState.EXPLORING;
                }
                break;

            case TREASURE:
                if (content != null && content.hasEquipment()) {
                    state = GameState.TREASURE;
                } else {
                    // No more treasure, stay in exploring
                    state = GameState.EXPLORING;
                }
                break;

            case MERCHANT:
                state = GameState.MERCHANT;
                break;

            case HEALER:
                state = GameState.HEALER;
                break;

            case EXIT:
                handleExit();
                break;

            default:
                state = GameState.EXPLORING;
                break;
        }
    }

    /**
     * Starts a combat encounter.
     */
    private void startCombat(List<Enemy> enemies) {
        currentCombat = new Combat(hero, enemies);
        state = GameState.COMBAT;
    }

    /**
     * Ends the current combat.
     */
    public void endCombat() {
        if (currentCombat == null) {
            return;
        }

        if (!hero.isAlive()) {
            state = GameState.DEFEAT;
        } else if (currentCombat.isHeroVictorious()) {
            // Clear enemies from the room content
            Room currentRoom = dungeon.getCurrentFloor().getCurrentRoom();
            RoomContent content = currentRoom.getContent();
            if (content != null) {
                content.clearEnemies();
            }
            
            currentCombat = null;
            state = GameState.EXPLORING;
        }
    }

    /**
     * Handles exit room.
     */
    private void handleExit() {
        if (dungeon.goToNextFloor()) {
            state = GameState.EXPLORING;
        } else {
            state = GameState.VICTORY;
        }
    }

    /**
     * Picks up equipment from treasure room.
     * @param equipment the equipment to pick up
     * @param row the row in backpack
     * @param col the column in backpack
     * @param rotation the rotation
     * @return true if pickup successful
     */
    public boolean pickupEquipment(Equipment equipment, int row, int col, int rotation) {
        Objects.requireNonNull(equipment);

        if (state != GameState.TREASURE) {
            return false;
        }

        Room currentRoom = dungeon.getCurrentFloor().getCurrentRoom();
        RoomContent content = currentRoom.getContent();

        if (content == null || !content.getEquipment().contains(equipment)) {
            return false;
        }

        // Try to place in backpack
        if (hero.getBackpack().placeEquipment(equipment, row, col, rotation)) {
            content.removeEquipment(equipment);

            // If no more equipment, return to exploring
            if (!content.hasEquipment()) {
                state = GameState.EXPLORING;
            }

            return true;
        }

        return false;
    }

    /**
     * Leaves treasure room without taking all items.
     */
    public void leaveTreasureRoom() {
        if (state == GameState.TREASURE) {
            state = GameState.EXPLORING;
        }
    }

    /**
     * Purchases healing from healer.
     * @return true if purchase successful
     */
    public boolean purchaseHealing() {
        if (state != GameState.HEALER) {
            return false;
        }

        Room currentRoom = dungeon.getCurrentFloor().getCurrentRoom();
        RoomContent content = currentRoom.getContent();

        if (content == null) {
            return false;
        }

        int cost = content.getHealingCost();
        int amount = content.getHealingAmount();

        if (hero.useGold(cost)) {
            hero.heal(amount);
            return true;
        }

        return false;
    }

    /**
     * Leaves healer room.
     */
    public void leaveHealerRoom() {
        if (state == GameState.HEALER) {
            state = GameState.EXPLORING;
        }
    }

    /**
     * Buys equipment from merchant.
     * @param equipment the equipment to buy
     * @param row the row in backpack
     * @param col the column in backpack
     * @param rotation the rotation
     * @return true if purchase successful
     */
    public boolean buyEquipment(Equipment equipment, int row, int col, int rotation) {
        Objects.requireNonNull(equipment);

        if (state != GameState.MERCHANT) {
            return false;
        }

        int price = equipment.getBuyPrice();

        if (hero.useGold(price)) {
            if (hero.getBackpack().placeEquipment(equipment, row, col, rotation)) {
                return true;
            } else {
                // Refund if couldn't place
                hero.addGold(price);
            }
        }

        return false;
    }

    /**
     * Sells equipment to merchant.
     * @param placedEquipment the equipment to sell
     * @return true if sale successful
     */
    public boolean sellEquipment(PlacedEquipment placedEquipment) {
        Objects.requireNonNull(placedEquipment);

        if (state != GameState.MERCHANT) {
            return false;
        }

        Equipment eq = placedEquipment.equipment();
        int price = eq.getSellPrice();

        if (hero.getBackpack().removeEquipment(placedEquipment)) {
            hero.addGold(price);
            return true;
        }

        return false;
    }

    /**
     * Leaves merchant room.
     */
    public void leaveMerchantRoom() {
        if (state == GameState.MERCHANT) {
            state = GameState.EXPLORING;
        }
    }

    /**
     * Gets the Hall of Fame.
     * @return the hall of fame
     */
    public HallOfFame getHallOfFame() {
        return hallOfFame;
    }

    /**
     * Records the player's score when the game ends.
     * @param playerName the player's name
     */
    public void recordScore(String playerName) {
        if (state != GameState.VICTORY && state != GameState.DEFEAT) {
            return;
        }

        boolean victory = (state == GameState.VICTORY);
        Score score = new Score(playerName, hero, victory);
        hallOfFame.addScore(score);
    }

    /**
     * Checks if the game has ended.
     * @return true if the game is over (victory or defeat)
     */
    public boolean isGameOver() {
        return state == GameState.VICTORY || state == GameState.DEFEAT;
    }
}
