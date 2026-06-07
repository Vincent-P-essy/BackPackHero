package controller;

import model.GameData;
import model.GameData.GameState;
import model.dungeon.Room;
import model.combat.Enemy;
import model.Cell.PlacedEquipment;
import model.equipment.Equipment;
import model.equipment.CombatContext;
import view.GameView;
import java.util.Scanner;
import java.util.List;
import java.util.Objects;

/**
 * Game controller - handles user input and game logic.
 */
public class GameController {
    private final GameData gameData;
    private final GameView gameView;
    private final Scanner scanner;
    private boolean running;
    private model.equipment.Equipment pendingEquipment; // Equipment waiting to be placed

    /**
     * Creates a new game controller.
     * @param gameData the game data
     * @param gameView the game view
     */
    public GameController(GameData gameData, GameView gameView) {
        this.gameData = Objects.requireNonNull(gameData);
        this.gameView = Objects.requireNonNull(gameView);
        this.scanner = new Scanner(System.in);
        this.running = false;
        this.pendingEquipment = null;
    }

    /**
     * Starts the game loop.
     */
    public void start() {
        running = true;

        System.out.println("Welcome to Backpack Hero!");
        System.out.println("Type 'help' for commands.\n");

        while (running && gameData.getHero().isAlive()) {
            gameView.display(gameData);

            GameState state = gameData.getState();

            if (state == GameState.VICTORY || state == GameState.DEFEAT) {
                running = false;
                break;
            }

            handleInput();
        }

        System.out.println("Game Over!");
        scanner.close();
    }

    /**
     * Handles user input based on current state.
     */
    private void handleInput() {
        System.out.print("> ");
        String input = scanner.nextLine().trim().toLowerCase();

        if (input.isEmpty()) {
            return;
        }

        String[] parts = input.split("\\s+");
        String command = parts[0];

        switch (command) {
            case "quit":
            case "exit":
                running = false;
                break;

            case "help":
                displayHelp();
                break;

            case "status":
                // View is already displaying status
                break;

            case "backpack":
            case "bag":
                displayBackpack();
                break;

            default:
                handleStateSpecificInput(input, parts);
                break;
        }
    }

    /**
     * Handles state-specific input.
     */
    private void handleStateSpecificInput(String input, String[] parts) {
        switch (gameData.getState()) {
            case EXPLORING:
                handleExploringInput(input, parts);
                break;

            case COMBAT:
                handleCombatInput(input, parts);
                break;

            case TREASURE:
                handleTreasureInput(input, parts);
                break;

            case MERCHANT:
                handleMerchantInput(input, parts);
                break;

            case HEALER:
                handleHealerInput(input, parts);
                break;

            default:
                System.out.println("Unknown command. Type 'help' for commands.");
                break;
        }
    }

