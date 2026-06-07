package model.equipment;

import java.util.Objects;

/**
 * Record representing equipment data.
 * Immutable data structure for equipment instances.
 */
public record EquipmentData(
    String name,
    boolean[][] shape,
    int energyCost,
    int manaCost,
    int goldCost,
    Equipment.Rarity rarity,
    int buyPrice,
    int sellPrice,
    EquipmentBehavior behavior
) {
    /**
     * Compact constructor with validation.
     */
    public EquipmentData {
        Objects.requireNonNull(name, "Name cannot be null");
        Objects.requireNonNull(shape, "Shape cannot be null");
        Objects.requireNonNull(rarity, "Rarity cannot be null");
        Objects.requireNonNull(behavior, "Behavior cannot be null");

        if (energyCost < 0 || manaCost < 0 || goldCost < 0) {
            throw new IllegalArgumentException("Costs cannot be negative");
        }
        if (buyPrice < 0 || sellPrice < 0) {
            throw new IllegalArgumentException("Prices cannot be negative");
        }
    }

    /**
     * Gets the rotated shape.
     * @param rotation rotation in degrees (0, 90, 180, 270)
     * @return the rotated shape
     */
    public boolean[][] getRotatedShape(int rotation) {
        if (rotation < 0 || rotation % 90 != 0) {
            throw new IllegalArgumentException("Rotation must be a multiple of 90");
        }

        int times = (rotation / 90) % 4;
        boolean[][] result = shape;

        for (int t = 0; t < times; t++) {
            result = rotateOnce(result);
        }

        return result;
    }

    /**
     * Rotates a shape 90 degrees clockwise.
     */
    private boolean[][] rotateOnce(boolean[][] s) {
        int rows = s.length;
        int cols = s[0].length;
        boolean[][] rotated = new boolean[cols][rows];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                rotated[j][rows - 1 - i] = s[i][j];
            }
        }

        return rotated;
    }

    /**
     * Uses this equipment in combat.
     * @param context the combat context
     */
    public void use(CombatContext context) {
        behavior.use(context);
    }
}
