package controller;

import com.github.forax.zen.ApplicationContext;
import com.github.forax.zen.Event;
import com.github.forax.zen.KeyboardEvent;
import com.github.forax.zen.PointerEvent;
import model.GameData;
import model.GameData.GameState;
import model.dungeon.Floor;
import model.dungeon.Room;
import view.SimpleGameView;

public class SimpleGameController {
    private final GameData gameData;
    private final SimpleGameView view;
    private boolean running;
    private model.Cell.PlacedEquipment selectedEquipment; // Equipment selected for use/placement
    private model.equipment.Equipment pendingEquipment; // Equipment waiting to be placed in backpack

    public SimpleGameController(GameData gameData, SimpleGameView view) {
        this.gameData = gameData;
        this.view = view;
        this.running = true;
        this.selectedEquipment = null;
        this.pendingEquipment = null;
    }

    public void start() {
        ApplicationContext context = view.context();

        while (running && !gameData.isGameOver()) {
            Event event = context.pollOrWaitEvent(20);
            if (event != null) {
                handleEvent(event);
            }

            view.display(gameData);
        }

        // Game over - show final screen
        if (gameData.isGameOver()) {
            view.display(gameData);
            // Wait for Q to quit
            while (running) {
                Event event = context.pollOrWaitEvent(100);
                if (event instanceof KeyboardEvent ke) {
                    if (ke.action() == KeyboardEvent.Action.KEY_PRESSED && ke.key() == KeyboardEvent.Key.Q) {
                        running = false;
                    }
                }
            }
        }

        view.context().dispose();
    }

    private void handleEvent(Event event) {
        if (event instanceof KeyboardEvent ke) {
            if (ke.action() == KeyboardEvent.Action.KEY_PRESSED) {
                handleKeyPress(ke.key());
            }
        } else if (event instanceof PointerEvent pe) {
            if (pe.action() == PointerEvent.Action.POINTER_DOWN) {
                handleMouseClick(pe.location().x(), pe.location().y());
            }
        }
    }

    private void handleKeyPress(KeyboardEvent.Key key) {
        GameState state = gameData.getState();

        // Q always quits
        if (key == KeyboardEvent.Key.Q) {
            running = false;
            return;
        }

        switch (state) {
            case EXPLORING:
                handleExploringKeys(key);
                break;
            case COMBAT:
                handleCombatKeys(key);
                break;
            case TREASURE:
                handleTreasureKeys(key);
                break;
            case MERCHANT:
                handleMerchantKeys(key);
                break;
            case HEALER:
                handleHealerKeys(key);
                break;
            default:
                break;
        }
    }

    private void handleExploringKeys(KeyboardEvent.Key key) {
        Floor floor = gameData.getDungeon().getCurrentFloor();
        Room currentRoom = floor.getCurrentRoom();
        int row = currentRoom.getRow();
        int col = currentRoom.getCol();

        Room targetRoom = null;

        switch (key) {
            case UP:
                targetRoom = floor.getRoom(row - 1, col);
                break;
            case DOWN:
                targetRoom = floor.getRoom(row + 1, col);
                break;
            case LEFT:
                targetRoom = floor.getRoom(row, col - 1);
                break;
            case RIGHT:
                targetRoom = floor.getRoom(row, col + 1);
                break;
            default:
                break;
        }

        if (targetRoom != null) {
            gameData.moveToRoom(targetRoom);
        }
    }

    private void handleCombatKeys(KeyboardEvent.Key key) {
        if (gameData.getCurrentCombat() == null) {
            return;
        }

        switch (key) {
            case E:
                gameData.getCurrentCombat().endHeroTurn();
                if (gameData.getCurrentCombat().isCombatEnded()) {
                    gameData.endCombat();
                }
                break;
            default:
                // Equipment usage would be handled by mouse clicks
                break;
        }
    }

    private void handleTreasureKeys(KeyboardEvent.Key key) {
        if (key == KeyboardEvent.Key.SPACE) {
            gameData.leaveTreasureRoom();
        }
    }

    private void handleMerchantKeys(KeyboardEvent.Key key) {
        if (key == KeyboardEvent.Key.SPACE) {
            gameData.leaveMerchantRoom();
        }
    }

    private void handleHealerKeys(KeyboardEvent.Key key) {
        switch (key) {
            case H:
                gameData.purchaseHealing();
                break;
            case SPACE:
                gameData.leaveHealerRoom();
                break;
            default:
                break;
        }
    }

    private void handleMouseClick(int x, int y) {
        GameState state = gameData.getState();

        switch (state) {
            case COMBAT:
                handleCombatClick(x, y);
                break;
            case TREASURE:
                handleTreasureClick(x, y);
                break;
            case MERCHANT:
                handleMerchantClick(x, y);
                break;
            default:
                break;
        }
    }

