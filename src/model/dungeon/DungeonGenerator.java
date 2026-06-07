package model.dungeon;

import model.Hero;
import model.combat.*;
import model.equipment.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generates dungeons.
 * Phase 1: Hardcoded floors
 * Phase 2: Random equipment generation
 */
public class DungeonGenerator {

    /**
     * Creates a Phase 1 dungeon with 3 floors (hardcoded).
     * @param hero the hero
     * @return the generated dungeon
     */
    public static Dungeon generatePhase1Dungeon(Hero hero) {
        Dungeon dungeon = new Dungeon(hero, 3);

        // Generate each floor
        generateFloor1(dungeon.getFloor(0));
        generateFloor2(dungeon.getFloor(1));
        generateFloor3(dungeon.getFloor(2));

        return dungeon;
    }

    /**
     * Creates a Phase 2 dungeon with 3 floors (random equipment).
     * @param hero the hero
     * @return the generated dungeon
     */
    public static Dungeon generatePhase2Dungeon(Hero hero) {
        Dungeon dungeon = new Dungeon(hero, 3);

        // Generate each floor with random equipment
        generateFloor1Phase2(dungeon.getFloor(0));
        generateFloor2Phase2(dungeon.getFloor(1));
        generateFloor3Phase2(dungeon.getFloor(2));

        return dungeon;
    }

    /**
     * Generates floor 1.
     */
    private static void generateFloor1(Floor floor) {
        // Create a simple path through the floor
        // Start at (2, 0)
        Room start = new Room(Room.RoomType.CORRIDOR, 2, 0);
        floor.setRoom(start);

        // Add corridor
        Room corridor1 = new Room(Room.RoomType.CORRIDOR, 2, 1);
        floor.setRoom(corridor1);

        // Add first enemy room
        Room enemy1 = new Room(Room.RoomType.ENEMY, 2, 2);
        RoomContent content1 = new RoomContent();
        content1.addEnemy(new SmallRatWolf());
        enemy1.setContent(content1);
        floor.setRoom(enemy1);

        // Add corridor
        Room corridor2 = new Room(Room.RoomType.CORRIDOR, 2, 3);
        floor.setRoom(corridor2);

        // Add treasure room
        Room treasure1 = new Room(Room.RoomType.TREASURE, 2, 4);
        RoomContent treasureContent = new RoomContent();
        treasureContent.addEquipment(new HealthStone());
        treasureContent.addEquipment(new Gold(10));
        treasure1.setContent(treasureContent);
        floor.setRoom(treasure1);

        // Add corridor
        Room corridor3 = new Room(Room.RoomType.CORRIDOR, 2, 5);
        floor.setRoom(corridor3);

        // Add merchant room
        Room merchant = new Room(Room.RoomType.MERCHANT, 2, 6);
        RoomContent merchantContent = new RoomContent();
        for (Equipment eq : EquipmentFactory.generateMerchantInventory()) {
            merchantContent.addEquipment(eq);
        }
        merchant.setContent(merchantContent);
        floor.setRoom(merchant);

        // Add corridor
        Room corridor4 = new Room(Room.RoomType.CORRIDOR, 2, 7);
        floor.setRoom(corridor4);

        // Add second enemy room
        Room enemy2 = new Room(Room.RoomType.ENEMY, 2, 8);
        RoomContent content2 = new RoomContent();
        content2.addEnemy(new SmallRatWolf());
        content2.addEnemy(new SmallRatWolf());
        enemy2.setContent(content2);
        floor.setRoom(enemy2);

        // Add corridor
        Room corridor5 = new Room(Room.RoomType.CORRIDOR, 2, 9);
        floor.setRoom(corridor5);

        // Add exit
        Room exit = new Room(Room.RoomType.EXIT, 2, 10);
        floor.setRoom(exit);
    }

