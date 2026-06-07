package view;

import com.github.forax.zen.ApplicationContext;
import model.GameData;
import java.awt.Color;
import java.awt.Graphics2D;

public record SimpleGameView(ApplicationContext context, int width, int height) implements GameView {

    @Override
    public void display(GameData gameData) {
        context.renderFrame(graphics -> {
            // Clear screen
            graphics.setColor(Color.BLACK);
            graphics.fillRect(0, 0, width, height);

            // Draw title
            graphics.setColor(Color.WHITE);
            graphics.drawString("Backpack Hero - " + gameData.getState(), 20, 30);

            // Draw Hero stats (top left)
            drawStats(graphics, gameData.getHero(), 20, 60);

            // Draw Map (top right)
            drawMap(graphics, gameData.getDungeon().getCurrentFloor(), 550, 60);

            // Draw Backpack (bottom left)
            drawBackpack(graphics, gameData.getHero().getBackpack(), 20, 420);

            // Draw Room Content (bottom right)
            drawRoomContent(graphics, gameData, 550, 280);
        });
    }

    private void drawStats(Graphics2D g, model.Hero hero, int x, int y) {
        g.setColor(Color.YELLOW);
        g.drawString("=== HERO STATS ===", x, y);
        
        g.setColor(Color.WHITE);
        g.drawString("HP: " + hero.getCurrentHealthPoints() + "/" + hero.getMaxHealthPoints(), x, y + 25);
        g.drawString("Energy: " + hero.getEnergy() + "/" + hero.getMaxEnergy(), x, y + 45);
        g.drawString("Mana: " + hero.getMana(), x, y + 65);
        g.drawString("Gold: " + hero.getGold(), x, y + 85);
        g.drawString("Level: " + hero.getLevel() + " (XP: " + hero.getExperience() + ")", x, y + 105);
        g.drawString("Protection: " + hero.getProtection(), x, y + 125);
    }

    private void drawMap(Graphics2D g, model.dungeon.Floor floor, int x, int y) {
        int cellSize = 25;

        g.setColor(Color.YELLOW);
        g.drawString("=== MAP (Floor " + floor.getFloorNumber() + ") ===", x, y - 15);
        
        g.setColor(Color.GRAY);
        g.drawRect(x - 5, y - 5, (floor.getCols() * cellSize) + 10, (floor.getRows() * cellSize) + 10);

        for (int r = 0; r < floor.getRows(); r++) {
            for (int c = 0; c < floor.getCols(); c++) {
                model.dungeon.Room room = floor.getRoom(r, c);
                if (room != null) {
                    int rx = x + c * cellSize;
                    int ry = y + r * cellSize;

                    // Color based on type
                    if (room == floor.getCurrentRoom()) {
                        g.setColor(Color.YELLOW); // Current room
                    } else if (room.isVisited()) {
                        g.setColor(Color.LIGHT_GRAY);
                    } else {
                        g.setColor(Color.DARK_GRAY);
                    }

                    g.fillRect(rx + 2, ry + 2, cellSize - 4, cellSize - 4);

                    // Draw border
                    g.setColor(Color.WHITE);
                    g.drawRect(rx + 2, ry + 2, cellSize - 4, cellSize - 4);

                    // Draw type letter
                    g.setColor(Color.BLACK);
                    String letter = room.getType().toString().substring(0, 1);
                    g.drawString(letter, rx + 10, ry + 20);
                }
            }
        }
    }

