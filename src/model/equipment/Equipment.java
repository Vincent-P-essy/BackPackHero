package model.equipment;

import java.util.Objects;

/**
 * Abstract base class for all equipment in the game.
 */
public abstract class Equipment {
    private final String name;
    private final boolean[][] shape;
    private final int energyCost;
    private final int manaCost;
    private final int goldCost;
    private final Rarity rarity;
    private final int buyPrice;
    private final int sellPrice;

    /**
     * Equipment rarity levels.
     */
    public enum Rarity {
        COMMON, UNCOMMON, RARE, EPIC, LEGENDARY
    }

    /**
     * Creates a new equipment.
     * @param name the equipment name
     * @param shape the equipment shape (true = occupied cell)
     * @param energyCost energy cost to use
     * @param manaCost mana cost to use
     * @param goldCost gold cost to use
     * @param rarity the rarity level
     * @param buyPrice the buy price
     * @param sellPrice the sell price
     */
    protected Equipment(String name, boolean[][] shape, int energyCost, int manaCost,
                       int goldCost, Rarity rarity, int buyPrice, int sellPrice) {
        this.name = Objects.requireNonNull(name);
        this.shape = Objects.requireNonNull(shape);

        if (energyCost < 0 || manaCost < 0 || goldCost < 0) {
            throw new IllegalArgumentException("Costs cannot be negative");
        }
        if (buyPrice < 0 || sellPrice < 0) {
            throw new IllegalArgumentException("Prices cannot be negative");
        }

        this.energyCost = energyCost;
        this.manaCost = manaCost;
        this.goldCost = goldCost;
        this.rarity = Objects.requireNonNull(rarity);
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }

    /**
     * Gets the equipment name.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the equipment shape.
     * @return the shape
     */
    public boolean[][] getShape() {
        return shape.clone();
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
     * Gets the energy cost.
     * @return energy cost
     */
    public int getEnergyCost() {
        return energyCost;
    }

    /**
     * Gets the mana cost.
     * @return mana cost
     */
    public int getManaCost() {
        return manaCost;
    }

    /**
     * Gets the gold cost.
     * @return gold cost
     */
    public int getGoldCost() {
        return goldCost;
    }

    /**
     * Gets the rarity.
     * @return rarity
     */
    public Rarity getRarity() {
        return rarity;
    }

    /**
     * Gets the buy price.
     * @return buy price
     */
    public int getBuyPrice() {
        return buyPrice;
    }

    /**
     * Gets the sell price.
     * @return sell price
     */
    public int getSellPrice() {
        return sellPrice;
    }

    /**
     * Uses the equipment.
     * @param context the combat context
     */
    public abstract void use(CombatContext context);

    /**
     * Checks if this equipment can be rotated.
     * @return true if can be rotated
     */
    public boolean canRotate() {
        return true;
    }
}
