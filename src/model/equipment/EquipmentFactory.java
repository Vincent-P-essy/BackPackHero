package model.equipment;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Factory for generating random equipment.
 */
public class EquipmentFactory {
    private static final Random random = new Random();

    private static final List<Supplier<Equipment>> COMMON_SUPPLIERS = List.of(
        WoodenSword::new,
        WoodenShield::new,
        Dart::new,
        LeatherArmor::new,
        TunaCan::new,
        ManaStone::new
    );

    private static final List<Supplier<Equipment>> UNCOMMON_SUPPLIERS = List.of(
        MagicWand::new,
        HealthStone::new
    );

    private static final List<Supplier<Equipment>> RARE_SUPPLIERS = List.of(
        HeartGem::new
    );

    /**
     * Generates random equipment. Common: 60%, Uncommon: 30%, Rare: 10%
     */
    public static Equipment generateRandom() {
        int roll = random.nextInt(100);

        if (roll < 60) {
            return pick(COMMON_SUPPLIERS).get();
        } else if (roll < 90) {
            return pick(UNCOMMON_SUPPLIERS).get();
        } else {
            return pick(RARE_SUPPLIERS).get();
        }
    }

    private static <T> T pick(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }

    /**
     * Generates multiple random equipment items.
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
     */
    public static Gold generateGold(int min, int max) {
        return new Gold(min + random.nextInt(max - min + 1));
    }

    /**
     * Generates rewards for defeating enemies.
     */
    public static List<Equipment> generateCombatRewards(int enemyCount) {
        List<Equipment> rewards = new ArrayList<>();
        int equipmentCount = enemyCount + random.nextInt(enemyCount + 1);
        rewards.addAll(generateMultiple(equipmentCount));
        rewards.add(new Gold(5 * enemyCount + random.nextInt(10 * enemyCount)));
        return rewards;
    }

    /**
     * Generates treasure room rewards.
     */
    public static List<Equipment> generateTreasureRewards() {
        List<Equipment> rewards = new ArrayList<>();
        rewards.addAll(generateMultiple(2 + random.nextInt(3)));
        rewards.add(generateGold(10, 30));
        return rewards;
    }

    /**
     * Generates merchant inventory.
     */
    public static List<Equipment> generateMerchantInventory() {
        List<Equipment> inventory = new ArrayList<>();
        inventory.addAll(generateMultiple(3 + random.nextInt(3)));
        inventory.add(new HealthStone());
        return inventory;
    }
}