    private void drawBackpack(Graphics2D g, model.Backpack backpack, int x, int y) {
        int cellSize = 40;

        // Get equipment list
        java.util.List<model.Cell.PlacedEquipment> equipmentList = backpack.getEquipment();
        int equipmentCount = equipmentList.size();

        g.setColor(Color.YELLOW);
        g.drawString("=== BACKPACK (" + equipmentCount + " items) ===", x, y - 15);
        
        // DEBUG: Print equipment to console
        if (equipmentCount > 0) {
            System.out.println("[DEBUG] Drawing " + equipmentCount + " equipment(s):");
            for (model.Cell.PlacedEquipment pe : equipmentList) {
                System.out.println("  - " + pe.equipment().getName() + " at (" + pe.row() + "," + pe.col() + ") rot=" + pe.rotation());
            }
        }

        // Draw grid background first
        for (int r = 0; r < backpack.getRows(); r++) {
            for (int c = 0; c < backpack.getCols(); c++) {
                int bx = x + c * cellSize;
                int by = y + r * cellSize;
                g.setColor(Color.DARK_GRAY);
                g.fillRect(bx, by, cellSize, cellSize);
                g.setColor(Color.WHITE);
                g.drawRect(bx, by, cellSize, cellSize);
            }
        }

        // Draw equipment shapes with distinct colors
        Color[] equipmentColors = {
            new Color(100, 150, 255),  // Light blue
            new Color(255, 150, 100),  // Orange
            new Color(150, 255, 150),  // Light green
            new Color(255, 200, 100),  // Yellow
            new Color(200, 100, 255),  // Purple
            new Color(100, 255, 255)   // Cyan
        };
        int colorIndex = 0;
        
        for (model.Cell.PlacedEquipment pe : equipmentList) {
            
            // Use different color for each equipment
            Color equipColor = equipmentColors[colorIndex % equipmentColors.length];
            colorIndex++;
            
            // Draw equipment shape with border
            boolean[][] shape = pe.equipment().getRotatedShape(pe.rotation());
            for (int i = 0; i < shape.length; i++) {
                for (int j = 0; j < shape[i].length; j++) {
                    if (shape[i][j]) {
                        int bx = x + (pe.col() + j) * cellSize;
                        int by = y + (pe.row() + i) * cellSize;
                        // Fill with equipment color
                        g.setColor(equipColor);
                        g.fillRect(bx + 2, by + 2, cellSize - 4, cellSize - 4);
                        // Draw white border
                        g.setColor(Color.WHITE);
                        g.drawRect(bx + 1, by + 1, cellSize - 2, cellSize - 2);
                    }
                }
            }
            
            // Draw equipment name at top-left with black background for readability
            int bx = x + pe.col() * cellSize;
            int by = y + pe.row() * cellSize;
            String name = pe.equipment().getName();
            
            // Draw black background for text
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(bx + 2, by + 2, Math.min(name.length() * 6, cellSize * shape[0].length - 4), 14);
            
            // Draw equipment name in yellow
            g.setColor(Color.YELLOW);
            g.drawString(name, bx + 4, by + 13);
        }
    }

    private void drawRoomContent(Graphics2D g, GameData data, int x, int y) {
        g.setColor(Color.YELLOW);
        g.drawString("=== CURRENT ROOM ===", x, y);

        var state = data.getState();
        switch (state) {
            case COMBAT:
                drawCombat(g, data, x, y + 25);
                break;
            case TREASURE:
                drawTreasure(g, data, x, y + 25);
                break;
            case MERCHANT:
                drawMerchant(g, data, x, y + 25);
                break;
            case HEALER:
                drawHealer(g, data, x, y + 25);
                break;
            case VICTORY:
                drawGameOver(g, data, x, y + 25, true);
                break;
            case DEFEAT:
                drawGameOver(g, data, x, y + 25, false);
                break;
            default:
                g.setColor(Color.CYAN);
                g.drawString("Exploring...", x, y + 30);
                g.setColor(Color.WHITE);
                g.drawString("Use Arrow Keys to move", x, y + 50);
                break;
        }
    }

    private void drawCombat(Graphics2D g, GameData data, int x, int y) {
        model.combat.Combat combat = data.getCurrentCombat();
        if (combat == null) {
            return;
        }

        g.setColor(Color.RED);
        g.drawString("=== COMBAT ===", x, y);

        // Instructions
        g.setColor(Color.YELLOW);
        g.drawString("Click equipment in backpack, then enemy to use it", x, y + 20);
        g.setColor(Color.CYAN);
        g.drawString("Or click enemy directly for basic punch (1 dmg)", x, y + 35);
        g.setColor(Color.WHITE);
        g.drawString("Press 'E' to end turn", x, y + 50);

        // Draw enemies
        var enemies = combat.getEnemies();
        int enemyY = y + 75;
        int enemyIndex = 1;

        for (model.combat.Enemy enemy : enemies) {
            if (!enemy.isAlive()) {
                continue;
            }

            g.setColor(Color.RED);
            g.drawString("Enemy " + enemyIndex + ": " + enemy.getName(), x, enemyY);
            g.drawString("  HP: " + enemy.getCurrentHealthPoints() + "/" + enemy.getMaxHealthPoints(), x, enemyY + 15);

            // Show next action
            if (enemy.getNextAction() != null) {
                g.setColor(Color.ORANGE);
                String actionDesc = (enemy.getNextAction() == model.combat.Enemy.EnemyAction.ATTACK) ?
                    "Will Attack" : "Will Defend";
                g.drawString("  Next: " + actionDesc, x, enemyY + 30);
            }

            // Show protection
            if (enemy.getProtection() > 0) {
                g.setColor(Color.BLUE);
                g.drawString("  Shield: " + enemy.getProtection(), x, enemyY + 45);
            }

            // Show status effects
            var effects = enemy.getStatusEffects();
            if (!effects.isEmpty()) {
                g.setColor(Color.MAGENTA);
                StringBuilder effectStr = new StringBuilder("  Effects: ");
                for (model.combat.StatusEffect effect : effects) {
                    effectStr.append(effect.getName()).append("(").append(effect.getDuration()).append(") ");
                }
                g.drawString(effectStr.toString(), x, enemyY + 45);
                enemyY += 60;
            } else {
                enemyY += 45;
            }

            enemyIndex++;
        }

        // Show hero status effects
        var heroEffects = data.getHero().getStatusEffects();
        if (!heroEffects.isEmpty()) {
            g.setColor(Color.CYAN);
            g.drawString("Your effects:", x, enemyY + 15);
            int effectY = enemyY + 30;
            for (model.combat.StatusEffect effect : heroEffects) {
                g.drawString("  " + effect.getName() + " (" + effect.getDuration() + ")", x, effectY);
                effectY += 15;
            }
        }
    }

