package model.equipment;

/**
 * A health stone - healing item.
 * Cost: free, but can only be used once per turn
 * Effect: Heals 1 HP
 * Shape: 1x1
 */
public class HealthStone extends Equipment {
    private static final int HEALING = 1;
    private boolean usedThisTurn;

    public HealthStone() {
        super(
            "Health Stone",
            new boolean[][] {
                {true}
            },
            0, // energy cost
            0, // mana cost
            0, // gold cost
            Rarity.UNCOMMON,
            20, // buy price
            10  // sell price
        );
        this.usedThisTurn = false;
    }

    @Override
    public void use(CombatContext context) {
        if (!usedThisTurn) {
            context.getHero().heal(HEALING);
            usedThisTurn = true;
        }
    }

    /**
     * Resets the usage for a new turn.
     */
    public void resetTurn() {
        usedThisTurn = false;
    }

    /**
     * Checks if this stone was used this turn.
     * @return true if used
     */
    public boolean isUsedThisTurn() {
        return usedThisTurn;
    }
}