    /**
     * Generates floor 2.
     */
    private static void generateFloor2(Floor floor) {
        // Start at (2, 0)
        Room start = new Room(Room.RoomType.CORRIDOR, 2, 0);
        floor.setRoom(start);

        // Add healer room
        Room healer = new Room(Room.RoomType.HEALER, 2, 1);
        RoomContent healerContent = new RoomContent();
        healerContent.setHealing(5, 10);
        healer.setContent(healerContent);
        floor.setRoom(healer);

        // Add corridor
        Room corridor1 = new Room(Room.RoomType.CORRIDOR, 2, 2);
        floor.setRoom(corridor1);

        // Add enemy room with rat-wolf
        Room enemy1 = new Room(Room.RoomType.ENEMY, 2, 3);
        RoomContent content1 = new RoomContent();
        content1.addEnemy(new RatWolf());
        enemy1.setContent(content1);
        floor.setRoom(enemy1);

        // Add corridor
        Room corridor2 = new Room(Room.RoomType.CORRIDOR, 2, 4);
        floor.setRoom(corridor2);

        // Add treasure room
        Room treasure = new Room(Room.RoomType.TREASURE, 2, 5);
        RoomContent treasureContent = new RoomContent();
        treasureContent.addEquipment(new ManaStone());
        treasureContent.addEquipment(new MagicWand());
        treasureContent.addEquipment(new Gold(15));
        treasure.setContent(treasureContent);
        floor.setRoom(treasure);

        // Add corridor
        Room corridor3 = new Room(Room.RoomType.CORRIDOR, 2, 6);
        floor.setRoom(corridor3);

        // Add second enemy room
        Room enemy2 = new Room(Room.RoomType.ENEMY, 2, 7);
        RoomContent content2 = new RoomContent();
        content2.addEnemy(new SmallRatWolf());
        content2.addEnemy(new RatWolf());
        enemy2.setContent(content2);
        floor.setRoom(enemy2);

        // Add corridor
        Room corridor4 = new Room(Room.RoomType.CORRIDOR, 2, 8);
        floor.setRoom(corridor4);

        // Add merchant
        Room merchant = new Room(Room.RoomType.MERCHANT, 2, 9);
        RoomContent merchantContent = new RoomContent();
        for (Equipment eq : EquipmentFactory.generateMerchantInventory()) {
            merchantContent.addEquipment(eq);
        }
        merchant.setContent(merchantContent);
        floor.setRoom(merchant);

        // Add exit
        Room exit = new Room(Room.RoomType.EXIT, 2, 10);
        floor.setRoom(exit);
    }

    /**
     * Generates floor 3.
     */
    private static void generateFloor3(Floor floor) {
        // Start at (2, 0)
        Room start = new Room(Room.RoomType.CORRIDOR, 2, 0);
        floor.setRoom(start);

        // Add corridor
        Room corridor1 = new Room(Room.RoomType.CORRIDOR, 2, 1);
        floor.setRoom(corridor1);

        // Add treasure room
        Room treasure1 = new Room(Room.RoomType.TREASURE, 2, 2);
        RoomContent treasureContent1 = new RoomContent();
        treasureContent1.addEquipment(new TunaCan());
        treasureContent1.addEquipment(new Dart());
        treasureContent1.addEquipment(new Gold(20));
        treasure1.setContent(treasureContent1);
        floor.setRoom(treasure1);

        // Add corridor
        Room corridor2 = new Room(Room.RoomType.CORRIDOR, 2, 3);
        floor.setRoom(corridor2);

        // Add healer
        Room healer = new Room(Room.RoomType.HEALER, 2, 4);
        RoomContent healerContent = new RoomContent();
        healerContent.setHealing(8, 15);
        healer.setContent(healerContent);
        floor.setRoom(healer);

        // Add corridor
        Room corridor3 = new Room(Room.RoomType.CORRIDOR, 2, 5);
        floor.setRoom(corridor3);

        // Add big enemy room
        Room enemy1 = new Room(Room.RoomType.ENEMY, 2, 6);
        RoomContent content1 = new RoomContent();
        content1.addEnemy(new RatWolf());
        content1.addEnemy(new RatWolf());
        enemy1.setContent(content1);
        floor.setRoom(enemy1);

        // Add corridor
        Room corridor4 = new Room(Room.RoomType.CORRIDOR, 2, 7);
        floor.setRoom(corridor4);

        // Add second treasure
        Room treasure2 = new Room(Room.RoomType.TREASURE, 2, 8);
        RoomContent treasureContent2 = new RoomContent();
        treasureContent2.addEquipment(new LeatherArmor());
        treasureContent2.addEquipment(new Gold(25));
        treasure2.setContent(treasureContent2);
        floor.setRoom(treasure2);

        // Add corridor
        Room corridor5 = new Room(Room.RoomType.CORRIDOR, 2, 9);
        floor.setRoom(corridor5);

        // Add final exit
        Room exit = new Room(Room.RoomType.EXIT, 2, 10);
        floor.setRoom(exit);
    }

    // =========== PHASE 2 GENERATORS WITH RANDOM EQUIPMENT ===========

