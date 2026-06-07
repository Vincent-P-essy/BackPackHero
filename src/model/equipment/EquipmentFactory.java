package model.equipment;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory for generating random equipment.
 */
public class EquipmentFactory {
    private static final Random random = new Random();

    /**
     * Equipment pools by rarity.
     */
    private static final List<Class<? extends Equipment>> COMMON_EQUIPMENT = new ArrayList<>();
    private static final List<Class<? extends Equipment>> UNCOMMON_EQUIPMENT = new ArrayList<>();
    private static final List<Class<? extends Equipment>> RARE_EQUIPMENT = new ArrayList<>();

    static {
        // Common equipment
        COMMON_EQUIPMENT.add(WoodenSword.class);
        COMMON_EQUIPMENT.add(WoodenShield.class);
        COMMON_EQUIPMENT.add(Dart.class);
        COMMON_EQUIPMENT.add(LeatherArmor.class);
        COMMON_EQUIPMENT.add(TunaCan.class);
        COMMON_EQUIPMENT.add(ManaStone.class);

        // Uncommon equipment
        UNCOMMON_EQUIPMENT.add(MagicWand.class);
        UNCOMMON_EQUIPMENT.add(HealthStone.class);

        // Add more equipment as they are created
    }

    /**
     * Generates random equipment based on rarity weights.
     * Common: 60%, Uncommon: 30%, Rare: 10%
     * @return a random equipment
     */
    public static Equipment generateRandom() {
        int roll = random.nextInt(100);

        if (roll < 60) {
            // Common (60%)
            return createFromClass(
                COMMON_EQUIPMENT.get(random.nextInt(COMMON_EQUIPMENT.size()))
            );
        } else if (roll < 90) {
            // Uncommon (30%)
            if (!UNCOMMON_EQUIPMENT.isEmpty()) {
                return createFromClass(
                    UNCOMMON_EQUIPMENT.get(random.nextInt(UNCOMMON_EQUIPMENT.size()))
                );
            } else {
                return createFromClass(
                    COMMON_EQUIPMENT.get(random.nextInt(COMMON_EQUIPMENT.size()))
                );
            }
        } else {
            // Rare (10%)
            if (!RARE_EQUIPMENT.isEmpty()) {
                return createFromClass(
                    RARE_EQUIPMENT.get(random.nextInt(RARE_EQUIPMENT.size()))
                );
            } else if (!UNCOMMON_EQUIPMENT.isEmpty()) {
                return createFromClass(
                    UNCOMMON_EQUIPMENT.get(random.nextInt(UNCOMMON_EQUIPMENT.size()))
                );
            } else {
                return createFromClass(
                    COMMON_EQUIPMENT.get(random.nextInt(COMMON_EQUIPMENT.size()))
                );
            }
        }
    }

    /**
     * Generates multiple random equipment items.
     * @param count number of items to generate
     * @return list of equipment
     */
    public static List<Equipment> generateMultiple(int count) {
        List<Equipment> equipment = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            equipment.add(generateRandom());
        }

        return equipment;
    }

    /**
     * Generates random gold.
     * @param min minimum amount
     * @param max maximum amount
     * @return gold equipment
     */
    public static Gold generateGold(int min, int max) {
        int amount = min + random.nextInt(max - min + 1);
        return new Gold(amount);
    }

    /**
     * Creates an equipment instance from a class.
     */
    private static Equipment createFromClass(Class<? extends Equipment> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            // Fallback to wooden sword if creation fails
            return new WoodenSword();
        }
    }

    /**
     * Generates rewards for defeating enemies.
     * @param enemyCount number of enemies defeated
     * @return list of equipment rewards
     */
    public static List<Equipment> generateCombatRewards(int enemyCount) {
        List<Equipment> rewards = new ArrayList<>();

        // 1-2 equipment per enemy
        int equipmentCount = enemyCount + random.nextInt(enemyCount + 1);
        rewards.addAll(generateMultiple(equipmentCount));

        // Always give some gold
        int goldAmount = 5 * enemyCount + random.nextInt(10 * enemyCount);
        rewards.add(new Gold(goldAmount));

        return rewards;
    }

    /**
     * Generates treasure room rewards.
     * @return list of equipment
     */
    public static List<Equipment> generateTreasureRewards() {
        List<Equipment> rewards = new ArrayList<>();

        // 2-4 items
        int count = 2 + random.nextInt(3);
        rewards.addAll(generateMultiple(count));

        // Gold
        rewards.add(generateGold(10, 30));

        return rewards;
    }

    /**
     * Generates merchant inventory.
     * @return list of equipment for sale
     */
    public static List<Equipment> generateMerchantInventory() {
        List<Equipment> inventory = new ArrayList<>();

        // 3-5 items for sale
        int count = 3 + random.nextInt(3);
        inventory.addAll(generateMultiple(count));

        // Always have a health stone
        inventory.add(new HealthStone());

        return inventory;
    }
}