    private void handleCombatClick(int x, int y) {
        if (gameData.getCurrentCombat() == null) {
            return;
        }

        // Backpack area is at (20, 420), cell size 40
        int backpackX = 20;
        int backpackY = 420;
        int cellSize = 40;

        // Check if click is on backpack (to select equipment)
        if (x >= backpackX && y >= backpackY &&
            x <= backpackX + (gameData.getHero().getBackpack().getCols() * cellSize) &&
            y <= backpackY + (gameData.getHero().getBackpack().getRows() * cellSize)) {

            int col = (x - backpackX) / cellSize;
            int row = (y - backpackY) / cellSize;

            model.Cell.PlacedEquipment pe = gameData.getHero().getBackpack().getEquipmentAt(row, col);
            if (pe != null) {
                selectedEquipment = pe;
            }
            return;
        }

        // Combat area is at (550, 280)
        // Each enemy takes about 100 pixels vertically
        int combatX = 550;
        int combatY = 280;
        int enemyStartY = combatY + 50;

        var enemies = gameData.getCurrentCombat().getEnemies();
        int currentY = enemyStartY;

        for (var enemy : enemies) {
            if (!enemy.isAlive()) {
                continue;
            }

            // Check if click is on this enemy
            if (x >= combatX && x <= combatX + 400 &&
                y >= currentY && y <= currentY + 100) {
                
                // If equipment is selected, use it on the enemy
                if (selectedEquipment != null) {
                    gameData.getCurrentCombat().useEquipment(selectedEquipment, enemy);
                    selectedEquipment = null;
                    if (gameData.getCurrentCombat().isCombatEnded()) {
                        gameData.endCombat();
                    }
                } else {
                    // Basic attack (1 damage, costs 1 energy)
                    if (gameData.getHero().useEnergy(1)) {
                        enemy.takeDamage(1);
                        gameData.getCurrentCombat().cleanupDeadEnemies();
                        if (gameData.getCurrentCombat().isCombatEnded()) {
                            gameData.endCombat();
                        }
                    }
                }
                return;
            }

            currentY += (enemy.getStatusEffects().isEmpty() ? 100 : 120);
        }
    }

    private void handleTreasureClick(int x, int y) {
        var room = gameData.getDungeon().getCurrentFloor().getCurrentRoom();
        var content = room.getContent();
        
        if (content == null || !content.hasEquipment()) {
            return;
        }

        // Treasure items area is at (550, 325)
        int treasureX = 550;
        int treasureY = 325;
        int lineHeight = 35;

        // Check if click is on an item
        int itemIndex = (y - treasureY) / lineHeight;
        var equipmentList = new java.util.ArrayList<>(content.getEquipment());
        
        if (itemIndex >= 0 && itemIndex < equipmentList.size() &&
            x >= treasureX && x <= treasureX + 400 &&
            y >= treasureY && y <= treasureY + (equipmentList.size() * lineHeight)) {
            
            var equipment = equipmentList.get(itemIndex);
            
            // Gold is picked up directly
            if (equipment instanceof model.equipment.Gold gold) {
                gameData.getHero().addGold(gold.getAmount());
                content.removeEquipment(equipment);
                return;
            }

            // Other items need to be placed in the backpack
            pendingEquipment = equipment;
            content.removeEquipment(equipment);
            return;
        }

        // Check if click is on backpack to place pending equipment
        if (pendingEquipment != null) {
            handleBackpackPlacement(x, y);
        }
    }

    private void handleMerchantClick(int x, int y) {
        var room = gameData.getDungeon().getCurrentFloor().getCurrentRoom();
        var content = room.getContent();
        
        if (content == null || !content.hasEquipment()) {
            return;
        }

        // Merchant items area is at (550, 355)
        int merchantX = 550;
        int merchantY = 355;
        int lineHeight = 20;

        // Check if click is on an item to buy
        int itemIndex = (y - merchantY) / lineHeight;
        var equipmentList = new java.util.ArrayList<>(content.getEquipment());
        
        if (itemIndex >= 0 && itemIndex < equipmentList.size() &&
            x >= merchantX && x <= merchantX + 400 &&
            y >= merchantY && y <= merchantY + (equipmentList.size() * lineHeight)) {
            
            var equipment = equipmentList.get(itemIndex);
            
            if (gameData.getHero().getGold() < equipment.getBuyPrice()) {
                return;
            }

            gameData.getHero().useGold(equipment.getBuyPrice());
            content.removeEquipment(equipment);
            pendingEquipment = equipment;
            return;
        }

        // Check if click is on backpack to place pending equipment
        if (pendingEquipment != null) {
            handleBackpackPlacement(x, y);
        }
    }

    private void handleBackpackPlacement(int x, int y) {
        int backpackX = 20;
        int backpackY = 420;
        int cellSize = 40;

        // Check if click is on backpack
        if (x >= backpackX && y >= backpackY &&
            x <= backpackX + (gameData.getHero().getBackpack().getCols() * cellSize) &&
            y <= backpackY + (gameData.getHero().getBackpack().getRows() * cellSize)) {
            
            int col = (x - backpackX) / cellSize;
            int row = (y - backpackY) / cellSize;
            
            if (gameData.getHero().getBackpack().placeEquipment(pendingEquipment, row, col, 0)) {
                pendingEquipment = null;
            }
        }
    }
}