    /**
     * Generates floor 1 with random equipment (Phase 2).
     */
    private static void generateFloor1Phase2(Floor floor) {
        // Same structure as Phase 1, but with random equipment

        Room start = new Room(Room.RoomType.CORRIDOR, 2, 0);
        floor.setRoom(start);

        Room corridor1 = new Room(Room.RoomType.CORRIDOR, 2, 1);
        floor.setRoom(corridor1);

        // Enemy room with random post-combat rewards
        Room enemy1 = new Room(Room.RoomType.ENEMY, 2, 2);
        RoomContent content1 = new RoomContent();
        content1.addEnemy(new SmallRatWolf());
        enemy1.setContent(content1);
        floor.setRoom(enemy1);

        Room corridor2 = new Room(Room.RoomType.CORRIDOR, 2, 3);
        floor.setRoom(corridor2);

        // Treasure with random equipment
        Room treasure1 = new Room(Room.RoomType.TREASURE, 2, 4);
        RoomContent treasureContent = new RoomContent();
        for (Equipment eq : EquipmentFactory.generateTreasureRewards()) {
            treasureContent.addEquipment(eq);
        }
        treasure1.setContent(treasureContent);
        floor.setRoom(treasure1);

        Room corridor3 = new Room(Room.RoomType.CORRIDOR, 2, 5);
        floor.setRoom(corridor3);

        Room merchant = new Room(Room.RoomType.MERCHANT, 2, 6);
        floor.setRoom(merchant);

        Room corridor4 = new Room(Room.RoomType.CORRIDOR, 2, 7);
        floor.setRoom(corridor4);

        Room enemy2 = new Room(Room.RoomType.ENEMY, 2, 8);
        RoomContent content2 = new RoomContent();
        content2.addEnemy(new SmallRatWolf());
        content2.addEnemy(new SmallRatWolf());
        enemy2.setContent(content2);
        floor.setRoom(enemy2);

        Room corridor5 = new Room(Room.RoomType.CORRIDOR, 2, 9);
        floor.setRoom(corridor5);

        Room exit = new Room(Room.RoomType.EXIT, 2, 10);
        floor.setRoom(exit);
    }

    /**
     * Generates floor 2 with random equipment (Phase 2).
     */
    private static void generateFloor2Phase2(Floor floor) {
        Room start = new Room(Room.RoomType.CORRIDOR, 2, 0);
        floor.setRoom(start);

        Room healer = new Room(Room.RoomType.HEALER, 2, 1);
        RoomContent healerContent = new RoomContent();
        healerContent.setHealing(5, 10);
        healer.setContent(healerContent);
        floor.setRoom(healer);

        Room corridor1 = new Room(Room.RoomType.CORRIDOR, 2, 2);
        floor.setRoom(corridor1);

        Room enemy1 = new Room(Room.RoomType.ENEMY, 2, 3);
        RoomContent content1 = new RoomContent();
        content1.addEnemy(new RatWolf());
        enemy1.setContent(content1);
        floor.setRoom(enemy1);

        Room corridor2 = new Room(Room.RoomType.CORRIDOR, 2, 4);
        floor.setRoom(corridor2);

        Room treasure = new Room(Room.RoomType.TREASURE, 2, 5);
        RoomContent treasureContent = new RoomContent();
        for (Equipment eq : EquipmentFactory.generateTreasureRewards()) {
            treasureContent.addEquipment(eq);
        }
        treasure.setContent(treasureContent);
        floor.setRoom(treasure);

        Room corridor3 = new Room(Room.RoomType.CORRIDOR, 2, 6);
        floor.setRoom(corridor3);

        Room enemy2 = new Room(Room.RoomType.ENEMY, 2, 7);
        RoomContent content2 = new RoomContent();
        content2.addEnemy(new SmallRatWolf());
        content2.addEnemy(new RatWolf());
        enemy2.setContent(content2);
        floor.setRoom(enemy2);

        Room corridor4 = new Room(Room.RoomType.CORRIDOR, 2, 8);
        floor.setRoom(corridor4);

        Room merchant = new Room(Room.RoomType.MERCHANT, 2, 9);
        floor.setRoom(merchant);

        Room exit = new Room(Room.RoomType.EXIT, 2, 10);
        floor.setRoom(exit);
    }

