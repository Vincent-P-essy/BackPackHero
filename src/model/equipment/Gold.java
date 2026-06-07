package model.equipment;

/**
 * Gold - currency item.
 * Shape: 1x1
 * Stores any amount of gold in a single cell
 */
public class Gold extends Equipment {
    private int amount;

    /**
     * Creates a gold item with specified amount.
     * @param amount the amount of gold
     */
    public Gold(int amount) {
        super(
            "Gold",
            new boolean[][] {
                {true}
            },
            0, // energy cost
            0, // mana cost
            0, // gold cost
            Rarity.COMMON,
            0, // buy price (gold is not bought)
            0  // sell price (gold is not sold)
        );

        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.amount = amount;
    }

    /**
     * Gets the amount of gold.
     * @return gold amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Sets the amount of gold.
     * @param amount the new amount
     */
    public void setAmount(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.amount = amount;
    }

    @Override
    public void use(CombatContext context) {
        // Gold is not used in combat
    }
}
