package model.equipment;

/**
 * Heart Gem - passive healing when adjacent weapon is used.
 * Interaction: When any weapon in adjacent cell is used, heal 1 HP.
 * Shape: 1x1
 */
public class HeartGem extends Equipment {

    public HeartGem() {
        super(
            "Heart Gem",
            new boolean[][] {
                {true}
            },
            0, // No energy cost (passive)
            0, // No mana cost
            0, // No gold cost
            Rarity.RARE,
            30, // Buy price
            15  // Sell price
        );
    }

    @Override
    public void use(CombatContext context) {
        // Passive effect, handled in combat system
        // When adjacent weapon is used, this triggers healing
        context.getHero().heal(1);
    }

    /**
     * Checks if this gem should trigger based on equipment position.
     * This would be called by the combat system when checking interactions.
     * @param weaponRow row of weapon being used
     * @param weaponCol column of weapon being used
     * @param gemRow row of this gem
     * @param gemCol column of this gem
     * @return true if adjacent
     */
    public static boolean isAdjacent(int weaponRow, int weaponCol, int gemRow, int gemCol) {
        int rowDiff = Math.abs(weaponRow - gemRow);
        int colDiff = Math.abs(weaponCol - gemCol);

        return (rowDiff == 1 && colDiff == 0) || (rowDiff == 0 && colDiff == 1);
    }
}