    /**
     * Generates floor 3 with random equipment (Phase 2).
     */
    private static void generateFloor3Phase2(Floor floor) {
        Room start = new Room(Room.RoomType.CORRIDOR, 2, 0);
        floor.setRoom(start);

        Room corridor1 = new Room(Room.RoomType.CORRIDOR, 2, 1);
        floor.setRoom(corridor1);

        Room treasure1 = new Room(Room.RoomType.TREASURE, 2, 2);
        RoomContent treasureContent1 = new RoomContent();
        for (Equipment eq : EquipmentFactory.generateTreasureRewards()) {
            treasureContent1.addEquipment(eq);
        }
        treasure1.setContent(treasureContent1);
        floor.setRoom(treasure1);

        Room corridor2 = new Room(Room.RoomType.CORRIDOR, 2, 3);
        floor.setRoom(corridor2);

        Room healer = new Room(Room.RoomType.HEALER, 2, 4);
        RoomContent healerContent = new RoomContent();
        healerContent.setHealing(8, 15);
        healer.setContent(healerContent);
        floor.setRoom(healer);

        Room corridor3 = new Room(Room.RoomType.CORRIDOR, 2, 5);
        floor.setRoom(corridor3);

        Room enemy1 = new Room(Room.RoomType.ENEMY, 2, 6);
        RoomContent content1 = new RoomContent();
        content1.addEnemy(new RatWolf());
        content1.addEnemy(new RatWolf());
        enemy1.setContent(content1);
        floor.setRoom(enemy1);

        Room corridor4 = new Room(Room.RoomType.CORRIDOR, 2, 7);
        floor.setRoom(corridor4);

        Room treasure2 = new Room(Room.RoomType.TREASURE, 2, 8);
        RoomContent treasureContent2 = new RoomContent();
        for (Equipment eq : EquipmentFactory.generateTreasureRewards()) {
            treasureContent2.addEquipment(eq);
        }
        treasure2.setContent(treasureContent2);
        floor.setRoom(treasure2);

        Room corridor5 = new Room(Room.RoomType.CORRIDOR, 2, 9);
        floor.setRoom(corridor5);

        Room exit = new Room(Room.RoomType.EXIT, 2, 10);
        floor.setRoom(exit);
    }

    /**
     * Creates a Phase 3 dungeon with procedurally generated floors.
     * @param hero the hero
     * @param floorCount number of floors
     * @return the generated dungeon
     */
    public static Dungeon generateProceduralDungeon(Hero hero, int floorCount) {
        Dungeon dungeon = new Dungeon(hero, floorCount);
        Random random = new Random();

        for (int i = 0; i < floorCount; i++) {
            generateProceduralFloor(dungeon.getFloor(i), i + 1, random);
        }

        return dungeon;
    }

    /**
     * Generates a single procedural floor.
     * @param floor the floor to generate
     * @param floorNumber the floor number (1-indexed)
     * @param random the random generator
     */
    private static void generateProceduralFloor(Floor floor, int floorNumber, Random random) {
        // Start position (middle left)
        int startRow = 2;
        int startCol = 0;

        // Exit position (middle right)
        int exitRow = 2;
        int exitCol = 10;

        // Create main path from start to exit
        List<int[]> mainPath = new ArrayList<>();
        mainPath.add(new int[]{startRow, startCol});

        int currentRow = startRow;
        int currentCol = startCol;

        // Build path to exit using random walk
        while (currentCol < exitCol) {
            // Move right with 70% probability, or up/down with 30%
            if (random.nextDouble() < 0.7 || currentCol == exitCol - 1) {
                currentCol++;
            } else {
                // Move vertically
                if (random.nextBoolean() && currentRow < 4) {
                    currentRow++;
                } else if (currentRow > 0) {
                    currentRow--;
                }
            }

            mainPath.add(new int[]{currentRow, currentCol});
        }

        // Ensure exit is at correct position
        if (currentRow != exitRow || currentCol != exitCol) {
            mainPath.add(new int[]{exitRow, exitCol});
        }

        // Place rooms along main path
        Room startRoom = new Room(Room.RoomType.CORRIDOR, startRow, startCol);
        floor.setRoom(startRoom);

        for (int i = 1; i < mainPath.size() - 1; i++) {
            int[] pos = mainPath.get(i);
            Room.RoomType type = determineRoomType(i, mainPath.size(), floorNumber, random);

            Room room = new Room(type, pos[0], pos[1]);

            // Add content based on type
            if (type == Room.RoomType.ENEMY) {
                RoomContent content = new RoomContent();
                int enemyCount = 1 + random.nextInt(2); // 1-2 enemies
                for (int j = 0; j < enemyCount; j++) {
                    content.addEnemy(generateRandomEnemy(floorNumber, random));
                }
                room.setContent(content);
            } else if (type == Room.RoomType.TREASURE) {
                RoomContent content = new RoomContent();
                for (Equipment eq : EquipmentFactory.generateTreasureRewards()) {
                    content.addEquipment(eq);
                }
                room.setContent(content);
            } else if (type == Room.RoomType.MERCHANT) {
                RoomContent content = new RoomContent();
                for (Equipment eq : EquipmentFactory.generateMerchantInventory()) {
                    content.addEquipment(eq);
                }
                room.setContent(content);
            } else if (type == Room.RoomType.HEALER) {
                RoomContent content = new RoomContent();
                content.setHealing(10, 15); // 10 gold for 15 HP
                room.setContent(content);
            }

            floor.setRoom(room);
        }

        // Place exit
        Room exitRoom = new Room(Room.RoomType.EXIT, exitRow, exitCol);
        floor.setRoom(exitRoom);

        // Add some side branches (optional rooms)
        addSideBranches(floor, mainPath, floorNumber, random);
    }

