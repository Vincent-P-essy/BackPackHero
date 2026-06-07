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
    private model.equipment.Equipment selectedEquipment; // Equipment selected for use/placement
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
            System.out.println("Exiting game...");
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
            if (gameData.moveToRoom(targetRoom)) {
                System.out.println("Moved to " + targetRoom.getType() + " room");
            } else {
                System.out.println("Cannot move there!");
            }
        }
    }

    private void handleCombatKeys(KeyboardEvent.Key key) {
        if (gameData.getCurrentCombat() == null) {
            return;
        }

        switch (key) {
            case E:
                // End turn (which triggers enemy actions)
                System.out.println("Ending turn...");
                gameData.getCurrentCombat().endHeroTurn();

                // Check if combat is over
                if (gameData.getCurrentCombat().isCombatEnded()) {
                    gameData.endCombat();
                    if (gameData.getHero().isAlive()) {
                        System.out.println("Victory!");
                    } else {
                        System.out.println("Defeat!");
                    }
                }
                break;
            default:
                // Equipment usage would be handled by mouse clicks
                break;
        }
    }

    private void handleTreasureKeys(KeyboardEvent.Key key) {
        switch (key) {
            case SPACE:
                // Leave treasure room
                System.out.println("Leaving treasure room...");
                gameData.leaveTreasureRoom();
                break;
            default:
                break;
        }
        // Equipment pickup handled by mouse clicks
    }

    private void handleMerchantKeys(KeyboardEvent.Key key) {
        switch (key) {
            case SPACE:
                // Leave merchant
                System.out.println("Leaving merchant...");
                gameData.leaveMerchantRoom();
                break;
            default:
                break;
        }
    }

    private void handleHealerKeys(KeyboardEvent.Key key) {
        switch (key) {
            case H:
                // Purchase healing
                if (gameData.purchaseHealing()) {
                    System.out.println("Purchased healing!");
                } else {
                    System.out.println("Not enough gold!");
                }
                break;
            case SPACE:
                // Leave healer
                System.out.println("Leaving healer...");
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
            
            // Find equipment at this position
            for (model.Cell.PlacedEquipment pe : gameData.getHero().getBackpack().getEquipment()) {
                if (pe.row() == row && pe.col() == col) {
                    selectedEquipment = pe.equipment();
                    System.out.println("Selected: " + selectedEquipment.getName() + " - Click on enemy to use");
                    return;
                }
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
                    useEquipmentOnEnemy(selectedEquipment, enemy);
                    selectedEquipment = null;
                } else {
                    // Basic attack
                    if (gameData.getHero().getEnergy() >= 1) {
                        gameData.getHero().useEnergy(1);
                        enemy.takeDamage(1);
                        System.out.println("Punched " + enemy.getName() + " for 1 damage!");
                        
                        if (!enemy.isAlive()) {
                            System.out.println(enemy.getName() + " defeated!");
                        }

                        gameData.getCurrentCombat().cleanupDeadEnemies();
                        if (gameData.getCurrentCombat().isCombatEnded()) {
                            gameData.endCombat();
                            System.out.println("Combat ended!");
                        }
                    } else {
                        System.out.println("Not enough energy! Press E to end turn.");
                    }
                }
                return;
            }

            currentY += (enemy.getStatusEffects().isEmpty() ? 100 : 120);
        }
    }

    private void useEquipmentOnEnemy(model.equipment.Equipment equipment, model.combat.Enemy enemy) {
        var hero = gameData.getHero();
        
        // Check resources
        if (equipment.getEnergyCost() > hero.getEnergy()) {
            System.out.println("Not enough energy! Need " + equipment.getEnergyCost() + ", have " + hero.getEnergy());
            return;
        }
        
        if (equipment.getManaCost() > hero.getMana()) {
            System.out.println("Not enough mana! Need " + equipment.getManaCost() + ", have " + hero.getMana());
            return;
        }
        
        // Use equipment
        hero.useEnergy(equipment.getEnergyCost());
        hero.useMana(equipment.getManaCost());
        
        model.equipment.CombatContext context = new model.equipment.CombatContext(
            hero, 
            gameData.getCurrentCombat().getEnemies()
        );
        context.setTargetEnemy(enemy);
        equipment.use(context);
        
        System.out.println("Used " + equipment.getName() + " on " + enemy.getName() + "!");
        
        if (!enemy.isAlive()) {
            System.out.println(enemy.getName() + " defeated!");
        }

        gameData.getCurrentCombat().cleanupDeadEnemies();
        if (gameData.getCurrentCombat().isCombatEnded()) {
            gameData.endCombat();
            System.out.println("Combat ended!");
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
                System.out.println("Picked up " + gold.getAmount() + " gold!");
                return;
            }
            
            // Other items need to be placed
            pendingEquipment = equipment;
            content.removeEquipment(equipment);
            System.out.println("Selected " + equipment.getName() + " - Click on backpack to place it");
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
            
            // Check if hero has enough gold
            if (gameData.getHero().getGold() < equipment.getBuyPrice()) {
                System.out.println("Not enough gold! Need " + equipment.getBuyPrice() + ", have " + gameData.getHero().getGold());
                return;
            }
            
            // Buy the item
            gameData.getHero().useGold(equipment.getBuyPrice());
            content.removeEquipment(equipment);
            pendingEquipment = equipment;
            System.out.println("Bought " + equipment.getName() + " - Click on backpack to place it");
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
            
            // Try to place equipment
            if (gameData.getHero().getBackpack().placeEquipment(pendingEquipment, row, col, 0)) {
                System.out.println("Placed " + pendingEquipment.getName() + " at (" + row + ", " + col + ")");
                pendingEquipment = null;
            } else {
                System.out.println("Cannot place here. Try another spot.");
            }
        }
    }
}
