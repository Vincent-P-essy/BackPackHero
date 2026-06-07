package model.equipment;

import java.util.Map;
import java.util.HashMap;

/**
 * Registry containing all equipment definitions as static final instances.
 * This follows the professor's suggestion to avoid inheritance and use
 * static final collections instead of creating a class per equipment.
 */
public final class EquipmentRegistry {

    // Prevent instantiation
    private EquipmentRegistry() {}

    // Common shapes
    private static final boolean[][] SHAPE_1x1 = {{true}};
    private static final boolean[][] SHAPE_1x2 = {{true, true}};
    private static final boolean[][] SHAPE_1x3 = {{true, true, true}};
    private static final boolean[][] SHAPE_2x1 = {{true}, {true}};
    private static final boolean[][] SHAPE_2x2 = {{true, true}, {true, true}};
    private static final boolean[][] SHAPE_L = {{true, false}, {true, true}};

    /**
     * Wooden Sword - basic melee weapon.
     */
    public static final EquipmentData WOODEN_SWORD = new EquipmentData(
        "Wooden Sword",
        SHAPE_1x3,
        1, // energy cost
        0, // mana cost
        0, // gold cost
        Equipment.Rarity.COMMON,
        10, // buy price
        5,  // sell price
        context -> {
            if (context.getTargetEnemy() != null) {
                context.getTargetEnemy().takeDamage(6);
            }
        }
    );

    /**
     * Wooden Shield - basic defense item.
     */
    public static final EquipmentData WOODEN_SHIELD = new EquipmentData(
        "Wooden Shield",
        SHAPE_2x2,
        1, // energy cost
        0, // mana cost
        0, // gold cost
        Equipment.Rarity.COMMON,
        15, // buy price
        7,  // sell price
        context -> context.getHero().addProtection(5)
    );

    /**
     * Dart - quick throwing weapon.
     */
    public static final EquipmentData DART = new EquipmentData(
        "Dart",
        SHAPE_1x1,
        1, // energy cost
        0, // mana cost
        0, // gold cost
        Equipment.Rarity.COMMON,
        5,  // buy price
        2,  // sell price
        context -> {
            if (context.getTargetEnemy() != null) {
                context.getTargetEnemy().takeDamage(3);
            }
        }
    );

    /**
     * Leather Armor - provides protection.
     */
    public static final EquipmentData LEATHER_ARMOR = new EquipmentData(
        "Leather Armor",
        SHAPE_2x2,
        0, // energy cost (passive)
        0, // mana cost
        0, // gold cost
        Equipment.Rarity.COMMON,
        20, // buy price
        10, // sell price
        context -> {} // Passive effect handled elsewhere
    );

    /**
     * Tuna Can - food item that heals.
     */
    public static final EquipmentData TUNA_CAN = new EquipmentData(
        "Tuna Can",
        SHAPE_1x2,
        0, // energy cost
        0, // mana cost
        0, // gold cost
        Equipment.Rarity.COMMON,
        8,  // buy price
        4,  // sell price
        context -> context.getHero().heal(5)
    );

    /**
     * Mana Stone - provides mana in combat.
     */
    public static final EquipmentData MANA_STONE = new EquipmentData(
        "Mana Stone",
        SHAPE_1x1,
        0, // energy cost (passive)
        0, // mana cost
        0, // gold cost
        Equipment.Rarity.COMMON,
        12, // buy price
        6,  // sell price
        context -> {} // Provides 1 mana at combat start
    );

    /**
     * Magic Wand - uses mana to deal damage.
     */
    public static final EquipmentData MAGIC_WAND = new EquipmentData(
        "Magic Wand",
        SHAPE_1x2,
        0, // energy cost
        1, // mana cost
        0, // gold cost
        Equipment.Rarity.UNCOMMON,
        25, // buy price
        12, // sell price
        context -> {
            if (context.getTargetEnemy() != null) {
                context.getTargetEnemy().takeDamage(8);
            }
        }
    );

    /**
     * Health Stone - healing item.
     */
    public static final EquipmentData HEALTH_STONE = new EquipmentData(
        "Health Stone",
        SHAPE_1x1,
        0, // energy cost
        0, // mana cost
        0, // gold cost
        Equipment.Rarity.UNCOMMON,
        15, // buy price
        7,  // sell price
        context -> context.getHero().heal(10)
    );

    /**
     * Heart Gem - special gem that increases max HP.
     */
    public static final EquipmentData HEART_GEM = new EquipmentData(
        "Heart Gem",
        SHAPE_1x1,
        0, // energy cost (passive)
        0, // mana cost
        0, // gold cost
        Equipment.Rarity.RARE,
        30, // buy price
        15, // sell price
        context -> {} // Passive effect - increases max HP
    );

    /**
     * Gold - currency item.
     */
    public static EquipmentData createGold(int amount) {
        return new EquipmentData(
            "Gold (" + amount + ")",
            SHAPE_1x1,
            0, // energy cost
            0, // mana cost
            0, // gold cost
            Equipment.Rarity.COMMON,
            0,      // buy price
            amount, // sell price
            context -> {} // Special handling in game logic
        );
    }

    /**
     * Rust Curse - negative item that weakens hero.
     */
    public static final EquipmentData RUST_CURSE = new EquipmentData(
        "Rust Curse",
        SHAPE_2x1,
        0, // energy cost
        0, // mana cost
        0, // gold cost
        Equipment.Rarity.COMMON,
        0,  // buy price
        0,  // sell price
        context -> {} // Passive negative effect
    );

    /**
     * Heavy Curse - blocks multiple cells.
     */
    public static final EquipmentData HEAVY_CURSE = new EquipmentData(
        "Heavy Curse",
        SHAPE_2x2,
        0, // energy cost
        0, // mana cost
        0, // gold cost
        Equipment.Rarity.COMMON,
        0,  // buy price
        0,  // sell price
        context -> {} // Passive negative effect
    );

    /**
     * Map of all equipment by name for easy lookup.
     */
    public static final Map<String, EquipmentData> ALL_EQUIPMENT;

    static {
        Map<String, EquipmentData> equipment = new HashMap<>();
        equipment.put("WoodenSword", WOODEN_SWORD);
        equipment.put("WoodenShield", WOODEN_SHIELD);
        equipment.put("Dart", DART);
        equipment.put("LeatherArmor", LEATHER_ARMOR);
        equipment.put("TunaCan", TUNA_CAN);
        equipment.put("ManaStone", MANA_STONE);
        equipment.put("MagicWand", MAGIC_WAND);
        equipment.put("HealthStone", HEALTH_STONE);
        equipment.put("HeartGem", HEART_GEM);
        equipment.put("RustCurse", RUST_CURSE);
        equipment.put("HeavyCurse", HEAVY_CURSE);
        ALL_EQUIPMENT = Map.copyOf(equipment); // Immutable map
    }

    /**
     * Gets equipment by name.
     * @param name the equipment name
     * @return the equipment data, or null if not found
     */
    public static EquipmentData getByName(String name) {
        return ALL_EQUIPMENT.get(name);
    }
}