    /**
     * Determines the room type based on position in the path.
     * @param index position in path
     * @param pathLength total path length
     * @param floorNumber floor number
     * @param random random generator
     * @return room type
     */
    private static Room.RoomType determineRoomType(int index, int pathLength, int floorNumber, Random random) {
        // First few rooms are corridors
        if (index < 2) {
            return Room.RoomType.CORRIDOR;
        }

        // Last room before exit is usually a boss enemy
        if (index == pathLength - 2) {
            return Room.RoomType.ENEMY;
        }

        // Random distribution
        int roll = random.nextInt(100);

        if (roll < 40) {
            return Room.RoomType.ENEMY;
        } else if (roll < 60) {
            return Room.RoomType.TREASURE;
        } else if (roll < 70) {
            return Room.RoomType.MERCHANT;
        } else if (roll < 80) {
            return Room.RoomType.HEALER;
        } else {
            return Room.RoomType.CORRIDOR;
        }
    }

    /**
     * Generates a random enemy based on floor number.
     * @param floorNumber floor number
     * @param random random generator
     * @return random enemy
     */
    private static Enemy generateRandomEnemy(int floorNumber, Random random) {
        List<Enemy> possibleEnemies = new ArrayList<>();

        // Basic enemies (all floors)
        possibleEnemies.add(new SmallRatWolf());
        possibleEnemies.add(new RatWolf());

        // Phase 3 enemies (floor 2+)
        if (floorNumber >= 2) {
            possibleEnemies.add(new FrogSorcerer());
            possibleEnemies.add(new LivingShadow());
        }

        // Boss enemies (floor 3+)
        if (floorNumber >= 3) {
            possibleEnemies.add(new BeeQueen());
        }

        return possibleEnemies.get(random.nextInt(possibleEnemies.size()));
    }

    /**
     * Adds side branches to the floor for additional rooms.
     * @param floor the floor
     * @param mainPath the main path positions
     * @param floorNumber floor number
     * @param random random generator
     */
    private static void addSideBranches(Floor floor, List<int[]> mainPath, int floorNumber, Random random) {
        // Add 1-3 side branches
        int branchCount = 1 + random.nextInt(3);

        for (int i = 0; i < branchCount; i++) {
            // Pick a random point on main path (not start or exit)
            if (mainPath.size() < 4) continue;

            int pathIndex = 2 + random.nextInt(mainPath.size() - 3);
            int[] branchPoint = mainPath.get(pathIndex);

            // Try to add a room above or below (must be traversable from branch point)
            int[][] deltas = {{-1, 0}, {1, 0}}; // up, down
            for (int[] delta : deltas) {
                int branchRow = branchPoint[0] + delta[0];
                int branchCol = branchPoint[1];

                // Check if valid position
                if (branchRow < 0 || branchRow >= 5) continue;
                if (floor.getRoom(branchRow, branchCol) != null) continue;

                // Create side room (usually treasure or healer - both are CORRIDOR type so traversable)
                Room.RoomType type = random.nextDouble() < 0.6 ? Room.RoomType.TREASURE : Room.RoomType.HEALER;
                Room sideRoom = new Room(type, branchRow, branchCol);

                if (type == Room.RoomType.TREASURE) {
                    RoomContent content = new RoomContent();
                    for (Equipment eq : EquipmentFactory.generateTreasureRewards()) {
                        content.addEquipment(eq);
                    }
                    sideRoom.setContent(content);
                } else {
                    RoomContent content = new RoomContent();
                    content.setHealing(10, 15);
                    sideRoom.setContent(content);
                }

                floor.setRoom(sideRoom);
                break; // Only add one branch per attempt
            }
        }
    }
}