    /**
     * Handles exploring state input.
     */
    private void handleExploringInput(String input, String[] parts) {
        String command = parts[0];

        switch (command) {
            case "move":
            case "go":
                if (parts.length >= 3) {
                    try {
                        int row = Integer.parseInt(parts[1]);
                        int col = Integer.parseInt(parts[2]);
                        Room targetRoom = gameData.getDungeon().getCurrentFloor().getRoom(row, col);

                        if (targetRoom != null) {
                            if (gameData.moveToRoom(targetRoom)) {
                                System.out.println("Moved to [" + row + ", " + col + "]");
                            } else {
                                System.out.println("Cannot move there!");
                            }
                        } else {
                            System.out.println("No room at that position!");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid coordinates!");
                    }
                } else {
                    System.out.println("Usage: move <row> <col>");
                }
                break;

            case "backpack":
            case "bag":
                displayBackpack();
                break;

            default:
                System.out.println("Unknown command. Type 'help' for commands.");
                break;
        }
    }

    /**
     * Handles combat state input.
     */
    private void handleCombatInput(String input, String[] parts) {
        if (gameData.getCurrentCombat() == null) {
            return;
        }

        String command = parts[0];

        switch (command) {
            case "use":
                if (parts.length >= 3) {
                    try {
                        String itemName = String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length - 1));
                        int targetIndex = Integer.parseInt(parts[parts.length - 1]) - 1;
                        handleUseEquipment(itemName, targetIndex);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid target! Usage: use <item> <enemy number>");
                    }
                } else {
                    System.out.println("Usage: use <item> <enemy number>");
                }
                break;

            case "attack":
                if (parts.length >= 2) {
                    try {
                        int enemyIndex = Integer.parseInt(parts[1]) - 1;
                        handleBasicAttack(enemyIndex);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid enemy number!");
                    }
                } else {
                    System.out.println("Usage: attack <enemy number>");
                }
                break;

            case "endturn":
            case "end":
                gameData.getCurrentCombat().endHeroTurn();

                if (gameData.getCurrentCombat().isCombatEnded()) {
                    gameData.endCombat();
                    System.out.println("Combat ended!");
                }
                break;

            case "backpack":
            case "bag":
                displayBackpack();
                break;

            default:
                System.out.println("Unknown command. In combat, use: use <item> <target>, attack <num>, endturn, backpack");
                break;
        }
    }

    /**
     * Handles using equipment in combat.
     */
    private void handleUseEquipment(String itemName, int targetIndex) {
        List<Enemy> enemies = gameData.getCurrentCombat().getEnemies();
        
        if (targetIndex < 0 || targetIndex >= enemies.size()) {
            System.out.println("Invalid enemy number!");
            return;
        }

        Enemy target = enemies.get(targetIndex);
        if (!target.isAlive()) {
            System.out.println("That enemy is already defeated!");
            return;
        }

        // Find equipment in backpack
        Equipment foundEquipment = null;
        for (PlacedEquipment pe : gameData.getHero().getBackpack().getEquipment()) {
            if (pe.equipment().getName().equalsIgnoreCase(itemName)) {
                foundEquipment = pe.equipment();
                break;
            }
        }

        if (foundEquipment == null) {
            System.out.println("Item '" + itemName + "' not found in backpack!");
            return;
        }

        // Check if hero has enough resources
        if (foundEquipment.getEnergyCost() > gameData.getHero().getEnergy()) {
            System.out.println("Not enough energy! Need " + foundEquipment.getEnergyCost() + ", have " + gameData.getHero().getEnergy());
            return;
        }

        if (foundEquipment.getManaCost() > gameData.getHero().getMana()) {
            System.out.println("Not enough mana! Need " + foundEquipment.getManaCost() + ", have " + gameData.getHero().getMana());
            return;
        }

        // Use the equipment
        CombatContext context = new CombatContext(gameData.getHero(), enemies);
        context.setTargetEnemy(target);
        
        // Consume resources
        gameData.getHero().useEnergy(foundEquipment.getEnergyCost());
        gameData.getHero().useMana(foundEquipment.getManaCost());
        
        // Use the item
        foundEquipment.use(context);
        System.out.println("Used " + foundEquipment.getName() + " on " + target.getName() + "!");

        if (!target.isAlive()) {
            System.out.println(target.getName() + " defeated!");
        }

        gameData.getCurrentCombat().cleanupDeadEnemies();
        if (gameData.getCurrentCombat().isCombatEnded()) {
            gameData.endCombat();
            System.out.println("Combat ended!");
        }
    }

    /**
     * Handles basic attack (punch with 1 damage, costs 1 energy).
     */
    private void handleBasicAttack(int targetIndex) {
        List<Enemy> enemies = gameData.getCurrentCombat().getEnemies();
        
        if (targetIndex < 0 || targetIndex >= enemies.size()) {
            System.out.println("Invalid enemy number!");
            return;
        }

        Enemy target = enemies.get(targetIndex);
        if (!target.isAlive()) {
            System.out.println("That enemy is already defeated!");
            return;
        }

        if (gameData.getHero().getEnergy() < 1) {
            System.out.println("Not enough energy! Need 1, have " + gameData.getHero().getEnergy());
            return;
        }

        gameData.getHero().useEnergy(1);
        target.takeDamage(1);
        System.out.println("You punch " + target.getName() + " for 1 damage!");
        
        if (!target.isAlive()) {
            System.out.println(target.getName() + " defeated!");
        }

        gameData.getCurrentCombat().cleanupDeadEnemies();
        if (gameData.getCurrentCombat().isCombatEnded()) {
            gameData.endCombat();
            System.out.println("Combat ended!");
        }
    }

    /**
     * Handles treasure room input.
     */
    private void handleTreasureInput(String input, String[] parts) {
        String command = parts[0];

        switch (command) {
            case "take":
            case "pickup":
                if (parts.length >= 2) {
                    String itemName = String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length));
                    handleTakeItem(itemName);
                } else {
                    System.out.println("Usage: take <item name>");
                }
                break;

            case "place":
                if (parts.length >= 3) {
                    try {
                        int row = Integer.parseInt(parts[1]);
                        int col = Integer.parseInt(parts[2]);
                        int rotation = (parts.length >= 4) ? Integer.parseInt(parts[3]) : 0;
                        handlePlaceItem(row, col, rotation);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid coordinates! Usage: place <row> <col> [rotation]");
                    }
                } else {
                    System.out.println("Usage: place <row> <col> [rotation]");
                }
                break;

            case "backpack":
            case "bag":
                displayBackpack();
                break;

            case "leave":
                if (pendingEquipment != null) {
                    System.out.println("You have an item waiting to be placed. Use 'place <row> <col>' or it will be discarded.");
                    pendingEquipment = null;
                }
                gameData.leaveTreasureRoom();
                System.out.println("Left treasure room.");
                break;

            default:
                System.out.println("Unknown command. Use: take <item>, place <row> <col>, backpack, leave");
                break;
        }
    }

    /**
     * Handles taking an item from the treasure room.
     */
    private void handleTakeItem(String itemName) {
        var room = gameData.getDungeon().getCurrentFloor().getCurrentRoom();
        var content = room.getContent();

        if (content == null || !content.hasEquipment()) {
            System.out.println("No items available.");
            return;
        }

        // Find item by name (case insensitive)
        model.equipment.Equipment foundItem = null;
        for (model.equipment.Equipment eq : content.getEquipment()) {
            if (eq.getName().equalsIgnoreCase(itemName)) {
                foundItem = eq;
                break;
            }
        }

        if (foundItem == null) {
            System.out.println("Item '" + itemName + "' not found.");
            return;
        }

        // Special handling for Gold - add directly to hero
        if (foundItem instanceof model.equipment.Gold gold) {
            gameData.getHero().addGold(gold.getAmount());
            content.removeEquipment(foundItem);
            System.out.println("Picked up " + gold.getName() + "! (+" + gold.getAmount() + " gold)");
            return;
        }

        // For other equipment, set as pending
        pendingEquipment = foundItem;
        content.removeEquipment(foundItem);
        System.out.println("Pick up " + foundItem.getName() + "? Use 'place <row> <col>' to place it in your backpack.");
        System.out.println("Item size: " + foundItem.getShape()[0].length + "x" + foundItem.getShape().length);
    }

    /**
     * Handles placing pending equipment in backpack.
     */
    private void handlePlaceItem(int row, int col, int rotation) {
        if (pendingEquipment == null) {
            System.out.println("No item to place. Use 'take <item>' first.");
            return;
        }

        model.Backpack backpack = gameData.getHero().getBackpack();

        if (backpack.placeEquipment(pendingEquipment, row, col, rotation)) {
            System.out.println(pendingEquipment.getName() + " placed at (" + row + ", " + col + ")");
            pendingEquipment = null;
        } else {
            System.out.println("Cannot place item at (" + row + ", " + col + "). Try different coordinates or rotation.");
            System.out.println("Backpack size: " + backpack.getRows() + "x" + backpack.getCols());
        }
    }

    /**
     * Handles merchant room input.
     */
    private void handleMerchantInput(String input, String[] parts) {
        String command = parts[0];

        switch (command) {
            case "list":
            case "items":
                displayMerchantItems();
                break;

            case "buy":
                if (parts.length >= 2) {
                    String itemName = String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length));
                    handleBuyItem(itemName);
                } else {
                    System.out.println("Usage: buy <item name>");
                }
                break;

            case "sell":
                if (parts.length >= 2) {
                    String itemName = String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length));
                    handleSellItem(itemName);
                } else {
                    System.out.println("Usage: sell <item name>");
                }
                break;

            case "backpack":
            case "bag":
                displayBackpack();
                break;

            case "leave":
                if (pendingEquipment != null) {
                    System.out.println("You have an item waiting to be placed. It will be discarded.");
                    pendingEquipment = null;
                }
                gameData.leaveMerchantRoom();
                System.out.println("Left merchant.");
                break;

            default:
                System.out.println("Unknown command. Use: list, buy <item>, sell <item>, backpack, leave");
                break;
        }
    }

    /**
     * Displays merchant items for sale.
     */
    private void displayMerchantItems() {
        var room = gameData.getDungeon().getCurrentFloor().getCurrentRoom();
        var content = room.getContent();

        System.out.println("\n=== MERCHANT INVENTORY ===");
        
        if (content == null || !content.hasEquipment()) {
            System.out.println("No items for sale.");
        } else {
            for (model.equipment.Equipment eq : content.getEquipment()) {
                System.out.println("- " + eq.getName() + " - " + eq.getBuyPrice() + " gold");
                System.out.println("  Size: " + eq.getShape()[0].length + "x" + eq.getShape().length);
            }
        }
        
        System.out.println("Your gold: " + gameData.getHero().getGold());
        System.out.println("=========================\n");
    }

    /**
     * Handles buying an item from merchant.
     */
    private void handleBuyItem(String itemName) {
        var room = gameData.getDungeon().getCurrentFloor().getCurrentRoom();
        var content = room.getContent();

        if (content == null || !content.hasEquipment()) {
            System.out.println("No items for sale.");
            return;
        }

        // Find item
        model.equipment.Equipment foundItem = null;
        for (model.equipment.Equipment eq : content.getEquipment()) {
            if (eq.getName().equalsIgnoreCase(itemName)) {
                foundItem = eq;
                break;
            }
        }

        if (foundItem == null) {
            System.out.println("Item '" + itemName + "' not found.");
            return;
        }

        // Check if hero has enough gold
        if (gameData.getHero().getGold() < foundItem.getBuyPrice()) {
            System.out.println("Not enough gold! Need " + foundItem.getBuyPrice() + ", have " + gameData.getHero().getGold());
            return;
        }

        // Set as pending for placement
        pendingEquipment = foundItem;
        content.removeEquipment(foundItem);
        gameData.getHero().useGold(foundItem.getBuyPrice());
        
        System.out.println("Bought " + foundItem.getName() + " for " + foundItem.getBuyPrice() + " gold!");
        System.out.println("Use 'place <row> <col>' to place it in your backpack.");
        System.out.println("Item size: " + foundItem.getShape()[0].length + "x" + foundItem.getShape().length);
    }

    /**
     * Handles selling an item from backpack.
     */
    private void handleSellItem(String itemName) {
        // Find equipment in backpack
        PlacedEquipment foundEquipment = null;
        for (PlacedEquipment pe : gameData.getHero().getBackpack().getEquipment()) {
            if (pe.equipment().getName().equalsIgnoreCase(itemName)) {
                foundEquipment = pe;
                break;
            }
        }

        if (foundEquipment == null) {
            System.out.println("Item '" + itemName + "' not found in backpack!");
            return;
        }

        int price = foundEquipment.equipment().getSellPrice();
        if (gameData.sellEquipment(foundEquipment)) {
            System.out.println("Sold " + foundEquipment.equipment().getName() + " for " + price + " gold!");
        } else {
            System.out.println("Could not sell item.");
        }
    }

    /**
     * Handles healer room input.
     */
    private void handleHealerInput(String input, String[] parts) {
        String command = parts[0];

        switch (command) {
            case "heal":
                if (gameData.purchaseHealing()) {
                    System.out.println("Healed!");
                } else {
                    System.out.println("Not enough gold!");
                }
                break;

            case "leave":
                gameData.leaveHealerRoom();
                System.out.println("Left healer.");
                break;

            default:
                System.out.println("Unknown command. Use: heal, leave");
                break;
        }
    }

    /**
     * Displays backpack contents.
     */
    private void displayBackpack() {
        System.out.println("\n=== BACKPACK ===");
        List<PlacedEquipment> equipment = gameData.getHero().getBackpack().getEquipment();

        if (equipment.isEmpty()) {
            System.out.println("Empty!");
        } else {
            for (PlacedEquipment pe : equipment) {
                System.out.println("- " + pe.equipment().getName() +
                    " at [" + pe.row() + ", " + pe.col() + "]" +
                    " rotation: " + pe.rotation() + "°");
            }
        }
        System.out.println("================\n");
    }

    /**
     * Displays help text.
     */
    private void displayHelp() {
        System.out.println("\n=== COMMANDS ===");
        System.out.println("General:");
        System.out.println("  help         - Show this help");
        System.out.println("  status       - Show hero status");
        System.out.println("  backpack     - Show backpack contents");
        System.out.println("  quit         - Quit game");
        System.out.println("\nExploring:");
        System.out.println("  move <r> <c> - Move to room at row r, column c");
        System.out.println("\nCombat:");
        System.out.println("  use <item> <n> - Use equipment on enemy number n");
        System.out.println("  attack <n>   - Basic attack (1 damage, 1 energy) on enemy n");
        System.out.println("  backpack     - Show backpack contents");
        System.out.println("  endturn      - End your turn");
        System.out.println("\nTreasure:");
        System.out.println("  take <item>  - Take item by name");
        System.out.println("  place <r> <c> [rot] - Place item at row r, col c (optional rotation)");
        System.out.println("  backpack     - Show current backpack");
        System.out.println("  leave        - Leave room");
        System.out.println("\nHealer:");
        System.out.println("  heal         - Purchase healing");
        System.out.println("  leave        - Leave healer");
        System.out.println("\nMerchant:");
        System.out.println("  list         - Show items for sale");
        System.out.println("  buy <item>   - Buy equipment");
        System.out.println("  sell <item>  - Sell equipment from backpack");
        System.out.println("  place <r> <c> [rot] - Place bought item at row r, col c");
        System.out.println("  backpack     - Show backpack contents");
        System.out.println("  leave        - Leave merchant");
        System.out.println("================\n");
    }
}