    private void drawTreasure(Graphics2D g, GameData data, int x, int y) {
        g.setColor(Color.GREEN);
        g.drawString("=== TREASURE ===", x, y);
        g.setColor(Color.YELLOW);
        g.drawString("Click item to take, then click backpack to place", x, y + 20);
        g.setColor(Color.WHITE);
        g.drawString("Press SPACE to leave", x, y + 35);

        var room = data.getDungeon().getCurrentFloor().getCurrentRoom();
        var content = room.getContent();

        if (content != null && content.hasEquipment()) {
            int itemY = y + 60;
            for (model.equipment.Equipment eq : content.getEquipment()) {
                g.setColor(Color.CYAN);
                boolean[][] shape = eq.getShape();
                int shapeHeight = shape.length;
                int shapeWidth = (shapeHeight > 0) ? shape[0].length : 0;
                g.drawString("- " + eq.getName() + " (" + shapeWidth + "x" + shapeHeight + ")", x, itemY);
                g.setColor(Color.LIGHT_GRAY);
                g.drawString("  Energy: " + eq.getEnergyCost() + ", Mana: " + eq.getManaCost(), x + 10, itemY + 15);
                itemY += 35;
            }
        } else {
            g.setColor(Color.GRAY);
            g.drawString("No items remaining", x, y + 45);
        }
    }

    private void drawMerchant(Graphics2D g, GameData data, int x, int y) {
        g.setColor(Color.YELLOW);
        g.drawString("=== MERCHANT ===", x, y);
        g.setColor(Color.WHITE);
        g.drawString("Click item to buy, then click backpack to place", x, y + 20);
        g.drawString("Press SPACE to leave", x, y + 35);

        // In a complete implementation, would show available items to buy
        var room = data.getDungeon().getCurrentFloor().getCurrentRoom();
        var content = room.getContent();

        if (content != null && content.hasEquipment()) {
            int itemY = y + 60;
            for (model.equipment.Equipment eq : content.getEquipment()) {
                g.setColor(Color.CYAN);
                g.drawString("- " + eq.getName() + " - " + eq.getBuyPrice() + " gold", x, itemY);
                itemY += 20;
            }
        }
    }

    private void drawHealer(Graphics2D g, GameData data, int x, int y) {
        g.setColor(Color.GREEN);
        g.drawString("=== HEALER ===", x, y);

        var room = data.getDungeon().getCurrentFloor().getCurrentRoom();
        var content = room.getContent();

        if (content != null) {
            int cost = content.getHealingCost();
            int amount = content.getHealingAmount();

            g.setColor(Color.WHITE);
            g.drawString("Healing available: +" + amount + " HP for " + cost + " gold", x, y + 25);
            g.setColor(Color.YELLOW);
            g.drawString("Press 'H' to heal, SPACE to leave", x, y + 45);
        }
    }

    private void drawGameOver(Graphics2D g, GameData data, int x, int y, boolean victory) {
        if (victory) {
            g.setColor(Color.GREEN);
            g.drawString("=== VICTORY! ===", x, y);
            g.setColor(Color.YELLOW);
            g.drawString("You have conquered the dungeon!", x, y + 25);
        } else {
            g.setColor(Color.RED);
            g.drawString("=== DEFEAT ===", x, y);
            g.setColor(Color.ORANGE);
            g.drawString("Your hero has fallen...", x, y + 25);
        }

        g.setColor(Color.WHITE);
        var hero = data.getHero();
        g.drawString("Final Stats:", x, y + 50);
        g.drawString("  Level: " + hero.getLevel(), x, y + 70);
        g.drawString("  Experience: " + hero.getExperience(), x, y + 85);
        g.drawString("  Gold: " + hero.getGold(), x, y + 100);

        g.setColor(Color.CYAN);
        g.drawString("Press Q to quit", x, y + 130);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
}
